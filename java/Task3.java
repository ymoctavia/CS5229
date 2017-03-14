import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.*;


public class Task3{

	public long numOfAllRecords = 0;

	private BufferedWriter bw = null;

	private int L;
	private double R;

	private HashMap<String, Integer> asDegreeMap = null;
	private HashMap<String, HashSet<String>> asSetMap = null;
	private HashMap<String, Integer> transitMap = null;
	private HashMap<String, String> asPairMap = null;
	private HashMap<String, Integer> notPeeringMap = null;

	private BufferedReader br1 = null;
	private BufferedReader br2 = null;
	private BufferedReader br3 = null;
	private BufferedReader br4 = null;
	private BufferedReader br5 = null;

	public static void main(String[] args){

		if(args.length < 4){
			System.out.println("Correct Format: java Task3 <L> <R> <inputfile> <outputfile>");
			return;
		}

		Task3 task = new Task3();
		task.executeTask(args);

	}


	public void executeTask(String[] args){

		L = Integer.parseInt(args[0]);
		R = Double.parseDouble(args[1]);

		String inputFilePath = args[2];
		String outputFilePath = args[3];


		try{
			br1 = new BufferedReader(new FileReader(inputFilePath));
			br2 = new BufferedReader(new FileReader(inputFilePath));
			br3 = new BufferedReader(new FileReader(inputFilePath));
			br4 = new BufferedReader(new FileReader(inputFilePath));
			br5 = new BufferedReader(new FileReader(inputFilePath));

			File file = new File(outputFilePath);
			bw = new BufferedWriter(new FileWriter(file));


			asSetMap = new HashMap<String, HashSet<String>>();
			String curLine = br1.readLine();
			while(curLine != null) {	
				printStatus("Non p2p Phase 1");
				if(curLine.contains(":")){
					break;
				}
				doNonP2PPhaseOne(curLine);
				curLine = br1.readLine();
			}
			asDegreeMap = convertToAsDegreeMap();

			transitMap = new HashMap<String, Integer>();
			curLine = br2.readLine();
			numOfAllRecords = 0;
			while(curLine != null) {	
				
				printStatus("Non p2p Phase 2");	
				if(curLine.contains(":")){
					break;
				}
				doNonP2PPhaseTwo(curLine);
				curLine = br2.readLine();
			}
			
			asPairMap = new HashMap<String, String>();
			curLine = br3.readLine();
			numOfAllRecords = 0;
			while(curLine != null) {	
				
				printStatus("Non p2p Phase 3");	
				if(curLine.contains(":")){
					break;
				}	
				doNonP2PPhaseThree(curLine);
				curLine = br3.readLine();
			}
			
			notPeeringMap = new HashMap<String, Integer>();
			curLine = br4.readLine();
			numOfAllRecords = 0;
			while(curLine != null) {	
				
				printStatus("p2p Phase 2");		
				if(curLine.contains(":")){
					break;
				}
				doP2PPhaseTwo(curLine);
				curLine = br4.readLine();
			}

			curLine = br5.readLine();
			numOfAllRecords = 0;
			while(curLine != null) {	
				
				printStatus("p2p Phase 3");		
				if(curLine.contains(":")){
					break;
				}
				doP2PPhaseThree(curLine);
				curLine = br5.readLine();
			}

			for(String key : asPairMap.keySet()){
				String split[] = key.split(",");

				writeToFile(split[0] + " " + split[1] + " " + asPairMap.get(key));
			}

		} catch (FileNotFoundException e){
			System.out.println("File not found");
		} catch (IOException ioe){
			System.out.println("Error reading/writing file");
		} finally {
			try{ 
				br1.close();
				br2.close();
				br3.close();
				br4.close();
				br5.close();
				bw.close();
			} catch (IOException e){
				System.out.println("Error closing the reader/writer");
			}
	
		}
	}



	//-------------------nonP2PPhaseOne Methods----------------------------------------------
	private void doNonP2PPhaseOne(String curLine){
		if(curLine.contains(":")){
			return;
		}

		String[] split = curLine.trim().split(" ");

		for(int i = 0; i < (split.length -1); i ++){
			updateMap(split[i], split[i + 1]);
			updateMap(split[i + 1], split[i]);
		}
	}

	private void updateMap(String key, String value){
		if(this.asSetMap.get(key) == null){
			this.asSetMap.put(key, new HashSet<String>());
		} 
		this.asSetMap.get(key).add(value);
	}
	

	private HashMap<String, Integer> convertToAsDegreeMap(){
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for(String key : asSetMap.keySet()){
			map.put(key, asSetMap.get(key).size());
		}
		return map;
	}
	//---------------------------------------------------------------------------------------


	//-------------------nonP2PPhaseTwo Methods----------------------------------------------
	private void doNonP2PPhaseTwo(String curLine){

		String[] split = curLine.trim().split(" ");
		int smallestIndex = findSmallestIndex(curLine, split);

		for(int i = 0; i <= smallestIndex - 1; i ++){
			increaseTransitCountInMap(split[i] + "," + split[i+1]);
		}

		for(int j = smallestIndex; j <= (split.length - 2); j ++){
			increaseTransitCountInMap(split[j + 1] + "," + split[j]);
		}
	}

	private int findSmallestIndex(String curLine, String[] split){
		int max = -1;
		int index = 0;

		for(int i = 0; i < split.length; i ++ ){
			setZeroForNotFoundDegreeInMap(split[i]);

			if(asDegreeMap.get(split[i]) > max){
				max = asDegreeMap.get(split[i]);
				index = i;
			}
		}

		return index;
	}

