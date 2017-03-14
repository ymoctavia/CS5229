import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.*;


public class Advance{

	public long numOfAllRecords = 0;

	private BufferedWriter bw = null;
	private BufferedReader br1 = null;
	private BufferedReader br2 = null;
	private BufferedReader br3 = null;

	private HashMap<String, HashMap<String, Integer>> edgeCountMap = null;
	private HashMap<String, String> classMap = null;

	private HashMap<String, Integer> asDegreeMap = null;
	private HashMap<String, HashSet<String>> asSetMap = null;
	private HashMap<String, HashMap<Integer, Integer>> classifiedDegreeMap = null;
	private HashMap<String, HashMap<Integer, Integer>> culmulativeDegreeMap = null;

	private static HashMap<String, String> asClassNameToLevelMap = new HashMap<String, String>();
	static{
		asClassNameToLevelMap.put("dense", "0");
		asClassNameToLevelMap.put("transit", "1");
		asClassNameToLevelMap.put("outer", "2");
		asClassNameToLevelMap.put("regional", "3");
		asClassNameToLevelMap.put("stub", "4");
	}


public long count = 0;

	public static void main(String[] args){

		if(args.length < 4){
			System.out.println("Correct Format: java Advance <asPathFile> <edgeFile> <classFile> <outputFile>");
			return;
		}

		Advance task = new Advance();
		task.executeTask(args);

	}


	public void executeTask(String[] args){

		String asPathFilePath = args[0];
		String edgeFilePath = args[1];
		String classFilePath = args[2];
		String outputFilePath = args[3];


		try{
			br1 = new BufferedReader(new FileReader(classFilePath));
			br2 = new BufferedReader(new FileReader(edgeFilePath));
			br3 = new BufferedReader(new FileReader(asPathFilePath));

			File file = new File(outputFilePath);
			bw = new BufferedWriter(new FileWriter(file));


			//load class file first into an map
			classMap = new HashMap<String, String>();
			String curLine = br1.readLine();
			while(curLine != null) {	
				printLineStatus("Loading class file");
				loadClassFileIntoMap(curLine);
				curLine = br1.readLine();
			}



			//load edge file then to generate the classified edge map
			curLine = br2.readLine();
			edgeCountMap = new HashMap<String, HashMap<String, Integer>>();
			asSetMap = new HashMap<String, HashSet<String>>();
			while(curLine != null) {	
				printLineStatus("Loading edge file and generate classified edge map");
				loadEdgeFileAndGenerateClassifiedEdgeCountMap(curLine);
				curLine = br2.readLine();
			}

			//load degree map
			curLine = br3.readLine();
			asSetMap = new HashMap<String, HashSet<String>>();
			while(curLine != null) {	
				printLineStatus("Loading degree map");
				loadDegreeMap(curLine);
				curLine = br3.readLine();
			}
			asDegreeMap = convertToAsDegreeMap();

			//generate classified degree map
			classifiedDegreeMap = new HashMap<String, HashMap<Integer, Integer>>();
			culmulativeDegreeMap = new HashMap<String, HashMap<Integer, Integer>>();
			generateClassifiedDegreeMap();

			//writeToFile
			writeToFile("Level,0,1,2,3,4");
			for(int i = 0; i < 4; i ++){
				String line = "" + i;
				for(int j = 0; j < 5; j ++){
					int count = 0;
					if(edgeCountMap.get("" + i).get("" + j) == null){
						count = 0;
					} else {
						count = edgeCountMap.get("" + i).get("" + j);
					}
					line += "," + count;
				}
				writeToFile(line);
			}

			writeToFile("");
	
			for(int i = 1; i < 10 ; i ++){
				writeToFile(i + "," + 
							getPercent("0", i) + "," + 
							getPercent("1", i) + "," + 
							getPercent("2", i) + "," +
							getPercent("3", i) + "," +
							getPercent("4", i)); 
			}

			for(int i = 10; i < 90 ; i = i + 10){
				writeToFile(i + "," + 
							getPercent("0", i) + "," + 
							getPercent("1", i) + "," + 
							getPercent("2", i) + "," +
							getPercent("3", i) + "," +
							getPercent("4", i)); 
			}

			for(int i = 100; i < 1000 ; i = i + 100){
				writeToFile(i + "," + 
							getPercent("0", i) + "," + 
							getPercent("1", i) + "," + 
							getPercent("2", i) + "," +
							getPercent("3", i) + "," +
							getPercent("4", i)); 
			}

			for(int i = 1000; i <= 10000 ; i = i + 1000){
				writeToFile(i + "," + 
							getPercent("0", i) + "," + 
							getPercent("1", i) + "," + 
							getPercent("2", i) + "," +
							getPercent("3", i) + "," +
							getPercent("4", i)); 
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
				bw.close();
			} catch (IOException e){
				System.out.println("Error closing the reader/writer");
			}
	
		}
	}

