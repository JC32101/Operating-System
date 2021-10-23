public class MyMemoryAllocation {
    
    int alloc(int size)
    {
        //allocates momory with specified size
        //if mem available, allocate and return offset
        //if failed, return 0
    }
    void free(int addr)
    {
       // release allocated memory. 
       //The memory is referenced by its address (offset).
       //The function must detect if it is a valid address, that is, the function must detect if the memory was previously allocated
       //doesnt return 0, use  System.setErr(new PrintStream(errContent)); -> assert(errContent.toString().length() != 0);
    }
    int size()
    {
        //returns the total size of available memory, it is the sum of all available blocks of memory. 

    }
    int max_size()
    {
       // returns the size of the biggest available block of memory. It is the biggest size that can be allocated.

    }
    void print()
    {
        //prints out blocks in an ascending order of their offsets. 
        //It is your design decision to either maintain a sorted linked list or implement a sorting algorithm for your linked list.
    }
}
