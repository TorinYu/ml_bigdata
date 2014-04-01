

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;

//import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry.Entry;

//Here, the first argument 10000 is the vocabulary size. 
//Second argument 0.5 is the initial value of the learning rate.   λ
//The third argument 0.1 is the regular- ization coefficient.      2μ = 0.1
//The fourth argument 20 is the max iteration (# of passes through data). 
//The fifth argument is the size of the training dataset (which allows you to determine the starting point of a new pass).

//  0    1   2   3   4        5
//10000 0.5 0.1 20 1000 testData.txt

public class LR {


	public static void main(String[] args) throws IOException {

		/**
		 *  Train Part 
		 */
		//int k = 0; // Keep the iteration time
		String[] labelType = {"nl", "el", "ru", "sl", "pl", "ca", "fr", "tr", "hu", "de", "hr", "es", "ga", "pt"}; 
		HashMap<String, HashMap<Integer, Integer>> A = new HashMap<String, HashMap<Integer,Integer>>();
		HashMap<String, HashMap<Integer, Double>> B = new HashMap<String, HashMap<Integer,Double>>();
		
		HashSet<String> record = new HashSet<String>();
		for (String a : labelType) {
			record.add(a);
			A.put(a, new HashMap<Integer, Integer>());
			B.put(a, new HashMap<Integer, Double>());
		}

		int vocab_size = Integer.valueOf(args[0]);
		double initial_lr = Double.parseDouble(args[1]);
		double u = Double.parseDouble(args[2]);
		u = u *2;
		//double decrease = 0.5;
		//int times = Integer.valueOf(args[3]);
		long datasize = Long.parseLong(args[4]);
		long num_line = 0;
		//FileInputStream in = new FileInputStream(args[5]); 
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String strLine;
		double lr = initial_lr;
		int k = 1;
		//for (int i = 1; i <= times; i++) {
			try { 				
				while ((strLine = br.readLine()) != null) {
					
					HashSet<String> zeroLabel = new HashSet<String>(record);
					String[] label_text = strLine.split("\t");
					String[] labels = label_text[0].split(",");
					String[] words = label_text[1].split(" ");
					int[] wordsValue = new int[words.length];
					for (int j = 0; j < words.length; j++) {
						int id = words[j].hashCode() % vocab_size;
						if (id < 0) {
							id += vocab_size;
						}
						wordsValue[j] = id;
					}
					/**
					 * For labels with value 1
					 */
					for (String label : labels) {
						zeroLabel.remove(label);   // Delete the labels with value 1
						HashMap<Integer, Double> Btemp = B.get(label);
						HashMap<Integer, Integer> Atemp = A.get(label);

						for (int value : wordsValue) {
							if (!Atemp.containsKey(value)) {
								Atemp.put(value, 0);
								Btemp.put(value, 0.0);
							} else {
								// Todo so only when Xj is not zero
								double updated_Bj = Btemp.get(value) * Math.pow((1-lr*u) , (k-Atemp.get(value)));
								Btemp.put(value, updated_Bj);
								//Atemp.put(value, k);        //TODO when to update K
							}
						}
						
						double temp = 0.0;
						for (int value : wordsValue) {
							temp += Btemp.get(value);
						}
						double P = 1 / (1 + Math.exp(-temp));
						
						for (int value : wordsValue) {
							double updated_Bj = Btemp.get(value);
							updated_Bj += lr*(1-P);
							Btemp.put(value, updated_Bj);
							Atemp.put(value, k);
						}

						// Update HashMap for each label 
						//B.put(label, Btemp);    
						//A.put(label, Atemp);
					}
					/**
					 * For labels with value 0
					 */
					Set<String> zerolabels = zeroLabel;
					for (String label : zerolabels) {
						HashMap<Integer, Double> Btemp = B.get(label);
						HashMap<Integer, Integer> Atemp = A.get(label);

						for (int value : wordsValue) {
							if (!Atemp.containsKey(value)) {
								Atemp.put(value, 0);
								Btemp.put(value, 0.0);
							} else {
								// Todo so only when Xj is not zero
								double updated_Bj = Btemp.get(value) * Math.pow((1-lr*u) , (k-Atemp.get(value)));
								Btemp.put(value, updated_Bj);
								//Atemp.put(value, k);        //TODO when to update K
							}
						}
						double temp = 0.0;
						for (int value : wordsValue) {
							temp += Btemp.get(value);
						}
						double P = 1 / (1 + Math.exp(-temp));
						for (int value : wordsValue) {
							double updated_Bj = Btemp.get(value);
							updated_Bj += lr*(0-P);
							Btemp.put(value, updated_Bj);
							Atemp.put(value, k);
						}

						// Update Hashtable for each label 
						//B.put(label, Btemp);    
						//A.put(label, Atemp);
					}

					num_line ++;
					// Next iteration 
//					if (num_line == datasize) {
//						k++;
//						num_line = 0;
//						lr = initial_lr/(k*k);
//					}
					if (num_line == datasize) {
						break;
					}
					
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		//}
		/**
		 *  Update Bj in the last time
		 */

		for (String a : B.keySet()) {
			HashMap<Integer, Double> Btemp = B.get(a);
			//HashMap<Integer, Integer> Atemp = A.get(a);
			for (int b : Btemp.keySet()) {
				double updated_Bj = Btemp.get(b);
				updated_Bj *= Math.pow((1-lr*u), k-A.get(a).get(b));
				Btemp.put(b, updated_Bj);
			}
			//B.put(a, Btemp);
		}
//		for (java.util.Map.Entry<String, HashMap<Integer, Double>> Btemp : B.entrySet()) {
//			
//		}
//		for (java.util.Map.Entry<Integer, Double> b :  Btemp.entrySet()) {
//			
//		}


		/**
		 *  Debug Train Part 
		 */
//		for (String a : B.keySet()) {
//			Hashtable<Integer, Double> Btemp = B.get(a);
//			for (int b : Btemp.keySet()) {
//				System.out.println("label :" + a +  " Integer :" + b + " value : " + Btemp.get(b));
//			}
//		}


		/**
		 *  Test Part
		 */
		FileInputStream in = new FileInputStream(args[5]);
		BufferedReader testBr = new BufferedReader(new InputStreamReader(in));
		String testLine;
		
		while ( (testLine = testBr.readLine()) != null) {
			String[] label_words = testLine.split("\t");
			String[] testWords = label_words[1].split(" ");
			
			int[] wordsValue = new int[testWords.length];
			for (int j = 0; j < testWords.length; j++) {
				int id = testWords[j].hashCode() % vocab_size;
				if (id < 0) {
					id += vocab_size;
				}
				wordsValue[j] = id;
			}
			int printRange = 0;
			for (String a : labelType) {
				HashMap<Integer, Double> testTemp = B.get(a);
				double temp = 0.0;
				for (int value : wordsValue) {
					if (testTemp.containsKey(value)) {   // TODO Deal with unexisted values 
						temp += testTemp.get(value);
					}
				}
				double P = 1 / (1 + Math.exp(-temp));
				if (printRange == 13) {
					System.out.print(a + "\t" + P + "\n");  
				} else {
					System.out.print(a + "\t" + P + ",");
				} 
				printRange ++;
			}
		}
	}

}
