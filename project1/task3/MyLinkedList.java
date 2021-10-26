package task3;

import java.util.Iterator;

// Custom Linked List class using Generics
class MyLinkedList implements Iterable {

  Block head;

  public MyLinkedList() {
  }

  public void insertMayCompact(int offset, int size) { //alloc into used
    Block newBlock = new Block(offset, size);
    Block insertionPoint = head;
    Iterator it = this.iterator();
    if (head == null)
      head = newBlock;
    else {
      while (it.hasNext() == true && newBlock.offset > insertionPoint.nextBlock.offset) {
        insertionPoint = (Block) it.next();
      }
      if((newBlock.offset == insertionPoint.offset) || (newBlock.offset == insertionPoint.nextBlock.offset)){
        return; //already alloc
      }
      if (insertionPoint.nextBlock == null) {
        insertionPoint.nextBlock = newBlock;//good
      } else if (newBlock.offset < insertionPoint.nextBlock.offset) {
        newBlock.nextBlock = insertionPoint.nextBlock;
        insertionPoint.nextBlock = newBlock;
      }
    }
  }

  public int splitMayDelete(int offset){
    Block extractionPoint = head;
    Iterator it = this.iterator();

    if(head == null){
      return 0; //freed nothing
    }else if(extractionPoint.offset == offset){
      head = head.nextBlock;
      return extractionPoint.size;
    }
    while(it.hasNext() == true && offset > extractionPoint.nextBlock.offset){
      extractionPoint = (Block) it.next();
    }
    if(offset == extractionPoint.nextBlock.offset){
      int saveSize = extractionPoint.nextBlock.size;
      extractionPoint.nextBlock = extractionPoint.nextBlock.nextBlock;
      return saveSize;
    }
    else{
      return 0; //problem, maybe address is silly or cant free bc already free. gotta flesh this out later
    }
  }

  public String toString() {
    return null;
  }

  public Iterator iterator() {
    return new Iterator() {
        	
      private Block currentBlock = head;

      public boolean hasNext() {
        return (currentBlock.nextBlock != null);
      }

			public Block next() {
				currentBlock = currentBlock.nextBlock;
				return currentBlock;
			}
			
			
			public void reset() {
				currentBlock = head;
			}
    };
  }
}
