import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * un automate est représenté par un tableau a deux dimensions contenant des
 * listes d'entiers. - indice des colones : - 0 représente les etats atteignable
 * depuis une espilon transition - 1 à 127 inclus : charactère en ascii , ex : a
 * = 97 - 128 & 129 respectivement etat initial / final, si null = false, sinon
 * true. - les lignes représentent l'id des etats - chaque liste d'entier
 * indique quel etat (id) on peut atteindre
 */
public class Automaton {

	public static int AUTOMATON_NB_COLUMNS = 130;
	public static int EPSILON = 0;
	private static int INITIAL_STATE = 128;
	private static int FINAL_STATE = 129;

	/**
	 * Table de transition de l'automate
	 */
	private List<Integer>[][] transitions;

	/**
	 * Construit un automate de nbStates états
	 * 
	 * @param nbStates nombre d'états de l'automate
	 * @throws Exception si nbStates < 0
	 */
	@SuppressWarnings("unchecked")
	public Automaton(int nbStates) throws Exception {
		if (nbStates < 0)
			throw new Exception("Le nombre d'état d'un automate doit être >= 0.");
		this.transitions = new ArrayList[nbStates][AUTOMATON_NB_COLUMNS];
	}

	/**
	 * Renvoie la table de transitions de l'automate
	 * 
	 * @return table de transitions de l'automate
	 */
	public List<Integer>[][] getTransitions() {
		return transitions;
	}

	/**
	 * Renvoie le nombre d'états total de l'automate
	 * 
	 * @return nombre d'états de l'automate
	 */
	public int numberOfStates() {
		return transitions.length;
	}

	/**
	 * L'état stateId devient initial
	 * 
	 * @param stateId id d'etat qui doit devenir initial
	 */
	public void addInitialState(int stateId) {
		transitions[stateId][INITIAL_STATE] = new ArrayList<Integer>();
	}

	/**
	 * L'état stateId n'est plus initial
	 * 
	 * @param stateId id d'état qui ne doit plus être initial
	 */
	public void removeInitialState(int stateId) {
		transitions[stateId][INITIAL_STATE] = null;
	}

	/**
	 * L'état stateId devient final
	 * 
	 * @param stateId id d'état qui doit devenir final
	 */
	public void addFinalState(int stateId) {
		transitions[stateId][FINAL_STATE] = new ArrayList<Integer>();
	}

	/**
	 * L'état stateId n'est plus final
	 * 
	 * @param stateId id d'état qui ne doit plus être final
	 */
	public void removeFinalState(int stateId) {
		transitions[stateId][FINAL_STATE] = null;
	}

	/**
	 * Ajoute une transition stateId -> destId étiqueté par c
	 * 
	 * @param stateId etat de depart de la transition
	 * @param destId  etat d'arrivé de la transition
	 * @param c       etiquette de la transition (peut être Automaton.EPSILON)
	 */
	public void addTransition(int stateId, int destId, int c) {
		if (transitions[stateId][c] == null)
			transitions[stateId][c] = new ArrayList<Integer>();
		transitions[stateId][c].add(destId);
	}

	/**
	 * Retourne l'id de l'état initial dans l'automate
	 * 
	 * @return id de l'état initial
	 * @throws Exception si pas d'état initial
	 */
	public int getInitialStateId() throws Exception {
		// on parcourt les etats
		for (int i = 0; i < transitions.length; i++) {
			if (transitions[i][INITIAL_STATE] != null)
				return i;
		}
		throw new Exception("L'Automate n'a pas d'état initial");
	}

	/**
	 * Retourne l'id de l'état final dans l'automate
	 * 
	 * @return id de l'état final
	 * @throws Exception si pas d'état final
	 */
	public int getFinalStateId() throws Exception {
		// on parcourt les etats
		for (int i = 0; i < transitions.length; i++) {
			if (transitions[i][FINAL_STATE] != null)
				return i;
		}
		throw new Exception("L'Automate n'a pas d'état final");
	}

	/**
	 * Retourne true si l'état d'id stateId est initial, false sinon
	 * 
	 * @param stateId numéro de l'état
	 * @return true si l'état d'id stateId est initial, false sinon
	 * @throws Exception
	 */
	public boolean isInitial(int stateId) throws Exception {
		// si l'état n'existe pas on lève une exception
		if (stateId > numberOfStates())
			throw new Exception("L'Etat n°" + stateId + " n'existe pas dans l'automate.");
		return transitions[stateId][INITIAL_STATE] != null;
	}

