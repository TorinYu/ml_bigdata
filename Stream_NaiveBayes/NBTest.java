import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Vector;


public class NBTest {
	
	private Hashtable<String, Long> labelRecord;
	private long totalLabelRecord;
	private Hashtable<String, Long> wordLabelRecord;
	private Hashtable<String, Long> labelTotalWord;
	
	public NBTest () {
		labelRecord = new Hashtable<String, Long>();
		totalLabelRecord = (long)0;
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
	
	
	public static void main(String[] argv) throws IOException {
		NBTest nbTest = new NBTest();
	
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		
		try {
			String line = in.readLine();
			while ( line != null) {
				String[] tempStrings = line.split("\t");
				String key = tempStrings[0];
				long value = Long.valueOf(tempStrings[1]);

				switch (key) {
				case "Y=CCAT":
					nbTest.labelRecord.put("Y=CCAT", value);
					break;
				case "Y=ECAT":
					nbTest.labelRecord.put("Y=ECAT", value);
					break;
				case "Y=GCAT":
					nbTest.labelRecord.put("Y=GCAT", value);
					break;
				case "Y=MCAT":
					nbTest.labelRecord.put("Y=MCAT", value);
					break;
				case "Y=*" :
					nbTest.totalLabelRecord = value;
					break;
					
				
				case "Y=CCAT,W=*" :
					nbTest.labelTotalWord.put("Y=CCAT,W=*", value);
					break;
				case "Y=ECAT,W=*" :
					nbTest.labelTotalWord.put("Y=ECAT,W=*", value);
					break;
				case "Y=GCAT,W=*" :
					nbTest.labelTotalWord.put("Y=GCAT,W=*", value);
					break;
				case "Y=MCAT,W=*" :
					nbTest.labelTotalWord.put("Y=MCAT,W=*", value);
					break;
					
				default:
					nbTest.wordLabelRecord.put(key, value);
					break;
				}	
				line = in.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        int addDenominator = nbTest.wordLabelRecord.size();
		
		double ccatProb = nbTest.labelRecord.get("Y=CCAT")/(double)nbTest.totalLabelRecord;
		
		double ecatProb = nbTest.labelRecord.get("Y=ECAT")/(double)nbTest.totalLabelRecord;
		double gcatProb = nbTest.labelRecord.get("Y=GCAT")/(double)nbTest.totalLabelRecord;
		double mcatProb = nbTest.labelRecord.get("Y=MCAT")/(double)nbTest.totalLabelRecord;
		//System.out.println(ccatProb + " " + ecatProb + " " + gcatProb + " " + mcatProb);
		
		
		BufferedReader br = new BufferedReader(new FileReader(argv[0]));
		//BufferedReader br = new BufferedReader(new FileReader("RCV1.very_small_test.txt"));
		String newLine = br.readLine();
		while (newLine != null) {	
			String[] tempStrings = newLine.split("\t");
			String words = tempStrings[1];
			Vector<String> wordList = tokenizeDoc(words);
			double prob;
			String label;
			//CCAT
			double ccat = 0;
			double ecat = 0;
			double gcat = 0;
			double mcat = 0;
			for (String a : wordList) {
				if (nbTest.wordLabelRecord.containsKey("Y=CCAT,W=" + a))
					ccat += Math.log((nbTest.wordLabelRecord.get("Y=CCAT,W=" + a)+1) / (double)(nbTest.labelTotalWord.get("Y=CCAT,W=*")+addDenominator));
				else {
					ccat += Math.log(1 / (double)(nbTest.labelTotalWord.get("Y=CCAT,W=*")+addDenominator));
				}
			}
			ccat += Math.log(ccatProb);
			label = "CCAT";
			prob = ccat;
			
			for (String a : wordList) {
				if (nbTest.wordLabelRecord.containsKey("Y=ECAT,W=" + a))
					ecat += Math.log((nbTest.wordLabelRecord.get("Y=ECAT,W=" + a)+1) / (double)(nbTest.labelTotalWord.get("Y=ECAT,W=*")+addDenominator));
				else {
					ecat += Math.log(1 / (double)(nbTest.labelTotalWord.get("Y=ECAT,W=*")+addDenominator));
				}
			}
		    ecat += Math.log(ecatProb);
			if (ecat > prob) {
				label = "ECAT";
				prob = ecat;
			}
			
			for (String a : wordList) {
				if (nbTest.wordLabelRecord.containsKey("Y=GCAT,W=" + a))
					gcat += Math.log((nbTest.wordLabelRecord.get("Y=GCAT,W=" + a)+1) / (double)(nbTest.labelTotalWord.get("Y=GCAT,W=*")+addDenominator));
				else {
					gcat += Math.log(1 / (double)(nbTest.labelTotalWord.get("Y=GCAT,W=*")+addDenominator));
				}
			}
			gcat += Math.log(gcatProb);
			if (gcat > prob) {
				label = "GCAT";
				prob = gcat;
			}
			
			for (String a : wordList) {
				if (nbTest.wordLabelRecord.containsKey("Y=MCAT,W=" + a))
					mcat += Math.log((nbTest.wordLabelRecord.get("Y=MCAT,W=" + a)+1) / (double)(nbTest.labelTotalWord.get("Y=MCAT,W=*")+addDenominator));
				else {
					mcat += Math.log(1 / (double)(nbTest.labelTotalWord.get("Y=MCAT,W=*")+addDenominator));
				}
			}
			mcat += Math.log(mcatProb);
			if (mcat > prob) {
				label = "MCAT";
				prob = mcat;
			}
			
			System.out.println(label + "\t" + prob);
			newLine = br.readLine();
		}
		br.close();
     }
}
