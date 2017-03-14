import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.*;


public class Task4{

	public long numOfAllRecords = 0;

	private BufferedWriter bw = null;
	private BufferedReader br1 = null;

	private HashMap<String, String> asPairMap = null;
	private HashSet<String> asSet = null;
	private HashMap<String, String> classMap = null;
public long count = 0;

	public static void main(String[] args){

		if(args.length < 2){
			System.out.println("Correct Format: java Task4 <inputfile> <outputfile>");
			return;
		}

		Task4 task = new Task4();
		task.executeTask(args);

	}


	public void executeTask(String[] args){

		String inputFilePath = args[0];
		String outputFilePath = args[1];


		try{
			br1 = new BufferedReader(new FileReader(inputFilePath));

			File file = new File(outputFilePath);
			bw = new BufferedWriter(new FileWriter(file));


			asPairMap = new HashMap<String, String>();
			asSet = new HashSet<String>();
			numOfAllRecords = 0;
			String curLine = br1.readLine();
			while(curLine != null) {	
				printLineStatus("Loading AS Pair Map");
				loadASPairMapAndSet(curLine);
				curLine = br1.readLine();
			}
			printMap(asPairMap);

			classMap = new HashMap<String, String>();
			classifyAsNodes();
			for(String key : classMap.keySet()){
				writeToFile(key + " " + classMap.get(key));
			}

		} catch (FileNotFoundException e){
			System.out.println("File not found");
		} catch (IOException ioe){
			System.out.println("Error reading/writing file");
		} finally {
			try{ 
				br1.close();
				bw.close();
			} catch (IOException e){
				System.out.println("Error closing the reader/writer");
			}
	
		}
	}

	private void loadASPairMapAndSet(String curLine) {
		String[] split = curLine.trim().split(" ");
		asPairMap.put((split[0] + "," + split[1]), split[2]);
		asSet.add(split[0]);
		asSet.add(split[1]);
	} 

	private void classifyAsNodes(){
		numOfAllRecords = 0;
		findLeaves(false, "Scanning For stubs");
		numOfAllRecords = 0;
		findRegionalNodes();
		findCores();
	}

	private void findCores(){
	
		
		//find dense core
		numOfAllRecords = 0;
		HashSet<String> denseCoreSet = new HashSet<String>();
		for(String core : asSet){		
			boolean isDenseCore = true;
			for(String core2 : asSet){
				printNodeStatus("Scanning for dense cores");
				setDefaultValueIfNullForKey(core + "," + core2);
				setDefaultValueIfNullForKey(core2 + "," + core);

				boolean doesHaveProvider = (asPairMap.get(core + "," + core2).contentEquals("c2p")) ||
									       (asPairMap.get(core2 + "," + core).contentEquals("p2c"));
		        if(doesHaveProvider) {
		        	isDenseCore = false;
		        	break;
		        }
			}
			if(isDenseCore){
				classMap.put(core, "dense core");
				denseCoreSet.add(core);
			}
		}

		for(String denseCore : denseCoreSet){
			asSet.remove(denseCore);
		}
		//find transit core
		numOfAllRecords = 0;
		HashSet<String> transitCoreSet = new HashSet<String>();
		for(String core : asSet){	
			boolean isTransitCore = false;
			for(String core2 : denseCoreSet){
				printNodeStatus("Scanning for transit cores");
				setDefaultValueIfNullForKey(core + "," + core2);
				setDefaultValueIfNullForKey(core2 + "," + core);

				boolean doesHavePeer = (asPairMap.get(core + "," + core2).contentEquals("p2p")) ||
									   (asPairMap.get(core2 + "," + core).contentEquals("p2p"));
		        if(doesHavePeer) {
		        	isTransitCore = true;
		        	break;
		        }
			}
			if(isTransitCore){
				classMap.put(core, "transit core");
				transitCoreSet.add(core);
			}
		}

		//find transit core
		numOfAllRecords = 0;
		
		for(String transitCore : transitCoreSet){
			asSet.remove(transitCore);
		}

		for(String core : asSet){
			printNodeStatus("Scanning for outer cores");
			classMap.put(core, "outer core");
		}
	}

	private void findRegionalNodes(){
		int lastSizeOfAsSet = asSet.size();

		while(true){
			count ++;
			findLeaves(true, "Scanning For regional ISP");
			if(lastSizeOfAsSet == asSet.size()){
				break;
			} else {
				lastSizeOfAsSet = asSet.size();
			}
		}
	}

	private void findLeaves(boolean isRegional, String phase){

		HashSet<String> hasProviderSet = new HashSet<String>();
		HashSet<String> hasCustomerSet = new HashSet<String>();
		HashSet<String> hasPeerSet = new HashSet<String>();

		for(String pair : asPairMap.keySet()){
			String relation = asPairMap.get(pair);
			String[] split = pair.split(",");

			if(relation.contentEquals("p2p")){
				hasPeerSet.add(split[0]);
				hasPeerSet.add(split[1]);
			} else if(relation.contentEquals("p2c")){
				hasCustomerSet.add(split[0]);
				hasProviderSet.add(split[1]);
			} else if(relation.contentEquals("c2p")){
				hasCustomerSet.add(split[1]);
				hasProviderSet.add(split[0]);
			}
		}

		HashSet<String> leafSet = new HashSet<String>();

		for(String node : asSet){
			boolean isLeaf = !hasCustomerSet.contains(node) &&
					         !hasPeerSet.contains(node);
			printNodeStatus(phase);

			if(isLeaf){
				if(isRegional){
					classMap.put(node, "regional ISP");
				} else {
					classMap.put(node, "stub");
				}
				leafSet.add(node);
			}
		}

		for(String leaf : leafSet){
			asSet.remove(leaf);
		}

		HashMap<String, String> updatePairMap = new HashMap<String, String>();
		for(String pair : asPairMap.keySet()){
			String[] split = pair.split(",");
			if(asSet.contains(split[0]) && asSet.contains(split[1])){
				updatePairMap.put(pair, asPairMap.get(pair));
			}
		}

		asPairMap = updatePairMap;

	}

	private void setDefaultValueIfNullForKey(String key){
		if(asPairMap.get(key) == null){
			asPairMap.put(key, "no relation");
		}
	}

	private void writeToFile(String curLine) throws IOException{
		bw.write(curLine + "\n");
	}

	
	private void printLineStatus(String phase){
		numOfAllRecords ++; 
		System.out.println("[" + phase + "] Line Processed : " + numOfAllRecords);
	}

	private void printNodeStatus(String phase){
		numOfAllRecords ++; 
		System.out.println("[" + phase + "] Node Processed : " + numOfAllRecords);
	}

	private void printMap(HashMap map){
		Set<Map.Entry> set = map.entrySet();
        List<Map.Entry> list = new ArrayList<Map.Entry>(set);
		for(Map.Entry entry : list){
			System.out.println(entry.getKey() + " : " + entry.getValue());
		}
	}

	private void printSet(HashSet<String> set){
		System.out.println(" ");
		for(String str : set){
			System.out.println(str);
		}
	}


}