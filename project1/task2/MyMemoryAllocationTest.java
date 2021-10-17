
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MyMemoryAllocationTest {
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;
	private final PrintStream originalErr = System.err;
	
	@Test
	public void testConstructor() {
		MyMemoryAllocation mal= new MyMemoryAllocation(500, "FF");
		assert(mal.size() == 499);
		assert(mal.max_size() == 499);
	}
	private MyMemoryAllocation prepHoles(String algo) {
		MyMemoryAllocation mal= new MyMemoryAllocation(14, algo);
		mal.alloc(1); // (usedList)->[1,1] ||(freelist)->[2,12]
		mal.alloc(3); // (usedList)->[1,1]->[2,3] ||(freelist)->[5,9]
		mal.alloc(2); // (usedList)->[1,1]->[2,3]->[5,2] ||(freelist)->[7,7]
		mal.alloc(2); // (usedList)->[1,1]->[2,3]->[5,2]->[7,2] ||(freelist)->[9,5]
		mal.alloc(1); // (usedList)->[1,1]->[2,3]->[5,2]->[7,2]->[9,1] ||(freelist)->[10,4]
		mal.alloc(1); // (usedList)->[1,1]->[2,3]->[5,2]->[7,2]->[9,1]->[10,1] ||(freelist)->[11,3]
		mal.alloc(1); // (usedList)->[1,1]->[2,3]->[5,2]->[7,2]->[9,1]->[10,1]->[11,1] ||(freelist)->[12,3]
		mal.alloc(2); // (usedList)->[1,1]->[2,3]->[5,2]->[7,2]->[9,1]->[10,1]->[11,1]->[12,2] ||(freelist)
		mal.free(2);  // (usedList)->[1,1]->[5,2]->[7,2]->[9,1]->[10,1]->[11,1]->[12,2] ||(freelist)->[2,3]
		mal.free(7);  // (usedList)->[1,1]->[5,2]->[9,1]->[10,1]->[11,1]->[12,2] ||(freelist)->[2,3]->[7,2]
		mal.free(10); // (usedList)->[1,1]->[5,2]->[9,1]->[11,1]->[12,2] ||(freelist)->[2,3]->[7,2]->[10,1]
		mal.free(12); // (usedList)->[1,1]->[5,2]->[9,1]->[11,1]-> ||(freelist)->[2,3]->[7,2]->[10,1]->[12,2]
		assert(mal.size() == 8);//(freelist)->[2,3]->[7,2]->[10,1]->[12,2] total free bytes is 8
		assert(mal.max_size() == 3);//(freelist)->[2,3]->[7,2]->[10,1]->[12,2] largest free block is 3 bytes
		return mal;
	}
	@Test
	public void testFFAlloc() {
		MyMemoryAllocation mal = prepHoles("FF");
		assert(mal.alloc(1)==2); // (usedList)->[1,1]->[5,2]->[9,1]->[11,1]->[2,1] ||(freelist)->[3,2]->[7,2]->[10,1]->[12,2]
		assert(mal.alloc(2)==3); // (usedList)->[1,1]->[5,2]->[9,1]->[11,1]->[2,1]->[3,2] ||(freelist)->[7,2]->[10,1]->[12,2]
		assert(mal.alloc(2)==7); // (usedList)->[1,1]->[5,2]->[9,1]->[11,1]->[2,1]->[3,2]->[7,2] ||(freelist)->[10,1]->[12,2]
		assert(mal.alloc(3)==0); //failed case ! fragments!
		// (usedList)->[1,1]->[5,2]->[9,1]->[11,1]->[2,1]->[3,2]->[7,2] ||(freelist)->[10,1]->[12,2]
	}
	@Test
	public void testBFAlloc() {
		MyMemoryAllocation mal = prepHoles("BF");
		assert(mal.alloc(1)==10); // (usedList)->[1,1]->[5,2]->[9,1]->[11,1]->[10,1] ||(freelist)->[2,3]->[7,2]->[12,2]
		assert(mal.alloc(2)==7); // (usedList)->[1,1]->[5,2]->[9,1]->[11,1]->[10,1]->[7,2] ||(freelist)->[2,3]->[12,2]
		assert(mal.alloc(2)==12); // (usedList)->[1,1]->[5,2]->[9,1]->[11,1]->[10,1]->[7,2]->[12,2] ||(freelist)->[2,3]
		assert(mal.alloc(3)==2); //success! less fragments!  
		// (usedList)->[1,1]->[5,2]->[9,1]->[11,1]->[10,1]->[7,2]->[12,2]->[2,3] ||(freelist)->
		
	}
	@Test
	public void testNFAlloc() {
		MyMemoryAllocation mal = prepHoles("NF");
		assert(mal.alloc(1)==2); // (usedList)->[1,1]->[5,2]->[9,1]->[11,1]->[2,1] ||(freelist)->[3,2]->[7,2]->[10,1]->[12,2]
		assert(mal.alloc(2)==7); // (usedList)->[1,1]->[5,2]->[9,1]->[11,1]->[2,1]->[7,2] ||(freelist)->[3,2]->[10,1]->[12,2]
		assert(mal.alloc(2)==12); // (usedList)->[1,1]->[5,2]->[9,1]->[11,1]->[2,1]->[7,2]->[12,2] ||(freelist)->[3,2]->[10,1]
		assert(mal.alloc(3)==0); // (usedList)->[1,1]->[5,2]->[9,1]->[11,1]->[2,1]->[7,2]->[12,2] ||(freelist)->[3,2]->[10,1]->(counter here)
		assert(mal.alloc(1)==3);  // (usedList)->[1,1]->[5,2]->[9,1]->[11,1]->[2,1]->[7,2]->[12,2]->[3,1] ||(freelist)->[4,1]->[10,1]
		//next available space for 1 byte starting at index 3, algorithm circles back to search for spaces again.
	}

	@Before
	public void setUpStreams() {
	    System.setOut(new PrintStream(outContent));
	    System.setErr(new PrintStream(errContent));
	}

	@Test
	public void testFree1() {
		MyMemoryAllocation mal = prepHoles("FF");
		mal.free(2);//check if there is an error message
		// (usedList)->[1,1]->[5,2]->[9,1]->[11,1]-> ||(freelist)->[2,3]->[7,2]->[10,1]->[12,2]
		assert(errContent.toString().length() != 0);//assert that error occurs is true
		mal.free(1); //free successfull.// (usedList)->[5,2]->[9,1]->[11,1]-> ||(freelist)->[1,1]->[2,3]->[7,2]->[10,1]->[12,2]
		assert(mal.alloc(4)==1); //free successfull.// (usedList)->[1,4]->[5,2]->[9,1]->[11,1] ||(freelist)->[7,2]->[10,1]->[12,2]
	}
	@After
	public void restoreStreams() {
	    System.setOut(originalOut);
	    System.setErr(originalErr);
	}
	@Test
	public void testFree2() {
		MyMemoryAllocation mal = prepHoles("FF");
		mal.free(9); // (usedList)->[1,1]->[5,2]->[11,1]-> ||(freelist)->[2,3]->[7,4]->[12,2]
		mal.free(5); // (usedList)->[1,1]->[11,1]-> ||(freelist)->[2,9]->[12,2]
		assert(mal.max_size() == 9); // (usedList)->[1,1]->[11,1]-> ||(freelist)->[2,9]->[12,2]
	}
	
	@Test
	public void testEndtoEndBF() {
		end2endTest1("BF");
		end2endTest2("BF");
		end2endTest3("BF");
	}

	@Test
	public void testEndtoEndFF() {
		end2endTest1("FF");
		end2endTest2("FF");
		end2endTest3("FF");
	}
	
	@Test
	public void testEndtoEndNF() {
		end2endTest1("NF");
		end2endTest2("NF");
		end2endTest3("NF");
	}
	static final int SIZE = 10000;

	static final int TEST_SIZE_1 = 10;
	static final int TEST_SIZE_2 = 20;
	
	private void end2endTest1(String algo) {
		MyMemoryAllocation m= new MyMemoryAllocation(SIZE, algo);
		boolean result = true;
		int ptr[] = new int[SIZE];
		int p = 0;
		while (m.max_size() >= TEST_SIZE_1) {
			ptr[p] = m.alloc(TEST_SIZE_1);
			if (ptr[p] == 0) {
				result = false;
			}
			p++;
		}
		int max_p = p;
		if (max_p < 400) {
			result = false;
		}
		int l_limit = p / 3;
		int u_limit = 2 * p / 3;
		for (int i = l_limit; i < u_limit; i++) {
			m.free(ptr[i]);
			ptr[i] = 0;
		}
		if(m.max_size() != (u_limit-l_limit)*TEST_SIZE_1) {
			result = false;
		}
		p = l_limit;
		while (p < u_limit && m.max_size() >= TEST_SIZE_1) {
			ptr[p] = m.alloc(TEST_SIZE_1);
			if (ptr[p] == 0) {
				result = false;
			}
			p++;
		}
		for (int i = 0; i < max_p; i++) {
			if (ptr[i] > 0)
				m.free(ptr[i]);
			ptr[i] = 0;
		}
		if(m.size() != SIZE-1) {
			result = false;
		}
		if (result) {
			System.out.println("end2endTest1: PASS " + max_p);
		} else {
			System.out.println("end2endTest1: FAIL");
		}
		assert(result == true);
	}
	
	private void end2endTest2(String algo) {
		MyMemoryAllocation m= new MyMemoryAllocation(SIZE, algo);
		boolean result = true;
		int ptr[] = new int[SIZE];
		int p = 0;
		while (m.max_size() >= TEST_SIZE_1) {
			ptr[p] = m.alloc(TEST_SIZE_1);
			if (ptr[p] == 0) {
				result = false;
			}
			p++;
		}
		int max_p = p;
		for (int i = 0; i < max_p; i += 3) {
			m.free(ptr[i]);
			ptr[i] = 0;
		}
		p = 0;
		while (p < max_p && m.max_size() >= TEST_SIZE_1) {
			ptr[p] = m.alloc(TEST_SIZE_1);
			if (ptr[p] == 0) {
				result = false;
			}
			p += 3;
		}
		if (p < max_p / 2) {
			result = false;
		}
		for (int i = 0; i < max_p; i++) {
			if (ptr[i] > 0)
				m.free(ptr[i]);
			ptr[i] = 0;
		}
		if (result) {
			System.out.println("end2endTest2: PASS " + max_p);
		} else {
			System.out.println("end2endTest2: FAIL");
		}
		assert(result == true);
	}
	
	private void end2endTest3(String algo) {
		MyMemoryAllocation m= new MyMemoryAllocation(SIZE, algo);
		boolean result = true;
		int ptr[] = new int[SIZE];
		int p = 0;
		while (m.max_size() >= (2 * TEST_SIZE_2 + TEST_SIZE_1)) {
			if (m.max_size() >= TEST_SIZE_1) {
				ptr[p] = m.alloc(TEST_SIZE_1);
				if (ptr[p] == 0) {
					result = false;
				}
				p++;
			}
			if (m.max_size() >= TEST_SIZE_2) {
				ptr[p] = m.alloc(TEST_SIZE_2);
				if (ptr[p] == 0) {
					result = false;
				}
				p++;
			}
			if (m.max_size() >= TEST_SIZE_2) {
				ptr[p] = m.alloc(TEST_SIZE_2);
				if (ptr[p] == 0) {
					result = false;
				}
				p++;
			}
		}
		int max_p = p;
		if (max_p < 90) {
			result = false;
		}
		for (int i = 0; i < max_p - 2; i += 3) {
			m.free(ptr[i]);
			ptr[i] = 0;
			m.free(ptr[i + 1]);
			ptr[i + 1] = 0;
		}
		p = 0;
		while (m.max_size() >= TEST_SIZE_1) {
			if ((p >= max_p) || (ptr[p] == 0)) {
				ptr[p] = m.alloc(TEST_SIZE_1);
			}
			p++;
		}
		if (p < max_p-2) {
			result = false;
		}
		if (result) {
			System.out.println("end2endTest3: PASS");
		} else {
			System.out.println("end2endTest3: FAIL");
		}
		assert(result == true);
	}
}
