import java.io.*;
import java.util.*;

public class CoorInvertIndex {

    HashMap<String, HashMap<Integer,ArrayList<Integer>>> index;
    private int position;
    private int files_num;

    public CoorInvertIndex(String folder){
        index = new HashMap();
        files_num=0;

        File dir = new File(folder);
        File[] files = dir.listFiles();

        int doc=-1;
        for (File file : files) {
            doc++;
            files_num++;
            position=-1;
            if(file.isFile()) {
                BufferedReader br = null;
                String line;
                try {
                    br = new BufferedReader(new FileReader(file));
                    while ((line = br.readLine()) != null) {
                        addLine(doc,line);
                    }
                    } catch (IOException e) {
                e.printStackTrace();
            }

                finally {
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    //splits a line into words and calls addWords methods
    private void addLine(int doc, String line){
        if(line.equals("")) return;
        String[] temp = line.split("[^a-zA-Z0-9_]+");
        for(int i=0;i<temp.length;i++){
            if(temp[i].matches("[a-zA-Z0-9_]+")) {
                position++;
                addWord(doc,temp[i].toLowerCase());
            }
        }
    }

    //adds words to hashmap
    private void addWord(int doc, String word) {
        if(!index.containsKey(word)){
            ArrayList<Integer> pos = new ArrayList();
            pos.add(position);
            HashMap<Integer,ArrayList<Integer>> IDs = new HashMap();
            IDs.put(doc,pos);

            index.put(word,IDs);
        }
        else if(!index.get(word).containsKey(doc)){
            ArrayList<Integer> pos = new ArrayList();
            pos.add(position);

            index.get(word).put(doc,pos);
        }
        else index.get(word).get(doc).add(position);
    }

    public HashMap<Integer,ArrayList<Integer>> search(String input) throws Exception {
        input = input.toLowerCase();
        String input_test = input.replaceAll("\\s+","");

        if (!input_test.matches("[\\w]+(((/)[0-9]+)?[\\w]+)*"))
            throw new Exception("Incorrect format.");

        String[] temp = input.split("[\\s]+");

        HashMap<Integer,ArrayList<Integer>> res = new HashMap();
        res=index.get(temp[0]);
        if (res==null) res = new HashMap();

        int i=1;
        while(i<temp.length){
            int dist = 1;
            if(temp[i].matches("(/)[0-9]+")) {
                temp[i] = temp[i].replaceAll("/","");
                dist = Integer.valueOf(temp[i]);
                i++;
            }
            HashMap<Integer,ArrayList<Integer>> temp_hash = new HashMap();


            temp_hash = index.get(temp[i]);
            if(temp_hash==null) temp_hash=new HashMap();

            res = intersect(dist,res,temp_hash);
            i++;
        }
        return res;
    }

    public HashMap<Integer,ArrayList<Integer>> intersect(int dist, HashMap<Integer,
            ArrayList<Integer>> first, HashMap<Integer,ArrayList<Integer>> second){

        HashMap<Integer,ArrayList<Integer>> res = new HashMap();
        for(int i=0; i<files_num;i++){
            ArrayList<Integer> res_list = new ArrayList();
            res.put(i,res_list);
            if(first.containsKey(i) && second.containsKey(i)){
                for(int j : first.get(i)){
                    for(int a=1;a<=dist;a++) {
                        int t =j+a;
                        if (second.get(i).contains(t)) {
                            res.get(i).add(j + dist);
                        }
                    }
                }
            }
            if(res.get(i).isEmpty()) res.remove(i);
        }
        return res;
    }

    public void print() {
        OutputStream out = new BufferedOutputStream(System.out);
        for(String s: index.keySet()){
            try {
                out.write(( "\n"+s + ":").getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Set entrySet = index.get(s).entrySet();
        Iterator it = entrySet.iterator();
        while (it.hasNext()) {
            Map.Entry me = (Map.Entry) it.next();
            try {
                out.write(("\n"+me.getKey() + ":"+me.getValue()+";").getBytes());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
        try {
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