	private float getPercent(String level, int degree){
		int total = culmulativeDegreeMap.get(level).get(10000);
		int count = culmulativeDegreeMap.get(level).get(degree);
		float percent = ((float)count/(float)total) * 100;
		return percent;
	}

	private void generateClassifiedDegreeMap() throws IOException{
		for(String as : asDegreeMap.keySet()){
			String className = classMap.get(as);
			String level = asClassNameToLevelMap.get(className);

			if(level == null){
				writeToFile("level " + level + "\n");
				writeToFile("className " + className + "\n");
				writeToFile("as " + as + "\n");
			}

			if(classifiedDegreeMap.get(level) == null){
				classifiedDegreeMap.put(level, new HashMap<Integer, Integer>());
			}
			int degree = asDegreeMap.get(as);
			if(classifiedDegreeMap.get(level).get(degree) == null){
				classifiedDegreeMap.get(level).put(degree, 0);
			}

			int preCount = classifiedDegreeMap.get(level).get(degree);
			classifiedDegreeMap.get(level).put(degree, preCount + 1);
		}

		
		for(String level : classifiedDegreeMap.keySet()){

			if(culmulativeDegreeMap.get(level) == null){
				culmulativeDegreeMap.put(level, new HashMap<Integer, Integer>());
			}

			for(int degree : classifiedDegreeMap.get(level).keySet()){

				for(int i = 1; i < 10 ; i ++){
					if(culmulativeDegreeMap.get(level).get(i) == null){
							culmulativeDegreeMap.get(level).put(i, 0);
						}
					if(degree <= i){
						int preCount = culmulativeDegreeMap.get(level).get(i);
						int curCount = classifiedDegreeMap.get(level).get(degree);
						culmulativeDegreeMap.get(level).put(i, (preCount + curCount));
					}
				}

				for(int i = 10; i < 90 ; i = i + 10){
					if(culmulativeDegreeMap.get(level).get(i) == null){
							culmulativeDegreeMap.get(level).put(i, 0);
						}
					if(degree <= i){
						int preCount = culmulativeDegreeMap.get(level).get(i);
						int curCount = classifiedDegreeMap.get(level).get(degree);
						culmulativeDegreeMap.get(level).put(i, (preCount + curCount));
					}
				}

				for(int i = 100; i < 1000 ; i = i + 100){
					if(culmulativeDegreeMap.get(level).get(i) == null){
							culmulativeDegreeMap.get(level).put(i, 0);
						}
					if(degree <= i){
						int preCount = culmulativeDegreeMap.get(level).get(i);
						int curCount = classifiedDegreeMap.get(level).get(degree);
						culmulativeDegreeMap.get(level).put(i, (preCount + curCount));
					}
				}

				for(int i = 1000; i <= 10000 ; i = i + 1000){
					if(culmulativeDegreeMap.get(level).get(i) == null){
							culmulativeDegreeMap.get(level).put(i, 0);
						}
					if(degree <= i){
						int preCount = culmulativeDegreeMap.get(level).get(i);
						int curCount = classifiedDegreeMap.get(level).get(degree);
						culmulativeDegreeMap.get(level).put(i, (preCount + curCount));
					}
				}
			}
		}
	}



	private void loadDegreeMap(String curLine){
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



	private void loadClassFileIntoMap(String curLine){
		String[] split = curLine.split(" ");
		classMap.put(split[0], split[1]);
	}

	private void loadEdgeFileAndGenerateClassifiedEdgeCountMap(String curLine){
		String[] split = curLine.split(" ");
		String fromNodeClass = classMap.get(split[0]);
		String toNodeClass = classMap.get(split[1]);

		String fromNodeLevel = asClassNameToLevelMap.get(fromNodeClass);
		String toNodeLevel = asClassNameToLevelMap.get(toNodeClass);

		if(edgeCountMap.get(fromNodeLevel) == null){
			edgeCountMap.put(fromNodeLevel, new HashMap<String, Integer>());
		}

		if(edgeCountMap.get(fromNodeLevel).get(toNodeLevel) == null){
			edgeCountMap.get(fromNodeLevel).put(toNodeLevel, 0);
		}
		int preCount = edgeCountMap.get(fromNodeLevel).get(toNodeLevel);
		edgeCountMap.get(fromNodeLevel).put(toNodeLevel, (preCount + 1));

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