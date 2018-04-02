package ru.krista.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class CheckingFile {
	
	private static File reportFiles = new File("../logs/logging.log");
	
	public static void addCheck (String name) {
		try {
			String fileName = name+"\n";
            Files.write(Paths.get(reportFiles.getAbsolutePath()), fileName.getBytes(), StandardOpenOption.APPEND);
        }
        catch (IOException e) {
            System.out.println(e);
        }
	}
	
	public static boolean getCheck(File file) {
		String fileName = file.getName();
		try {
	         FileReader reader = new FileReader(reportFiles);
	         BufferedReader in = new BufferedReader(reader);
	         String checkedFile;
	         while ((checkedFile = in.readLine()) != null) {
	        	 if (checkedFile.contains(fileName)) {
	        	   return false;
	           }
	         }
	         in.close();
	       } catch (IOException e) {
	         e.printStackTrace();
	       }
		return true;
	}
	

}
