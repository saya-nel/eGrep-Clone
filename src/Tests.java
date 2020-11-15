import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class Tests {

	private static String[] regexs = { "S(a|g|r)*on", "ab*", "ab+", "ab|er", "Sargon"};
	private static BufferedWriter br;
	private static String testFilePath = "../tests/babylone.txt";
	private static String outFilePath = "../results.dat";

	private static void processTests() {
		for (String regex : regexs) {
			try {
				br.write(regex + " ");
				// java program speed test
				long start = System.currentTimeMillis();
				RegEx.regEx = regex;
				RegExTree ret = RegEx.parse();
				Automaton ndfa = Algorithms.astToNDFA(ret);
				Automaton dfa = Algorithms.ndfaToDFA(ndfa);
				Automaton dfaMin = Algorithms.dfaToMin(dfa);
				long searchStart = System.currentTimeMillis();
				PatternSearch search = new PatternSearch(dfaMin, testFilePath, false);
				search.runPatternSearchOnFile();
				long searchEnd = System.currentTimeMillis();
				long end = System.currentTimeMillis();
				// écriture construction + recherche
				br.write("" + (end - start));
				br.write(" ");
				// écriture que recherche
				br.write("" + (searchEnd - searchStart));
				br.write(" ");

				// egrep speed test
				ProcessBuilder processBuilder = new ProcessBuilder();
				start = System.currentTimeMillis();

				processBuilder.command("egrep", regex, testFilePath);
				Process process = processBuilder.start();
				StringBuilder output = new StringBuilder();

				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

				String line;
				while ((line = reader.readLine()) != null) {
					output.append(line + "\n");
				}

				int exitVal = process.waitFor();
				if (exitVal == 0) {
//		            System.out.println("Success!");
//		            System.out.println(output);
				}

				end = System.currentTimeMillis();
				br.write("" + (end - start));

				br.newLine();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		try {
			br = new BufferedWriter(new FileWriter(new File(outFilePath)));
			br.write("commande construction+recherche recherche egrep\n");
			processTests();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
