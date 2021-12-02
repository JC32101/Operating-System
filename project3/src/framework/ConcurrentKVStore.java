	// All reducers also have concurrent access to a kv store which maps
	// a key to all the value associated to the key, for instace
	// if reducer_i has fetched ("foo", 1) ("bar",1) ("foo",1)
	// ("foo",1) and ("bar",1) from partition_i,
	// then after injecting them, reducer_i should have updated the KV
        // store to contain ("foo", {1,1,1}) and ("bar", {1,1}). You can use a
	// concurrent hashmap/tree to implement the concurrent KV store
