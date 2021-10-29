import java.util.Iterator;

// Probably a block class with at least these fields and methods.
class MyLinkedList implements Iterable { //generic types are not required, you can just do Task2.MyLinkedList for blocks but Iterable is mandatory.
    //in addition to other regular list member functions such as insert and delete: (split and consolidate blocks must be implemented at the level of linked list)

    private Block head;

    //Empty LinkedList
    public MyLinkedList() {
        this.head = null;
    }
    
    public MyLinkedList(int mem_size) {
        head = new Block(1, mem_size);
    }
    
    public boolean isEmpty() {
    	if (head == null) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    public Block getHead() {
    	return head;
    }
    
    public void setHead(Block b) {
    	head = b;
    }
    
    public void removeHead() {
    	head = head.next;
    }

    //Inserts the allocated offset and size into LinkedList
    public void insertSort(Block blockToInsert) {
        if(blockToInsert.offset <=0 || blockToInsert.mem_size <= 0){
            //Error cannot alloc 0 anything
        }
        if (head == null) { //Head case
            head = blockToInsert;
            return;
        }
        Block pointerBlock = head;
        Iterator it = this.iterator();
        if(blockToInsert.offset < pointerBlock.offset){ //Head Case
            blockToInsert.next = pointerBlock;
            head = blockToInsert;
            return;
        }
        while(it.hasNext() == true && blockToInsert.offset > pointerBlock.next.offset){
            pointerBlock = (Block) it.next();
        }
        if(pointerBlock.next == null) {
            pointerBlock.next = blockToInsert;
        }else{
            blockToInsert.next = pointerBlock.next;
            pointerBlock.next = blockToInsert;
        }
        return;
    }

    public Block delete(Block blockToDelete){ //return int mem_size to free?
        Block pointerBlock = head;
        if(head == null || blockToDelete.offset <= head.offset || blockToDelete.mem_size <=0){
            //Error cannot delete 0 anything or delete from empty list
        }
        int mem_size = 0;
        if(blockToDelete.offset == head.offset){
            head = head.next;
        }else {
            Iterator it = this.iterator();
            while (it.hasNext() == true && blockToDelete.offset > pointerBlock.next.offset) {
                pointerBlock = (Block) it.next();
            }
            if (pointerBlock.next == null || (pointerBlock.offset < blockToDelete.offset && blockToDelete.offset < pointerBlock.next.offset)) {
                // not found, cannot free
            }else if(blockToDelete.offset == pointerBlock.next.offset){
                pointerBlock.next = pointerBlock.next.next;
                blockToDelete.next = null;
            }
        }
        return blockToDelete;
    }

    public void splitMayDelete() {}

    public void insertMayCompact() {}

    //Returns total amount of collective allosize for each node
    public int size() {
        Block pointerBlock = head;
        int count = 0;
        while (pointerBlock != null) {
            count += pointerBlock.mem_size;
            pointerBlock = pointerBlock.next;
        }
        return count;
    }

    //Returns a string of the offsets and sizes in the list
    public String toString() {
        if (head == null) {
            return "head->null";
        }
        String string = "";
        Iterator it = this.iterator();
        for(Block pointerBlock = head; pointerBlock != null; pointerBlock = (Block) it.next()) {
            string += pointerBlock.toString();
        }
        return string;
    }

    public BlockIterator iterator() {
    	return new BlockIterator();
    }
    
    private class BlockIterator implements Iterator  {
    	private Block currentBlock = head;

        public boolean hasNext() {
            return (currentBlock.next != null);
        }

        public Block next() {
            currentBlock = currentBlock.next;
            return currentBlock;
        }
        public Block getCurrentBlock(){
            return currentBlock;
        }

    };
}
