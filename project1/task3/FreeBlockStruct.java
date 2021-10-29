import java.util.Iterator;

class FreeBlockStruct extends MyLinkedList {
	
	public FreeBlockStruct(int size) {
		super(size);
	}
    
    public Block splitMayDelete(Block blockToDelete) { //return int mem_size to free?
        Block pointerBlock = getHead();
        if (getHead() == null || blockToDelete.offset < getHead().offset || blockToDelete.mem_size <= 0) {
            System.out.println("u r trash >:( @ FBS Line 14 trying to delete " + blockToDelete.toString());
            //Error cannot alloc 0 anything
        }
        int mem_size = 0;
        if (blockToDelete.offset == getHead().offset) {
        	getHead().mem_size -= blockToDelete.mem_size;
        	getHead().offset += blockToDelete.mem_size;
            if(getHead().mem_size == 0){
                removeHead();
            }
        } else {
            Iterator it = this.iterator();
            while (it.hasNext() == true && blockToDelete.offset > pointerBlock.next.offset) {
                pointerBlock = (Block) it.next();
            }
            if (pointerBlock.next == null || (pointerBlock.offset < blockToDelete.offset && blockToDelete.offset < pointerBlock.next.offset)) {
                System.out.println("u r trash >:( @ FBS Line 30 14 trying to delete " + blockToDelete.toString());
                //Error cannot alloc 0 anything
            } else if (blockToDelete.offset == pointerBlock.next.offset) {
                pointerBlock.next.mem_size -= blockToDelete.mem_size;
                pointerBlock.next.offset += blockToDelete.mem_size;
                if(pointerBlock.next.mem_size == 0){
                    pointerBlock.next = pointerBlock.next.next;
                }
            }
        }return blockToDelete;
    }

    public void insertMayCompact(Block blockToInsert) {

        if (blockToInsert.offset <= 0 || blockToInsert.mem_size <= 0) {
            System.out.println("u r trash >:( trying to alloc " + blockToInsert.toString());
        }
        if (getHead() == null) { //Head case
            setHead(blockToInsert);
            return;
        }
        Block pointerBlock = getHead();
        Iterator it = this.iterator();
        if (blockToInsert.offset < pointerBlock.offset) { //Head Case
            blockToInsert.next = pointerBlock;
            setHead(blockToInsert);
            if(blockToInsert.offset + blockToInsert.mem_size == blockToInsert.next.offset){
                merge(blockToInsert, blockToInsert.next);
            }
            return;
        }
        while (it.hasNext() == true && blockToInsert.offset > pointerBlock.next.offset) {
            pointerBlock = (Block) it.next();
        }
        if (pointerBlock.next == null) {
            pointerBlock.next = blockToInsert;
            if(pointerBlock.offset + pointerBlock.mem_size == blockToInsert.offset){
                merge(pointerBlock, blockToInsert);
            }
        } else {
            blockToInsert.next = pointerBlock.next;
            pointerBlock.next = blockToInsert;
            if(blockToInsert.offset + blockToInsert.mem_size == blockToInsert.next.offset){
                merge(blockToInsert, blockToInsert.next);
            }
            if(pointerBlock.offset + pointerBlock.mem_size == blockToInsert.offset){
                merge(pointerBlock, blockToInsert);
            }
        }
        return;
    }

    private void merge(Block one, Block two) {
        one.mem_size += two.mem_size;
        one.next = two.next;
    }
}
