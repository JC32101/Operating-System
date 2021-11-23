package vm;
import java.util.*;

public class MyPageTable {

	private PageTableEntry[] vpnToPfn;
	private int numBuckets = 10;
	private PageTableEntry[] buckets;
	private int count = 0;

	public MyPageTable() {
		buckets = new PageTableEntry[numBuckets];
		vpnToPfn = new PageTableEntry[1024];
		for (int i = 0; i < 1024; i++) {
			vpnToPfn[i] = new PageTableEntry(-1, -1);
		}
	}


	public int transToPfn(int vpn) throws PageFaultException{ //TODO: implement nested hashtable that uses VPN as key
		int pfn = vpnToPfn[vpn].getTransKey();
		if (pfn == -1 || get(pfn) == null) { //dirty page will be left in table
			throw new PageFaultException();
		}
		return vpnToPfn[vpn].getTransKey();
	}

	public void put(int key,  int transKey) {
		PageTableEntry n = new PageTableEntry(key, transKey);
		int bucket = Math.abs(hash(key)) % numBuckets;
		if (buckets[bucket] == null) {
			buckets[bucket] = n;
		} else {
			PageTableEntry currentEntry = buckets[bucket];
			while (currentEntry.getNext() != null) {
				currentEntry = currentEntry.getNext();
			}
			currentEntry.setNext(n);
		}
		count++;

		PageTableEntry vpnPTE = new PageTableEntry(transKey, key);
		vpnToPfn[transKey] = vpnPTE;

		if((double)count/numBuckets >= 0.5){
			rehash();
		}
	}

	public boolean contains(int key){
		int bucket = Math.abs(hash(key)) % numBuckets;
		PageTableEntry currentBucket = buckets[bucket];
		while (currentBucket != null) {
			if (currentBucket.getKey() == key) {
				return true;
			}
			currentBucket = currentBucket.getNext();
		}
		return false;
	}

	public PageTableEntry get(int key){
		if(contains(key)){
			int bucket = Math.abs(hash(key)) % numBuckets;
			PageTableEntry currentBucket = buckets[bucket];
			while (currentBucket != null) {
				if (currentBucket.getKey() == key) {
					return currentBucket;
				}
				currentBucket = currentBucket.getNext();
			}
		}
		return null;
	}

	public void remove(int key) {
		if(!contains(key))
			throw new NoSuchElementException();
		else{
			int bucket = Math.abs(hash(key)) % numBuckets;
			if (buckets[bucket].getKey() == key) {
				buckets[bucket] = buckets[bucket].getNext();
			} else {
				PageTableEntry prevBucket = buckets[bucket];
				if (prevBucket.getNext().getKey() == key) {
					prevBucket.setNext(prevBucket.getNext().getNext());
				}
			}
			count--;
		}
	}

	public void dirtifyEntry(int key) { //for when a vpn to pfn value is no longer valid
		int bucket = Math.abs(hash(key)) % numBuckets;
		if (buckets[bucket].getKey() == key) {
			buckets[bucket].setDirty(true);
		} else {
			PageTableEntry currentBucket = buckets[bucket];
			while (currentBucket.getNext() != null) {
				currentBucket = currentBucket.getNext();
				if (currentBucket.getKey() == key) {
					currentBucket.setDirty(true);
					return;
				}
			}
		}
	}

	public void cleanEntry(int key) {
		int bucket = Math.abs(hash(key)) % numBuckets;
		if (buckets[bucket].getKey() == key) {
			buckets[bucket].setDirty(false);
		} else {
			PageTableEntry currentBucket = buckets[bucket];
			while (currentBucket.getNext() != null) {
				currentBucket = currentBucket.getNext();
				if (currentBucket.getKey() == key) {
					currentBucket.setDirty(false);
					return;
				}
			}
		}
	}

	public boolean isDirty(int key) { //for when a vpn to pfn value is no longer valid
		int bucket = Math.abs(hash(key)) % numBuckets;
		if (buckets[bucket] == null) {
			return false;
		}
		if (buckets[bucket].getKey() == key) {
			return buckets[bucket].isDirty();
		} else {
			PageTableEntry currentBucket = buckets[bucket];
			while (currentBucket.getNext() != null) {
				currentBucket = currentBucket.getNext();
				if (currentBucket.getKey() == key) {
					return buckets[bucket].isDirty();
				}
			}
		}
		return false;
	}

	public int getNumBuckets () {
		return numBuckets;
	}

	@Override
	public String toString() {
		return "{" +
				"numBuckets=" + numBuckets +
				", buckets=" + Arrays.toString(buckets) +
				'}';
	}

	private int hash(int a) {
		//CREDIT TO mikera ON STACKOVERFLOW
		//https://stackoverflow.com/questions/6082915/a-good-hash-function-to-use-in-interviews-for-integer-numbers-strings
		a ^= (a << 13);
		a ^= (a >>> 17);
		a ^= (a << 5);
		return a;
	}

	public void removeVpnToPfn(int vpn) {
		vpnToPfn[vpn].setTransKey(-1);
		vpnToPfn[vpn].setKey(-1);
		vpnToPfn[vpn].setDirty(false);
	}

	public int[] getDirtyPages() {
		int[] dirtyFrames = new int[1024];
		for (int i = 0; i < 1024; i++) {
			dirtyFrames[i] = -1;
		}
		for (int i = 0; i < 1024; i++) {
			if (vpnToPfn[i].getKey() != -1) {
				if (isDirty(vpnToPfn[i].getTransKey())) {
					dirtyFrames[i] = vpnToPfn[i].getTransKey(); //i is the vpn, dirtFrames[i] is the corresponding pfn
				}
			}
		}
		return dirtyFrames;
	}

	public int valueLookup(int pfn){
		int bucket = Math.abs(hash(pfn)) % numBuckets;
		int vpn = -1;
		PageTableEntry currentBucket = buckets[bucket];
		while (currentBucket != null) {
			if (currentBucket.getKey() == pfn) {
				vpn = currentBucket.getTransKey();
				return vpn;
			}
			currentBucket = currentBucket.getNext();
		}
		return vpn;
	}

	private void rehash() {//a stands for oldtable traversal pointer
		//TODO: your code here
		PageTableEntry[] oldBuckets = buckets;
		numBuckets*=2;
		count = 0;
		buckets = new PageTableEntry[numBuckets];
		PageTableEntry oldNodes;

		for (PageTableEntry oldBucket : oldBuckets) {
			if (oldBucket == null) {
				continue;
			}
			oldNodes = oldBucket;
			while (oldNodes != null) {
				PageTableEntry frontNode = oldNodes;
				int bucket = Math.abs(hash(frontNode.getKey()) % numBuckets);
				buckets[bucket] = insertIntoBucket(frontNode, buckets[bucket]);
				oldNodes = oldNodes.getNext();
				PageTableEntry addedEntry = buckets[bucket];
				while (addedEntry != frontNode) {
					addedEntry = addedEntry.getNext();
				}
				addedEntry.clearNext(); //we need to clear the "next" of the recently added node in order to avoid duplicate PTEs
				//^^^without this insertIntoBucket will create PTEs that point to themselves
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
			while (currentEntry.getNext() != null) {
				currentEntry = currentEntry.getNext();
			}
			currentEntry.setNext(node);
			return finalLinkedList;
		}
	}
}
