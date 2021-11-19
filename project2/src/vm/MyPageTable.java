package vm;
import java.util.*;

public class MyPageTable {

    private int[] vpnToPfn;
    private int numBuckets = 10;
    private PageTableEntry[] buckets;
    private int count = 0;
    
    public MyPageTable() {
        buckets = new PageTableEntry[numBuckets];
        vpnToPfn = new int[1024];
        for (int i = 0; i < 1024; i++) {
        	vpnToPfn[i] = -1;
        }
//        for(int i = 0; i < numBuckets; i++)
//            buckets[i] = new PageTableEntry();
//I'm thinking it's better to create PTE only when needed instead of initializing a bunch of empty ones
    }
    
    
    public int transToPfn(int vpn) throws PageFaultException{ //TODO: implement nested hashtable that uses VPN as key
    	int pfn = vpnToPfn[vpn];
    	if (pfn == -1 || get(pfn) == null || get(pfn).isDirty() == true) { //dirty page will be left in table
    		throw new PageFaultException();
    	}
    	return vpnToPfn[vpn];
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
        vpnToPfn[transKey] = key;
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
        	currentBucket = buckets[bucket].getNext();
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
            }
        }
		return null;
    }

//    private void rehash() {
//        PageTableEntry[] oldBuckets = buckets;
//        numBuckets*=2;
//        count = 0;
//        buckets = new PageTableEntry[numBuckets];
//
//        for(int i = 0; i < oldBuckets.length; i++){
//        	if (oldBuckets[i] == null) {
//        		continue;
//        	}
//            PageTableEntry a = oldBuckets[i];
//            int bucket = Math.abs(hash(a.getKey()) % numBuckets);
//            buckets[bucket] = oldBuckets[i];
//            count++;
//            PageTableEntry currentBucket = buckets[bucket];
//            while(currentBucket.getNext() != null){
//            	currentBucket = currentBucket.getNext();
//                int bucket2 = Math.abs(hash(currentBucket.getKey()) % numBuckets);
//                buckets[bucket2] = currentBucket;
//                count++;
//            }
//        }
//    }

    public void remove(int key) {
        if(contains(key) == false)
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
    
    public void addVpnToPfn(int vpn, int pfn) {
    	vpnToPfn[vpn] = pfn;
    }
    
    public int[] getDirtyPages() { 
    	int[] dirtyFrames = new int[1024];
    	for (int i = 0; i < 1024; i++) {
        	dirtyFrames[i] = -1;
        }
    	for (int i = 0; i < 1024; i++) {
    		if (vpnToPfn[i] != -1) {
    			if (isDirty(vpnToPfn[i])) {
    				dirtyFrames[i] = vpnToPfn[i]; //i is the vpn, dirtFrames[i] is the corresponding pfn
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
            	int bucket = Math.abs(hash(frontNode.getKey()) % numBuckets);
            	buckets[bucket] = insertIntoBucket(frontNode, buckets[bucket]);
				oldNodes = oldNodes.getNext();
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
