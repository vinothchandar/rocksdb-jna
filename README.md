Building Library:

1) Compile rocksdb: 

We need -fPIC enabled so we can make a static library. 
Change, rocksdb/MAKEFILE to have the CXXFLAGS include -fPIC

Something like below.
```
[rocksdb_home]$ git diff
diff --git a/Makefile b/Makefile
index be7758d..fc1aea7 100644
--- a/Makefile
+++ b/Makefile
@@ -33,7 +33,7 @@ endif
 
 WARNING_FLAGS = -Wall -Werror
 CFLAGS += -g $(WARNING_FLAGS) -I. -I./include $(PLATFORM_CCFLAGS) $(OPT)
-CXXFLAGS += -g $(WARNING_FLAGS) -I. -I./include $(PLATFORM_CXXFLAGS) $(OPT) -Woverloaded-virtual
+CXXFLAGS += -fPIC -g $(WARNING_FLAGS) -I. -I./include $(PLATFORM_CXXFLAGS) $(OPT) -Woverloaded-virtual
 
 LDFLAGS += $(PLATFORM_LDFLAGS)
```

2) Building rocksdb-jna native shared lib: 
```
$ cd cpp; rm rocksdb-jna.*o; 
# Replace with actual path
$ export ROCKSDB_HOME=~/projects/rocksdb; 
$ g++ -c rocksdb-jna.cc -fPIC -I $ROCKSDB_HOME/include -std=c++11; 
$ g++ -shared -fPIC -o rocksdb-jna-ubuntu.so rocksdb-jna.o $ROCKSDB_HOME/librocksdb.a $ROCKSDB_HOME/libmemenv.a -lrt
```

Then pass the location of this shared library to RocksDB.loadLibrary(..) before you do anything else!!

```
$ cd rocksdb-jna; ant
$ java -cp dist:dist/rocksdb-jna.jar RocksDBJNAExample `pwd`/cpp/rocksdb-jna-ubuntu.so
```

3) Get coding :
Now time to actually use it in a real application. grab dist/rocksdb-jna.jar. 




