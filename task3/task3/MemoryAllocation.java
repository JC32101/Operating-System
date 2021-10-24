package task3;

public abstract class MemoryAllocation {
	
	/*
	 * create the class for specific memory size
	 */
	public MemoryAllocation(int mem_size, String algorithm) {
	}

	/*
	 * Allocates memory with defined size. 
         * If the memory is evailable the function returns pointer (offset) of the begining of allocated memory. 
         * Otherwise it returns 0.
	 */
	abstract public int alloc(int size);

	/*
	 * release memory.from offset addr. The function makes detection if the memory was previously allocated. 
	 */
	abstract public void free(int addr);

	/*
	 * returns the global size of evailable memory, it is sum of all evailable parts of memory.
	 */
	abstract public int size();

	/*
	 * returns the size of the biggest evailable part of memory. It is the biggest size that can be allocated.
	 */
	abstract public int max_size();

	/*
	 * prints out the blocks by offset in an ascending order
	 */
	abstract public void print();
}