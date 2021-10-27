import java.util.Iterator;

class FreeBlockStruct extends MyLinkedList {
	
	public FreeBlockStruct(int mem_size) {
		
		front = new Block(1, mem_size);
		
	}

	public void insertList(int offset, int size) {
		Block newBlock = new Block(offset, size);
        Block insertionPoint = front;
        Iterator it = this.iterator();

        if (front == null)
          front = newBlock;
        else {
          while (it.hasNext() == true && newBlock.offset > insertionPoint.next.offset) {
            insertionPoint = (Block) it.next();
          }
          if (insertionPoint.next == null) {
            insertionPoint.next = newBlock;
          } else if (newBlock.offset < insertionPoint.next.offset) {
            if ((newBlock.offset + newBlock.allosize) == insertionPoint.next.offset) {
              merge(newBlock, insertionPoint.next);
            }
            if ((insertionPoint.offset + insertionPoint.allosize) == newBlock.offset) {
              merge(insertionPoint, newBlock);
            } else {
              newBlock.next = insertionPoint.next;
              insertionPoint.next = newBlock;
            }
          }
        }
      }
    
    public void merge(Block one, Block two) {
        one.allosize += two.allosize;
        one.next = two.next;
      }
	
//        Block finger = new Block(offset, size), newNode = front;
//        Iterator it = this.iterator();
//        if (front == null){
//            front = finger;
//        }
//        else{
//            while (it.hasNext() && finger.offset > newNode.next.offset){
//                newNode = (Block) it.next();
//            }
//            if (newNode.next == null) {
//                newNode.next = finger;
//            } else if (finger.offset < newNode.next.offset) {
//                finger.next = newNode.next;
//                newNode.next = finger;
//            }
//        }
//        emptyBlockClean();
//    }
	
	
	
	
	//Merges blocks together
    public void merge(){
        Block finger = front;
        Iterator it = this.iterator();
        
        while (it.hasNext()){
            int num = finger.offset + finger.allosize;
            
            if((finger.next.offset-num) == 0){
                finger.allosize = finger.allosize + finger.next.allosize;
                finger.next = finger.next.next;
            }
            
            finger = (Block) it.next();
        }
    }
    
	public void splitMayDelete(int offset, int size) {
		if (offset == 1) {
			front.offset += size;
			front.allosize = front.allosize - size;
		} else {
			Block finger = front;
			while (finger.next != null && finger.offset != offset) {
				finger = finger.next;
			}
			if (finger.next == null){
				int splitBlockSize = finger.allosize;
				finger.offset = finger.offset + size;
				finger.allosize = finger.allosize - size;
			} else if (finger.next != null) {
				int splitBlockSize = finger.allosize;
				finger.allosize =  finger.allosize-size;
				finger.offset += size;
				}
		}
		if (front.allosize == 0 && front.next == null) {
			front = null;
		}
		//emptyBlockClean();
		
	}
	
	public void emptyBlockClean() {
		if (front == null || front.next == null) {
			return;
		}
		Block finger = front;
		do {
			if (finger.allosize == 0) {
				finger.allosize = finger.next.allosize;
				finger.offset = finger.next.offset;
				finger.next = finger.next.next;
			}
			finger = finger.next;
			if (finger == null) {
				return;
			}
		} while (finger.next != null); 
	}
	
}
