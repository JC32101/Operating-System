package framework;

import utils.BoundedBuffer;

public class PartitionTable extends BoundedBuffer{

    public PartitionTable(int size) {
        super(size);
    }

    public void deposit(String key, Object value) throws InterruptedException {
        KVPair p = new KVPair(key, value);
        super.deposit(p);
    }


	//Notes:
	// (1) each partition works like a bounded buffer between
	// mappers and a reducer. (you can assume size = 10 or 50)
	// (2) if reducer_i wants to fetch a KV pair it can
	// only fetches from partition_i, but mapper_i can drop messages
	// into different partitions.
    

}

