package vm;
//import vm.VirtMemory;// is this nessisary?

public class Policy {
	
	private int fifoPFN;
	
	public Policy() {
		fifoPFN = -1;
	}
	
	public int getPfnToWrite() {
		fifoPFN++;
		if (fifoPFN > 255) {
			fifoPFN = 0; //total pfns = 256 while total vpns = 1024
		}

		return fifoPFN;
	}
	
}