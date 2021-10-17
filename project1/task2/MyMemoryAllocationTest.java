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
		mal.alloc(1); //allocate 1 byte at index 1, arbitrary adress, index 0 reserved, memory is 14 bytes, 13 usable bytes total
		mal.alloc(3); //allocate 3 bytes, indexes 2, 3, 4.
		mal.alloc(2);// allocate 2 bytes, index 5 and 6
		mal.alloc(2);// allocate 2 bytes, index 7 and 8
		mal.alloc(1);// allocate 1 byte, index 9
		mal.alloc(1);//allocate 1 byte, index 10
		mal.alloc(1);//allocate 1 byte, index 11
		mal.alloc(2);//allocate 2 bytes, index 12 and 13
		mal.free(2);// free byte chunk at index 2, which frees byte 2, 3, and 4, 3 bytes total
		mal.free(7);//frees 2 bytes at index 7 and 8
		mal.free(10);//frees 1 byte at index 10
		mal.free(12);//frees 2 bytes, at index 12 and 13
		assert(mal.size() == 8);//assert total number of free is 8, which is true
		assert(mal.max_size() == 3);//assert largest free chunk is 3 bytes big, which is true
		return mal;
	}
	@Test
	public void testFFAlloc() {
		MyMemoryAllocation mal = prepHoles("FF");
		assert(mal.alloc(1)==2);//assert that the first available alloc for 1 byte at index 2
		assert(mal.alloc(2)==3);//assert that the first available alloc for 2 bytes at index 3.
		assert(mal.alloc(2)==7);//assert that the first available alloc for 2 bytes at index 7
		assert(mal.alloc(3)==0); //failed case ! fragments!
		// assert that the first available alloc for 3 bytes is unavailable, mal.alloc(3) returns 0 for failed allocation, which equals the test case 0
	}
	@Test
	public void testBFAlloc() {
		MyMemoryAllocation mal = prepHoles("BF");
		assert(mal.alloc(1)==10);// first best fit for 1 byte is at index 10
		assert(mal.alloc(2)==7);// first best fit for 2 bytes is at index 7
		assert(mal.alloc(2)==12);//first best fit for 2 bytes is at index 12
		assert(mal.alloc(3)==2); //success! less fragments! 
		//first best fit is available at index 2 for 3 bytes, the largest available freespace
	}
	@Test
	public void testNFAlloc() {
		MyMemoryAllocation mal = prepHoles("NF");
		assert(mal.alloc(1)==2);//first available alloc for 1 byte at index 2.
		assert(mal.alloc(2)==7);//next available block starting at index 7, available for 2 byte alloc
		assert(mal.alloc(2)==12);//next available block starting at 12 is available for 2 byte alloc
		assert(mal.alloc(3)==0); //also failed case ! fragments! -> no available freespace for a 3 byte alloc
		assert(mal.alloc(1)==3); //wrap around
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
		//creates error message because cannot free at index 2, already free.
		assert(errContent.toString().length() != 0);//assert that error occurs is true
		mal.free(1); //free successfull.
		assert(mal.alloc(4)==1);//true, index 1-4 is free for a 4 byte alloc
	}
	@After
	public void restoreStreams() {
	    System.setOut(originalOut);
	    System.setErr(originalErr);
	}
	@Test
	public void testFree2() {
		MyMemoryAllocation mal = prepHoles("FF");
		mal.free(9);//free 1 byte at index 9
		mal.free(5);//free 2 bytes from index 5 to 6
		assert(mal.max_size() == 9);//true, index 2 to 10 free, largest available free space is 9 bytes
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