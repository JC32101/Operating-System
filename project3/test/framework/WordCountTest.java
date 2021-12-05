package framework;
import java.io.*;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class WordCountTest {
    private int[] stats ;
    private class WordCount extends MapperReducerClientAPI {
        private MapReduce myMapReduce;
        public WordCount(MapReduce myMapReduce) {
            this.myMapReduce = myMapReduce;
        }

        public void Map(Object inputSource) {
            String fileName = (String) inputSource;
            try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
                String token;
                while ((token = br.readLine()) != null) {
                    myMapReduce.MREmit(token, "1");
                }
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void Reduce(Object key, int partition_number) {
            int count = 0;
            while ((myMapReduce.MRGetNext(key, partition_number)) != null) {
                count++;
                stats[partition_number] ++;
            }
            myMapReduce.MRPostProcess((String) key, count, partition_number);
        }
    }
    @Test
    public void test1_small() {
        WordCount wordCountInstance = new WordCount(new MyMapReduce());
        this.stats = new int[2];
        int ret = wordCountInstance.myMapReduce.MRRun
                ("res/small", wordCountInstance, 2, 2);
        assertEquals(stats[0], 4);
        assertEquals(stats[1], 5);
        assertEquals(0, ret);
        //Code review questions:
        //q1: How many partitions does this test set up?
        
        //This test sets up 2 partitions. This is evident from the fact that 2 reducer threads are
        //created (4th value passed in when MRRun is called) and the number of partitions must be
        //equal to the number of reducers.
        
        //q2: Based on the Partitioner function in MapperReducerClientAPI, what should be stored in partition 0
        //and what should be stored in partition 1? (hint: you can write a little main() test func in MapperReducerClientAPI)
        
        //Partition 0 should hold (bar, woof, fox, boss) and partition 1 
        //should hold (foo, foo, foo, road, road).
        
        //q3: What are the key value pairs stored in the concurrent KV stores by reducers when the reduce() is called for the first time? Please use the format of 
        //(key,{val_1,val_2,..val_n})
        
        //(bar,{1})
        //(boss,{1})
        //(foo,{1,1,1})
        //(fox,{1})
        //(road,{1,1}
        //(woof,{1})
        
        //q4: For key "foo", how many times does MRGetNext get invoked?
        
        //For "foo", MRGetNext will be invoked 4 times; 3 times to get all 3 "foo"s,
        //and 1 more time to get null (causing the while loop to exit).
        
    }
    @Test
    public  void test2_large_single() {
        WordCount wordCountInstance = new WordCount(new MyMapReduce());
        this.stats = new int[1];
        int ret = wordCountInstance.myMapReduce.MRRun
                ("res/cybersla", wordCountInstance, 1, 1);
        assertEquals(0, ret);
    }

    @Test
    public  void test2_large_multiple() {
        WordCount wordCountInstance = new WordCount(new MyMapReduce());
        this.stats = new int[8];
        int ret = wordCountInstance.myMapReduce.MRRun
                ("res/cybersla", wordCountInstance, 8, 8);
        assertEquals(0, ret);
    }
}
