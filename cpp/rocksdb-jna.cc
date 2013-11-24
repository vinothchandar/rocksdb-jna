#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <iostream>
#include <string>
#include "rocksdb/db.h"
#include "rocksdb/cache.h"
#include "rocksdb/filter_policy.h"

enum StatusCode {
    kOk = 0,
    kNotFound = 1,
    kCorruption = 2,
    kNotSupported = 3,
    kInvalidArgument = 4,
    kIOError = 5,
    kMergeInProgress = 6,
    kIncomplete = 7
};

typedef struct ReadOptions {
	int verifyChecksums;
	int fillCache;
} ReadOptions;

typedef struct WriteOptions {
	int sync;
	int disableWAL;
} WriteOptions;

typedef struct Options {
	long cacheSizeBytes;
	int paranoidChecks;
	int writeBufferSize;
	int maxOpenFiles;
	int blockSizeBytes;
	int bloomFilterSize;
	int compressionType;
	int disableDataSync;
	int useFsync;
	int dbStatsLogInterval;
	long deleteObsoleteFilesPeriodMicros;
	int maxBackgroundCompactions;
	int maxBackgroundFlushes;
	int noBlockCache;
} Options;


void logOptions(Options* opts) {
	std::cout << "Options :" << std::endl;
	std::cout << "CacheSizeBytes :" << opts->cacheSizeBytes << std::endl;
	std::cout << "Paranoid Checks:" << opts->paranoidChecks << std::endl;
	std::cout << "WriteBufferSize:" << opts->writeBufferSize << std::endl;
	std::cout << "MaxOpenFiles   :" << opts->maxOpenFiles << std::endl;
	std::cout << "BlockSizeBytes :" << opts->blockSizeBytes << std::endl;
	std::cout << "BloomFilterSize:" << opts->bloomFilterSize << std::endl;
	std::cout << "CompressionType:" << opts->compressionType << std::endl;
	std::cout << "DisableDataSync:" << opts->disableDataSync << std::endl;
	std::cout << "UseFsync       :" << opts->useFsync << std::endl;
	std::cout << "StatsLogIntvl  :" << opts->dbStatsLogInterval << std::endl;
	std::cout << "delObsoleteUs  :" << opts->deleteObsoleteFilesPeriodMicros << std::endl;
	std::cout << "MaxBGCompaction:" << opts->maxBackgroundCompactions << std::endl;
	std::cout << "MaxBGFlushes   :" << opts->maxBackgroundFlushes << std::endl;
	std::cout << "noBlockCache   :" << opts->noBlockCache << std::endl;
}

void setupOpts(rocksdb::Options* ropts, Options* opts){

	ropts->create_if_missing = true;

	if (opts->cacheSizeBytes > 0){
		ropts->block_cache = rocksdb::NewLRUCache((size_t)opts->cacheSizeBytes);
	}
	ropts->paranoid_checks = opts->paranoidChecks;
	ropts->write_buffer_size = opts->writeBufferSize;
	ropts->max_open_files = opts->maxOpenFiles;
	ropts->block_size = opts->blockSizeBytes;

	if (opts->bloomFilterSize > 0){
		ropts->filter_policy = rocksdb::NewBloomFilterPolicy(opts->bloomFilterSize);
	}

	// TODO may need to link against snappy lib too
	ropts->compression = rocksdb::CompressionType::kNoCompression;

	ropts->disableDataSync = opts->disableDataSync;
	ropts->use_fsync = opts->useFsync;
	ropts->delete_obsolete_files_period_micros = opts->deleteObsoleteFilesPeriodMicros;
	ropts->db_stats_log_interval = opts->dbStatsLogInterval;
	ropts->max_background_compactions = opts->maxBackgroundCompactions;
	ropts->max_background_flushes = opts->maxBackgroundFlushes;
	ropts->no_block_cache = opts->noBlockCache;
}

