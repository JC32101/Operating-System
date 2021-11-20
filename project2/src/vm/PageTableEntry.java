package vm;

import java.util.Objects;

public class PageTableEntry {
    private int vpn;
    private int pfn;
    private boolean dirty;
    private PageTableEntry next; //in case of a hash collision, PTEs can be chained together

    PageTableEntry(int vpn, int pfn) {
        this.vpn = vpn;
        this.pfn = pfn;
        this.dirty = false;
    }

    public String toString() {
        String info = "VPN: " + vpn + " PFN: " + pfn + " Dirty: " + dirty + " Next's Info " + next;
        return info;
    }

    public void setKey(int key) {
        this.vpn = key;
    }

    public void setNext(PageTableEntry next) {
        this.next = next;
    }

    public void setValue(int value) {
        this.pfn = value;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public int getKey() {
        return vpn;
    }

    public boolean isDirty() {
        return dirty;
    }

    public int getValue() {
        return pfn;
    }

    public PageTableEntry getNext() {
        return next;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageTableEntry)) return false;
        PageTableEntry that = (PageTableEntry) o;
        return getKey() == that.getKey() &&
                getValue() == that.getValue() &&
                isDirty() == that.isDirty() &&
                Objects.equals(getNext(), that.getNext());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getValue(), isDirty(), getNext());
    }
}