import org.apache.commons.lang3.ObjectUtils.Null;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class TopTitles extends Configured implements Tool {

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new TopTitles(), args);
        System.exit(res);
    }

    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = this.getConf();
        FileSystem fs = FileSystem.get(conf);
        Path tmpPath = new Path("./tmp");
        fs.delete(tmpPath, true);

        Job jobA = Job.getInstance(conf, "Title Count");
        jobA.setOutputKeyClass(Text.class);
        jobA.setOutputValueClass(IntWritable.class);

        jobA.setMapperClass(TitleCountMap.class);
        jobA.setReducerClass(TitleCountReduce.class);

        FileInputFormat.setInputPaths(jobA, new Path(args[0]));
        FileOutputFormat.setOutputPath(jobA, tmpPath);

        jobA.setJarByClass(TopTitles.class);
        jobA.waitForCompletion(true);

        Job jobB = Job.getInstance(conf, "Top Titles");
        jobB.setOutputKeyClass(Text.class);
        jobB.setOutputValueClass(IntWritable.class);

        jobB.setMapOutputKeyClass(NullWritable.class);
        jobB.setMapOutputValueClass(TextArrayWritable.class);

        jobB.setMapperClass(TopTitlesMap.class);
        jobB.setReducerClass(TopTitlesReduce.class);
        jobB.setNumReduceTasks(1);

        FileInputFormat.setInputPaths(jobB, tmpPath);
        FileOutputFormat.setOutputPath(jobB, new Path(args[1]));

        jobB.setInputFormatClass(KeyValueTextInputFormat.class);
        jobB.setOutputFormatClass(TextOutputFormat.class);

        jobB.setJarByClass(TopTitles.class);
        return jobB.waitForCompletion(true) ? 0 : 1;
    }

    public static String readHDFSFile(String path, Configuration conf) throws IOException{
        Path pt=new Path(path);
        FileSystem fs = FileSystem.get(pt.toUri(), conf);
        FSDataInputStream file = fs.open(pt);
        BufferedReader buffIn=new BufferedReader(new InputStreamReader(file));

        StringBuilder everything = new StringBuilder();
        String line;
        while( (line = buffIn.readLine()) != null) {
            everything.append(line);
            everything.append("\n");
        }
        return everything.toString();
    }

    public static class TextArrayWritable extends ArrayWritable {
        public TextArrayWritable() {
            super(Text.class);
        }

        public TextArrayWritable(String[] strings) {
            super(Text.class);
            Text[] texts = new Text[strings.length];
            for (int i = 0; i < strings.length; i++) {
                texts[i] = new Text(strings[i]);
            }
            set(texts);
        }
    }

    public static class TitleCountMap extends Mapper<Object, Text, Text, IntWritable> {
        List<String> stopWords;
        String delimiters;

        @Override
        protected void setup(Context context) throws IOException,InterruptedException {

            Configuration conf = context.getConfiguration();

            String stopWordsPath = conf.get("stopwords");
            String delimitersPath = conf.get("delimiters");

            this.stopWords = Arrays.asList(readHDFSFile(stopWordsPath, conf).split("\n"));
            this.delimiters = readHDFSFile(delimitersPath, conf);
        }


        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            //TODO
            String line = value.toString();
            System.err.println(" line in map method of TitleCountMap "+line);
            StringTokenizer st = new StringTokenizer(line, delimiters);
            while(st.hasMoreTokens()) {
                String nt = st.nextToken();
                if (!stopWords.contains(nt.trim().toLowerCase())) {
                    context.write(new Text(nt.trim().toLowerCase()), new IntWritable(1));
                }
            }
            //context.write(<Text>, <IntWritable>); // pass this output to reducer
        }
    }

    public static class TitleCountReduce extends Reducer<Text, IntWritable, Text, IntWritable> {
        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            //TODO
            int sum = 0;
            for(IntWritable val: values) {
                sum += val.get();
            }
            System.err.println(" line in reduce method of TitleCountReduce: key is:  "+ key + ", sum is: "+ sum );
            context.write(key, new IntWritable(sum));
            //context.write(<Text>, <IntWritable>); // pass this output to TopTitlesMap mapper
        }
    }

    public static class TopTitlesMap extends Mapper<Text, Text, NullWritable, TextArrayWritable> {
        //TODO
        private TreeSet<Pair> pairTreeSet ;

        @Override
        protected void setup(Context context) throws IOException,InterruptedException {
            Configuration conf = context.getConfiguration();
            pairTreeSet = new TreeSet<>();
        }

        @Override
        public void map(Text key, Text value, Context context) throws IOException, InterruptedException {
            //TODO
            /* Set<Map.Entry<String, Integer>> set = new TreeSet<>(new Comparator<Map.Entry<String, Integer>>() {
                @Override
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    int valueCompare = Integer.compare(o1.getValue(), o2.getValue());
                    if (valueCompare != 0) {
                        return valueCompare;
                    } else {
                        return o1.getKey().compareTo(o2.getKey());
                    }
                }
            }); */
            //Pair pair = new Pair(key.toString(), Integer.parseInt(value.toString()));
            System.err.println("Inside the Toptitles map: key " + key.toString()+ " , value " + Integer.parseInt(value.toString()) );
            //pairTreeSet.add(new Pair<String, Integer>(key.toString(), Integer.parseInt(value.toString())));
            pairTreeSet.add(new Pair< Integer, String>(Integer.parseInt(value.toString()), key.toString() ));

            /* if(pairTreeSet.size() > 10) {
                pairTreeSet.remove(pairTreeSet.first());
            }*/
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            //TODO
            //Cleanup operation starts after all mappers are finished
            for (Pair pair: pairTreeSet.descendingSet()) {
                String[] fields = new String[2];
                fields[0] = pair.first.toString();
                fields[1] = pair.second.toString();
                System.err.println("Inside the Toptitles cleanup: key " + fields[1]+ " , value " + fields[0]  );
                TextArrayWritable value = new TextArrayWritable(fields);
                context.write(NullWritable.get(), value);

            }
        

            //context.write(null, null);
            //context.write(<NullWritable>, <TextArrayWritable>); // pass this output to reducer
        }
    }

    public static class TopTitlesReduce extends Reducer<NullWritable, TextArrayWritable, Text, IntWritable> {
        // TODO
        private TreeSet<Pair> pairTreeSet;

        @Override
        protected void setup(Context context) throws IOException,InterruptedException {
            Configuration conf = context.getConfiguration();
            pairTreeSet = new TreeSet<>();
        }

        @Override
        public void reduce(NullWritable key, Iterable<TextArrayWritable> values, Context context) throws IOException, InterruptedException {
            //TODO
            for(TextArrayWritable value: values) {
                Text[] fields = (Text[])value.toArray();
                String title = fields[1].toString();
                int count = Integer.parseInt(fields[0].toString());
                System.err.println("Inside the Toptitles reduce: key " + title+ " , value " + count  );
                pairTreeSet.add(new Pair<Integer, String>( count, title));
                if(pairTreeSet.size() > 10) {
                    pairTreeSet.remove(pairTreeSet.first());
                }
                
            }
            Iterator<Pair> iterator = pairTreeSet.iterator();
            while(iterator.hasNext()) {
                Pair pair = iterator.next();
                int count = (int)pair.first;
                String title = pair.second.toString();
                System.err.println("Inside the Toptitles reduce descending set: key " + title+ " , value " + count  );
                context.write(new Text(title), new IntWritable(count));
            }

            /* for(Pair pair: pairTreeSet.descendingSet()){
                int count = (int)pair.first;
                String title = pair.second.toString();
                System.err.println("Inside the Toptitles reduce descending set: key " + title+ " , value " + count  );
                context.write(new Text(title), new IntWritable(count));
            } */
            //context.write(<Text>, <IntWritable>); // print as final output
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
