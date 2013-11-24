package rocksdb.jna;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class WriteOptions extends Structure implements Structure.ByReference {

    /**
     * Default: false
     */
    public boolean sync = false;
    /**
     * Default: false
     */
    public boolean disableWAL = false;

    @Override
    protected List getFieldOrder() {
        return Arrays.asList(new String[] { "sync", "disableWAL" });
    }
}
