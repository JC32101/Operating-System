import java.util.Iterator;

// I would like a simulation class to do most of the work.
class MyMemoryAllocation extends MemoryAllocation {
    String algorithm; //best fit, first fit or next fit
    FreeBlockStruct free_list;
    MyLinkedList used_list;
    Block globalPointer;

    public MyMemoryAllocation(int size, String algo) {
        super(size, algo);
        free_list = new FreeBlockStruct(size - 1);
        used_list = new MyLinkedList();
        algorithm = algo;
        globalPointer = free_list.head;
    }

    // Strongly recommend you start with printing out the pieces.
    public void print() {
        System.out.println("Free List: " + free_list.toString());
        System.out.println("Used List: " + used_list.toString());
    }

    public int alloc(int mem_size) {
        int offsetToAlloc = 0;
        if (algorithm == "FF") {
            offsetToAlloc = firstFit(mem_size);
        } else if (algorithm == "NF") {
            offsetToAlloc = nextFit(mem_size);
        } else if (algorithm == "BF") {
            offsetToAlloc = bestFit(mem_size);
        }
        Block tempBlock = new Block(offsetToAlloc, mem_size);
        if(offsetToAlloc == 0){
            return 0; //can't allocate, no place given
        }
        free_list.splitMayDelete(tempBlock);
        used_list.insertSort(tempBlock);
        return offsetToAlloc;
    }

    public void free(int offset) {
        Iterator it = used_list.iterator();
        for (Block pointerBlock = used_list.head; pointerBlock != null; pointerBlock = (Block) it.next()) {
            if (pointerBlock.offset == offset) {
                free_list.insertMayCompact(used_list.delete(pointerBlock));
                return;
            }
        }
        System.err.println("u r trash >:(");

    }

    public int size() {
        Block pointerBlock = free_list.head;
        Iterator it = free_list.iterator();
        int sum = pointerBlock.mem_size;

        while (it.hasNext()) {
            pointerBlock = (Block) it.next();
            sum += pointerBlock.mem_size;
        }
        return sum;
    }

    public int max_size() {
        Block pointerBlock = free_list.head;
        Iterator it = free_list.iterator();
        int max = 0;

        if (pointerBlock.next == null) {
            return pointerBlock.mem_size;
        }
        while (it.hasNext()) {
            if (pointerBlock.mem_size > max) {
                max = pointerBlock.mem_size;
            }
            pointerBlock = (Block) it.next();
        }
        return max;
    }

    private int firstFit(int size) {
        if (used_list.isEmpty()) {
            return 1;
        } else {
            Iterator it = free_list.iterator();
            int address = 0;
            for (Block pointerBlock = free_list.head; pointerBlock != null; pointerBlock = (Block) it.next()) {
                if (pointerBlock.mem_size >= size) {
                    return pointerBlock.offset;
                }
            }
            return 0;
        }
    }

    private int bestFit(int size) {
        int offset = 0;
        int math_size = size;
        Iterator it = free_list.iterator();
        for (Block pointerBlock = free_list.head; pointerBlock != null; pointerBlock = (Block) it.next()) {
            if (pointerBlock.mem_size >= size) {
                if (pointerBlock.mem_size == size) {
                    return pointerBlock.offset;
                }
                math_size = pointerBlock.mem_size - size;
                offset = pointerBlock.offset;
            }
        }
        return offset;
    }

    private int nextFit(int size) {
        if (used_list.isEmpty()) {
            return 1;
        } else {
            if (globalPointer.next == null) {
                globalPointer = free_list.head;
            }
            Iterator it = free_list.iterator();
            while (globalPointer != free_list.head && (Block) it.next() != globalPointer) {
                //lets play catch up
            }
            int address = 0;
            while (true) {
                if (globalPointer.mem_size >= size) {
                    address = globalPointer.offset;
                    if (globalPointer.next == null) {
                        globalPointer = free_list.head;
                    } else {
                        globalPointer = (Block) it.next();
                    }
                    return address;
                }
                if (globalPointer.next == null) {
                    return 0; // Alloc request too big, what do we do?
                } else {
                    globalPointer = (Block) it.next();
                }
            }
        }
    }
}