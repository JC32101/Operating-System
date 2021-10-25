
package task3;

import java.util.Comparator;

class Block {
    int offset;
    int allosize;
    Block next;


    Block(int offset, int allosize){
        this.offset = offset;
        this.allosize = allosize;
    }

    Block(int offset, int allosize, Block next){
        this.offset = offset;
        this.allosize = allosize;
        this.next = next;
    }

    public String toString() // highly recommended
    {
        return "Offset: " + offset + "\nSize: " + allosize;
    }

    public boolean is_adjacent(Block other) {
        if((offset == other.offset) && (allosize == other.allosize)){
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
