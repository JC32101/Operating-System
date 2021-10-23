package task3;

import task3.MemoryAllocation;

import java.util.Iterator;

// I would like a simulation class to do most of the work.
class MyMemoryAllocation extends MemoryAllocation {
    String algorithm; //best fit, first fit or next fit
    FreeBlockStruct free_list;
    MyLinkedList used_list;

    public MyMemoryAllocation(int size, String algo) {
        super(size, algo);
        free_list = new FreeBlockStruct(size, algo);
        used_list = new MyLinkedList();
    }

    // Strongly recommend you start with printing out the pieces.
    public void print() {
        System.out.println("Free List: ");
        free_list.toString();
        System.out.println("Used List: ");
        used_list.toString();
    }

    public int alloc(int size) {
        if(used_list.front == null){
        	free_list.allocMem(1, size);
            used_list.insertList(1, size);
            return 0;
        }
        else{
            Block finger = free_list.front;
            Iterator it = free_list.iterator();
            int address = 0;

            while (it.hasNext()){
                if(finger.allosize >= size){
                	free_list.allocMem(finger.offset, size);
                    used_list.insertList(finger.offset, size);
                }
                finger = (Block) it.next();
            }
            return finger.offset;
        }
    }

    public void free(int address) {
        Block finger = used_list.front;
        Iterator it = used_list.iterator();

        while(it.hasNext()){
            if(address == finger.offset){
                free_list.insertList(address, used_list.removeByOffset(address)); //removeByOffset returns the size of the block removed
                return;
            }
            finger = (Block) it.next();
        }
    }

    public int size() {
        Block finger = free_list.front;
        Iterator it = free_list.iterator();
        int sum = 0;

        while(it.hasNext()){
            sum += finger.allosize;
            finger = (Block) it.next();
        }
        return sum;
    }

    public int max_size() {
        Block finger = free_list.front;
        Iterator it = free_list.iterator();
        int max = 0;

        while(it.hasNext()){
            if(finger.allosize > max){
                max = finger.allosize;
            }
            finger = (Block) it.next();
        }
        return max;
    }
}