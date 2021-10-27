import java.util.Iterator;

// Probably a block class with at least these fields and methods.
class MyLinkedList implements Iterable { //generic types are not required, you can just do Task2.MyLinkedList for blocks but Iterable is mandatory.
    //in addition to other regular list member functions such as insert and delete: (split and consolidate blocks must be implemented at the level of linked list)

    public Block front;

    public MyLinkedList(){
        //Empty LinkedList
    }

    //Returns a new LinkedList with the given ListNode
    private MyLinkedList(Block front) {
        this.front = front;
    }

    //Inserts the allocated offset and size into LinkedList
    public void insertList(int offset, int size) {
        Block finger = new Block(offset, size), newNode = front;
        Iterator it = this.iterator();
        if (front == null){
            front = finger;
        }
        else{
            while (it.hasNext() == true && finger.offset > newNode.next.offset){
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

  //Returns total amount of collective allosize for each node
    public int size (){
        Block finger = front;
        int count = 0;
        while (finger != null){
            count += finger.allosize;
            finger = finger.next;
        }
        return count;
    }

    public int removeByOffset(int offset) {
        MyLinkedList L = new MyLinkedList(front);
        Iterator it = L.iterator();
        int removedSize = 0;
        if (offset < 0) {
            throw new IndexOutOfBoundsException("Out of Bound");
        }
        else if(offset == 1){
        	removedSize = front.allosize;
            front = front.next;
        }
        else{
            Block finger = front;
            boolean success = false;
            while (it.hasNext()) {
            	if (finger.next.offset == offset) {
            		removedSize = finger.next.allosize;
            		finger.next = finger.next.next;
            		success = true;
            		break;
            	}
            	finger = (Block) it.next();
            }
            if (success == false) {
            	System.out.println("nah nah nah");
            	System.err.println("im so groovy");
            }
        }
        return removedSize;
    }

    //Returns a string of the offsets and sizes in the list
    public String toString() {
        if (front == null) {
            return "()";
        }
        int position = 0;
        String result = "(" + front.offset + " " + front.allosize;
        for (Block p = front.next; p != null; p = p.next) {
            result += ", " + p.offset + " " + p.allosize;
            position += 1;
        }
        result += ")";
        return result;
    }

    @Override
    public Iterator iterator() {
        return new Iterator() {
        	
        	private Block currentBlock = front;

        	public boolean hasNext() {
        		return (currentBlock.next != null);
        	}

			public Block next() {
				currentBlock = currentBlock.next;
				return currentBlock;
			}
			
			
			public void reset() {
				currentBlock = front;
			}
        	
        };
    }
}