void fillInStatus(rocksdb::Status* s, int* statusCode, char** errMsg) {
	if(s->ok()){
		*statusCode = kOk;
	} else if (s->IsNotFound()) {
		*statusCode = kNotFound;
	} else if (s->IsCorruption()) {
		*statusCode = kCorruption;
	} else if (s->IsNotSupported()) {
		*statusCode = kNotSupported;
	} else if (s->IsInvalidArgument()) {
		*statusCode = kInvalidArgument;
	} else if (s->IsIOError()) {
		*statusCode = kIOError;
	} else if (s->IsMergeInProgress()) {
		*statusCode = kMergeInProgress;
	} else if (s->IsIncomplete()) {
		*statusCode = kIncomplete;
	} else {
		*statusCode = -1;
	}

	if (s->ok()){
		*errMsg = NULL;
	} else {
		std::string serr = s->ToString();

		*errMsg = (char*)malloc(sizeof(char) * serr.size());
		memset(*errMsg, 0, sizeof(char) * serr.size());
		strcpy(*errMsg, serr.c_str());
	}

}

extern "C" void* dbOpen(const char* dbPath,
						Options* opts,
						int* statusCode,
						char** errorMsg)
{
	// Configure the database as requested
	rocksdb::Options options;
	logOptions(opts);
	setupOpts(&options, opts);

	// Alright, let's try to open it up.
	rocksdb::DB* db = NULL;
	rocksdb::Status status = rocksdb::DB::Open(options,
												dbPath,
												&db);
	fillInStatus(&status, statusCode, errorMsg);
	return db;
}

extern "C" void* _get(void* dbReference,
					  const char* keyBuf,
					  int keyLen,
					  ReadOptions* readOpts,
					  int* valueLen,
					  int* statusCode,
					  char** errorMsg)
{

	rocksdb::DB* db = (rocksdb::DB*) dbReference;

	rocksdb::Slice key(keyBuf, keyLen);
	std::string* val = new std::string();
	std::string value;

	rocksdb::ReadOptions rOpts;
	rOpts.verify_checksums = readOpts->verifyChecksums;
	rOpts.fill_cache = readOpts->fillCache;

	rocksdb::Status status = db->Get(rOpts,
									 key,
									 &value);
	fillInStatus(&status, statusCode, errorMsg);

	*valueLen = value.length();
	char* ret = (char*) malloc(*valueLen);
	// TODO avoid this extra copy
	memcpy(ret, value.data(), *valueLen);
	return (void*) ret;
}

void prepWriteOpts(rocksdb::WriteOptions* wOpts,
				   WriteOptions* opts)
{
	wOpts->sync = opts->sync;
	wOpts->disableWAL = opts->disableWAL;
}


extern "C" void _put(void* dbReference,
		             const char* keyBuf,
		             int keyLen,
		             const char* valueBuf,
		             int valueLen,
		             WriteOptions* writeOpts,
					 int* statusCode,
					 char** errorMsg)
{
	rocksdb::DB* db = (rocksdb::DB*) dbReference;
	rocksdb::Slice key(keyBuf, keyLen);
	rocksdb::Slice value(valueBuf, valueLen);

	rocksdb::WriteOptions wOpts;
	prepWriteOpts(&wOpts, writeOpts);
	rocksdb::Status status = db->Put(wOpts,
									 key,
									 value);
	fillInStatus(&status, statusCode, errorMsg);
}

extern "C" void _delete(void* dbReference,
						const char* keyBuf,
						int keyLen,
						WriteOptions* writeOpts,
						int* statusCode,
						char** errorMsg)
{
	rocksdb::DB* db = (rocksdb::DB*) dbReference;
	rocksdb::Slice key(keyBuf, keyLen);
	rocksdb::WriteOptions wOpts;
	prepWriteOpts(&wOpts, writeOpts);
	rocksdb::Status status = db->Delete(wOpts,
									    key);
	fillInStatus(&status, statusCode, errorMsg);
}

extern "C" void dbClose(void* dbReference) {
	delete (rocksdb::DB*) dbReference;
}

extern "C" void freePointer(void* memory){
	free(memory);
}
