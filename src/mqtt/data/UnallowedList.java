package mqtt.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

public class UnallowedList {
	public Vector<String> list;
	private BufferedWriter w = null;
	private BufferedReader r = null;
	public static String fileName = "data/UnallowedList.txt";
	
	public UnallowedList() {
		list = new Vector<String>();
		//fileName = "data/UnallowedList.txt";
		
		String str;
		try {
			r = new BufferedReader(new FileReader(fileName));
			while((str = r.readLine()) != null) {
				list.add(str);
			}
			r.close();
		} catch(IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			w = new BufferedWriter(new FileWriter(fileName, true));
			w.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	// file���� lineToRemove �� �� ������ �� ���� ����� ���� ���� ����
	public static void removeLineFromFile(String file, String lineToRemove) {

        try {
            File inFile = new File(file);

            if (!inFile.isFile()) {
                System.out.println("Parameter is not an existing file");
                return;
            }

            // Construct the new file that will later be renamed to the original
            // filename.
            File tempFile = new File(inFile.getAbsolutePath() + ".tmp");

            BufferedReader br = new BufferedReader(new FileReader(file));
            PrintWriter pw = new PrintWriter(new FileWriter(tempFile));

            String line = null;

            // Read from the original file and write to the new
            // unless content matches data to be removed.
            while ((line = br.readLine()) != null) {
                if (!line.trim().equalsIgnoreCase(lineToRemove)) {
                    pw.println(line);
                    pw.flush();
                }
            }
            pw.close();
            br.close();

            // Delete the original file
            if (!inFile.delete()) {
                System.out.println("Could not delete file");
                return;
            }

            // Rename the new file to the filename the original file had.
            if (!tempFile.renameTo(inFile))
                System.out.println("Could not rename file");

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
 
	
}
