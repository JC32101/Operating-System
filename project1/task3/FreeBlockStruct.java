package task3;

import java.util.Iterator;

import javax.security.auth.kerberos.DelegationPermission;

public class FreeBlockStruct extends MyLinkedList {
  public FreeBlockStruct(int size) {

    head = new Block(1, size);

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
        if ((newBlock.offset + newBlock.size) == insertionPoint.nextBlock.offset) {
          merge(newBlock, insertionPoint.nextBlock);
        }
        if ((insertionPoint.offset + insertionPoint.size) == newBlock.offset) {
          merge(insertionPoint, newBlock);
        } else {
          newBlock.nextBlock = insertionPoint.nextBlock;
          insertionPoint.nextBlock = newBlock;
        }
      }
    }
  }

  public int splitMayDelete(int offset, int size) {
    Block extractionPoint = head;
    Iterator it = this.iterator();

    if (head == null) {
      return 0; // or catch failed ??
    }
    while(it.hasNext() == true && size > extractionPoint.nextBlock.size){
      extractionPoint = (Block) it.next();
    }
    if(extractionPoint.nextBlock == null && extractionPoint.size > size){//problematic because ep can be too small?
      extractionPoint.offset += size;
      extractionPoint.size -= size;
    }else if(size == extractionPoint.nextBlock.size){
      extractionPoint.nextBlock = extractionPoint.nextBlock.nextBlock;
    }else if(size < extractionPoint.nextBlock.size){
      extractionPoint.nextBlock.offset += size;
      extractionPoint.nextBlock.size -= size;
    }else{
      return 0; //error
    }
    return offset;
  }

  public void merge(Block one, Block two) {
    one.size += two.size;
    one.nextBlock = two.nextBlock;
  }
}