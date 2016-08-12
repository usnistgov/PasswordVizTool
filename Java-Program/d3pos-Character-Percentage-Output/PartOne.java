/* Henry Chen SHIP 2016
 * This program is created by Henry Chen as part of his 2016 SHIP (Summer High School Intern Program) project, sponsored by NIST (National Institute of Standards and Technology), under DOC (US Department of Commerce).
 * Analyzes a single matrix of one password type
 */

import java.io.*;
import java.text.*;
import java.util.*;

public class PartOne{

	private int numExamine;

	private String currentPath;

	private final String tab = "\t";

	private int numPasswords;
	private ArrayList<ArrayList<Integer>> lengths;

	private ArrayList<ArrayList<String>> outputMatrix;
	private ArrayList<String> text;
	private String passwordType;
	private String dateAndTime;
	private String originalFileName;

	private String freqTitles;

	public PartOne(ArrayList<String> info, int num, String path, String pT, String dAt, String fT, String origFname){

		originalFileName = origFname;
		freqTitles = fT;
		dateAndTime = dAt;
		passwordType = pT;
		currentPath = path;
		text = new ArrayList<String>();
		for(String data : info)
			text.add(data);

		numExamine = num;
		outputMatrix = new ArrayList<ArrayList<String>>();
	}

	//save the file
	private void JFileSaver(){

		//Uncomment below code only if JRE version 1.7 or later is running
		/*//set look and feel of file selector
		try{
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		}catch(Exception e){
			System.out.println("Problems editing the look and feel");
			System.exit(-1);
		}*/

		//save the file
		try{
			//create or replace the file
			String title = currentPath + originalFileName + "_per PWD_" + "POS" + numExamine + "_" + dateAndTime + "_" + passwordType + ".txt";
			FileWriter outFile = new FileWriter(title);

			String label = "SID" + tab + "PID" + tab + "PWD" + tab + "Type" + tab + "Length" + tab + "U" + tab + "L" + tab + "N" + tab + "S" + tab + "WS" + tab;

			for(int i = 0; i < numExamine; i++)
				label += "POS" + (i+1) + tab;

			label += freqTitles;

			outFile.write(label + "\r\n");

			int maxRows = outputMatrix.size() - 1;
			int maxCols = outputMatrix.get(0).size();
			for(int row = 0; row < maxRows; row++){
				for(int col = 0; col < maxCols; col++)
					outFile.write(outputMatrix.get(row).get(col) + tab);

				outFile.write("\r\n");
			}

			//avoid extra tab and line break
			maxCols--;
			for(int col = 0; col < maxCols; col++)
				outFile.write(outputMatrix.get(maxRows).get(col) + tab);

			outFile.write(outputMatrix.get(maxRows).get(maxCols));

			outFile.close();

		}catch(IOException e){
			System.out.println("IO issue");
			System.exit(-1);
		}
	}

	//creates array list matrix and starts output
	public void analyzeFile(){

		for(String line : text){

			String[] temp = line.split(tab);
			ArrayList<String> row = new ArrayList<String>();

			for(int i = 0; i < temp.length; i++)
				row.add(temp[i]);

			String password = row.get(2);

			//add length of password
			row.add(password.length() + tab);

			//ULNSWS
			int[]count = numTypes(password);
			for(int i = 0; i < count.length; i++)
				row.add(count[i] + tab);

			//POS1, POS2, etc
			String[]charTypes = charAnalyze(password);
			for(int i = 0; i < charTypes.length; i++)
				row.add(charTypes[i] + tab);

			//individual character count
			int[]all = allChar(password);
			for(int i = 0; i < all.length; i++)
				row.add(all[i] + tab);

			outputMatrix.add(row);
		}

		//print
		JFileSaver();
	}

	//returns count of upper case, lower case, numbers, symbols, and spaces for each password
	private int[] numTypes(String word){

		int[] count = {0, 0, 0, 0, 0};

		//categorize all letters
		for(int i = 0; i < word.length(); i++){

			char letter = word.charAt(i);
			int value = (int) letter;

			//space
			if(value == 32)
				count[4]++;

			//number
			else if(value > 47 && value < 58)
				count[2]++;

			//upper case
			else if(value > 64 && value < 91)
				count[0]++;

			//lower case
			else if(value > 96 && value < 123)
				count[1]++;

			//symbols: else if((value > 32 && value < 48) || (value > 57 && value < 65) || (value > 90 && value < 97) || value > 122)
			else
				count[3]++;
		}

		return count;
	}

	//return char type of POS1, POS2, POS3, etc
	private String[] charAnalyze(String word){

		String[] charTypes = new String[numExamine];

		for(int i = 0; i < numExamine; i++){

			char letter = word.charAt(i);
			int value = (int) letter;

			//space
			if(value == 32)
				charTypes[i] = "WS";

			//number
			else if(value > 47 && value < 58)
				charTypes[i] = "N";

			//upper case
			else if(value > 64 && value < 91)
				charTypes[i] = "U";

			//lower case
			else if(value > 96 && value < 123)
				charTypes[i] = "L";

			//symbols: else if((value > 32 && value < 48) || (value > 57 && value < 65) || (value > 90 && value < 97) || value > 122)
			else
				charTypes[i] = "S";

			if(i >= word.length() - 1)
				i = numExamine;
		}

		return charTypes;
	}

