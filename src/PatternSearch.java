import java.io.BufferedReader;
import java.io.FileReader;

public class PatternSearch {

	private Automaton pattern;
	private String filePath;
	private boolean display;

	/**
	 * Construit une recherche avec un motif et un chemin de fichier
	 * 
	 * @param pattern  automate representant le motif
	 * @param filePath chemin du fichier
	 * @param display  true if need display results on standard output, false else
	 */
	public PatternSearch(Automaton pattern, String filePath, boolean display) {
		this.pattern = pattern;
		this.filePath = filePath;
		this.display = display;
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
		int actualStateId = pattern.getInitialStateId();

		for (int i = 1; i < line.length(); i++) {
			// on "mange" le caractère courant
			if (RegEx.isLetter(line.charAt(i))) {
				actualStateId = pattern.getTransitions()[actualStateId][line.charAt(i)].get(0);
				// si on est sur un état final, la ligne contient un motif respectant la regex
				if (pattern.isFinal(actualStateId))
					return true;
				// si on est sur un état puit, le motif n'est pas reconnu, on repart de l'état
				// initial
				if (pattern.isWell(actualStateId))
					actualStateId = pattern.getInitialStateId();
			} else
				actualStateId = pattern.getInitialStateId();
		}
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
				if (display && lineContainsPattern)
					System.out.println(currentLine);
				currentLine = reader.readLine();
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
