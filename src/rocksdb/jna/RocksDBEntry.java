package rocksdb.jna;

public class RocksDBEntry {
    
    byte[] key;
    byte[] value;
    
    RocksDBEntry(byte[] key, byte[] value){
        this.key = key;
        this.value = value;
    }
    
    public byte[] getKey() {
        return key;
    }
    
    public byte[] getValue(){
        return value;
    }

}
