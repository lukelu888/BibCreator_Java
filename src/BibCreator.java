/**
 * @topic Java Final Project - Bib Creatot
 * @author Yinglin Lu - 2212059
 * @deadline Dec 20, 2022
 */
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BibCreator {

	public static int invalid = 0;

	public static void main(String[] args) {
		System.out.println("\n====================== Welcome to BibCreator! ======================\n");

		Scanner readFile = null;
		PrintWriter writeFile = null;
		
		// read and validate .bib file and write eligible .bib file to .json file
		for (int i = 1; i < 11; i++) {

			try {

				// open input .bib files
				readFile = new Scanner(new FileInputStream("Latex" + i + ".bib"));

				// validate file and write valid .bib file to .json file
				processFilesForValidation(readFile, i, writeFile);

			} catch (FileNotFoundException e) {
				readFile.close();
				System.out.println("Cannot open input file Latex" + i
						+ ".bib for reading.\nPlease check if file exists! Program will terminate after closing any opened files.");
				System.exit(0);
			} catch (FileInvalidException e) {
				System.out.println(e.getMessage());
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}

		}
		System.out.println("A total of " + invalid + " files were invalid, and could not be processed. All other "
				+ (10 - invalid) + " files have been created.\n");

		
		// read json file
		BufferedReader br = null;
		Scanner scan = new Scanner(System.in);
		int count = 0;
		while (count < 2) {
			try {
				System.out.print("Please enter the name of one of the files that you need to review: ");
				String fileName = scan.nextLine().trim();
				
				br = new BufferedReader(new FileReader(fileName));
				System.out.println("Here are the contents of the successfully created JSON file: " + fileName + "\n");
				String line = br.readLine();

				while (line != null) {
					System.out.println(line);
					line = br.readLine();
				}
				br.close();
				break;
			} catch (IOException e) {
				if (count == 0) {
					System.out.println(
							"Could not open input file. File does not exist; possibly it could not be created!\nYou have another chance to enter a file name.\n\n");
				}else{
					System.out.println(
							"Could not open input file again!File does not exist; possibly it could not be created!\nProgram will exit!\n\n");
					System.exit(0);
				}
				count++;
			}
		}
		scan.close();

		System.out.print("Goodbye! Hope you enjoyed creating the needed files using BibCreator.");
	}

	private static void processFilesForValidation(Scanner readFile, int i, PrintWriter writeFile)
			throws FileInvalidException, FileNotFoundException, IOException {
		String bibFileContent = "";
		while (readFile.hasNextLine()) {
			bibFileContent += readFile.nextLine();
		}
		readFile.close();
		String ieee = "", acm = "", nj = "";
		int articleIndex = 1; // used to increment article numbers for ACM files

		StringTokenizer articleTokenizer = new StringTokenizer(bibFileContent, "@");

		while (articleTokenizer.hasMoreTokens()) {
			String article = articleTokenizer.nextToken();
			String[] authors = { "" };
			String title = "", journal = "", volume = "", number = "", pages = "", month = "", year = "", doi = "";

			// regex to find fields
			Pattern patternFields = Pattern.compile("([a-zA-Z]*?)=\\{(.*?)\\}"); //.*? make ungreedy: match at least as possible
			Matcher matcherFields = patternFields.matcher(article);

			while (matcherFields.find()) // find each field and check for validity
			{
				String field = matcherFields.group();

				String[] splitField = field.split("=");
				String fieldName = splitField[0];
				String fieldValue = splitField[1].substring(1, splitField[1].length() - 1).trim();

				if (fieldValue.isEmpty()) {
					invalid++;
					throw new FileInvalidException("Error: Detected Empty Field!\n" + "============================\n\n"
							+ "Problem detected with input file: Latex" + i + ".bib\n" + "File is Invalid: Field \""
							+ fieldName
							+ "\" is empty. Processing stopped at this point. Other empty fields may be present as well!\n");

				} else if (fieldName.equals("author")) {
					authors = fieldValue.split(" and ");

				} else if (fieldName.equals("title")) {
					title = fieldValue;

				} else if (fieldName.equals("journal")) {
					journal = fieldValue;

				} else if (fieldName.equals("volume")) {
					volume = fieldValue;

				} else if (fieldName.equals("number")) {
					number = fieldValue;

				} else if (fieldName.equals("pages")) {
					pages = fieldValue;

				} else if (fieldName.equals("month")) {
					month = fieldValue;

				} else if (fieldName.equals("year")) {
					year = fieldValue;

				} else if (fieldName.equals("doi")) {
					doi = fieldValue;
				}
			}

			// get IEEE info
			ieee += String.join(", ", authors);
			ieee += ". \"" + title + "\", " + journal + ", vol. " + volume + ", no. " + number + ", p. " + pages + ", "
					+ month + " " + year + ".\n\n";

			// get ACM info
			String autorsInfo;
			if (authors.length > 1) {
				autorsInfo = authors[0] + " et al. ";
			} else {
				autorsInfo = authors[0] + ". ";
			}
			acm += "[" + articleIndex + "] " + autorsInfo + year + ". " + title + ". " + journal + ". " + volume + ", "
					+ number + " (" + year + "), " + pages + ". DOI:https://doi.org/" + doi + "\n\n";
			articleIndex++;

			// get NJ info
			nj += String.join(" & ", authors);
			nj += ". " + title + ". " + journal + ". " + volume + ", " + pages + "(" + year + ").\n\n";
		}

		// create IEEE.json file
		writeFile = new PrintWriter(new FileOutputStream("IEEE" + i + ".json"));
		writeFile.println(ieee);
		writeFile.close();

		// create ACM.json file
		writeFile = new PrintWriter(new FileOutputStream("ACM" + i + ".json"));
		writeFile.println(acm);
		writeFile.close();

		// create NJ.json file
		writeFile = new PrintWriter(new FileOutputStream("NJ" + i + ".json"));
		writeFile.println(nj);
		writeFile.close();
	}
}
