package task3;

import java.util.Iterator;
  
// Custom Linked List class using Generics
class MyLinkedList implements Iterable {
    Block head, tail;
      
    public MyLinkedList() {

    }

    // add new Element at tail of the linked list in O(1)
    public void insert(int data)
    {
        Block newBlock = new Block(data, data);
        if (head == null)
            tail = head = newBlock;
        else {
            tail.setNextBlock(newBlock);
            tail = newBlock;
        }
    }
      
    // return Head
    public Block getHead()
    {
        return head;
    }
      
    // return Tail
    public Block getTail()
    {
        return tail;
    }
      
    // return Iterator instance
    public Iterator iterator()
    {
        return new ListIterator(this);
    }

    public String toString() {
        return null;
    }
}
  
class ListIterator implements Iterator {
    
    Block current;
      
    // initialize pointer to head of the list for iteration
    public ListIterator(MyLinkedList list)
    {
        current = list.getHead();
    }
      
    // returns false if next element does not exist
    public boolean hasNext()
    {
        return current != null;
    }
      
    // return current data and update pointer
    public Block next()
    {
        current = current.getNextBlock();
        return current;
    }
      
    // implement if needed
    public void delete(int offset) {
        Block currentBlock = current;
        for(int i = 0; i < offset - 1; i++){
            currentBlock = currentBlock.getNextBlock();
        }
        currentBlock.setNextBlock(currentBlock.getNextBlock().getNextBlock());
    }
}