/*Henry Chen
 * This program is created by Henry Chen as part of his 2016 SHIP (Summer High School Intern Program) project, sponsored by NIST (National Institute of Standards and Technology), under DOC (US Department of Commerce).
 * Part One of the Password Data Analysis and Visualization Project
 */

import java.io.*;
import java.text.*;
import java.util.*;

import javax.swing.*;

public class Driver {

	private ArrayList<String> text;
	private int numOutputs;
	private String currentPath;
	private int numExamine;

	private ArrayList<PartOne> allOutputs;
	private String dateAndTime;
	private String originalFileName;

	private final String tab = "\t";

	public Driver(){

		dateAndTime = getDateAndTime();

		text = new ArrayList<String>();
		allOutputs = new ArrayList<PartOne>();

		boolean maliciousInput = false;
		boolean current = true;
		String input = "";

		do{
			input = JOptionPane.showInputDialog("Enter number of examined characters", "type here");
			int length = input.length();

			boolean legit = true;

			for(int i = 0; i < length; i++){

				//make sure only positive integers are entered
				int examineThis = (int) input.charAt(i);

				//if not a number
				if(examineThis < 48 || examineThis > 57){
					maliciousInput = true;
					legit = false;
					i = length;
				}
			}

			if(legit)
				maliciousInput = false;
		}while(maliciousInput);

		numExamine = Integer.parseInt(input);

		JFileChooser();

		//Create master folder for all output 
		String folderPath = currentPath;
		File dir = new File(folderPath + "_Folder_" + dateAndTime);
		boolean success = dir.mkdir();
		if(success){
			currentPath = dir.getAbsolutePath() + "/";
			System.out.println("Directory: " + currentPath + " created");
		}

		sort();

		for(PartOne file : allOutputs){
			file.analyzeFile();
			file.percentCalculator();
		}

		//create file for d3js frequency program
		try{
			//create or replace the file
			String title = currentPath + originalFileName + "_per PWD_" + "POS" + numExamine + "_" + dateAndTime + "_MASTER_" + "d3js.txt";

			FileWriter outFile = new FileWriter(title);

			ArrayList<ArrayList<Integer>> master = new ArrayList<ArrayList<Integer>>();

			for(PartOne file : allOutputs)
				master.add(file.d3js());

			//will store the tallied number of appearances for each character
			ArrayList<Integer> finalVersion = new ArrayList<Integer>();

			//add up all the frequencies
			for(int col = 0; col < master.get(0).size(); col++){

				int total = 0;

				for(int row = 0; row < master.size(); row++)
					total += master.get(row).get(col);

				finalVersion.add(total);
			}

			//output to file
			int max = finalVersion.size() - 1;
			for(int i = 0; i < max; i++)
				outFile.write(finalVersion.get(i) + tab);
			outFile.write(finalVersion.get(max));

			outFile.close();

		}catch(IOException e){
			System.out.println("IO issue");
			System.exit(-1);
		}

		d3posPrinter();
	}

	//create file for d3js position program
	private void d3posPrinter(){
		try{
			//create or replace the file
			String title = currentPath + originalFileName + "_per PWD_" + "POS" + numExamine + "_" +  dateAndTime + "_MASTER" + "_d3pos.txt";
			FileWriter outFile = new FileWriter(title);

			ArrayList<ArrayList<ArrayList<Integer>>> master = new ArrayList<ArrayList<ArrayList<Integer>>>();

			for(PartOne file : allOutputs)
				master.add(file.d3pos());

			//will store the tallied number of appearances for each character
			ArrayList<ArrayList<Integer>> info = new ArrayList<ArrayList<Integer>>();

			//add up all the frequencies
			for(int row = 0; row < master.get(0).size(); row++){

				ArrayList<Integer> position = new ArrayList<Integer>();

				//start at column one to avoid adding the position numbers
				for(int col = 1; col < master.get(0).get(0).size(); col++){

					int total = 0;
					for(int i = 0; i < master.size(); i++)
						total += master.get(i).get(row).get(col);

					position.add(total);
				}

				info.add(position);
			}//fix

			//output to file
			int maxRows = info.size() - 1;
			int maxCols = info.get(0).size();

			//output to file
			for(int row = 0; row < maxRows; row++){
				
				//calculate percentage
				int total = 0;

				for(int col = 1; col < maxCols; col++)
					total += info.get(row).get(col);

				outFile.write(info.get(row).get(0) + tab);

				for(int col = 1; col < maxCols; col++)
					outFile.write(((double)info.get(row).get(col)/(total)*100) + tab);

				outFile.write("\r\n");
			}

			int total = 0;
			for(int col = 1; col < maxCols; col++)
				total += info.get(maxRows).get(col);

			outFile.write(info.get(maxRows).get(0) + tab);
			for(int col = 1; col < maxCols; col++)
				outFile.write(((double)info.get(maxRows).get(col)/(total)*100) + tab);

			outFile.close();

		}catch(IOException e){
			System.out.println("IO issue");
			System.exit(-1);
		}
	}

