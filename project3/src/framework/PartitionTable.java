package framework;

import utils.BoundedBuffer;

public class PartitionTable extends BoundedBuffer{

    private class PartPair {
        Object key;
        Object value;

        PartPair(Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        Object getKey() {
            return key;
        }

        Object getValue() {
            return value;
        }

    }

    public PartitionTable(int size) {
        super(size);
    }

    public void deposit(Object key, Object value) throws InterruptedException {
        PartPair p = new PartPair(key, value);
        super.deposit(p);
    }

    //TODO: your codde here
	//Notes:
	// (1) each partition works like a bounded buffer between
	// mappers and a reducer. (you can assume size = 10 or 50)
	// (2) if reducer_i wants to fetch a KV pair it can
	// only fetches from partition_i, but mapper_i can drop messages
	// into different partitions.
    

}

