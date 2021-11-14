package vm;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyPageTable<PageTableEntry> {
    private static class PageTableEntry {
        int vpn;
        int pfn;
        boolean dirty;
        PageTableEntry next; //in case of a hash collision, PTEs can be chained together
        
        PageTableEntry(int pfn, int vpn) {
            this.pfn = pfn;
            this.vpn = vpn;
        }
    }
    private int numBuckets = 1024;
    private PageTableEntry[] buckets;
    private int count = 0;
    public MyPageTable() {
        buckets = new PageTableEntry[numBuckets];
//        for(int i = 0; i < numBuckets; i++)
//            buckets[i] = new PageTableEntry();
//I'm thinking it's better to create PTE only when needed instead of initializing a bunch of empty ones
    }

    public void put(int pfn,  int vpn) {
        PageTableEntry n = new PageTableEntry(pfn, vpn);
        int bucket = Math.abs(hash(pfn)) % numBuckets;
        if (buckets[bucket] == null) {
        	buckets[bucket] = n;
        } else {
        	PageTableEntry currentEntry = buckets[bucket];
        	while (buckets[bucket].next != null) {
        		currentEntry = buckets[bucket].next;
        	}
        	currentEntry.next = n;
        }
        count++;

//        if((double)count/numBuckets >= 0.5){
//            rehash();
//        }
// Right now numBuckets = max number of pages so we will never need to reshash. We could start with less buckets and expand as needed
    }

    public boolean contains(int pfn) {
    	int bucket = Math.abs(hash(pfn)) % numBuckets;
        PageTableEntry currentBucket = buckets[bucket];
        while (currentBucket != null) {
        	if (currentBucket.pfn == pfn) {
        		return true;
        	}
        	currentBucket = buckets[bucket].next;
        	}
        return false;
        }

    public PageTableEntry get(int pfn) {
        if(contains(pfn)){
        	int bucket = Math.abs(hash(pfn)) % numBuckets;
            PageTableEntry currentBucket = buckets[bucket];
            while (currentBucket != null) {
            	if (currentBucket.pfn == pfn) {
            		return currentBucket;
            	}
            }
        }
        return null;
    }

//    private void rehash() {
//        //TODO: your code here
//        ArrayList<Pair> [] oldBucket = buckets;
//        numBuckets*=2;
//        count = 0;
//        buckets = new ArrayList[numBuckets];
//
//        for (int i = 0; i < numBuckets; i++) {
//            buckets[i] = new ArrayList<Pair>();
//        }
//        for(int i = 0; i < oldBucket.length; i++){
//            ArrayList<Pair> a = oldBucket[i];
//            for(Pair p: a){
//                int bucket = Math.abs(p.key.hashCode() % numBuckets);
//                buckets[bucket].add(p);
//                count++;
//            }
//        }
//    }
 // Right now numBuckets = max number of pages so we will never need to reshash. We could start with less buckets and expand as need

    public void remove(int pfn) {
        if(contains(pfn) == false)
            throw new NoSuchElementException();
        else{
        	int bucket = Math.abs(hash(pfn)) % numBuckets;
            if (buckets[bucket].pfn == pfn) {
            	buckets[bucket] = buckets[bucket].next;
            } else {
            	PageTableEntry prevBucket = buckets[bucket];
            	if (prevBucket.next.pfn == pfn) {
            		prevBucket.next = prevBucket.next.next;
            	}
            	
  
            }
            count--;
        }
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
}
