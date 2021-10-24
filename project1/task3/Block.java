package task3;
//fix visibility

class Block {
    int offset;
    int size;
    Block nextBlock;


    public Block(int offset, int size, Block next){
    this.offset = offset;
    this.size = size;
    this.nextBlock = next;
    }
    public Block(int offset, int size){
        this.offset = offset;
        this.size = size;
        }

    public int getOffset()
    {
        return offset;
    }

    public int getSize()
    {
        return size;
    }

    public Block getNextBlock() {
        return nextBlock;
    }

    public String toString()
    {
        return "[" + offset + "," + size + "]->";
    }

    public void setNextBlock(Block nextBlock) {
        this.nextBlock = nextBlock;
    }

    public boolean is_adjacent(Block isAdj) {
        return false;
    }

    class Block {
        int data;
        Block next;
        public Block(int data, Block next)
        {
            this.data = data;
            this.next = next;
        }
          
        // Setter getter methods for Data and Next Pointer
        public void setData(int data)
        {
            this.data = data;
        }
          
        public void setNext(Block next)
        {
            this.next = next;
        }
          
        public int getData()
        {
            return data;
        }
          
        public Block getNext()
        {
            return next;
        }
    }
}
