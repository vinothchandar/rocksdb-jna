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

void prepWriteOpts(rocksdb::WriteOptions* wOpts,
				   WriteOptions* opts)
{
	wOpts->sync = opts->sync;
	wOpts->disableWAL = opts->disableWAL;
}

void prepReadOpts(rocksdb::ReadOptions* rOpts, ReadOptions* opts){
	rOpts->verify_checksums = opts->verifyChecksums;
	rOpts->fill_cache = opts->fillCache;
}
