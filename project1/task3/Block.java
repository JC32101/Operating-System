class Block {

    int offset;
    int mem_size;
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

    public String toString(){
        String string = "[" + this.offset + ", " + this.mem_size + "]->";
        if(this.next == null){
            string += "null";
        }
        return string;
    }; // highly recommended

    //public boolean is_adjacent(Block other){};


}