	private void setZeroForNotFoundDegreeInMap(String key){
		if(asDegreeMap.get(key) == null){
			asDegreeMap.put(key, 0);
		}
	}
	
	private void increaseTransitCountInMap(String key){
		if(transitMap.get(key) == null){
			transitMap.put(key, 0);
		}
		transitMap.put(key, (transitMap.get(key) + 1));
	}

	
	//---------------------------------------------------------------------------------------

	//-------------------nonP2PPhaseThree Methods----------------------------------------------
	private void doNonP2PPhaseThree(String curLine){
		String[] split = curLine.trim().split(" ");

		for(int i = 0; i < (split.length - 1); i ++){
			String ui_1 = split[i + 1];
			String ui = split[i];


			setZeroForNotFoundPairInMap(ui_1 + "," + ui);
			setZeroForNotFoundPairInMap(ui + "," + ui_1);

			if((transitMap.get(ui_1 + "," + ui) > L && transitMap.get(ui + "," + ui_1) > L) ||
			   (transitMap.get(ui + "," + ui_1) <= L && 
			   	transitMap.get(ui + "," + ui_1) > 0 && 
			   	transitMap.get(ui_1 + "," + ui) <= L &&
			   	transitMap.get(ui_1 + "," + ui) > 0)){
				asPairMap.put((ui +"," + ui_1), "s2s");
			} else if(transitMap.get(ui_1 + "," + ui) > L || 
					  transitMap.get(ui + "," + ui_1) == 0){
				asPairMap.put((ui +"," + ui_1), "p2c");
			} else if(transitMap.get(ui + "," + ui_1) > L ||
					  transitMap.get(ui_1 + "," + ui) == 0){
				asPairMap.put((ui +"," + ui_1), "c2p");
			}
		}
	}

	private void setZeroForNotFoundPairInMap(String key){
		if(transitMap.get(key) == null){
			transitMap.put(key, 0);
		}
	}
	//---------------------------------------------------------------------------------------



	//-------------------P2PPhaseTwo Methods----------------------------------------------
	private void doP2PPhaseTwo(String curLine){
		String[] split = curLine.trim().split(" ");
		int smallestIndex = findSmallestIndex(curLine, split);

		for(int i = 0; i <= smallestIndex - 2; i ++){
			notPeeringMap.put((split[i] + "," + split[i+1]), 1);
		}

		for(int j = smallestIndex + 1; j <= (split.length - 2); j ++){
			notPeeringMap.put((split[j] + "," + split[j+1]), 1);
		}


		String uj_m1;
		String uj;
		String uj_1;

		try{
			uj_m1 = split[smallestIndex - 1];
			uj = split[smallestIndex];
		    uj_1 = split[smallestIndex + 1];
		} catch (Exception e) {
			return;
		}

		if((asPairMap.get(uj_m1 + "," + uj) == null || 
		    !asPairMap.get(uj_m1 + "," + uj).contentEquals("s2s")) 
		    && 
		    (asPairMap.get(uj + "," + uj_1) == null ||
		    !asPairMap.get(uj + "," + uj_1).contentEquals("s2s")) ) {

			if(asDegreeMap.get(uj_m1) < asDegreeMap.get(uj_1)){
				notPeeringMap.put((uj + "," + uj_1), 1);
			} else {
				notPeeringMap.put((uj_m1 + "," + uj), 1);
			}
		}
	}
	//---------------------------------------------------------------------------------------


	//-------------------P2PPhaseThree Methods----------------------------------------------
	private void doP2PPhaseThree(String curLine){
		String[] split = curLine.trim().split(" ");
		for(int j = 0; j < split.length - 1; j ++ ){
			String uj = split[j];
			String uj_1 = split[j + 1];

			setZeroForNotFoundPeerInMap(uj + "," + uj_1);
			setZeroForNotFoundPeerInMap(uj_1 + "," + uj);

			if(asDegreeMap.get(uj) == 0 || asDegreeMap.get(uj_1) == 0){
				return;
			}

			double ujDegreeDouble = (double)asDegreeMap.get(uj);
			double uj_1DegreeDouble = (double)asDegreeMap.get(uj_1);

			if(notPeeringMap.get(uj + "," + uj_1) != 1 &&
			   notPeeringMap.get(uj_1 + "," + uj) != 1 &&
			   (ujDegreeDouble/uj_1DegreeDouble) < R &&
			   (ujDegreeDouble/uj_1DegreeDouble) > (1/R)) {
				asPairMap.put((uj + "," + uj_1), "p2p");
			}
		}
	}

	private void setZeroForNotFoundPeerInMap(String key){
		if(notPeeringMap.get(key) == null){
			notPeeringMap.put(key, 0);
		}
	}
	//---------------------------------------------------------------------------------------




	private void writeToFile(String curLine) throws IOException{
		bw.write(curLine + "\n");
	}

	
	private void printStatus(String phase){
		numOfAllRecords ++; 
		System.out.println("[" + phase + "] Line Processed : " + numOfAllRecords);
	}

	// private void printMap(HashMap map){
	// 	Set<Map.Entry> set = map.entrySet();
 //        List<Map.Entry> list = new ArrayList<Map.Entry>(set);
	// 	for(Map.Entry entry : list){
	// 		System.out.println(entry.getKey() + " : " + entry.getValue());
	// 	}
	// }


}