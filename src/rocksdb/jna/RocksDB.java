package rocksdb.jna;

import java.nio.ByteBuffer;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class RocksDB {

    private static RocksDBWrapper rocksDBWrapper;

    public static void loadLibrary(String nativeLibPath) {
        rocksDBWrapper = (RocksDBWrapper) Native.loadLibrary(nativeLibPath,
                RocksDBWrapper.class);
    }

    private String dbPath;
    private Pointer dbReference;

    private void throwExceptionIfNeeded(IntByReference statusCode,
            PointerByReference errMsgPtr) throws RocksDBException {
        RocksDBStatus status = RocksDBStatus.mapStatus(statusCode.getValue());
        Pointer ptr = errMsgPtr.getValue();

        String errMsg = "";
        if (ptr != Pointer.NULL) {
            errMsg = ptr.getString(0);
            rocksDBWrapper.freePointer(ptr);
        }

        if (status != RocksDBStatus.OK) {
            throw new RocksDBException(errMsg, status);
        }
    }

    public RocksDB(String dbPath, Options opts) throws RocksDBException {
        this.dbPath = dbPath;
        IntByReference statusCode = new IntByReference();
        PointerByReference errorMsgPtr = new PointerByReference();

        dbReference = rocksDBWrapper.dbOpen(dbPath, opts, statusCode,
                errorMsgPtr);
        throwExceptionIfNeeded(statusCode, errorMsgPtr);
    }

    public byte[] get(byte[] key, ReadOptions readOpts) throws RocksDBException {
        IntByReference valueLenReference = new IntByReference();

        IntByReference statusCode = new IntByReference();
        PointerByReference errorMsgPtr = new PointerByReference();

        Pointer valPtr = rocksDBWrapper._get(dbReference, ByteBuffer.wrap(key),
                key.length, readOpts, valueLenReference, statusCode,
                errorMsgPtr);

        if (valPtr == Pointer.NULL) {
            return null;
        }

        int valueLen = valueLenReference.getValue();
        byte[] value = valPtr.getByteArray(0, valueLen);
        rocksDBWrapper.freePointer(valPtr);
        return value;
    }

    public void put(byte[] key, byte[] value, WriteOptions writeOpts)
            throws RocksDBException {
        IntByReference statusCode = new IntByReference();
        PointerByReference errorMsgPtr = new PointerByReference();
        rocksDBWrapper._put(dbReference, ByteBuffer.wrap(key), key.length,
                ByteBuffer.wrap(value), value.length, writeOpts, statusCode,
                errorMsgPtr);
    }

    public void delete(byte[] key, WriteOptions writeOpts)
            throws RocksDBException {
        IntByReference statusCode = new IntByReference();
        PointerByReference errorMsgPtr = new PointerByReference();
        rocksDBWrapper._delete(dbReference, ByteBuffer.wrap(key), key.length,
                writeOpts, statusCode, errorMsgPtr);
    }
    
    
    public RocksDBEntryIterator entriesIterator(ReadOptions readOpts) {
        return new RocksDBEntryIterator(rocksDBWrapper, dbReference, readOpts);
    }

    public void close() {
        System.out.println("Closing " + dbPath);
        rocksDBWrapper.dbClose(dbReference);
    }
}
