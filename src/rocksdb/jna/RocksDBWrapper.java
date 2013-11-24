package rocksdb.jna;
import java.nio.ByteBuffer;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;


public interface RocksDBWrapper extends Library {
		
    Pointer dbOpen(String dbPath, Options opts);
    
    Pointer _get(Pointer dbReference, ByteBuffer keyBuf, int keyLen, ReadOptions readOpts, IntByReference valueLen);
    
    void _put(Pointer dbReference, ByteBuffer keyBuf, int keyLen, ByteBuffer valueBuf, int valueLen, WriteOptions writeOpts);
    
    void _delete(Pointer dbReference, ByteBuffer keyBuf, int keyLen, WriteOptions writeOpts);
    
    void dbClose(Pointer dbReference);
    
    void freePointer(Pointer memory);
}
