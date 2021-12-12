package utils;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/* Reference: https://www.java67.com/2021/07/how-to-implement-thread-safe-bounded-buffer-in-java.html */

public class BoundedBuffer <T> {
//Please note the biggest difference between this BoundBuffer
//and the one we demoed in class is <T>
//implement member functions: deposit() and fetch()

    private final Object[] buffer;
    private int capacity;
    private int count;
    private int front;
    private int rear;
    private Lock lock = new ReentrantLock();
    private Condition notFull = lock.newCondition();
    private Condition notEmpty = lock.newCondition();

    public BoundedBuffer(int size){
        buffer = new Object[size];
        capacity = size;
    }

    public  void deposit(T obj) throws InterruptedException{
        lock.lock();
        while (count == capacity){
            notFull.await();
        }
        buffer[rear] = obj;
        rear = (rear+1)%capacity;
        count++;
        notEmpty.signal();
        lock.unlock();
    }

    public T fetch() throws InterruptedException {
        lock.lock();
        while (count == 0)
            notEmpty.await();
        T temp = (T) buffer[front];
        front = (front + 1) % capacity;
        count--;
        notFull.signal();
        lock.unlock();
        return temp;
    }
}
