package vm;

import storage.PhyMemory;

public class VirtMemory extends Memory {

	MyPageTable pt;
	
	public VirtMemory(PhyMemory ram) {
		super(ram);
		pt = new MyPageTable();
		// TODO Auto-generated constructor stub
	}
	
	public VirtMemory() {
		super(new PhyMemory());
		pt = new MyPageTable();
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
			pfn = pt.mapPage(vpn);
			if (pt.isDirty(pfn)) {
				ram.store(0, pfn*64+offset); //first value needs to be the blocks of physical mem to write to, not 0
			}
		}
		ram.write(pfn*64 + offset, value);
		
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
			
		}
		return ram.read(pfn*64 + offset);
	}

	@Override
	protected void sync_to_disk() {
		// TODO Auto-generated method stub
		
	}

}
