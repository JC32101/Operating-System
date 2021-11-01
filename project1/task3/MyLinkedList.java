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
    	head = head.getNext();
    }

    //Inserts the allocated offset and size into LinkedList
    public void insertSort(Block blockToInsert) {
        if(blockToInsert.getOffset() <=0 || blockToInsert.getMem_size() <= 0){
            //Error cannot alloc 0 anything
        }
        if (head == null) { //Head case
            head = blockToInsert;
            return;
        }
        Block pointerBlock = head;
        Iterator it = this.iterator();
        if(blockToInsert.getOffset() < pointerBlock.getOffset()){ //Head Case
            blockToInsert.setNext(pointerBlock);
            head = blockToInsert;
            return;
        }
        while(it.hasNext() == true && blockToInsert.getOffset() > pointerBlock.getNext().getOffset()){
        	it.next();
            pointerBlock = pointerBlock.getNext();
        }
        if(pointerBlock.getNext() == null) {
            pointerBlock.setNext(blockToInsert);
        }else{
            blockToInsert.setNext(pointerBlock.getNext());
            pointerBlock.setNext(blockToInsert);
        }
        return;
    }

    public Block delete(Block blockToDelete){ //return int mem_size to free?
        Block pointerBlock = head;
        if(head == null || blockToDelete.getOffset() <= head.getOffset() || blockToDelete.getMem_size() <=0){
            //Error cannot delete 0 anything or delete from empty list
        }
        int mem_size = 0;
        if(blockToDelete.getOffset() == head.getOffset()){
            head = head.getNext();
        }else {
            Iterator it = this.iterator();
            while (it.hasNext() == true && blockToDelete.getOffset() > pointerBlock.getNext().getOffset()) {
                pointerBlock = pointerBlock.getNext();
            }
            if (pointerBlock.getNext() == null || (pointerBlock.getOffset() < blockToDelete.getOffset() && blockToDelete.getOffset() < pointerBlock.getNext().getOffset())) {
                // not found, cannot free
            }else if(blockToDelete.getOffset() == pointerBlock.getNext().getOffset()){
                pointerBlock.setNext(pointerBlock.getNext().getNext());
                blockToDelete.setNext(null);
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
            count += pointerBlock.getMem_size();
            pointerBlock = pointerBlock.getNext();
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
        for(Block pointerBlock = head; pointerBlock != null; pointerBlock = pointerBlock.getNext()) {
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
            return (currentBlock.getNext() != null);
        }

        public Block next() {
            currentBlock = currentBlock.getNext();
            return new Block(currentBlock.getOffset(), currentBlock.getMem_size(), currentBlock.getNext());
        }
        public Block getCurrentBlock(){
            return new Block(currentBlock.getOffset(), currentBlock.getMem_size(), currentBlock.getNext());
        }

    };
}
