package vm;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
//import vm.PageTableEntry;

public class MyPageTable {
	private int numBuckets = 64;
	private PageTableEntry[] vpnIndex;
	private int[] pfnValue = new int[256];
	private int count = 0;
	public MyPageTable() {
		vpnIndex = new PageTableEntry[1024];
		for (int i = 0; i < 1024; i++) {
			vpnIndex[i] = new PageTableEntry(i, -1);//-1 means disk
		}
		for (int i = 0; i < 256; i++){
			pfnValue[i] = -1;
		}
	}

	public PageTableEntry getTableEntry(int vpn){
		return vpnIndex[vpn];
	}

	public int pfnLookup(int pfn){
		return pfnValue[pfn];
	}

	public int addrLookup(int addr) throws PageFaultException{
		int vpn = Math.abs(hash(addr));
		int pfn = -1;
        PageTableEntry pte = vpnIndex[vpn];
        while (pte != null) {
        	if (pte.getKey() == vpn) {
				pfn = pte.getValue(); //null pointer exception? or illegal access
				if (pfn == -1) { //dirty page will be left in table <- what? if pfn = -1, its on disk
					throw new PageFaultException();
				}
        		return pfn;
        	}
        	pte = pte.getNext();
        }
		return pfn;
	}

	//who uses put??
	public void put(int vpn,  int pfn) {
		PageTableEntry newPTE = new PageTableEntry(vpn, pfn);
		vpnIndex[vpn] = newPTE; //old: vpnIndex[vpn] = insertIntoBucket(newPTE, vpnIndex[vpn]);
		pfnValue[pfn] = vpn;
        //count++;
		//TODO: your code here to call rehash as needed
		//if((double)count/numBuckets >= 0.5){
		//	rehash();
		//}
	}

	

//	public PageTableEntry insertIntoBucket(PageTableEntry node, PageTableEntry finalLinkedList){
//		if (finalLinkedList == null) {
//      	finalLinkedList = node;
//			return finalLinkedList;
//        } else {
//        	PageTableEntry currentEntry = finalLinkedList;
//        	while (finalLinkedList.getNext() != null) {
//        		currentEntry = finalLinkedList.getNext();
//        	}
//        	currentEntry.getNext().equals(node);
//			return finalLinkedList;
//        }
//	}

	public int getNumBuckets () {
		return numBuckets;
	}

	private int hash(int addr) {
		int vpn = addr / 64;
        return vpn; 
    }
	
	public boolean isDirty(int vpn) { //for when a vpn to pfn value is no longer valid
    	if (vpn == -1 || vpnIndex[vpn] == null) {
    		return false;
    	}
    	return vpnIndex[vpn].isDirty();
    }

	public void dirtifyEntry(int vpn) { //for when a vpn to pfn value is no longer valid
    	vpnIndex[vpn].setDirty(true);
    }

	@Override
	public String toString() {
		return "lol";
	}
		
//	private void rehash() {//a stands for oldtable traversal pointer
//		//TODO: your code here
//		PageTableEntry[] oldBuckets = buckets;
//		numBuckets*=2;
//		count = 0;
//		buckets = new PageTableEntry[numBuckets];
//		PageTableEntry oldNodes;
//
//		//for (int i = 0; i < numBuckets; i++) {
//		//	buckets[i] = new ArrayList<DataType>();
//		//}
//		for(int i = 0; i < oldBuckets.length; i++){
//        	if (oldBuckets[i] == null) {
//        		continue;
//        	}
//			oldNodes = oldBuckets[i];
//            while(oldNodes != null){
//				PageTableEntry frontNode = oldNodes;
//				frontNode.getNext().equals(null);
//            	int bucket = Math.abs(hash(frontNode.getKey()) % numBuckets);
//            	buckets[bucket] = insertIntoBucket(frontNode, buckets[bucket]);
//				oldNodes.getNext();
//            	count++;
//            }
//        }
//	}

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

//	public boolean contains(int vpn) {
//		//TODO: your code here
//		int bucket = Math.abs(hash(key)) % numBuckets;
//      PageTableEntry currentBucket = buckets[bucket];
//    while (currentBucket != null) {
//  	if (currentBucket.getKey() == key) {
//		return true;
//	}
//	currentBucket = buckets[bucket].getNext();
//	}
//  return false;
	}


