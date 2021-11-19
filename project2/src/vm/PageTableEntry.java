package vm;

import java.util.Objects;

public class PageTableEntry {
    private int key;
    private int transKey;
    private boolean dirty;
    private PageTableEntry next; //in case of a hash collision, PTEs can be chained together

    PageTableEntry(int key, int transKey) {
        this.key = key;
        this.transKey = transKey;
        this.dirty = false;
    }

    public String toString() {
        String info = "PFN: " + key + " VPN: " + transKey + " Dirty: " + dirty + " Next's Info " + next;
        return info;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public void setNext(PageTableEntry next) {
        this.next = next;
    }

    public void setTransKey(int transKey) {
        this.transKey = transKey;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public int getKey() {
        return key;
    }

    public boolean isDirty() {
        return dirty;
    }

    public int getTransKey() {
        return transKey;
    }

    public PageTableEntry getNext() {
        return next;
    }
    
    public int getVpn() {
    	return transKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageTableEntry)) return false;
        PageTableEntry that = (PageTableEntry) o;
        return getKey() == that.getKey() &&
                getTransKey() == that.getTransKey() &&
                isDirty() == that.isDirty() &&
                Objects.equals(getNext(), that.getNext());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getTransKey(), isDirty(), getNext());
    }
}