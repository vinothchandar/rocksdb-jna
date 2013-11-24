package rocksdb.jna;

import java.nio.ByteBuffer;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public interface RocksDBWrapper extends Library {

    Pointer dbOpen(String dbPath, Options opts, IntByReference statusCode,
            PointerByReference errorMsg);

    Pointer _get(Pointer dbReference, ByteBuffer keyBuf, int keyLen,
            ReadOptions readOpts, IntByReference valueLen,
            IntByReference statusCode, PointerByReference errorMsg);

    void _put(Pointer dbReference, ByteBuffer keyBuf, int keyLen,
            ByteBuffer valueBuf, int valueLen, WriteOptions writeOpts,
            IntByReference statusCode, PointerByReference errorMsg);

    void _delete(Pointer dbReference, ByteBuffer keyBuf, int keyLen,
            WriteOptions writeOpts, IntByReference statusCode,
            PointerByReference errorMsg);

    void dbClose(Pointer dbReference);

    void freePointer(Pointer memory);
}
