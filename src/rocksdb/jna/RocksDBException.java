package rocksdb.jna;

public class RocksDBException extends Exception {

    RocksDBStatus errorStatus;

    public RocksDBException(String msg, RocksDBStatus errorStatus) {
        super(msg);
        this.errorStatus = errorStatus;
    }

    public String toString() {
        if (this.errorStatus != null) {
            return super.toString() + ", " + errorStatus.toString();
        } else {
            return super.toString();
        }
    }
}
