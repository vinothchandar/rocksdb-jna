
#define FALSE 0
#define TRUE 1

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
