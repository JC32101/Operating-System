package task3;

import java.util.Iterator;

public class FreeBlockStruct extends MyLinkedList {
	
	public FreeBlockStruct(int mem_size) {
		
		front = new Block(1, mem_size);
		
	}

	public void insertList(int offset, int size) {
        Block finger = new Block(offset, size), newNode = front;
        Iterator it = this.iterator();
        if (front == null){
            front = finger;
        }
        else{
            while (it.hasNext() && finger.offset > newNode.next.offset){
                newNode = (Block) it.next();
            }
            if (newNode.next == null) {
                newNode.next = finger;
            } else if (finger.offset < newNode.next.offset) {
                finger.next = newNode.next;
                newNode.next = finger;
            }
        }
    }
	
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
			while (finger.next != null && finger.offset > offset) {
				finger = finger.next;
			}
			if (finger.next == null){
				int splitBlockSize = finger.allosize;
				finger.offset = finger.offset + size;
				finger.allosize = finger.allosize - size;
			} else if (finger.next != null) {
				int splitBlockSize = finger.allosize;
				finger.allosize =  finger.allosize-size;
				if (finger.allosize == 0) {
					finger.allosize = finger.next.allosize;
					finger.offset = finger.next.offset;
					finger.next = finger.next.next;
				}
				finger.offset += size;
				if (offset + size !=  finger.next.offset) {
					Block holder = finger.next;
					finger.next = new Block(offset+size, holder.offset-offset+size);
				}
		}
	}
	}
}
