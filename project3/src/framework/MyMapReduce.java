package framework;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.logging.Level;
public class MyMapReduce extends MapReduce {
	private PartitionTable partitions[];
	private MapperReducerClientAPI mapperReducerObj;
	private ConcurrentKVStore kvStore;

	public MyMapReduce() {
		kvStore = new ConcurrentKVStore();
	}

	public void MREmit(Object key, Object value) {
		int partitionNum = (int) mapperReducerObj.Partitioner(key, partitions.length);
		while (true) {
			try {
				partitions[partitionNum].deposit((String) key, value);
				break;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public Object MRGetNext(Object key, int partition_number) {
		return kvStore.getNext(key, partition_number);
	}
	@Override
	protected void MRRunHelper(String inputFileName, MapperReducerClientAPI mapperReducerObj,
		    		  int num_mappers, int num_reducers) {
		//setup
		this.mapperReducerObj = mapperReducerObj;
		partitions = new PartitionTable[num_reducers];
		Thread mappers[] = new Thread[num_mappers];
		kvStore.setup(num_reducers);

		//create partitions
		for (int i = 0; i < num_reducers; i++) {
			partitions[i] = new PartitionTable(10);
		}

		//start mapper threads
		for (int i = 0; i < num_mappers; i++) {
			mappers[i] = new Mapper(i, inputFileName);
			mappers[i].start();
		}

		//start reducer threads
		Thread reducers[] = new Thread[num_mappers];
		for (int i = 0; i < num_reducers; i++) {
			reducers[i] = new Reducer(i);
			reducers[i].start();
		}

		//join mappers
		for (int i = 0; i < num_mappers; i++) {
			try {
				mappers[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		//add EOF to partitions
		for (int i = 0; i < num_reducers; i++) {
			try {
				partitions[i].deposit("EOF", 1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		//join reducers
		for (int i = 0; i < num_reducers; i++) {
			try {
				reducers[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void emptyBuffer(int partitionNum) {
		while(true) {
			try {
				KVPair key = (KVPair) partitions[partitionNum].fetch();
				if ((key.getKey() == "EOF")) {
					return;
				}
				kvStore.put(key, partitionNum);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	private class Mapper extends Thread {

		int mapperNum;
		String fileName;

		public Mapper(int i, String fileName) {
			mapperNum = i;
			this.fileName = fileName;
		}

		public void run() {
			mapperReducerObj.Map(fileName + ".0" + mapperNum);
		}

	}

	private class Reducer extends Thread {

		int bufferToReduce;

		private Reducer(int i) {
			bufferToReduce = i;
		}

		public void run() {
			emptyBuffer(bufferToReduce);
			ArrayList<String> keys = kvStore.getKeys(bufferToReduce);
			for (int i = 0; i < keys.size(); i++) {
				mapperReducerObj.Reduce(keys.get(i), bufferToReduce);
			}
		}
	}
}
