package rocksdb.jna;

import java.util.concurrent.atomic.AtomicBoolean;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public abstract class RocksDBIterator {
    protected RocksDBWrapper rocksDBWrapper;
    protected Pointer iteratorReference;
    protected AtomicBoolean isOpen;

    RocksDBIterator(RocksDBWrapper rocksDBWrapper) {
        this.rocksDBWrapper = rocksDBWrapper;
        isOpen = new AtomicBoolean(true);
    }

    public RocksDBEntry next() {
        if (!isOpen.get()) {
            throw new IllegalStateException("Iterator already closed");
        }

        IntByReference keyLengthReference = new IntByReference();
        IntByReference entryLengthReference = new IntByReference();

        Pointer entryPointer = rocksDBWrapper.next(iteratorReference,
                keyLengthReference, entryLengthReference);
        if (entryPointer == Pointer.NULL) {
            throw new IllegalStateException(
                    "Returned a null entry.. Unexpected.");
        }

        int keyLen = keyLengthReference.getValue();
        int entryLen = entryLengthReference.getValue();

        byte[] key = entryPointer.getByteArray(0, keyLen);
        byte[] value = entryPointer.getByteArray(keyLen, entryLen - keyLen);

        rocksDBWrapper.freePointer(entryPointer);

        return new RocksDBEntry(key, value);
    }

    public void close() {
        isOpen.set(false);
        rocksDBWrapper.closeIterator(iteratorReference);
    }
}
