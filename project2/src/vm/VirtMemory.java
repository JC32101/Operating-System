package vm;

import storage.PhyMemory;

public class VirtMemory extends Memory {

	MyPageTable pt;
	Policy policy;
	
	public VirtMemory(PhyMemory ram) {
		super(ram);
		pt = new MyPageTable();
		policy = new Policy();
	}
	
	public VirtMemory() {
		super(new PhyMemory());
		pt = new MyPageTable();
		policy = new Policy();
    }


	@Override
	public void write(int addr, byte value) {
		if (addr >= 65535 || addr < 0) {
			System.err.println("Attempted write at " + addr + "! Out of bounds!");
			return;
		}
		int vpn = addr/64;
		int pfn = -1;
		try {
			pfn = pt.addrLookup(addr);
		} catch (PageFaultException e) {
			pfn = policy.getPfnToWrite();
			int oldVPN = pt.pfnLookup(pfn);
			if (pt.isDirty(oldVPN) && oldVPN != -1) {
				ram.store(pt.pfnLookup(pfn), pfn*64);
				pt.put(oldVPN, -1); //make clean
			}
			ram.load(vpn, pfn*64);
			pt.put(vpn, pfn); //make dirty
		}
		int offset = addr % 64;
		ram.write(pfn*64+offset, value);
		ram.store(vpn, pfn*64);
		pt.dirtifyEntry(vpn);
	}

	@Override
	public byte read(int addr) {
		if (addr >= 65535 || addr < 0) {
			System.err.println("Attempted read at " + addr + "! Out of bounds!");
			return Byte.parseByte("-1");
		}
		int vpn = addr/64;
		int pfn = -1;
		try {
			pfn = pt.addrLookup(addr);
		} catch (PageFaultException e) {
			pfn = policy.getPfnToWrite();
			if (pt.isDirty(pt.pfnLookup(pfn))) {
				ram.store(pt.pfnLookup(pfn), pfn*64);
				pt.put(pt.pfnLookup(pfn), -1);
			}
			ram.load(vpn, pfn*64);
			pt.put(vpn, pfn);
		}
		int offset = addr % 64;
		return ram.read(pfn*64 + offset);
	}

	@Override
	protected void sync_to_disk() { //this method was only looping to 256
	//	int[] dirtyPages = pt.getDirtyPages();
	//	for (int i = 0; i < 1024; i++) {
	//		if (dirtyPages[i] != -1) {
	//			ram.store(i, dirtyPages[i]);
	//		}
	//	}
	}

}
