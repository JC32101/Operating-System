import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import vm.PageTableEntry;

public class MyPageTable<DataType> {
	private int numBuckets = 64;
	private PageTableEntry[] buckets;
	private int count = 0;
	public MyPageTable() {
		buckets = new PageTableEntry[numBuckets];
		//for (int i = 0; i < numBuckets; i++) {
		//	buckets[i] = new ArrayList<DataType>();//?????
		//}
	}

	public void put(int key,  int transKey) {
		PageTableEntry n = new PageTableEntry(key, transKey);
		int bucket = Math.abs(hash(key)) % numBuckets; //bucket is the index after hashing the key.
		buckets[bucket] = insertIntoBucket(n, buckets[bucket]);
        count++;
		//TODO: your code here to call rehash as needed
		if((double)count/numBuckets >= 0.5){
			rehash();
		}
	}

	public boolean contains(int key) {
		//TODO: your code here
		int bucket = Math.abs(hash(key)) % numBuckets;
        PageTableEntry currentBucket = buckets[bucket];
        while (currentBucket != null) {
        	if (currentBucket.getKey() == key) {
        		return true;
        	}
        	currentBucket = buckets[bucket].getNext();
        	}
        return false;
	}

	//public void remove(DataType item) {
	//	//TODO: your code here
	//	if(contains(item) == false)
	//		throw new NoSuchElementException();
	//	else{
	//		int bucket = Math.abs(item.hashCode() % numBuckets);
	//		buckets[bucket].remove(item);
	//		count--;
	//	}
	//}

	private void rehash() {//a stands for oldtable traversal pointer
		//TODO: your code here
		PageTableEntry[] oldBuckets = buckets;
		numBuckets*=2;
		count = 0;
		buckets = new PageTableEntry[numBuckets];
		PageTableEntry oldNodes;

		//for (int i = 0; i < numBuckets; i++) {
		//	buckets[i] = new ArrayList<DataType>();
		//}
		for(int i = 0; i < oldBuckets.length; i++){
        	if (oldBuckets[i] == null) {
        		continue;
        	}
			oldNodes = oldBuckets[i];
            while(oldNodes != null){
				PageTableEntry frontNode = oldNodes;
				frontNode.getNext().equals(null);
            	int bucket = Math.abs(hash(frontNode.getKey()) % numBuckets);
            	buckets[bucket] = insertIntoBucket(frontNode, buckets[bucket]);
				oldNodes.getNext();
            	count++;
            }
        }
	}

	public PageTableEntry insertIntoBucket(PageTableEntry node, PageTableEntry finalLinkedList){
		if (finalLinkedList == null) {
        	finalLinkedList = node;
			return finalLinkedList;
        } else {
        	PageTableEntry currentEntry = finalLinkedList;
        	while (finalLinkedList.getNext() != null) {
        		currentEntry = finalLinkedList.getNext();
        	}
        	currentEntry.getNext().equals(node);
			return finalLinkedList;
        }
	}

	public int getNumBuckets () {
		return numBuckets;
	}

	private int hash(int a) {
    	//CREDIT TO mikera ON STACKOVERFLOW
    	//https://stackoverflow.com/questions/6082915/a-good-hash-function-to-use-in-interviews-for-integer-numbers-strings
    	a ^= (a << 13);
        a ^= (a >>> 17);        
        a ^= (a << 5);
        return a; 
    }

	@Override
	public String toString() {
		return "{" +
				"numBuckets=" + numBuckets +
				", buckets=" + Arrays.toString(buckets) +
				'}';
	}
}
