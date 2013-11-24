package rocksdb.jna;
import java.nio.ByteBuffer;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;


public class RocksDB {

	private static RocksDBWrapper rocksDBWrapper;

	public static void loadLibrary(String nativeLibPath){
		rocksDBWrapper =  (RocksDBWrapper) Native.loadLibrary(nativeLibPath, RocksDBWrapper.class);
	}
	
	private String dbPath;
	private Pointer dbReference;
	
	public RocksDB(String dbPath, Options opts){
		this.dbPath = dbPath;
		dbReference = rocksDBWrapper.dbOpen(dbPath, opts);
	}
	
	public byte[] get(byte[] key, ReadOptions readOpts){
		IntByReference valueLenReference = new IntByReference();
		Pointer valPtr = rocksDBWrapper._get(dbReference, 
											 ByteBuffer.wrap(key), 
											 key.length,
											 readOpts,
											 valueLenReference);
		
		if (valPtr == Pointer.NULL){
			return null;
		}
		
		int valueLen = valueLenReference.getValue();
		byte[] value = valPtr.getByteArray(0, valueLen);
		rocksDBWrapper.freePointer(valPtr);
		return value;		
	}
	
	public void put(byte[] key, byte[] value, WriteOptions writeOpts){
		rocksDBWrapper._put(dbReference, ByteBuffer.wrap(key), key.length, ByteBuffer.wrap(value), value.length, writeOpts);
	}
	
	public void delete(byte[] key, WriteOptions writeOpts){
		rocksDBWrapper._delete(dbReference,ByteBuffer.wrap(key), key.length, writeOpts);
	}
	
	public void close() {
		System.out.println("Closing "+ dbPath);
		rocksDBWrapper.dbClose(dbReference);
	}
}
