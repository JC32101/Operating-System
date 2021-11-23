package vm;

public class Policy {
	
	private int pfnToWrite;
	
	public Policy() {
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
