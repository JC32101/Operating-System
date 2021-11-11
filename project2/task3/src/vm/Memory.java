package vm;
import storage.PhyMemory;
public abstract class Memory {
    protected long frequency = 30; //10sec
    protected PhyMemory ram; //inherited and accessible to subclass
    /*
     * The constructor creates memory with specified size mem_size and a prepared disk
     */
    public Memory(PhyMemory ram) {
        this.ram = ram;
    }

    protected PhyMemory getPhyMemory(){
        return ram;
    }
    /*
     * writes specified value into memory at specified position - addr
     */
    abstract public void write(int addr, byte value);

    /*
     * returns value that was stored at address addr
     */
    abstract public byte read(int addr);
    /*
     * flush back dirty pages to disk
     */
    abstract protected void sync_to_disk();
    /*
     * start the background thread to write back dirty pages
     * at the interval of frequency.
     */
    public void startup() {
        //func stub for future
    }

    public void shutdown() {
        sync_to_disk();
        ram.flush();
    }
}
