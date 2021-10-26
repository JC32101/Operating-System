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

	public void splitMayDelete(int offset, int size) {
		if (offset == 1) {
			front.offset += size;
			front.allosize = front.allosize - size;
		} else {
			Block finger = front;
			while (finger.next != null && finger.offset > offset) {
				finger = finger.next;
			}
			if (finger.next == null) {
				int splitBlockSize = finger.allosize;
				finger.offset = finger.offset + size;
				finger.allosize = finger.allosize - size;
			} else if (finger.next != null) {
				int splitBlockSize = finger.allosize;
				finger.allosize = finger.allosize - size;
				if (finger.allosize == 0) {
					finger.allosize = finger.next.allosize;
					finger.offset = finger.next.offset;
					finger.next = finger.next.next;
				}
				finger.offset += size;
				if (offset + size != finger.next.offset) {
					Block holder = finger.next;
					finger.next = new Block(offset + size, holder.offset - offset + size);
				}
			}
		}
	}
}
