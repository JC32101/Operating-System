package vm;

import storage.PhyMemory;

public class VirtMemory extends Memory {

	private MyPageTable pt;
	private Policy policy;
	private int[] pageWrites;
	private int writeCount;
	
	public VirtMemory(PhyMemory ram) {
		super(ram);
		pt = new MyPageTable();
		policy = new Policy();
		pageWrites = new int[1024];
		writeCount = 0;
	}
	
	public VirtMemory() {
		super(new PhyMemory());
		pt = new MyPageTable();
		policy = new Policy();
		pageWrites = new int[1024];
		writeCount = 0;
    }

	@Override
	public void write(int addr, byte value) {
		if (addr > 65535 || addr < 0) {
			System.err.println("Attempted write at " + addr + "! Out of bounds!");
			return;
		}
		int vpn = addr / 64;
		int offset = addr % 64;
		int pfn = -1;
		try {
			pfn = pt.transToPfn(vpn);
		} catch (PageFaultException e) {
			pfn = policy.getPfnToWrite();
			if (pt.isDirty(pfn)) {
				ram.store(pt.valueLookup(pfn), pfn*64);
				pt.cleanEntry(pfn);
			}
			if (pt.valueLookup(pfn) != -1) {
				pt.removeVpnToPfn(pt.valueLookup(pfn));
				pt.remove(pfn);
			}
// 			pt.addVpnToPfn(vpn, pfn);
			pt.put(pfn, vpn); //we weren't creating PTEs before lol
			ram.load(vpn, pfn*64);
		}
		
		ram.write(pfn*64+offset, value);
		writeCount++;
		pageWrites[vpn]++;
		if (pageWrites[vpn] >= 32 || writeCount >= 32) {
			ram.store(vpn, pfn*64);
			pageWrites[vpn] = 0;
			pt.cleanEntry(pfn);
			writeCount = 0;
			return;
		}
		
		pt.dirtifyEntry(pfn);
	}

	@Override
	public byte read(int addr) {
		if (addr > 65535 || addr < 0) {
			System.err.println("Attempted read at " + addr + "! Out of bounds!");
			return Byte.parseByte("-1");
		}
		int vpn = addr / 64;
		int offset = addr % 64;
		int pfn = -1;
		try {
			pfn = pt.transToPfn(vpn);
		} catch (PageFaultException e) {
			pfn = policy.getPfnToWrite();
			if (pt.isDirty(pfn)) {
				ram.store(pt.valueLookup(pfn), pfn*64);
				pt.cleanEntry(pfn);
			}
			if (pt.valueLookup(pfn) != -1) {
				pt.removeVpnToPfn(pt.valueLookup(pfn));
				pt.remove(pfn);
			}
 			pt.addVpnToPfn(vpn, pfn);
			pt.put(pfn, vpn);
			ram.load(vpn, pfn*64);
		}
		return ram.read(pfn*64 + offset);
	}

	@Override
	protected void sync_to_disk() { //this method was only looping to 256
		int[] dirtyPages = pt.getDirtyPages();
		for (int i = 0; i < 1024; i++) {
			if (dirtyPages[i] != -1) {
				ram.store(i, dirtyPages[i]);
			}
		}
	}

}
