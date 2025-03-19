import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.lang.Integer;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.print.attribute.standard.JobName;

public class TopPopularLinks extends Configured implements Tool {
    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new TopPopularLinks(), args);
        System.exit(res);
    }

    @Override
    public int run(String[] args) throws Exception {
        //TODO
        Configuration conf = this.getConf();
        FileSystem fs = FileSystem.get(conf);
        Path tmpPath = new Path("./tmp");
        fs.delete(tmpPath, true);

        Job jobA = Job.getInstance(this.getConf(),  "Link count");
        jobA.setOutputKeyClass(IntWritable.class);
        jobA.setOutputValueClass(IntWritable.class);

        jobA.setMapOutputKeyClass(IntWritable.class);
        jobA.setMapOutputValueClass(IntWritable.class);

        jobA.setMapperClass(LinkCountMap.class);
        jobA.setReducerClass(LinkCountReduce.class);

        FileInputFormat.setInputPaths(jobA, new Path(args[0]));
        
        FileOutputFormat.setOutputPath(jobA, tmpPath);
        
        jobA.setJarByClass(TopPopularLinks.class);

        //jobA.waitForCompletion(true) ? 0 : 1 ;
        jobA.waitForCompletion(true);
        

        Job jobB = Job.getInstance(this.getConf(), "Top Links");
        jobB.setOutputKeyClass(IntWritable.class);
        jobB.setOutputValueClass(IntWritable.class);

        jobB.setMapOutputKeyClass(NullWritable.class);
        jobB.setMapOutputValueClass(IntArrayWritable.class);

        jobB.setMapperClass(TopLinksMap.class);
        jobB.setReducerClass(TopLinksReduce.class);
        jobB.setNumReduceTasks(1);

        
        FileInputFormat.setInputPaths(jobB, tmpPath);
        FileOutputFormat.setOutputPath(jobB, new Path(args[1]));
        

        jobB.setInputFormatClass(KeyValueTextInputFormat.class);
        jobB.setOutputFormatClass(TextOutputFormat.class);

        jobB.setJarByClass(TopPopularLinks.class);
        return jobB.waitForCompletion(true) ? 0 : 1;
        


    }

    public static class IntArrayWritable extends ArrayWritable {
        public IntArrayWritable() {
            super(IntWritable.class);
        }

        public IntArrayWritable(Integer[] numbers) {
            super(IntWritable.class);
            IntWritable[] ints = new IntWritable[numbers.length];
            for (int i = 0; i < numbers.length; i++) {
                ints[i] = new IntWritable(numbers[i]);
            }
            set(ints);
        }
    }

    public static class LinkCountMap extends Mapper<Object, Text, IntWritable, IntWritable> {
        //TODO
        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            //TODO
            String line = value.toString();
            System.err.println("line:" + line);
            int colonIndex = line.indexOf(':');
            String id = line.substring(0, colonIndex);
            System.err.println("id:" + id);
            String links = line.substring(colonIndex + 1);
            // Emit each id as a key with value of zero
            context.write(new IntWritable(Integer.parseInt(id)), new IntWritable(Integer.valueOf(0)));

             // Emit each outgoing link as a key with value of 1
            StringTokenizer tokenizer = new StringTokenizer(links);
            while (tokenizer.hasMoreTokens()) {
                /* outKey.set(tokenizer.nextToken());
                outValue.set(id);
                context.write(outKey, outValue); */

                String linkedToken = tokenizer.nextToken();

                if( Integer.parseInt(linkedToken) != Integer.parseInt(id)) {
                    context.write(new IntWritable(Integer.parseInt(linkedToken)), new IntWritable(Integer.valueOf(1)));
                }
                
            }

          //context.write(<IntWritable>, <IntWritable>); // pass this output to reducer
        }
    }

    public static class LinkCountReduce extends Reducer<IntWritable, IntWritable, IntWritable, IntWritable> {
        //TODO
        @Override
        public void reduce(IntWritable key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;
            for(IntWritable value: values ) {
                sum += value.get();
            }
            System.err.println("Oraphan page reduce key:" + key + "number of values : "+sum);
            
                context.write(key, new IntWritable(sum));

            
        }
            
    }

    public static class TopLinksMap extends Mapper<Text, Text, NullWritable, IntArrayWritable> {

        @Override
        protected void setup(Context context) throws IOException,InterruptedException {
            Configuration conf = context.getConfiguration();
        }

       //TODO
       private TreeSet<Pair> pairTreeSet = new TreeSet<>();
        

       @Override
       public void map(Text key, Text value, Context context) throws IOException, InterruptedException {

            System.err.println("Inside the Toptitles map: key " + key.toString()+ " , value " + Integer.parseInt(value.toString()) );
            //pairTreeSet.add(new Pair<String, Integer>(key.toString(), Integer.parseInt(value.toString())));
            pairTreeSet.add(new Pair< Integer, Integer>(Integer.parseInt(value.toString()), Integer.parseInt(key.toString()) ));


       }

       @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            //TODO
            //Cleanup operation starts after all mappers are finished
            for (Pair pair: pairTreeSet.descendingSet()) {
                Integer[] fields = new Integer[2];
                fields[0] = Integer.parseInt(pair.first.toString());
                fields[1] = Integer.parseInt(pair.second.toString());
                System.err.println("Inside the Toptitles cleanup: key " + fields[1]+ " , value " + fields[0]  );
                IntArrayWritable value = new IntArrayWritable(fields);
                context.write(NullWritable.get(), value);

            }
        }

    }

    public static class TopLinksReduce extends Reducer<NullWritable, IntArrayWritable, IntWritable, IntWritable> {

        @Override
        protected void setup(Context context) throws IOException,InterruptedException {
            Configuration conf = context.getConfiguration();
        }
        //TODO
        private TreeSet<Pair> pairTreeSet = new TreeSet<>();

        @Override
        public void reduce(NullWritable key, Iterable<IntArrayWritable> values, Context context) throws IOException, InterruptedException {
            
            for(IntArrayWritable value: values) {
                
                IntWritable[] intWritableArray = (IntWritable[])value.toArray();
                
                Integer[] integerArray = new Integer[intWritableArray.length];

                for (int i = 0; i < intWritableArray.length; i++) {
                    integerArray[i] = intWritableArray[i].get();
                }

                Integer id = integerArray[1];
                Integer count = integerArray[0];
                System.err.println("Inside the Toptitles reduce: id " + id+ " , value " + count  );
                pairTreeSet.add(new Pair<Integer, Integer>( count, id));
                if(pairTreeSet.size() > 10) {
                    pairTreeSet.remove(pairTreeSet.first());
                }
                
            }
            
            
            /* for(IntArrayWritable value: values) {
                
                Writable[] writables = value.get();
                
                Integer[] fields = new Integer[writables.length];
                
                for (int i = 0; i < writables.length; i++) {
                    fields[i] = writables[i];
                }
                
                ArrayWritable[] ints = (ArrayWritable[])value.get();
                Integer[] fields = (Integer[])value.toArray();
                Integer id = fields[1];
                int count = Integer.parseInt(fields[0].toString());
                System.err.println("Inside the Toptitles reduce: id " + id+ " , value " + count  );
                pairTreeSet.add(new Pair<Integer, Integer>( count, id));
                if(pairTreeSet.size() > 10) {
                    pairTreeSet.remove(pairTreeSet.first());
                }
                
            } */
            Iterator<Pair> iterator = pairTreeSet.iterator();
            while(iterator.hasNext()) {
                Pair pair = iterator.next();
                Integer count = (Integer)pair.first;
                Integer id = (Integer)pair.second;
                System.err.println("Inside the Toptitles reduce descending set: key " + id+ " , value " + count  );
                context.write(new IntWritable(id), new IntWritable(count));
            }

        }


    }

}

class Pair<A extends Comparable<? super A>,
        B extends Comparable<? super B>>
        implements Comparable<Pair<A, B>> {

    public final A first;
    public final B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public static <A extends Comparable<? super A>,
            B extends Comparable<? super B>>
    Pair<A, B> of(A first, B second) {
        return new Pair<A, B>(first, second);
    }

    @Override
    public int compareTo(Pair<A, B> o) {
        int cmp = o == null ? 1 : (this.first).compareTo(o.first);
        return cmp == 0 ? (this.second).compareTo(o.second) : cmp;
    }

    @Override
    public int hashCode() {
        return 31 * hashcode(first) + hashcode(second);
    }

    private static int hashcode(Object o) {
        return o == null ? 0 : o.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Pair))
            return false;
        if (this == obj)
            return true;
        return equal(first, ((Pair<?, ?>) obj).first)
                && equal(second, ((Pair<?, ?>) obj).second);
    }

    private boolean equal(Object o1, Object o2) {
        return o1 == o2 || (o1 != null && o1.equals(o2));
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ')';
    }
}
