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
        free_list = new FreeBlockStruct(size-1);
        used_list = new MyLinkedList();
        algorithm = algo;
    }

    // Strongly recommend you start with printing out the pieces.
    public void print() {
        System.out.println("Free List: " + free_list.toString());// make a tostring method
        System.out.println("Used List: " + used_list.toString());// make tostring method
    }

    public int alloc(int size) {
        int offsetToAlloc = 0;
        if (algorithm == "FF") {
            offsetToAlloc = firstFit(size);
        }else if(algorithm == "BF"){

        }else if(algorithm == "NF"){

        }
        free_list.splitMayDelete(offsetToAlloc, size);
        used_list.insertMayCompact(offsetToAlloc, size);
        return offsetToAlloc;
    }

    public void free(int offset) {
        Block freePoint = used_list.head;
        Iterator it = used_list.iterator();
        do {
            if (offset == freePoint.offset) {
                free_list.insertMayCompact(offset, used_list.splitMayDelete(offset)); // removeByOffset returns the size of // the block removed
                return;
            }
            freePoint = (Block) it.next();
        } while (it.hasNext());
    }

    public int size() {
        Block finger = free_list.head;
        Iterator it = free_list.iterator();
        int sum = finger.size;

        while (it.hasNext()) {
            finger = (Block) it.next();
            sum += finger.size;
        }
        return sum;
    }

    public int max_size() {
        Block finger = free_list.head;
        Iterator it = free_list.iterator();
        int max = 0;

        if (finger.nextBlock == null) {
            return finger.size;
        }
        while (it.hasNext()) {
            if (finger.size > max) {
                max = finger.size;
            }
            finger = (Block) it.next();
        }
        return max;
    }

    public int firstFit(int size) {
        if (used_list.head == null) {
            return 1;
        } else {
            Block allocPoint = free_list.head;
            Iterator it = free_list.iterator();
            int address = 0;

            do {
                if (allocPoint.size >= size) {
                    return allocPoint.offset;
                }
                allocPoint = (Block) it.next();
            } while (it.hasNext());
            return 0;
        }
    }
}