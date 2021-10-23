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
	//this isn't working, need to have different cases for if we need to split up a block before we allocate
//	public void allocMem(int offset, int size) {
//		Block current = front;
//		if (front.next == null) { //only one free block is present - could mean that entire memory is full
//			current.allosize -= size;
//			current.offset += size;
//			System.out.println(this.toString());
//			if (current.allosize == 0) { //signifies that the final block has been used and memory is full
//				front = null;
//			}
//		} else {
//			while (current.next != null && current.next.offset > offset) {
//				current = current.next;
//				}
//			if (current.offset == offset) {
//				current.allosize -= size;
//				current.offset += size;
//				if (current.allosize == 0) {
//					current.next = current.next.next;
//				}
//			} else if (current.next.offset > offset) {
//				current.allosize 
//			}
//			current.next.allosize -= size;
//			current.next.offset += size;
//			if (current.allosize == 0) {
//				current.next = current.next.next;
//			}
//			System.out.println(this.toString());
//		}
//	}
//}
