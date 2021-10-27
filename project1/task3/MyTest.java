package task3;

public class MyTest {

	public static void main(String[] args) {
		MyMemoryAllocation mal= new MyMemoryAllocation(14, "FF");
		mal.alloc(1);
		mal.alloc(3);
		mal.alloc(2);
		mal.alloc(2);
		mal.alloc(1);
		mal.alloc(1);
		mal.alloc(1);
		mal.alloc(2);
		mal.free(2);
		mal.free(7);
		mal.free(10);
		mal.free(12);
		mal.free(2);
		mal.print();
	}

}
