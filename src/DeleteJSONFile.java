import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class DeleteJSONFile {
	
	public static void main(String[] args) {
		
	
		for (int j = 1; j < 11; j++)
		{
			File file = null;
			file = new File("IEEE"+ j +".json");
			file.delete();
			file = new File("ACM" + j + ".json");
			file.delete();
			file = new File("NJ" + j + ".json");
			file.delete();
		}
	}
}
