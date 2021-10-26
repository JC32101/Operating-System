package task3;

import java.util.Iterator;

// Custom Linked List class using Generics
class MyLinkedList implements Iterable {

  Block head;

  public MyLinkedList() {
  }

  public void insertMayCompact(int offset, int size) {
    Block newBlock = new Block(offset, size);
    Block insertionPoint = head;
    Iterator it = this.iterator();
    if (head == null)
      head = newBlock;
    else {
      while (it.hasNext() == true && newBlock.offset > insertionPoint.nextBlock.offset) {
        insertionPoint = (Block) it.next();
      }
      if (insertionPoint.nextBlock == null) {
        insertionPoint.nextBlock = newBlock;
      } else if (newBlock.offset < insertionPoint.nextBlock.offset) {
        newBlock.nextBlock = insertionPoint.nextBlock;
        insertionPoint.nextBlock = newBlock;
      }
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
