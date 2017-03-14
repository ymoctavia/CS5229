import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.HashSet;

public class Task1{

	public long numOfAllRecords = 0;


	public long numOfValidAsPath = 0;
	private BufferedWriter bw = null;
	private BufferedReader br = null;
	private HashSet<String> asSet = null;
	private HashSet<String> asPathSet = null;

	public static void main(String[] args){

		if(args.length < 2){
			System.out.println("Correct Format: java Task1 <inputfile> <outputfile>");
			return;
		}

		Task1 task = new Task1();
		task.executeTask(args);

	}


	public void executeTask(String[] args){

		asSet = new HashSet<String>();
		asPathSet = new HashSet<String>();
		String inputFilePath = args[0];
		String outputFilePath = args[1];


		try{
			br = new BufferedReader(new FileReader(inputFilePath));

			File file = new File(outputFilePath);
			bw = new BufferedWriter(new FileWriter(file));

			String curLine = br.readLine();

			while(curLine != null) {		
				processLine(curLine);
				curLine = br.readLine();
			}

			for(String line : asPathSet){
				writeToFile(line);
			}

			bw.write("Number of Ases: " + asSet.size() + "\n");
			bw.write("Number of AS paths: " + asPathSet.size());

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

	private void processLine(String curLine) throws IOException{
		 printStatus();
		 String processedLine = processAndUpdateSetOfAses(curLine);
		 if(processedLine != null){
		 	asPathSet.add(processedLine);
		 }
	}

	private void writeToFile(String curLine) throws IOException{
		bw.write(curLine + "\n");
	}

	private String processAndUpdateSetOfAses(String curLine){
		String updatedPath = " ";

		curLine = curLine.replace("ASPATH:", "").trim();

		if(curLine.contains("{") || 
		   curLine.trim().isEmpty()){
			return null;
		}

		String[] split = curLine.split(" ");
		for(String as : split){
			asSet.add(as);
			if(!updatedPath.contains(" " + as + " ")){
				updatedPath += as + " ";
			}
		} 

		updatedPath = updatedPath.trim();
		return updatedPath;
	}

	private void printStatus(){
		numOfAllRecords ++; 
		System.out.println("Line Processed : " + numOfAllRecords);
	}

}