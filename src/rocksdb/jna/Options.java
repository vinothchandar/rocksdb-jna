package rocksdb.jna;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class Options extends Structure implements Structure.ByReference {

    /**
     * Default : 8MB
     */
    public long cacheSizeBytes = 8 * 1024 * 1024;
    /**
     * Default : false
     */
    public boolean paranoidChecks = false;
    /**
     * Default : 4MB
     */
    public int writeBufferSize = 4 * 1024 * 1024;
    /**
     * Default : 30k
     */
    public int maxOpenFiles = 30000;
    /**
     * Default: 4kb
     */
    public int blockSizeBytes = 4 * 1024;
    /**
     * Default : 10 bits. < 1, means no bloom filter
     */
    public int bloomFilterSize = 10;
    /**
     * Default : 0, no compression TODO: support all compression types
     */
    public int compressionType = 0;
    /**
     * Default : false
     */
    public boolean disableDataSync = false;
    /**
     * Default : false
     */
    public boolean useFsync = false;
    /**
     * Default : 300 ( 5 minutes)
     */
    public int dbStatsLogInterval = 300;
    /**
     * Default : 30 mins
     */
    public long deleteObsoleteFilesPeriodMicros = 30 * 60 * 1000 * 1000;
    /**
     * Default : 1
     */
    public int maxBackgroundCompactions = 1;
    /**
     * Default : 0
     */
    public int maxBackgroundFlushes = 0;
    /**
     * Default : false
     */
    public boolean noBlockCache = false;

    @Override
    protected List getFieldOrder() {
        return Arrays.asList(new String[] { "cacheSizeBytes", "paranoidChecks",
                "writeBufferSize", "maxOpenFiles", "blockSizeBytes",
                "bloomFilterSize", "compressionType", "disableDataSync",
                "useFsync", "dbStatsLogInterval",
                "deleteObsoleteFilesPeriodMicros", "maxBackgroundCompactions",
                "maxBackgroundFlushes", "noBlockCache" });
    }

}
