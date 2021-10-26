class MyMemoryAllocation extends MemoryAllocation {
    String algorithm; //best fit, first fit or next fit
    MyLinkedList free_list;
    MyLinkedList used_list;
    // Strongly recommend you start with printing out the pieces.
    public void print();
    public int alloc(int size) { ...}
    …
    public void free(int address) {...}
  
  }
  // Probably a block class with at least these fields and methods.
  class Block {
    int offset;
    int size;
    public String toString(); // highly recommended
    public boolean is_adjacent(Block other) ;
  }
  // Probably a block class with at least these fields and methods.
  class MyLinkedList implements Iterable { //generic types are not required, you can just do MyLinkedList for blocks but Iterable is mandatory.
   //in addition to other regular list member functions such as insert and delete: (split and consolidate blocks must be implemented at the level of linked list)
    public void splitMayDelete() { ... }
    public void insertMayCompact() { ... }
    public String toString(); {...} //highly recommended
  }
  // Sort-by in Java: (needs a class)
  // You may need it to sort your list or you can maintain a sorted list upon insertion
  class ByOffset implements Comparator<Block> {
    @Override int compare(Block lhs, Block rhs) {
      return Integer.compare(lhs.offset, rhs.offset);
    }
  }
  