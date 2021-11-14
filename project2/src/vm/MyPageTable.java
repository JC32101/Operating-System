package vm;

public class MyPageTable {
	private PTEHashmap pfnMap;
	private PTEHashmap vpnMap;
	
	public MyPageTable() {
		pfnMap = new PTEHashmap();
		vpnMap = new PTEHashmap();
	}

	
	
}