	/**
	 * Retourne true si l'état d'id stateId est final, false sinon
	 * 
	 * @param stateId numéro de l'état
	 * @return true si l'état d'id stateId est final, false sinon
	 * @throws Exception
	 */
	public boolean isFinal(int stateId) throws Exception {
		// si l'état n'existe pas on lève une exception
		if (stateId > numberOfStates())
			throw new Exception("L'Etat n°" + stateId + " n'existe pas dans l'automate.");
		return transitions[stateId][FINAL_STATE] != null;
	}

	/**
	 * Fusionne deux automates en un nouveau automate de taille automaton1 +
	 * automaton2 + nbNewStates
	 * 
	 * @param automaton1  premier automate
	 * @param automaton2  deuxieme automate
	 * @param nbNewStates nombre de nouveaux etats à ajouter (0 si aucuns)
	 * @return nouvel automate fusionné
	 */
	public static Automaton mergeAutomatons(Automaton automaton1, Automaton automaton2, int nbNewStates)
			throws Exception {
		Automaton res = new Automaton(automaton1.transitions.length + automaton2.transitions.length + nbNewStates);
		// copie du premier automate dans res
		for (int i = 0; i < automaton1.transitions.length; i++)
			for (int j = 0; j < automaton1.transitions[i].length; j++)
				res.transitions[i][j] = automaton1.transitions[i][j];

		// copie du deuxieme automate dans res
		for (int i = 0; i < automaton2.transitions.length; i++)
			for (int j = 0; j < automaton2.transitions[i].length; j++) {
				// il faut corriger les "pointeurs" vers les ids des etats,
				// car les ids vont être incrémentés dans le nouvel automate
				List<Integer> l = null;
				// si il y avait des pointeurs, on les incrémentes
				if (automaton2.transitions[i][j] != null)
					l = automaton2.transitions[i][j].stream().map(e -> e + automaton1.transitions.length)
							.collect(Collectors.toList());
				// on associe la nouvelle liste à l'état copié dans le nouvel automate
				res.transitions[i + automaton1.transitions.length][j] = l;
			}

		return res;
	}

	/**
	 * Ne fonctionne que pour un dfa / dfa min. return true si le mot est reconnu
	 * par l'automate, false sinon
	 * 
	 * @param word mort à tester
	 * @return true si le mot est reconnu par l'automate, false sinon
	 */
	public boolean match(String word) throws Exception {
		int currentStateId = getInitialStateId();
		for (int i = 0; i < word.length(); i++) {
			// si le caractère n'est pas alphabetique on annule tout de suite
			if (!RegEx.isLetter(word.charAt(i)))
				return false;
			currentStateId = transitions[currentStateId][word.charAt(i)].get(0);
		}
		return isFinal(currentStateId);
	}

	/**
	 * Affiche un automate dans le terminal
	 */
	public void print() {
		System.out.println("\nAUTOMATON : ");
		// on parcours tout les états
		for (int i = 0; i < transitions.length; i++) {
			System.out.print("state n°" + i + " : ");

			// on parcours toute les colonnes
			for (int j = 0; j < AUTOMATON_NB_COLUMNS; j++) {

				// on affiche si l'état est initial / final
				if (j == INITIAL_STATE && transitions[i][j] != null)
					System.out.print("(initial state)");
				if (j == FINAL_STATE && transitions[i][j] != null)
					System.out.print("(final state)");

				// si la colonne admet des transitions on les affiches
				if (transitions[i][j] != null && j != INITIAL_STATE && j != FINAL_STATE) {
					if (j == EPSILON) {
						System.out.print("epsilon : ");
						printList(transitions[i][j]);
					}
					if (RegEx.isLetter(j)) {
						System.out.print("\'" + (char) (j) + "' : ");
						printList(transitions[i][j]);
					}
					System.out.print(" / ");
				}
			}

			System.out.println();
		}
		System.out.println("\n");
	}

	/**
	 * Affiche une liste dans le terminal
	 * 
	 * @param list liste à afficher
	 */
	private static void printList(List<Integer> list) {
		System.out.print("[");
		for (int i = 0; i < list.size(); i++) {
			if (i == 0)
				System.out.print(list.get(i));
			else
				System.out.print("," + list.get(i));
		}
		System.out.print("]");
	}

}
