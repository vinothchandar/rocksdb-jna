import java.nio.ByteBuffer;
import java.util.Random;

import rocksdb.jna.Options;
import rocksdb.jna.ReadOptions;
import rocksdb.jna.RocksDB;
import rocksdb.jna.RocksDBEntry;
import rocksdb.jna.RocksDBEntryIterator;
import rocksdb.jna.WriteOptions;

public class RocksDBJNAIteratorExample {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

        if (args.length != 1) {
            System.err
                    .println("usage: java RocksDBJNAIteratorExample <path_to_shared_lib>");
            System.exit(1);
        }

        RocksDB.loadLibrary(args[0]);

        Options opts = new Options();
        opts.cacheSizeBytes = 20 * 1024 * 1024;
        RocksDB rocksdb = new RocksDB("testdb", opts);

        ReadOptions readOpts = new ReadOptions();
        WriteOptions writeOpts = new WriteOptions();
       
        for (int i=0; i < 1000; i++){
            rocksdb.put(("key"+i).getBytes(), ("value"+i).getBytes(), writeOpts);
        }
        
        
        RocksDBEntryIterator iterator = rocksdb.entriesIterator(readOpts);
        int count = 0;
        while (iterator.hasNext()){
            RocksDBEntry entry = iterator.next();
            System.out.println("key :"+ new String(entry.getKey()) + 
                               "value:"+ new String(entry.getValue()));
            count++;
        }
        
        iterator.close();
        
        
        
        
        
        
        
        
        
        
        
        
        rocksdb.close();
    }

}
