package task3;

public class MyMemoryAllocation extends MemoryAllocation {
	
	private String algorithm;
	private int mem_size;
	private FreeBlockStruct freeBlocks;
	private UsedBlockStruct usedBlocks;
	
	public MyMemoryAllocation(int mem_size, String algorithm) {
		super(mem_size, algorithm);
		this.algorithm = algorithm;
		this.mem_size = mem_size;	
		this.freeBlocks = new FreeBlockStruct(algorithm, mem_size);
		this.usedBlocks = new UsedBlockStruct();
	}


	@Override
	public int alloc(int size) {
		
		return 0;
	}



	@Override
	public void free(int addr) {
		freeBlocks.insertList(addr, usedBlocks.removeByOffset(addr));
	}



	@Override
	public int size() {
		return freeBlocks.size();
	}



	@Override
	public int max_size() {
		return freeBlocks.maxSize(); //not implemented
	}



	@Override
	public void print() {
		
	}

}
