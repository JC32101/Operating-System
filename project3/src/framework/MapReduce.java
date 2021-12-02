package framework;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class MapReduce {
    // for final output
    protected PrintWriter[] pw;
    protected StopWatch stopWatch;
    protected Logger LOGGER;

    abstract void MREmit(Object key, Object value);

    public Object MRGetNext(Object key, int partition_number) {
        throw new UnsupportedOperationException();
    }

    public int MRRun(String inputFileName,
                     MapperReducerClientAPI mapperReducerObj,
                     int num_mappers,
                     int num_reducers)
    {
        assert(num_mappers == num_reducers);
        setup(num_mappers, inputFileName);
        MRRunHelper(inputFileName, mapperReducerObj,num_mappers, num_reducers);
        return teardown(num_mappers, inputFileName);
    }

    abstract protected void MRRunHelper(String inputFileName,
                                        MapperReducerClientAPI mapperReducerObj,
                                        int num_mappers,
                                        int num_reducers) ;

    public void MRPostProcess(String key, int value, int partNum) {
        //The following is only IO for post-process
        pw[partNum].printf("%s:%d\n", (String)key, value);
       
    }
    protected void setup (int nSplits, String inputFile) {
        try {
            LOGGER = Logger.getLogger(MyMapReduce.class.getName());
            //split input into as many as nSplits files.
            //If split.sh doesn't work for you because you are developing on a windows PC, you can comment the lines underneath
            //to skip spliting process, copy pre-split data to res/ and then proceed.
            Process p = Runtime.getRuntime().exec(new String[] { "/bin/bash" , "-c", "./res/split.sh "+inputFile +" " +nSplits});
            p.waitFor();
            int exitVal = p.exitValue();
            assert(exitVal == 0);
            pw = new PrintWriter[nSplits];
            for(int i=0; i<nSplits;i++) {
                pw[i]=new PrintWriter(new FileWriter("res/out_0"+i));
            }
            LOGGER.log(Level.INFO, "MapReduce setup completed");
            stopWatch= new StopWatch();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.toString());
        }
    }
    protected int teardown(int nMappers, String inputFile) {
        stopWatch.pause();
        LOGGER.log(Level.INFO, "Total runtime of your maps and reduces:" + stopWatch.getElapsedTime() + " secs");
        for (int i=0; i<nMappers; i++)
            pw[i].close();
        try {
            //Again, if you are developing on a windows PC, you need to find a line comparasion tool to compare
            //the expected output with your output manually.
            Process p = Runtime.getRuntime().exec(new String[] { "/bin/sh" , "-c", "./res/test.sh "+inputFile});
            p.waitFor();
            int exitVal = p.exitValue();
            if(exitVal == 0) {
                LOGGER.log(Level.INFO, "PASSED");
            } else {
                LOGGER.log(Level.INFO, "FAILED, process exit value = {0}", exitVal);
            }
            return exitVal;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.toString());
            return -1;
        }


    }

    protected class StopWatch{
        private long lastStartTime;
        private long endTime;
        private long cum;
        public StopWatch() {
            cum = 0;
            lastStartTime = System.currentTimeMillis();
            endTime = 0;
        }
        public void pause() {
            endTime = System.currentTimeMillis();
            cum +=  (double) (endTime - lastStartTime) / (1000);
        }
        public void resume() {
            endTime = 0;
            lastStartTime = System.currentTimeMillis();
        }
        public double getElapsedTime() {
            return cum;
        }
        public double getEndTime() {
            return endTime;
        }
        public double getLastStartTime() {
            return lastStartTime;
        }
    }
}
