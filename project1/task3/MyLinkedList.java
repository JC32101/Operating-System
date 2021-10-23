package task3;

public class MyLinkedList{

    private Block head;

    public MyLinkedList() {

    }
    //for prep?
    public void insert(int offset, int size) {
        Block newBlock = new Block(offset, size);// fix this
        if (this.head == null) {
            head = newBlock;
        } else {
            Block currentBlock = head;
            for(int i = 0; i < offset - 1; i++){
                currentBlock = currentBlock.getNextBlock();
            }
            newBlock.setNextBlock(currentBlock.getNextBlock());
            currentBlock.setNextBlock(newBlock);
        }
    }


    void delete(int offset) {
        Block currentBlock = head;
        for(int i = 0; i < offset - 1; i++){
            currentBlock = currentBlock.getNextBlock()
        }
        currentBlock.setNextBlock(currentBlock.getNextBlock().getNextBlock());
    }


    public String toString() {
        return null;
    }

}
