package vm;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class VirtMemoryTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }
    @Test
    public void test1_OutOfRange() {
        Memory m = new VirtMemory();
        m.startup();
        m.write(0xFFFFFF, Byte.parseByte("-1"));
        MatcherAssert.assertThat(0, not(errContent.toString().length()));
        byte x = m.read(0xFFFFFF);
        MatcherAssert.assertThat(0, not(errContent.toString().length()));
        //Code review q1: what is the max legit address for m.write()??
        //The max legit address would be 0xFFFF. Our virtual memory is 64kb, or 2^16 bytes, meaning 16 bytes will be required
        //in total for the address space. 10 bits are dedicated to the 1,024 pages that will need to be addressed
        //(virutal mem size / page size = 65,536/64 = 1,024). The remaining 6 bits are used to identify the offset
        //within the 64-byte page. The max value that can be represented with 16 bits is 0xFFFF.
        m.shutdown();
    }
    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }
    @Test
    public void test2_SingleWrite() {
        Memory m = new VirtMemory();
        m.startup();
        m.write(0x8000, Byte.parseByte("-1")); //write it to somewhere way beyond 16K
        m.shutdown();
        //now the disk should have persisted your write, reboot
        m = new VirtMemory();
        m.startup();
        byte data = m.read(0x8000);
        m.shutdown();
        assertEquals(Byte.parseByte("-1"), data);
    }
    @Test
    public void test3_WriteBackToSameBlock() {
        //every 32 writes triggers a write-back to disk.
        Memory m = new VirtMemory();
        m.startup();
        for(int i=0; i<32; i++) {
            m.write(i, Byte.parseByte("-1"));
        }
        m.shutdown();
        int writeCount = m.getPhyMemory().writeCountDisk();
        int readCount = m.getPhyMemory().readCountDisk();
        assertEquals(1, writeCount);
        //Code review q2: why is there only 1 disk write?
        //This behavior is detailed in the project instructions as a batched-write-back policy. This policy
        //details that when writing to a page, instead of writing to disk every time the page is altered,
        //we only write to disk immediately after 32 pages writes have occured (aka 1/2 of the page has been written to).
        //In this case, exactly 32 writes on the same page occur, so the disk is only written to once. This one write
        //will write the entirety of the page to disk, ensuring all of the changes will be persistent on disk
        assertEquals(1, readCount);
        //Code review q3: why is there only 1 disk read?
        //Only 1 disk read will be needed to handle the page fault that will occur during the first m.write() call.
        //Because physical memory begins empty of pages, the initial page will first need to be loaded from
        //disk into memory before it can be written to. The following m.write() calls then write to this same page, meaning
        //additional disk reads are not required.
    }
    @Test
    public void test4_WriteBackToMultiBlocks() {
        //every 32 writes triggers a write-back to disk.
        Memory m = new VirtMemory();
        m.startup();
        for(int i=0; i<32; i++) {
            m.write(i*64, Byte.parseByte("-1"));
        }

        m.shutdown();
        int writeCount = m.getPhyMemory().writeCountDisk();
        int readCount = m.getPhyMemory().readCountDisk();
        assertEquals(32, writeCount);
        //Code review q4: why are there 32 disk writes?
        //The above "for" loop writes to a different page every time. This is indicated by the fact that each address
        //written to is over 64 bytes away (indicated by the *64). Because our implementation writes at the page
        //level (ie. if a page is dirtied, the entire page must be written to disk), 32 writes will need to occur (1 for
        //each dirtied page).
        assertEquals(32, readCount);
        //Code review q5: why are there 32 disk read?
        //Because each m.write() call is writing to a new page every time, each call will result in a page fault which
        //will need to be handled by retrieving the needed page from disk and loading it into physical memory.
    }
    //the following are more realistic workloads
    static final int TEST_SIZE = 64*1024;// 64K, test on max address space!
    static byte fce(int adr) {
        return (byte) ((adr * 5 + 6) % 256 - 128);
    }
    static byte fce2(int adr) {
        return (byte) ((adr * 7 + 5) % 256 - 128);
    }
    @Test
    public void test5_End2EndForward() {
        Memory m = new VirtMemory();
        m.startup();
        boolean result = true;
        for (int i = 0; i < TEST_SIZE; i++)
            m.write(i, fce(i));
        for (int i = 0; i < TEST_SIZE; i++)
            if (m.read(i) != fce(i))
                result = false;
        assertEquals(true, result);
        m.shutdown();
        assertEquals(2048, m.getPhyMemory().writeCountDisk());
        //Code review q6: why are there 2048 disk writes?
        //The first "for" loop will write to every address, incrementing the address by 1 each time. This means that the
        //entirety of each page will be written to before the next page is written to. As detailed in q2, our implementation employs a
        //batched-write-back policy, meaning that a page will immediately be written to disk every 32 writes. Because
        //each page is 64 bytes, 2 writes will be needed per page (1 write occurs after the first 32-byte writes, another occurs after
        //the second 32 writes). Given that we have 1,024 total pages that each will require 2 writes, we have 
        //1,024 * 2 = 2048 writes.
        assertEquals(2048, m.getPhyMemory().readCountDisk());
        //Code review q7: why are there 2048 disk reads?
        //Our implementation uses 1,024 pages and each page will need to be read from disk and loaded into physical
        //memory twice. Each page will be read from disk once when the inital m.write() occurs in the first "for" loop.
        //Eventually, physical memory will become full and pages will need to be removed in order to make room for
        //pages that are currently being written to. By the time that the second "for" loop occurs (which starts reading from the first page written to),
        //the initial pages that were written to will have been pushed out of memory and replaced by other pages. This means that each page
        //will need to be loaded into memory from disk again in order to be read; by the time a page is attempted to be read, it will
        //already have been removed from memory to make room for other pages and must be reloaded.

    }
    @Test
    public void test6_End2EndBackward() {
        Memory m = new VirtMemory();
        m.startup();
        boolean result = true;
        for (int i = 0; i < TEST_SIZE; i++)
            m.write(i, fce(i));
        for (int i = TEST_SIZE-1; i >= 0; i--)
            if (m.read(i) != fce(i))
                result = false;
        assertEquals(true, result);
        m.shutdown();
        assertEquals(2048, m.getPhyMemory().writeCountDisk());
        //Code review q8: why are there 2048 disk writes?
        //The m.write() "for" loop here is identical to the one in test5. 2048 writes will occur for the same
        //reason they occur in q6: each 64-byte page will be written to disk twice (once every time 32 bytes are written to as
        //detailed in the project instructions).
        assertEquals(1792, m.getPhyMemory().readCountDisk());
        //Code review q9: why are there 1792 disk writes? Why is it different from test5?
        //test6 differs from test5 in that it does NOT begin reading from the first page written to, and instead reads
        //in the opposite direction (the last written page will be read first, the second-to last written to will be read
        //next, etc.). Because pages written to more recently are read first, the first 256 pages to be read (16kb worth of data,
        //which is the size of the physical memory) will already be loaded into memory and will not have to be retrieved from disk. Eventually,
        //these 256 pages will have been read and the other 768 pages will need to be retrieved from disk before a 
        //read can occur.
        //In total, each page will be read once when written to, and 768 pages will need to be reloaded in order to
        //be read. 1,024 + 768 = 1,792 reads.
    }
    @Test
    public void test7_End2EndMix() {
        Memory m = new VirtMemory();
        m.startup();
        boolean result = true;
        for (int i = TEST_SIZE-1; i >= 0; i--)
            m.write(i, fce(i));
        for (int posun = 0; posun < TEST_SIZE; posun += 100) {
            for (int i = 0; i < TEST_SIZE; i++) {
                int adr = (i+posun)%TEST_SIZE;
                if (m.read(adr) != fce(adr))
                    result = false;
            }
        }
        int posun_zapis=55;
        for (int i = 0; i < TEST_SIZE; i++) {
            int adr = (i+posun_zapis)%TEST_SIZE;
            m.write(adr, fce2(adr));
        }
        for (int posun = 20; posun < TEST_SIZE; posun += 100) {
            for (int i = 0; i < TEST_SIZE; i++) {
                int adr = (i+posun)%TEST_SIZE;
                if (m.read(adr) != fce2(adr))
                    result = false;
            }
        }
        assertEquals(true, result);
        m.shutdown();
    }
}
