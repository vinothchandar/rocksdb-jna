package rocksdb.jna;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class ReadOptions extends Structure implements Structure.ByReference {

	/**
	 * Default: true
	 */
	public boolean verifyChecksums = true;
	/**
	 * Default: true
	 */
	public boolean fillCache = true; 
	
	@Override
	protected List getFieldOrder() {
		return Arrays.asList(new String[] { "verifyChecksums",
											"fillCache"}
		);
	}
}