	//open a file
	private void JFileChooser(){

		JFileChooser fileChooser = new JFileChooser();
		int result = fileChooser.showOpenDialog(null);

		File f = null;
		Scanner sc = null;

		if(result == JFileChooser.APPROVE_OPTION){
			f = fileChooser.getSelectedFile();

			currentPath = f.getAbsolutePath();
			originalFileName = f.getName();

			//read the data from the file
			try{
				sc = new Scanner(f);
			}catch(FileNotFoundException e){
				System.out.println("File Not Found!");
				System.exit(-1);
			}

			//skip first title line
			sc.nextLine();

			//get all info
			while(sc.hasNextLine())
				text.add(sc.nextLine());
		}

		else{
			System.out.println("File not opened");
			System.exit(-1);
		}
	}

	//sort output/analysis by password type
	private void sort(){

		//find out how many types of passwords there are
		ArrayList<String> passTypes = new ArrayList<String>();

		for(String line : text){

			String[] temp = line.split(tab);

			//password type
			passTypes.add(temp[temp.length-1]);
		}

		//remove duplicates
		Set<String> hs = new HashSet<String>();
		hs.addAll(passTypes);
		passTypes.clear();
		passTypes.addAll(hs);

		//sort all the lines of text by password type
		numOutputs = passTypes.size();

		ArrayList<ArrayList<String>> matrix = new ArrayList<ArrayList<String>>();
		for(int i = 0; i < numOutputs; i++)
			matrix.add(new ArrayList<String>());

		for(String line : text){

			String[] temp = line.split(tab);

			//add line to the output matrix once matching password type is found
			for(int i = 0; i < numOutputs; i++){

				String name = passTypes.get(i);

				if(temp[temp.length-1].equals(name)){
					matrix.get(i).add(line);
					i = numOutputs;
				}
			}
		}

		String freqTitles = freqTitles();
		//convert to PartOne objects
		for(int i = 0; i < numOutputs; i++)
			allOutputs.add(new PartOne(matrix.get(i), numExamine, currentPath, passTypes.get(i), dateAndTime, freqTitles, originalFileName));
	}

	//retrieve and format date and time
	private String getDateAndTime(){

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
		Date date = new Date();
		String alpha = dateFormat.format(date); //2014/08/06 15:59:48

		int divider = alpha.indexOf(" ");
		String time = alpha.substring(divider + 1);

		int hour = Integer.parseInt(time.substring(0, 2));

		if(hour < 12)
			time += " AM";

		else{
			String newTime = "";
			hour -= 12;

			//append a zero at front if necessary
			if((hour + "").length() < 2)
				newTime += "0";

			newTime += hour + time.substring(2) + " PM";

			time = newTime;
		}

		return alpha.substring(0, divider) + " at " + time;
	}

	private String freqTitles(){

		String label = "";

		//upper case
		for(int i = 65; i < 91; i++)
			label += (char) i + tab;

		//lower case
		for(int i = 97; i < 123; i++)
			label += (char) i + tab;

		//0-9
		for(int i = 0; i < 10; i++)
			label += i + tab;

		//symbols
		for(int i = 33; i < 48; i++)
			label += (char) i + tab;

		for(int i = 58; i < 65; i++)
			label += (char) i + tab;

		for(int i = 91; i < 97; i++)
			label += (char) i + tab;

		for(int i = 123; i < 127; i++)
			label += (char) i + tab;

		//space
		label += "WS";

		return label;
	}

	public static void main(String[] args){
		new Driver();
	}
}
