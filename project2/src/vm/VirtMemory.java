package vm;

import storage.PhyMemory;

public class VirtMemory extends Memory {

	MyPageTable pt;
	FIFOPolicy policy;
	
	public VirtMemory(PhyMemory ram) {
		super(ram);
		pt = new MyPageTable();
		policy = new FIFOPolicy();
	}
	
	public VirtMemory() {
		super(new PhyMemory());
		pt = new MyPageTable();
		policy = new FIFOPolicy();
    }

	@Override
	public void write(int addr, byte value) {
		if (addr >= 65535 || addr < 0) {
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
				ram.store(vpn, pfn*64);
			}
			pt.addVpnToPfn(vpn, pfn);
			pt.put(pfn, vpn); //we weren't creating PTEs before lol
		}
		
		ram.write(pfn*64+offset, value);
		pt.dirtifyEntry(pfn);
	}

	@Override
	public byte read(int addr) {
		if (addr >= 65535 || addr < 0) {
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
				ram.store(vpn, pfn*64);
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
