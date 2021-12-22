package framework;

public class KVPair {
    String key;
    Object value;
    KVPair next;

    KVPair(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    public KVPair getNext() {
        return next;
    }

    public void setNext(KVPair p) {
        next = p;
    }

    public boolean hasNext() {
        if (next == null) {
            return false;
        } else {
            return true;
        }
    }

}
