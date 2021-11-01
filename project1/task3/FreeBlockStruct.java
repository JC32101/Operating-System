import java.util.Iterator;

class FreeBlockStruct extends MyLinkedList {
	
	public FreeBlockStruct(int size) {
		super(size);
	}
    
    public Block splitMayDelete(Block blockToDelete) { //return int mem_size to free?
        Block pointerBlock = getHead();
        if (getHead() == null || blockToDelete.getOffset() < getHead().getOffset() || blockToDelete.getMem_size() <= 0) {
            System.out.println("u r trash >:( @ FBS Line 12 trying to delete " + blockToDelete.toString());
            //Error cannot alloc 0 anything
        }
        int mem_size = 0;
        if (blockToDelete.getOffset() == getHead().getOffset()) {
        	getHead().setMem_size(getHead().getMem_size() - blockToDelete.getMem_size());
        	getHead().setOffset(getHead().getOffset() + blockToDelete.getMem_size());
            if(getHead().getMem_size() == 0){
                removeHead();
            }
        } else {
            Iterator it = this.iterator();
            while (it.hasNext() == true && blockToDelete.getOffset() > pointerBlock.next.getOffset()) {
                pointerBlock = pointerBlock.next;
            }
            if (pointerBlock.next == null || (pointerBlock.getOffset() < blockToDelete.getOffset() && blockToDelete.getOffset() < pointerBlock.next.getOffset())) {
                System.out.println("u r trash >:( @ FBS Line 30 14 trying to delete " + blockToDelete.toString());
                //Error cannot alloc 0 anything
            } else if (blockToDelete.getOffset() == pointerBlock.next.getOffset()) {
                pointerBlock.next.setMem_size(pointerBlock.next.getMem_size() - blockToDelete.getMem_size());
                pointerBlock.next.setOffset(pointerBlock.next.getOffset() + blockToDelete.getMem_size());
                if(pointerBlock.next.getMem_size() == 0){
                    pointerBlock.next = pointerBlock.next.next;
                }
            }
        }return blockToDelete;
    }

    public void insertMayCompact(Block blockToInsert) {

        if (blockToInsert.getOffset() <= 0 || blockToInsert.getMem_size() <= 0) {
            System.out.println("u r trash >:( trying to alloc " + blockToInsert.toString());
        }
        if (getHead() == null) { //Head case
            setHead(blockToInsert);
            return;
        }
        Block pointerBlock = getHead();
        Iterator it = this.iterator();
        if (blockToInsert.getOffset() < pointerBlock.getOffset()) { //Head Case
            blockToInsert.next = pointerBlock;
            setHead(blockToInsert);
            if(blockToInsert.getOffset() + blockToInsert.getMem_size() == blockToInsert.next.getOffset()){
                merge(blockToInsert, blockToInsert.next);
            }
            return;
        }
        while (it.hasNext() == true && blockToInsert.getOffset() > pointerBlock.next.getOffset()) {
        	it.next();
            pointerBlock = pointerBlock.next;
        }
        if (pointerBlock.next == null) {
            pointerBlock.next = blockToInsert;
            if(pointerBlock.getOffset() + pointerBlock.getMem_size() == blockToInsert.getOffset()){
                merge(pointerBlock, blockToInsert);
            }
        } else {
            blockToInsert.next = pointerBlock.next;
            pointerBlock.next = blockToInsert;
            if(blockToInsert.getOffset() + blockToInsert.getMem_size() == blockToInsert.next.getOffset()){
                merge(blockToInsert, blockToInsert.next);
            }
            if(pointerBlock.getOffset() + pointerBlock.getMem_size() == blockToInsert.getOffset()){
                merge(pointerBlock, blockToInsert);
            }
        }
        return;
    }

    private void merge(Block one, Block two) {
        one.setMem_size(one.getMem_size()+ two.getMem_size());
        one.next = two.next;
    }
}
