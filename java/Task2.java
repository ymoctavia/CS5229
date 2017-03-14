import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.*;

public class Task2{

	public long numOfAllRecords = 0;

	private BufferedWriter bw = null;
	private BufferedReader br = null;

	private HashMap<String, HashSet<String>> asSetMap = null;
	private List<Map.Entry<String, Integer>> sortedEntryList = null;

	public static void main(String[] args){

		if(args.length < 2){
			System.out.println("Correct Format: java Task2 <inputfile> <outputfile>");
			return;
		}

		Task2 task = new Task2();
		task.executeTask(args);

	}


	public void executeTask(String[] args){

		asSetMap = new HashMap<String, HashSet<String>>();
		String inputFilePath = args[0];
		String outputFilePath = args[1];


		try{
			br = new BufferedReader(new FileReader(inputFilePath));

			File file = new File(outputFilePath);
			bw = new BufferedWriter(new FileWriter(file));

			String curLine = br.readLine();

			while(curLine != null) {		
				processAndUpdateMap(curLine);
				printStatus();
				curLine = br.readLine();
			}

			sortedEntryList = ConvertAndSortMap(asSetMap);
			int i = 0;

			for(Map.Entry<String, Integer> entry : sortedEntryList){
				bw.write(entry.getKey() + "\n");
				System.out.println(entry.getKey() + " ===== " + entry.getValue());
				i ++;
				if(i == 10){
					break;
				}
			}

		} catch (FileNotFoundException e){
			System.out.println("File not found");
		} catch (IOException ioe){
			System.out.println("Error reading/writing file");
		} finally {
			try{ 
				br.close();
				bw.close();
			} catch (IOException e){
				System.out.println("Error closing the reader/writer");
			}
	
		}
	}

	private void writeToFile(String curLine) throws IOException{
		bw.write(curLine + "\n");
	}

	private void processAndUpdateMap(String curLine){
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

	private void printStatus(){
		numOfAllRecords ++; 
		System.out.println("Line Processed : " + numOfAllRecords);
	}


	private List<Map.Entry<String, Integer>> ConvertAndSortMap(HashMap<String, HashSet<String>> map){
		HashMap<String, Integer> countMap = new HashMap<String, Integer>();

		for(String key : map.keySet()){
			countMap.put(key, map.get(key).size());
		}

		Set<Map.Entry<String, Integer>> set = countMap.entrySet();
        List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(set);
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>()
        {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2)
            {
                return (o2.getValue()).compareTo( o1.getValue() );
            }
        } );

     	return list;
	}


}