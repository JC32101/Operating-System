package task3;

import java.util.Iterator;

public class FreeBlockStruct extends MyLinkedList {
	
	public FreeBlockStruct(int mem_size) {
		
		front = new Block(1, mem_size);
		
	}

	public void insertList(int offset, int size) { //merges via nested if loops
		Block finger = new Block(offset, size), newNode = front;
        Iterator it = this.iterator();
        if (front == null){
            front = finger;
        }
        else{
            while (it.hasNext() == true && finger.offset > newNode.next.offset){
            	if (newNode.offset + newNode.allosize == finger.offset) {
            		newNode.allosize += finger.allosize;
            		if (newNode.offset + newNode.allosize == newNode.next.offset) {
            			newNode.allosize += newNode.next.allosize;
            			newNode.next = newNode.next.next;
            		}
            		break;
            	}
                newNode = (Block) it.next();
            }
            if (newNode.next == null) {
            	newNode.next = finger;
            } else if (finger.offset < newNode.next.offset) {
            	finger.next = newNode.next;
            	newNode.next = finger;
            	if (finger.next.offset == finger.offset + finger.allosize) {
            		finger.allosize =+ finger.next.allosize;
            		finger.next = finger.next.next;
            	}
            		
            }
        }
	}
	public void splitMayDelete(int offset, int size) {
		if (offset == 1) {
			front.offset += size;
			front.allosize =- size;
		} else {
			Block finger = front;
			while (finger.next != null && finger.offset > offset) {
				finger = finger.next;
			}
			if (finger.next != null) {
				int splitBlockSize = finger.allosize;
				finger.allosize =  offset - finger.offset;
				if (offset + size !=  finger.next.offset) {
					Block holder = finger.next;
					finger.next = new Block(offset+size, holder.offset-offset+size);
				}
			} else if (finger.next == null){
			int splitBlockSize = finger.allosize;
			finger.allosize =  offset - finger.offset;
			if (offset+size != splitBlockSize - finger.allosize) {
				finger.next = new Block(offset+size, splitBlockSize-finger.allosize-size);
			}
		}
	}
	}
}
//}
