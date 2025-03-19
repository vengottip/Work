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
import java.util.*;

public class PopularityLeague extends Configured implements Tool {

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new PopularityLeague(), args);
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
        
        jobA.setJarByClass(PopularityLeague.class);

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

        jobB.setJarByClass(PopularityLeague.class);
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
    

    //TODO
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
            //System.err.println("Oraphan page reduce key:" + key + "number of values : "+sum);
            
                context.write(key, new IntWritable(sum));

            
        }
    }
        public static class TopLinksMap extends Mapper<Text, Text, NullWritable, IntArrayWritable> {
            List<String> league;
            @Override
            protected void setup(Context context) throws IOException,InterruptedException {

                Configuration conf = context.getConfiguration();

                String leaguePath = conf.get("league");
                //String delimitersPath = conf.get("delimiters");

                this.league = Arrays.asList(readHDFSFile(leaguePath, conf).split("\n"));
                System.err.println("league values inside setup:" + league);
                //this.delimiters = readHDFSFile(delimitersPath, conf);
            }
    
           //TODO
           private TreeSet<Pair> pairTreeSet = new TreeSet<>();
           
            
    
           @Override
           public void map(Text key, Text value, Context context) throws IOException, InterruptedException {
    
                //System.err.println("Inside the Toptitles map: key " + key.toString()+ " , value " + Integer.parseInt(value.toString()) );
                //pairTreeSet.add(new Pair<String, Integer>(key.toString(), Integer.parseInt(value.toString())));
                if(league.contains(key.toString())) {
                    System.err.println("Inside the Toptitles map:matched key " + key.toString()+ " , value " + Integer.parseInt(value.toString()) );
                    pairTreeSet.add(new Pair< Integer, Integer>(Integer.parseInt(value.toString()), Integer.parseInt(key.toString()) ));
                    //keyValueHashMap.put(Integer.parseInt(value.toString()), Integer.parseInt(key.toString()));
                }
                
    
    
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
                /* for(Map.Entry<Integer, Integer> entry: keyValueHashMap.entrySet()) {
                    Integer[] fields = new Integer[2];
                     fields[0] = entry.getKey();
                     fields[1] = entry.getValue();
                    System.err.println("Inside the TopTitles cleanup: Key "+fields[0] + " , value : "+fields[1]);
                    IntArrayWritable cleanupValue = new IntArrayWritable(fields);
                    context.write(NullWritable.get(), cleanupValue);


                } */


            }
    
        }
        public static class TopLinksReduce extends Reducer<NullWritable, IntArrayWritable, IntWritable, IntWritable> {

            @Override
            protected void setup(Context context) throws IOException,InterruptedException {
                Configuration conf = context.getConfiguration();
            }
            //TODO
            private TreeSet<Pair> pairTreeSet = new TreeSet<>();
            private TreeSet<Pair> pairTreeSetOrderByCount = new TreeSet<>();
            private LinkedHashMap<Integer, Integer> keyValueHashMap = new LinkedHashMap<Integer, Integer>();
            List<String> league;
    
            @Override
            public void reduce(NullWritable key, Iterable<IntArrayWritable> values, Context context) throws IOException, InterruptedException {
                
                for(IntArrayWritable value: values) {
                    
                    IntWritable[] intWritableArray = (IntWritable[])value.toArray();
                    
                    Integer[] integerArray = new Integer[intWritableArray.length];
    
                    for (int i = 0; i < intWritableArray.length; i++) {
                        integerArray[i] = intWritableArray[i].get();
                    }
    
                    Integer count = integerArray[0];
                    Integer id = integerArray[1];
                    System.err.println("Inside the Toptitles reduce: id " + id+ " , value " + count  );
                    pairTreeSet.add(new Pair<Integer, Integer>( count, id));
                    pairTreeSetOrderByCount.add(new Pair<Integer, Integer>(id, count));
                    keyValueHashMap.put(id, count);
                    /* if(pairTreeSet.size() > 10) {
                        pairTreeSet.remove(pairTreeSet.first());
                    } */
                    
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
                Iterator<Pair> sortediterator = pairTreeSet.descendingIterator();
                int rank = pairTreeSet.size();
                int previousCount = -999;
                int skipCount = 0;
                List<Pair> idsToUpdate = new ArrayList<>();
                List<Integer> skipIds = new ArrayList<>();
                Map<Integer, Integer> rankMap = new HashMap<>();
                Integer id = -1;
                while(sortediterator.hasNext()) {
                    Pair pair = sortediterator.next();
                    id = (Integer)pair.second;
                    Integer count = (Integer)pair.first;
                    System.err.println("Inside the Toptitles reduce descending set: key " + id+ " , value " + count +" , rank" +rank + " , previouseCount "+previousCount);
                    //idsToUpdate.add(id);
                    if (!sortediterator.hasNext()) {
                        //rank =  rank -1-skipCount;
                        rankMap.put(id, rank);
                        idsToUpdate.add(pair);
                    }
                                        
                    if(previousCount >=0 && count != previousCount){
                        rank =  rank -1-skipCount;
                        for (int i = 0; i < idsToUpdate.size(); i++) {
                            Pair idValuePair = idsToUpdate.get(i);
                            id = (Integer)idValuePair.second;
                            //count = (Integer)idValuePair.first;
                            
                            System.err.println("Inside the Toptitles reduce descending for loop: key " + id + " , value " + count +" , rank" +rank + " , skipcount: "+skipCount );
                            rankMap.put(id, rank);
                        }
                        /* for(Integer idToUpdate: idsToUpdate) {
                            rank =  rank -1-skipCount;
                            System.err.println("Inside the Toptitles reduce descending for loop: key " + id+ " , value " + count +" , rank" +rank + " , skipcount: "+skipCount );
                            rankMap.put(idToUpdate, rank);
                            
                        } */
                        idsToUpdate.clear();
                        idsToUpdate.add(pair);
                        skipCount = 0;
                       
                    } else {
                        idsToUpdate.add(pair);
                        skipIds.add(id);
                        if(previousCount >=0){
                            skipCount++;
                        }
                        
                        System.err.println("Inside the Toptitles reduce descending when skipCount is greater than zero: key " + id+ " , value " + count +" , rank" +rank + " , skipcount: "+skipCount );
                    }
                    
                     previousCount = count;
                    //context.write(new IntWritable(id), new IntWritable(rank++));
                }
                //Enter the rank for the last one
                System.err.println("Inside the Toptitles reduce last key " + id+ " , rank" +rank + " , skipcount: "+skipCount );
                rank =  rank -1-skipCount;
                rankMap.put(id, rank);
                for (Integer key1 : rankMap.keySet()) {
                    System.out.println(key1 + " The hashmap content: " + rankMap.get(key1));
                }
                
                //Iterator<Pair> iterator = pairTreeSet.iterator();
                //Configuration conf = context.getConfiguration();

                //String leaguePath = conf.get("league");
                //String delimitersPath = conf.get("delimiters");

                //this.league = Arrays.asList(readHDFSFile(leaguePath, conf).split("\n"));
                //System.err.println("league values inside setup:" + league);

                /* for(String leagueStr : league){
                    Integer leagueInt = Integer.parseInt(leagueStr);
                    System.err.println("leagueInt values inside setup:" + leagueInt);
                    if(rankMap.containsKey(leagueInt)){
                        context.write(new IntWritable(leagueInt), new IntWritable(rankMap.get(leagueInt)));
                    }
                    

                } */
                // create an iterator for the HashMap
                /* Iterator<Map.Entry<Integer, Integer>> it = keyValueHashMap.entrySet().iterator();
                // iterate over the entries using the iterator
                while (it.hasNext()) {
                    Map.Entry<Integer, Integer> entry = it.next();
                    System.err.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                    context.write(new IntWritable(entry.getKey() ), new IntWritable(rankMap.get(entry.getKey() )));

                } */
                Iterator<Pair> sortedByIditerator = pairTreeSetOrderByCount.descendingIterator();
                while(sortedByIditerator.hasNext()) {
                    Pair pair = sortedByIditerator.next();
                    id = (Integer)pair.first;
                    //count = (Integer)pair.second;
                    System.err.println("Inside the Toptitles reduce set: key " + id+ " , value " + rankMap.get(id)  );
                    //rankMap.put(id, rank++)
                    context.write(new IntWritable(id), new IntWritable(rankMap.get(id)));
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