package Task2;

import java.util.Iterator;

// Probably a block class with at least these fields and methods.
class MyLinkedList implements Iterable { //generic types are not required, you can just do Task2.MyLinkedList for blocks but Iterable is mandatory.
    //in addition to other regular list member functions such as insert and delete: (split and consolidate blocks must be implemented at the level of linked list)

    public MyListNode front;

    private static class MyListNode{
        int offset;
        int allosize;
        MyListNode next;

        MyListNode(int offset, int size){
            this.offset = offset;
            this.allosize = size;
        }

        MyListNode(int offset, int size, MyListNode next){
            this.offset = offset;
            this.allosize = size;
            this.next = next;
        }
    }

    public MyLinkedList(){
        //Empty LinkedList
    }

    //Returns a new LinkedList with the given ListNode
    private MyLinkedList(MyListNode front) {
        this.front = front;
    }

    public void splitMayDelete() {  }

    //Inserts the allocated offset and size into LinkedList
    public void insertList(int offset, int size) {
        MyListNode finger = new MyListNode(offset, size), newNode = front;
        if (front == null){
            front = finger;
        }
        else{
            while (newNode.next != null){
                newNode = newNode.next;
            }
            newNode.next = finger;
        }
    }

    //Returns list of size
    public int size (){
        MyListNode finger = front;
        int count = 0;
        while (finger != null){
            count++;
            finger = finger.next;
        }
        return count;
    }

    //Removes the List that contains the Offset
    public void removeByOffset(int offset) {
        // TODO: your code here
        MyLinkedList L = new MyLinkedList(front);
        if (offset < 0 || offset > L.size()-1){
            throw new IndexOutOfBoundsException("Out of Bound");
        }
        else if(offset == 0){
            front = front.next;
        }
        else{
            MyListNode finger = front;
            for(int i = 0; i < offset; i++){
                if (i == offset-1){
                    finger.next = finger.next.next;
                }
                else{
                    finger = finger.next;
                }
            }
        }
    }

    private int detectCycles() {
        if (front == null) {
            return 0;
        }
        MyListNode tortoise = front;
        MyListNode hare = front;
        int position = 0;
        while (true) {
            position += 1;
            if (hare.next != null) {
                hare = hare.next.next;
            } else {
                return 0;
            }
            tortoise = tortoise.next;
            if (tortoise == null || hare == null) {
                return 0;
            } else if (hare == tortoise) {
                return position;
            }
        }
    }

    //Returns a string of the offsets and sizes in the list
    public String toString() {
        if (front == null) {
            return "()";
        }
        int cycleLocation = detectCycles();
        int position = 0;
        String result = "(" + front.offset + " " + front.allosize;
        for (MyListNode p = front.next; p != null; p = p.next) {
            result += ", " + p.offset + " " + p.allosize;
            position += 1;
            if (cycleLocation > 0 && position > cycleLocation) {
                result += "... cycle exists ...";
                break;
            }
        }
        result += ")";
        return result;
    }

    @Override
    public Iterator iterator() {
        return null;
    }
}