	//return array with counts of the word's character types 
	private int[] allChar(String word){

		int[] all = new int[95];
		for(int i = 0; i < all.length; i++)
			all[i] = 0;

		for(int i = 0; i < word.length(); i++){

			char letter = word.charAt(i);
			int value = (int) letter;
			int index = -1;

			//space
			if(value == 32)
				index = all.length - 1;

			//number
			else if(value > 47 && value < 58){

				index = value - 48 + 26 +26;
			}

			//upper case
			else if(value > 64 && value < 91){

				index = value - 65;
			}

			//lower case
			else if(value > 96 && value < 123){

				index = value - 97 + 26;
			}

			//symbols
			else{

				/* first set :  + 26 + 26 + 10;
				 * second set: + 26 + 26 + 10 + 15
				 * third set : + 26 + 26 + 10 + 15 + 7;
				 * fourth set: + 26 + 26 + 10 + 15 + 7 + 6;*/

				if(value > 32 && value < 48)
					index = value - 33 + 26 + 26 + 10;

				else if(value > 57 && value < 65)
					index = value - 58 + 26 + 26 + 10 + 15;

				else if(value > 90 && value < 97)
					index = value - 91 + 26 + 26 + 10 + 15 + 7;

				//value > 122
				else
					index = value - 123 + 26 + 26 + 10 + 15 + 7 + 6;
			}

			try{
				all[index]++;
			}catch(ArrayIndexOutOfBoundsException e){ //special type of apostrophe: "’"
				all[39]++;
			}
		}

		return all;
	}

	//password analysis
	public void percentCalculator(){

		numPasswords = 0;
		ArrayList<Integer> lengthXaxis = new ArrayList<Integer>();
		ArrayList<String> allPasswords = new ArrayList<String>();

		//extract all the passwords from the ArrayList matrix
		for(ArrayList<String> data : outputMatrix)
			allPasswords.add(data.get(2));

		//get all the password lengths
		for(String data : allPasswords)
			lengthXaxis.add(data.length());

		//search for duplicates
		ArrayList<Integer> xTrimmed = new ArrayList<Integer>(); //contains no duplicates

		//don't use hashset - will destroy order of elements
		for(int i = 0; i < lengthXaxis.size(); i++){

			Integer num = lengthXaxis.get(i);

			//only add if not already in there
			if(!xTrimmed.contains(num))
				xTrimmed.add(num);
		}

		ArrayList<Integer> yTrimmed = new ArrayList<Integer>(); //contains the frequency for each unique length
		for(int i = 0; i < xTrimmed.size(); i++)
			yTrimmed.add(1);

		int trimmedCounter = 0;
		for(int i = 0; i < lengthXaxis.size(); i++){

			for(int j = i + 1; j < lengthXaxis.size(); j++){

				if(lengthXaxis.get(i).equals(lengthXaxis.get(j))){//idk

					lengthXaxis.remove(j);
					j--;
					yTrimmed.set(trimmedCounter, yTrimmed.get(trimmedCounter) + 1); //arraylist equivalent of array[i]++;
				}
			}

			trimmedCounter++;
		}

		lengths = new ArrayList<ArrayList<Integer>>();
		lengths.add(xTrimmed);
		lengths.add(yTrimmed);

		for(int i = 0; i < yTrimmed.size(); i++)
			numPasswords += yTrimmed.get(i);

		//char set

		//search ULNSWS for max/min digits, lower, upper, special and spaces
		Object[][] minMax = new Object[5][3];
		minMax[0][0] = "digit";
		minMax[1][0] = "lower";
		minMax[2][0] = "upper";
		minMax[3][0] = "special";
		minMax[4][0] = "white space";

		/* QUESTION: what does MIN/MAX entail? atm this loop finds the maximum number of each type of character in all the passwords
		 * first row:  digit min/max
		 * second row: lower min/max
		 * third row:  upper min/max
		 * fourth row: special min/max 
		 * fifth row:  spaces min/max */
		for(int col = 5; col < 10; col++){//56789 ULNSWS

			int max = Integer.MIN_VALUE;
			int min = Integer.MAX_VALUE;

			for(int row = 0; row < outputMatrix.size(); row++){

				//remove excess spaces
				String raw = outputMatrix.get(row).get(col);
				int loc = raw.indexOf(tab);
				String trimmed = raw;

				while(loc != -1){
					trimmed = raw.substring(0, raw.indexOf(tab));
					loc = trimmed.indexOf(tab);
				}

				int num = Integer.parseInt(trimmed);
				if(num > max)
					max = num;

				else if(num < min)
					min = num;
			}

			minMax[col - 5][1] = (Integer) min;
			minMax[col - 5][2] = (Integer) max;
		}

		//print
		try{
			//create or replace the file
			String title = currentPath + originalFileName + "_per PWD_" + "POS" + numExamine + "_" + dateAndTime + "_" + passwordType + "_output2.txt";
			FileWriter outFile = new FileWriter(title);

			//length
			outFile.write("Length: \r\n");

			for(int col = 0; col < lengths.get(0).size(); col++){

				int dataSize = lengths.get(1).get(col);

				//format to three decimal places
				NumberFormat formatter = new DecimalFormat("#0.000");
				String percentage = formatter.format(((double) dataSize / numPasswords)*100) + "%";

				//pad zero on beginning if necessary
				if(percentage.length() < 2)
					percentage = "0" + percentage;

				outFile.write(lengths.get(0).get(col) + ": " + percentage + "(" + dataSize + ")" + "\r\n");
			}

			outFile.write("\r\n");

			//char set
			outFile.write("Character-set: \r\n");

			//password complexity
			outFile.write("Password complexity: \r\n");

			for(int row = 0; row < minMax.length; row++)
				outFile.write(minMax[row][0] + ": min(" + minMax[row][1] + ")" + " max(" + minMax[row][2] + ")\r\n");

			outFile.close();

		}catch(IOException e){
			System.out.println("IO issue");
			System.exit(-1);
		}
	}

