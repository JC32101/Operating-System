package framework;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentKVStore{
    //TODO: your code here
    //Notes:
    // (1) each partition works like a bounded buffer between
    // mappers and a reducer. (you can assume size = 10 or 50)
    // (2) if reducer_i wants to fetch a KV pair it can
    // only fetches from partition_i, but mapper_i can drop messages
    // into different partitions.

    private int numBuckets = 10;
//    private ArrayList<KVPair>[] buckets;
    private Lock lock;
    private int count = 0;
    private KVPair head;

    private class KVPair{
        private Object key;
        private Object value;
        private KVPair next;
        private Lock lock;

        public KVPair(Object k, Object v){
            key = k;
            value = v;
            lock = new ReentrantLock();
            next = null;
        }

        public void setKey(Object key) {this.key = key;}
        public void setValue(Object value) {this.value = value;}
        public void setNext(KVPair next) {this.next = next;}
        public Object getKey() {return key;}
        public Object getValue() {return value;}
        public KVPair getNext() {return next;}
    }

    public ConcurrentKVStore(){
//        buckets = new ArrayList[numBuckets];
//        for(int i = 0; i < numBuckets; i++)
//            buckets[i] = new ArrayList<KVPair>();
        head = null;
        lock = new ReentrantLock();
    }

    public void put(Object k){
        this.lock.lock();
        KVPair p = new KVPair(k, 1);
        int bucket = Math.abs(k.hashCode() % numBuckets);
//        buckets[bucket].add(p);
        count++;

        if(head == null){
            head = p;
            this.lock.unlock();
        }
        else if(head.getNext() == null){
            head.setNext(p);
            this.lock.unlock();
        }
        else{
            head.lock.lock();
            this.lock.unlock();
            KVPair current = head.next;
            KVPair previous = head;

            if(current != null) current.lock.lock();
            while(current != null){
                previous.lock.unlock();
                previous = current;
                current = current.next;
                if(current != null) current.lock.lock();
            }
            previous.next = p;
            p.setNext(current);
            previous.lock.unlock();
            if(current!=null) current.lock.unlock();
        }

//        if((double)count/numBuckets >= 0.5){
//            rehash();
//        }
    }

//    public boolean contains(Object key) {
//        //TODO: your code here
//        int bucket = Math.abs(key.hashCode() % numBuckets);
//        ArrayList<KVPair> a = buckets[bucket];
//        for(KVPair p: a){
//            if(key.equals(p.key))
//                return true;
//        }
//       return false;
//        this.lock.lock();
//    }

//    private void rehash() {
//        //TODO: your code here
//        ArrayList<KVPair> [] oldBucket = buckets;
//        numBuckets*=2;
//        count = 0;
//        buckets = new ArrayList[numBuckets];
//
//        for (int i = 0; i < numBuckets; i++) {
//            buckets[i] = new ArrayList<KVPair>();
//        }
//        for(int i = 0; i < oldBucket.length; i++){
//            ArrayList<KVPair> a = oldBucket[i];
//            for(KVPair p: a){
//                int bucket = Math.abs(p.key.hashCode() % numBuckets);
//                buckets[bucket].add(p);
//                count++;
//            }
//        }
//    }

    public void remove(Object key) {
        //TODO: your code here
//        if(contains(key) == false)
//            throw new NoSuchElementException();
//        else{
//            int bucket = Math.abs(key.hashCode() % numBuckets);
//            ArrayList<KVPair> a = buckets[bucket];
//            for(KVPair p: a){
//                if(key.equals(p.key))
//                    buckets[bucket].remove(p);
//            }
            this.lock.lock();
            if(head.getKey() == key && head.getNext() == null){
                head = null;
                this.lock.unlock();
            }
//            else if(head.getKey() == key && head.getNext() != null){
//                head = head.getNext();
//                this.lock.unlock();
//            }
            else{
                head.lock.lock();
                this.lock.unlock();
                KVPair current = head.next;
                KVPair previous = head;

                if(current != null) current.lock.lock();
                while(current != null){
                    if(current.getKey() == key){
                        previous = current.getNext();
                        previous = current;
                        current = current.next;
                        if(current != null) current.lock.lock();
                    }
                    else{
                        previous.lock.unlock();
                        previous = current;
                        current = current.next;
                        if(current != null) current.lock.lock();
                    }
                }
                if(current.getKey() == key){
                    previous.setNext(null);
                    previous.lock.unlock();
                }
//            }
            count--;
        }
    }

    public int getNumBuckets () {
        return numBuckets;
    }
}
