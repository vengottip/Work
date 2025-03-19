import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.util.StringTokenizer;

public class OrphanPages extends Configured implements Tool {
    public static final Log LOG = LogFactory.getLog(OrphanPages.class);

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new OrphanPages(), args);
        System.exit(res);
    }

    @Override
    public int run(String[] args) throws Exception {
        Job job = Job.getInstance(this.getConf(), "Orphan Pages");
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(NullWritable.class);

        job.setMapOutputKeyClass(IntWritable.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setMapperClass(LinkCountMap.class);
        job.setReducerClass(OrphanPageReduce.class);

        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.setJarByClass(OrphanPages.class);
        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static class LinkCountMap extends Mapper<Object, Text, IntWritable, IntWritable> {
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

           /*  String restOfString = line.substring(line.indexOf(":")+1).trim();
            int countNumLinks =0;
            StringTokenizer st = new StringTokenizer(restOfString, " ");
            while (st.hasMoreTokens()) {
                String nt = st.nextToken();
                System.err.println("nt:" + nt);
                /* String substrNt = nt.substring(nt.indexOf(":")+1);
                System.err.println("substrNt:" + substrNt);
                StringTokenizer stDetails = new StringTokenizer(substrNt, " "); */
                //countNumLinks++;
                /* while (stDetails.hasMoreTokens()) {
                    
                    String stDetail = stDetails.nextToken();
                    System.err.println("stDetail:" + stDetail);
                } 
                //context.write(new IntWritable(Integer.parseInt(nt)), new IntWritable(countNumLinks));
                

            } */
            //context.write(<IntWritable>, <IntWritable>); // pass this output to reducer
        }
    }

    public static class OrphanPageReduce extends Reducer<IntWritable, IntWritable, IntWritable, NullWritable> {
        @Override
        public void reduce(IntWritable key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            //TODO
            int sum = 0;
            for(IntWritable value: values ) {
                sum += value.get();
            }
            System.err.println("Oraphan page reduce key:" + key + "number of values : "+sum);
            if(sum == 0) {
                context.write(key, NullWritable.get());
            }
            
            //context.write(<IntWritable>, <NullWritable>); // print as final output
        }
    }
}