	//graphing data for javascript, only prints frequency
	public ArrayList<Integer> d3js(){

		//contains the frequency for each character
		ArrayList<Integer> info = new ArrayList<Integer>();

		//print
		try{

			//measure the total frequency of each character
			for(int col = 9 + numExamine; col < outputMatrix.get(0).size(); col++){

				int total = 0;

				for(int row = 0; row < outputMatrix.size(); row++){

					String data = outputMatrix.get(row).get(col);

					int loc = data.indexOf(tab);

					while(loc != -1){
						data = data.substring(0, loc);
						loc = data.indexOf(tab);
					}

					char letter = data.charAt(0);
					if( (int) letter > 47 && (int) letter < 58)
						total += Integer.parseInt(data);
				}

				info.add(total);
			}

			//move the first element of info to the back - manually fixing b/c not sure why this happens
			int temp = info.get(0);
			info.remove(0);
			info.add(temp);

			//create or replace the file
			String title = currentPath + originalFileName + "_per PWD_" + "POS" + numExamine + "_" + dateAndTime + "_" + passwordType + "_d3js.txt";
			FileWriter outFile = new FileWriter(title);

			//output to file
			int max = info.size() - 1;
			for(int i = 0; i < max; i++)
				outFile.write(info.get(i) + tab);
			outFile.write(info.get(max));

			outFile.close();

		}catch(IOException e){
			System.out.println("IO issue");
			System.exit(-1);
		}

		return info;
	}

	//graphing data for javascript, prints frequency per position
	public ArrayList<ArrayList<Integer>> d3pos(){

		//contains the frequency for each character
		ArrayList<ArrayList<Integer>> info = new ArrayList<ArrayList<Integer>>();

		//print
		try{

			//measure the frequency
			for(int col = 9 + 1; col < 9 + 1 + numExamine; col++){

				int numUpper = 0;
				int numLower = 0;
				int numNumber = 0;
				int numSymbol = 0;
				int numSpaces = 0;

				for(int row = 0; row < outputMatrix.size(); row++){

					String data = outputMatrix.get(row).get(col);

					int loc = data.indexOf(tab);

					//trim off spaces just in case
					while(loc != -1){
						data = data.substring(0, loc);
						loc = data.indexOf(tab);
					}

					/*char letter = data.charAt(0);
					if(letter == 'U')
						numUpper++;
					else if(letter == 'L')
						numLower++;
					else if(letter == 'N')
						numNumber++;
					else if(letter == 'S')
						numSymbol++;
					else
						numSpaces++;*/
					String letter = data;
					if(letter.equals("U"))
						numUpper++;
					else if(letter.equals("L"))
						numLower++;
					else if(letter.equals("N"))
						numNumber++;
					else if(letter.equals("S"))
						numSymbol++;
					else if(letter.equals("WS"))
						numSpaces++;
				}

				ArrayList<Integer> dataSet = new ArrayList<Integer>();
				dataSet.add(col - 9);
				dataSet.add(numUpper);
				dataSet.add(numLower);
				dataSet.add(numNumber);
				dataSet.add(numSymbol);
				dataSet.add(numSpaces);

				info.add(dataSet);
			}

			//create or replace the file
			String title = currentPath + originalFileName + "_per PWD_" + "POS" + numExamine + "_" +  dateAndTime + "_" + passwordType + "_d3pos.txt";
			FileWriter outFile = new FileWriter(title);

			int maxRows = info.size()-1;
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

		return info;
	}
}