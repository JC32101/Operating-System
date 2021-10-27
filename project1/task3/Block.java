package task3;

public class Block {

        int offset;
        int allosize;
        Block next;

        Block(int offset, int size){
            this.offset = offset;
            this.allosize = size;
        }

        Block(int offset, int size, Block next){
            this.offset = offset;
            this.allosize = size;
            this.next = next;
        }
          
}
