class Block {

    private int offset;
    private int mem_size;
    Block next;

    Block(int offset, int mem_size) {
        this.offset = offset;
        this.mem_size = mem_size;
    }

    Block(int offset, int mem_size, Block next) {
        this.offset = offset;
        this.mem_size = mem_size;
        this.next = next;
    }

    public String toString() {
        String string = "[" + this.offset + ", " + this.mem_size + "]->";
        if (this.next == null) {
            string += "null";
        }
        return string;
    }

    public void setOffset(int offset){
        this.offset = offset;
    }

    public void setMem_size(int size){
        mem_size = size;
    }

    public int getOffset(){
        return offset;
    }

    public int getMem_size(){
        return mem_size;
    }
}
