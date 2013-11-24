import java.nio.ByteBuffer;
import java.util.Random;

import rocksdb.jna.Options;
import rocksdb.jna.ReadOptions;
import rocksdb.jna.RocksDB;
import rocksdb.jna.WriteOptions;

public class RocksDBJNAExample {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

        if (args.length != 1) {
            System.err
                    .println("usage: java RocksDBJNAExample <path_to_shared_lib>");
            System.exit(1);
        }

        RocksDB.loadLibrary(args[0]);

        Options opts = new Options();
        opts.cacheSizeBytes = 20 * 1024 * 1024;
        RocksDB rocksdb = new RocksDB("testdb", opts);

        ReadOptions readOpts = new ReadOptions();
        WriteOptions writeOpts = new WriteOptions();

        rocksdb.put("hello".getBytes(), "world".getBytes(), writeOpts);

        System.out.println("After put: "
                + new String(rocksdb.get("hello".getBytes(), readOpts)));

        rocksdb.delete("hello".getBytes(), writeOpts);

        System.out.println("After delete: "
                + new String(rocksdb.get("hello".getBytes(), readOpts)));

        int recCount = 10000;
        Random rand = new Random();

        for (int i = 0; i < recCount; i++) {

            byte[] key = new byte[10];
            byte[] val = new byte[100];

            rand.nextBytes(key);
            rand.nextBytes(val);

            rocksdb.put(key, val, writeOpts);

            byte[] readVal = rocksdb.get(key, readOpts);

            if (!ByteBuffer.wrap(val).equals(ByteBuffer.wrap(readVal))) {
                System.out.println("Wrote in :" + ByteBuffer.wrap(val));
                System.out.println("Read out :" + ByteBuffer.wrap(readVal));
            }
        }
        rocksdb.close();
    }

}
