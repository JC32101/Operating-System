package framework;
import java.util.logging.Level;
public class MyMapReduce extends MapReduce {
	//TODO: your code here. Define all attributes 
	//What is in a running instance of MapReduce?
	public void MREmit(Object key, Object value)
	{
		//TODO: your code here. Delete UnsupportedOperationException after your implementation is done.
		throw new UnsupportedOperationException();
	}

	public Object MRGetNext(Object key, int partition_number) {
		//TODO: your code here. Delete UnsupportedOperationException after your implementation is done.
		throw new UnsupportedOperationException();
	}
	@Override
	protected void MRRunHelper(String inputFileName,
		    		  MapperReducerClientAPI mapperReducerObj,
		    		  int num_mappers, 
		    		  int num_reducers)
	{
		//TODO: your code here. Delete UnsupportedOperationException after your implementation is done.
		throw new UnsupportedOperationException();
	}
}
