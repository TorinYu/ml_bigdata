import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;


public class NBTrain {
	
	private Hashtable<String, Long> labelRecord;
	private int totalLabelRecord;
	private Hashtable<String, Long> wordLabelRecord;
	private Hashtable<String, Long> labelTotalWord;
	
	public NBTrain () {
		labelRecord = new Hashtable<String, Long>();
		totalLabelRecord = 0;
		wordLabelRecord = new Hashtable<String, Long>();
		labelTotalWord = new Hashtable<String, Long>();
	}
	
	static Vector<String> tokenizeDoc(String cur_doc) {
        String[] words = cur_doc.split("\\s+");
        Vector<String> tokens = new Vector<String>();
        for (int i = 0; i < words.length; i++) {
        	words[i] = words[i].replaceAll("\\W", "");
        	if (words[i].length() > 0) {
        		tokens.add(words[i]);
        	}
        }
        return tokens;
	}
	
	
	public static void main(String[] argv) throws FileNotFoundException, UnsupportedEncodingException {
		NBTrain nbTrain = new NBTrain();
		nbTrain.labelRecord.put("Y=CCAT", (long)0);
		nbTrain.labelRecord.put("Y=ECAT", (long)0);
		nbTrain.labelRecord.put("Y=GCAT", (long)0);
		nbTrain.labelRecord.put("Y=MCAT", (long)0);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		String line;
		try {
			while ( (line = in.readLine()) != null) {
				String[] tempStrings = line.split("\t");
				String lables = tempStrings[0];
				String words = tempStrings[1];
				String[] label = lables.split(",");
				ArrayList<String> remainLabels = new ArrayList<>();
				for (String a : label) {
					switch (a) {
					case "CCAT":
						nbTrain.labelRecord.put("Y=CCAT", nbTrain.labelRecord.get("Y=CCAT") + 1);
						remainLabels.add("CCAT");
						break;
					case "ECAT":
						nbTrain.labelRecord.put("Y=ECAT", nbTrain.labelRecord.get("Y=ECAT") + 1);
						remainLabels.add("ECAT");
						break;
					case "GCAT":
						nbTrain.labelRecord.put("Y=GCAT", nbTrain.labelRecord.get("Y=GCAT") + 1);
						remainLabels.add("GCAT");
						break;
					case "MCAT":
						nbTrain.labelRecord.put("Y=MCAT", nbTrain.labelRecord.get("Y=MCAT") + 1);
						remainLabels.add("MCAT");
						break;
					default:
						break;
					}
				}
				nbTrain.totalLabelRecord += remainLabels.size();
				Vector<String> wordList = tokenizeDoc(words);				
				for (int i = 0; i < remainLabels.size(); i++) {
					String curlable = remainLabels.get(i);
					if (!nbTrain.labelTotalWord.containsKey("Y=" + curlable + ",W=*"))
						nbTrain.labelTotalWord.put("Y=" + curlable + ",W=*", (long)wordList.capacity());
					else
					    nbTrain.labelTotalWord.put("Y=" + curlable + ",W=*", nbTrain.labelTotalWord.get("Y=" + curlable + ",W=*") + wordList.capacity());
					for (String a : wordList) {
						if (!nbTrain.wordLabelRecord.containsKey("Y=" + curlable +",W=" + a))
							nbTrain.wordLabelRecord.put("Y=" + curlable +",W=" + a, (long)1);
						else 
							nbTrain.wordLabelRecord.put("Y=" + curlable +",W=" + a, nbTrain.wordLabelRecord.get("Y=" + curlable +",W=" + a) + 1);
					}
				}
				
					
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Set<String> outItem = nbTrain.labelRecord.keySet();
		for (String a : outItem) {
			System.out.println(a + "\t" + nbTrain.labelRecord.get(a));
		}
		    System.out.println("Y=*" + "\t" + nbTrain.totalLabelRecord);
		
		outItem = nbTrain.wordLabelRecord.keySet();
		for (String a : outItem )
			System.out.println(a + "\t" + nbTrain.wordLabelRecord.get(a));
		
		outItem = nbTrain.labelTotalWord.keySet();
		for (String a : outItem) 
			System.out.println(a + "\t" + nbTrain.labelTotalWord.get(a));

	}
}
