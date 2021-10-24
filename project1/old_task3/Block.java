package task3;

import java.util.Comparator;

class Block {
    int offset;
    int size;
    Block next;


    Block(int offset, int size){
        this.offset = offset;
        this.size = size;
    }

        Block(int offset, int size, Block next){
        this.offset = offset;
        this.size = size;
        this.next = next;
    }

    public String toString() // highly recommended
    {
        return "Offset: " + offset + "\nSize: " + size;
    }

    public boolean is_adjacent(Block other) {
        if((offset == other.offset) && (size == other.size)){
            return true;
        }
        else {
            return false;
        }
    }
}

// Sort-by in Java: (needs a class)
// You may need it to sort your list or you can maintain a sorted list upon insertion
class ByOffset implements Comparator<Block> {
    @Override
    public int compare(Block lhs, Block rhs) {
        return Integer.compare(lhs.offset, rhs.offset);
    }
}
