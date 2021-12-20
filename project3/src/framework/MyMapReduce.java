package framework;
import java.util.logging.Level;
public class MyMapReduce extends MapReduce {
	private PartitionTable partitions[];
	private MapperReducerClientAPI mapperReducerObj;
	//public ConcurrentKVStore kvStore;
	//What is in a running instance of MapReduce?
	public void MREmit(Object key, Object value) {
		int partitionNum = (int) mapperReducerObj.Partitioner(key, partitions.length);
		while (true) {
			try {
				partitions[partitionNum].deposit(key, value);
				break;
			} catch (InterruptedException e) {
				continue; //is the proper behavior?
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

		throw new UnsupportedOperationException();
	}

	private void bufferReduce(int partitionNum) {

	}

	private class Reducer extends Thread {

		int bufferToReduce;

		private Reducer(int i) {
			bufferToReduce = i;
		}

		public void run() {
			bufferReduce(bufferToReduce);
		}
	}
}
