package vm;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyPageTable<PageTableEntry> {
	
    private static class PageTableEntry {
        int key;
        int transKey;
        boolean dirty;
        PageTableEntry next; //in case of a hash collision, PTEs can be chained together
        
        PageTableEntry(int key, int transKey) {
            this.key = key;
            this.transKey = transKey;
            this.dirty = false;
        }
        
        public String toString() {
        	String info = "PFN: " + key + " VPN: " + transKey + " Dirty: " + dirty + " Next's Info " + next;
        	return info;
        }
        
    }
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
    	if (pfn == -1 || get(pfn) == null || get(pfn).dirty == true) { //dirty page will be left in table
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
        	while (buckets[bucket].next != null) {
        		currentEntry = buckets[bucket].next;
        	}
        	currentEntry.next = n;
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
        	if (currentBucket.key == key) {
        		return true;
        	}
        	currentBucket = buckets[bucket].next;
        	}
        return false;
        }

    public PageTableEntry get(int key){
        if(contains(key)){
        	int bucket = Math.abs(hash(key)) % numBuckets;
            PageTableEntry currentBucket = buckets[bucket];
            while (currentBucket != null) {
            	if (currentBucket.key == key) {
            		return currentBucket;
            	}
            }
        }
		return null;
    }

    private void rehash() {
        PageTableEntry[] oldBuckets = buckets;
        numBuckets*=2;
        count = 0;
        buckets = new PageTableEntry[numBuckets];

        for(int i = 0; i < oldBuckets.length; i++){
        	if (oldBuckets[i] == null) {
        		continue;
        	}
            PageTableEntry a = oldBuckets[i];
            int bucket = Math.abs(hash(a.key) % numBuckets);
            buckets[bucket] = oldBuckets[i];
            count++;
            PageTableEntry currentBucket = buckets[bucket];
            while(currentBucket.next != null){
            	currentBucket = currentBucket.next;
                int bucket2 = Math.abs(hash(currentBucket.key) % numBuckets);
                buckets[bucket2] = currentBucket;
                count++;
            }
        }
    }

    public void remove(int key) {
        if(contains(key) == false)
            throw new NoSuchElementException();
        else{
        	int bucket = Math.abs(hash(key)) % numBuckets;
            if (buckets[bucket].key == key) {
            	buckets[bucket] = buckets[bucket].next;
            } else {
            	PageTableEntry prevBucket = buckets[bucket];
            	if (prevBucket.next.key == key) {
            		prevBucket.next = prevBucket.next.next;
            	}
            }
            count--;
        }
    }

    public void dirtifyEntry(int key) { //for when a vpn to pfn value is no longer valid
    	int bucket = Math.abs(hash(key)) % numBuckets;
    	if (buckets[bucket].key == key) {
        	buckets[bucket].dirty = true;
        } else {
        	PageTableEntry currentBucket = buckets[bucket];
        	while (currentBucket.next != null) {
        		currentBucket = currentBucket.next;
        		if (currentBucket.key == key) {
        			currentBucket.dirty = true;
        			return;
        		}
        	}
        }
    }
    
    public boolean isDirty(int key) { //for when a vpn to pfn value is no longer valid
    	int bucket = Math.abs(hash(key)) % numBuckets;
    	if (buckets[key] == null) {
    		return false;
    	}
    	if (buckets[bucket].key == key) {
        	return buckets[bucket].dirty;
        } else {
        	PageTableEntry currentBucket = buckets[bucket];
        	while (currentBucket.next != null) {
        		currentBucket = currentBucket.next;
        		if (currentBucket.key == key) {
        			return buckets[bucket].dirty;
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
    		if (vpnToPfn[i] != -1) {
    			if (isDirty(vpnToPfn[i])) {
    				dirtyFrames[i] = vpnToPfn[i];
    			}
    		}
    	}
    	return dirtyFrames;
    }
}
