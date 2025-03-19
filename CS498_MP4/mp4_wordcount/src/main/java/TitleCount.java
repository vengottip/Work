import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class TitleCount extends Configured implements Tool {
    public static final Log LOG = LogFactory.getLog(TitleCount.class);

    public static void main(String[] args) throws Exception {
        System.err.println("in the main method args are "+ args);
        int res = ToolRunner.run(new Configuration(), new TitleCount(), args);
        System.exit(res);
    }

    @Override
    public int run(String[] args) throws Exception {
        System.err.println("in the run method args are "+ args);
        Job job = Job.getInstance(this.getConf(), "Title Count");
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setMapperClass(TitleCountMap.class);
        job.setReducerClass(TitleCountReduce.class);
        System.err.println("args[0] "+ args[1]);
        System.err.println("args[1] "+args[2]);
        FileInputFormat.setInputPaths(job, new Path(args[1]));
        FileOutputFormat.setOutputPath(job, new Path(args[2]));

        job.setJarByClass(TitleCount.class);
        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static String readHDFSFile(String path, Configuration conf) throws IOException{
        System.err.println("in the readHDFSFile method path is "+ path);
        System.err.println("in the readHDFSFile method conf is "+ conf.toString());
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

    public static class TitleCountMap extends Mapper<Object, Text, Text, IntWritable> {
        List<String> stopWords;
        String delimiters;

        @Override
        protected void setup(Context context) throws IOException,InterruptedException {
            System.err.println("in the map-setup method context is "+ context.toString());
            Configuration conf = context.getConfiguration();
            System.err.println("in the map-setup method conf is "+ conf.toString());
            String stopWordsPath = conf.get("stopwords");
            String delimitersPath = conf.get("delimiters");

            this.stopWords = Arrays.asList(readHDFSFile(stopWordsPath, conf).split("\n"));
            this.delimiters = readHDFSFile(delimitersPath, conf);
        }


        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            //TODO
            System.err.println("in the map-map method key is "+ key.toString());
            System.err.println("in the map-map method value is "+ value.toString());
            System.err.println("in the map-map method context is "+ context.toString());
            String line = value.toString();
            StringTokenizer st = new StringTokenizer(line, delimiters);
            while(st.hasMoreTokens()) {
                String nt = st.nextToken(line);
                if (!stopWords.contains(nt.trim().toLowerCase())) {
                    context.write(new Text(nt), new IntWritable(1));
                }
            }
            //context.write(<Text>, <IntWritable>); // pass this output to reducer
        }
    }

    public static class TitleCountReduce extends Reducer<Text, IntWritable, Text, IntWritable> {
        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            //TODO
            System.err.println("in the map-reduce method key is "+ key.toString());
           // System.err.println("in the map-reduce method value is "+ key.toString());
            int sum = 0;
            for(IntWritable val: values) {
                sum += val.get();
            }
            context.write(key, new IntWritable(sum));
            //context.write(<Text>, <NullWritable>); // print as final output
        }
    }
}
