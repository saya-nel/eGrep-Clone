public class Main {

	public static void main(String arg[]) {
		if (arg.length == 0 || arg[0] == null || arg[0].length() == 0) {
			System.out.println("Le premier argument doit être spécifié (expression regulière)");
			return;
		} else if (arg.length < 2 || arg[1] == null || arg[1].length() == 0) {
			System.out.println("Le deuxième argument doit être spécifié (chemin relatif du fichier)");
			return;
		}

		String regEx = arg[0];
		RegExTree ret = null;

		System.out.println("  >> Parsing regEx \"" + regEx + "\".");
		System.out.println("  >> ...");

		if (regEx.length() < 1) {
			System.err.println("  >> ERROR: empty regEx.");
		} else {
			System.out.print("  >> ASCII codes: [" + (int) regEx.charAt(0));
			for (int i = 1; i < regEx.length(); i++)
				System.out.print("," + (int) regEx.charAt(i));
			System.out.println("].");
			try {
				RegEx.regEx = regEx;
				ret = RegEx.parse();
				System.out.println("  >> Tree result: " + ret.toString() + ".");
			} catch (Exception e) {
				System.err.println("  >> ERROR: syntax error for regEx \"" + regEx + "\".");
			}
			try {
				if (ret != null) {
					System.out.println("\nBuild automaton");
					Automaton ndfa = Algorithms.astToNDFA(ret);
//					ndfa.print();
					Automaton dfa = Algorithms.ndfaToDFA(ndfa);
//					dfa.print();
					Automaton dfaMin = Algorithms.dfaToMin(dfa);
					System.out.println("Done.");
					dfaMin.print();
					System.out.println("Launch search\n\n========================================================\n");
					PatternSearch search = new PatternSearch(dfaMin, arg[1]);
					search.runPatternSearchOnFile();
				}
			} catch (Exception e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}

}
