package framework;
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
				partitions[partitionNum].deposit(key, value);
				break;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public Object MRGetNext(Object key, int partition_number) {
		//TODO: your code here. Delete UnsupportedOperationException after your implementation is done.
		throw new UnsupportedOperationException();
	}
	@Override
	protected void MRRunHelper(String inputFileName, MapperReducerClientAPI mapperReducerObj,
		    		  int num_mappers, int num_reducers) {
		this.mapperReducerObj = mapperReducerObj;
		partitions = new PartitionTable[num_reducers];
		Thread mappers[] = new Thread[num_mappers];

		for (int i = 0; i < num_reducers; i++) {
			partitions[i] = new PartitionTable(10);
		}

		for (int i = 0; i < num_mappers; i++) {
			mappers[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					mapperReducerObj.Map(inputFileName);
				}
			});
			mappers[i].start();
		}

		Thread reducers[] = new Thread[num_mappers];
		for (int i = 0; i < num_reducers; i++) {
			reducers[i] = new Reducer(i);
			reducers[i].start();
		}

		for (int i = 0; i < num_mappers; i++) {
			try {
				mappers[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void emptyBuffer(int partitionNum) {
		while(true) {
			try {
				Object key = partitions[partitionNum].fetch();
				kvStore.put(key);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	private class Reducer extends Thread {

		int bufferToReduce;

		private Reducer(int i) {
			bufferToReduce = i;
		}

		public void run() {
			emptyBuffer(bufferToReduce);
		}
	}
}
