package task3;

import java.util.Iterator;

public class FreeBlockStruct extends MyLinkedList {
	
	private AllocMethod algorithm;
	
	public FreeBlockStruct(String algorithm, int mem_size) {
		
		front = new Block(1, mem_size);
		if (algorithm == "FF") {
			this.algorithm = new FirstFit();
		} else if (algorithm == "NF") {
			this.algorithm = new NextFit();
		} else {
			this.algorithm = new BestFit(); //will use Best Fit be default
		}
		
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
}
