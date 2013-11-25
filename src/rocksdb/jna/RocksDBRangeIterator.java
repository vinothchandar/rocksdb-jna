package rocksdb.jna;

import java.nio.ByteBuffer;

import com.sun.jna.Pointer;

public class RocksDBRangeIterator extends RocksDBIterator {

    private byte[] startKey;
    private byte[] endKey;

    RocksDBRangeIterator(RocksDBWrapper rocksDBWrapper, Pointer dbReference,
            ReadOptions readOpts, byte[] startKey, byte[] endKey) {
        super(rocksDBWrapper);
        this.startKey = startKey;
        this.endKey = endKey;
        this.iteratorReference = rocksDBWrapper.openRangeIterator(dbReference,
                readOpts, ByteBuffer.wrap(startKey), startKey.length);
    }

    public boolean hasNext() {
        if (!isOpen.get()) {
            throw new IllegalStateException("Iterator already closed");
        }
        return rocksDBWrapper.hasRangeNext(iteratorReference,
                ByteBuffer.wrap(endKey), endKey.length);
    }
}
