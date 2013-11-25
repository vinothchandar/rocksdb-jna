package rocksdb.jna;

import com.sun.jna.Pointer;

public class RocksDBEntryIterator extends RocksDBIterator {

    RocksDBEntryIterator(RocksDBWrapper rocksDBWrapper, Pointer dbReference,
            ReadOptions readOpts) {
        super(rocksDBWrapper);
        this.iteratorReference = rocksDBWrapper.openIterator(dbReference,
                readOpts);
    }

    public boolean hasNext() {
        if (!isOpen.get()) {
            throw new IllegalStateException("Iterator already closed");
        }
        return rocksDBWrapper.hasNext(iteratorReference);
    }
}
