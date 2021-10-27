import java.util.Iterator;

// I would like a simulation class to do most of the work.
class MyMemoryAllocation extends MemoryAllocation {
    String algorithm; //best fit, first fit or next fit
    FreeBlockStruct free_list;
    MyLinkedList used_list;

    public MyMemoryAllocation(int size, String algo) {
        super(size, algo);
        free_list = new FreeBlockStruct(size-1);
        used_list = new MyLinkedList();
        algorithm = algo;
    }

    // Strongly recommend you start with printing out the pieces.
    public void print() {
        System.out.println("Free List: " + free_list.toString());
        System.out.println("Used List: " + used_list.toString());
        used_list.toString();
    }

    public int alloc(int size) {
    	int offsetToAlloc = 0;
        if (algorithm == "FF") {
    	   offsetToAlloc = firstFit(size);
       } 
//       else if (algorithm == "NF") {
//    	   offsetToAlloc = nextFit(size);
//       } else {
//    	   offsetToAlloc = bestFit(size);
//       }
       free_list.splitMayDelete(offsetToAlloc, size);
       used_list.insertList(offsetToAlloc, size);
       return offsetToAlloc;
    }

    public void free(int address) {
        Block finger = used_list.front;
        Iterator it = used_list.iterator();
        if(address == finger.offset){
            free_list.insertList(address, used_list.removeByOffset(address)); //removeByOffset returns the size of the block removed
            return;
        } else if (finger.next == null) {
        	return;
        }
        do{
        	finger = (Block) it.next();
            if(address == finger.offset){
                free_list.insertList(address, used_list.removeByOffset(address)); //removeByOffset returns the size of the block removed
                return;
            } 
            
        } while (it.hasNext());
    }

    public int size() {
        Block finger = free_list.front;
        Iterator it = free_list.iterator();
        int sum = finger.allosize;
        
        while(it.hasNext()){
        	finger = (Block) it.next();
            sum += finger.allosize;
        }
        return sum;
    }

    public int max_size() {
        Block finger = free_list.front;
        Iterator it = free_list.iterator();
        int max = 0;

        if (finger.next == null) {
        	return finger.allosize;
        }
        while(it.hasNext()){
            if(finger.allosize > max){
                max = finger.allosize;
            }
            finger = (Block) it.next();
        }
        return max;
    }
    
    public int firstFit(int size) {
    	if(used_list.front == null){
        	return 1;
        }
        else{
            Block finger = free_list.front;
            Iterator it = free_list.iterator();
            int address = 0;

            do {
                if(finger.allosize >= size){
                	return finger.offset;
                }
                finger = (Block) it.next();
            } while (it.hasNext());
           return 0;
        }
    }
    
    public int bestFit(int size) {
        if (used_list.front == null) {
            return 1;
        }
        Block fitFinder = free_list.front;
        Iterator it = free_list.iterator();
        int offset = 0;
        int sizeFit = size;
        do {
            if ((fitFinder.allosize - size >= 0) && (fitFinder.allosize - size < sizeFit)) {
                sizeFit = fitFinder.allosize - size;
                offset = fitFinder.offset;
            }
        } while (it.hasNext());
        return offset;
    }
}