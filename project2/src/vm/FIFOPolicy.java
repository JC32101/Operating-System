package vm;

public class FIFOPolicy {
	
	private int pfnToWrite;
	
	public FIFOPolicy() {
		pfnToWrite = -1;
	}
	
	public int getPfnToWrite() {
		pfnToWrite++;
		if (pfnToWrite > 255) {
			pfnToWrite = 0;
		}
		return pfnToWrite;
	}
	
}
