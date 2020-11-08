import java.io.BufferedReader;
import java.io.FileReader;

public class PatternSearch {

	private Automaton pattern;
	private String filePath;

	/**
	 * Construit une recherche avec un motif et un chemin de fichier
	 * 
	 * @param pattern  automate representant le motif
	 * @param filePath chemin du fichier
	 */
	public PatternSearch(Automaton pattern, String filePath) {
		this.pattern = pattern;
		this.filePath = filePath;
	}

	/**
	 * Renvoie true si substring est reconnu p
	 * 
	 * @param substring substring to test
	 * @return true if the substring match the pattern, false else
	 */
	private boolean substringMatch(String substring) {
		try {
			return pattern.match(substring);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Renvoie true si une sous chaine de la chaine match avec le pattern, false
	 * sinon
	 * 
	 * @param line ligne à tester
	 * @return true si une sous chaine de la chaine match avec le pattern, false
	 *         sinon
	 */
	private boolean lineSearch(String line) {
		for (int i = 0; i < line.length(); i++)
			for (int j = i + 1; j <= line.length(); j++)
				if (substringMatch(line.substring(i, j)))
					return true;
		return false;
	}

	/**
	 * Lance la recherche
	 */
	public void runPatternSearchOnFile() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			String currentLine = reader.readLine();
			while (currentLine != null) {
				boolean lineContainsPattern = lineSearch(currentLine);
				if (lineContainsPattern)
					System.out.println(currentLine);
				currentLine = reader.readLine();
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
