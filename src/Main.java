import java.util.Scanner;

public class Main {

	public static void main(String arg[]) {
		String regEx;
		RegExTree ret = null;

		if (arg.length != 0) {
			regEx = arg[0];
		} else {
			Scanner scanner = new Scanner(System.in);
			System.out.print("  >> Please enter a regEx: ");
			regEx = scanner.next();
			RegEx.regEx = regEx;
		}

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
				ret = RegEx.parse();
				System.out.println("  >> Tree result: " + ret.toString() + ".");
			} catch (Exception e) {
				System.err.println("  >> ERROR: syntax error for regEx \"" + regEx + "\".");
			}
			try {
				// NDFA
				System.out.println("\nBuild NDFA automaton");
				if (ret != null) {
					Automaton ndfa = Algorithms.astToNDFA(ret);
					ndfa.print();
					Automaton dfa = Algorithms.ndfaToDFA(ndfa);
					dfa.print();
					Automaton dfaMin = Algorithms.dfaToMin(dfa);
					dfaMin.print();
				}
			} catch (Exception e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}

}
