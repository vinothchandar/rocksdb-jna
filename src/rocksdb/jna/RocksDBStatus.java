package rocksdb.jna;

public enum RocksDBStatus {
    OK("ok"), NOT_FOUND("Not found"), IS_CORRUPTED("Corrupted!"), IS_NOT_SUPPORTED(
            "Not supported"), INVALID_ARGUMENT("Invalid Argument"), IO_ERROR(
            "IO Error"), MERGE_IN_PROGRESS("Merging in progress"), IS_INCOMPLETE(
            "In Complete");

    private String msg;

    RocksDBStatus(String msg) {
        this.msg = msg;
    }

    public String toString() {
        return this.msg;
    }

    public static RocksDBStatus mapStatus(int statusCode) {
        RocksDBStatus[] statusVals = RocksDBStatus.values();
        if (statusCode < 0 || statusCode >= statusVals.length) {
            return null;
        } else {
            return statusVals[statusCode];
        }
    }

}
