#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <iostream>
#include <string>
#include "rocksdb/db.h"
#include "rocksdb/cache.h"
#include "rocksdb/filter_policy.h"

#include "rocksdb-jna.h"
#include "rocksdb-jnautil.h"

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
	std::string value;

	rocksdb::ReadOptions rOpts;
	prepReadOpts(&rOpts, readOpts);

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

extern "C" void* openIterator(void* dbReference, ReadOptions* readOpts){
	rocksdb::Iterator* itr = createIterator((rocksdb::DB*) dbReference, readOpts);
	itr->SeekToFirst();
	return itr;
}

extern "C" void* openRangeIterator(void* dbReference,
								   ReadOptions* readOpts,
								   const char* startKeyBuf,
								   int startKeyLen)
{
	rocksdb::Iterator* itr = createIterator((rocksdb::DB*) dbReference, readOpts);
	rocksdb::Slice startKeySlice(startKeyBuf, startKeyLen);
	itr->Seek(startKeySlice);
	return itr;
}

extern "C" int hasNext(void* itrReference){
	rocksdb::Iterator* itr = (rocksdb::Iterator*) itrReference;
	return itr->Valid();
}

extern "C" int hasRangeNext(void* itrReference, const char* endKeyBuf, int endKeyLen){
	rocksdb::Iterator* itr = (rocksdb::Iterator*) itrReference;
	if (itr->Valid()){
		rocksdb::Slice key = itr->key();
		rocksdb::Slice endKey(endKeyBuf, endKeyLen);
		if (key.compare(endKey) <= 0){
			return TRUE;
		} else {
			return FALSE;
		}
	} else {
		return FALSE;
	}
}

extern "C" void* next(void* itrReference, int* keyLen, int* entryLen){
	rocksdb::Iterator* itr = (rocksdb::Iterator*) itrReference;
	rocksdb::Slice key = itr->key();
	rocksdb::Slice value = itr->value();

	*keyLen = key.size();
	*entryLen = *keyLen + value.size();
	char* entry = (char*) malloc(*entryLen);
	memcpy(entry, key.data(), *keyLen);
	memcpy(entry + *keyLen, value.data(), *entryLen - *keyLen);

	itr->Next();
	return entry;
}

extern "C" void closeIterator(void* itrReference){
	delete (rocksdb::Iterator*) itrReference;
}




