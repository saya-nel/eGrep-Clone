import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class Algorithms {

	/*
	 * 
	 * ALGORITHMES
	 * 
	 */

	/**
	 * Transforme un AST en ndfa avec epsilon transitions
	 * 
	 * @param ast ast
	 * @return ndfa avec epsilon transitions
	 * @throws Exception
	 */
	public static Automaton astToNDFA(RegExTree ast) throws Exception {
		// on parcours le noeud courant
		int root = ast.root;

		// si on tombe sur un caractère
		if (RegEx.isLetter(root)) {
			// on créer l'automate du caractère
			Automaton res = new Automaton(2);
			res.addInitialState(0); // état initial
			res.addFinalState(1); // état final
			// transition
			res.addTransition(0, 1, root);
			// pas de fils à explorer dans ce cas, on return l'automate
			return res;
		}

		// si on tombe sur un "|"
		else if (root == RegEx.ALTERN) {
			// si le noeud n'a pas deux enfants, on lève une exception
			if (ast.subTrees.size() != 2)
				throw new Exception("AST mal former");

			// on construit l'automate des deux enfants
			Automaton leftChild = astToNDFA(ast.subTrees.get(0));
			Automaton rightChild = astToNDFA(ast.subTrees.get(1));

			// les noeuds initiaux / finaux ne le sont plus
			int leftOldInitialStateId = leftChild.getInitialStateId();
			leftChild.removeInitialState(leftOldInitialStateId);
			int leftOldFinalStateId = leftChild.getFinalStateId();
			leftChild.removeFinalState(leftOldFinalStateId);
			int rightOldInitialStateId = rightChild.getInitialStateId();
			rightChild.removeInitialState(rightOldInitialStateId);
			int rightOldFinalStateId = rightChild.getFinalStateId();
			rightChild.removeFinalState(rightOldFinalStateId);
			rightOldInitialStateId += leftChild.numberOfStates();
			rightOldFinalStateId += leftChild.numberOfStates();
			// on fusionne les automates en ajoutant 2 états vides
			Automaton res = Automaton.mergeAutomatons(leftChild, rightChild, 2);
			// l'avant dernier etat devient initial et admet une epsilon transition vers les
			// anciens etats initiaux
			res.addInitialState(res.numberOfStates() - 2);
			res.addTransition(res.getInitialStateId(), leftOldInitialStateId, Automaton.EPSILON);
			res.addTransition(res.getInitialStateId(), rightOldInitialStateId, Automaton.EPSILON);
			// le dernier etat devient final
			res.addFinalState(res.numberOfStates() - 1);
			// les anciens etat finaux admettent une admettent une transition vers le nouvel
			// etat final
			res.addTransition(leftOldFinalStateId, res.getFinalStateId(), Automaton.EPSILON);
			res.addTransition(rightOldFinalStateId, res.getFinalStateId(), Automaton.EPSILON);
			return res;
		}

		// si on tombe sur un "."
		else if (root == RegEx.CONCAT) {
			// si le noeud n'a pas deux enfants, on lève une exception
			if (ast.subTrees.size() != 2)
				throw new Exception("AST mal former");

			// on construit l'automate des deux enfants
			Automaton leftChild = astToNDFA(ast.subTrees.get(0));
			Automaton rightChild = astToNDFA(ast.subTrees.get(1));
			// le noeud final de left n'est plus final
			int leftOldFinalStateId = leftChild.getFinalStateId();
			leftChild.removeFinalState(leftOldFinalStateId);
			// le noeud initial de right n'est plus initial
			int rightOldInitialStateId = rightChild.getInitialStateId();
			rightChild.removeInitialState(rightOldInitialStateId);
			// position de l'ancien etat initial de right dans l'automate fusionné
			rightOldInitialStateId += leftChild.numberOfStates();
			// on fusionne les automates
			Automaton res = Automaton.mergeAutomatons(leftChild, rightChild, 0);
			// on met une epsilon transition de l'ancien etat final de left a l'ancien etat
			// initial de right
			res.addTransition(leftOldFinalStateId, rightOldInitialStateId, Automaton.EPSILON);
			return res;
		}

		// si on tombe sur "*"
		else if (root == RegEx.ETOILE) {
			// si le noeud n'a pas un enfant, on lève une exception
			if (ast.subTrees.size() != 1)
				throw new Exception("AST mal former");

			// on construit l'automate de l'enfant
			Automaton child = astToNDFA(ast.subTrees.get(0));

			// le noeud initial / final ne l'est plus
			int oldInitialStateId = child.getInitialStateId();
			child.removeInitialState(oldInitialStateId);
			int oldFinalStateId = child.getFinalStateId();
			child.removeFinalState(oldFinalStateId);
			// l'ancien etat final admet une epsilon transition vers l'ancien etat initial
			child.addTransition(oldFinalStateId, oldInitialStateId, Automaton.EPSILON);
			// on créer un nouvel automate contenant le fils + 2 noeuds
			Automaton emptyAutomaton = new Automaton(0);
			Automaton res = Automaton.mergeAutomatons(child, emptyAutomaton, 2);
			// le dernier etat devient final
			res.addFinalState(res.numberOfStates() - 1);
			// l'ancien etat final admet une transition vers le nouvel etat final
			res.addTransition(oldFinalStateId, res.getFinalStateId(), Automaton.EPSILON);
			// l'avant dernier etat devient initial et admet une epsilon transition vers
			// l'ancien etat initial et vers le nouvel etat final
			res.addInitialState(res.numberOfStates() - 2);
			res.addTransition(res.getInitialStateId(), oldInitialStateId, Automaton.EPSILON);
			res.addTransition(res.getInitialStateId(), oldFinalStateId, Automaton.EPSILON);
			return res;
		}

		// si on tombe sur "+"
		else if (root == RegEx.PLUS) {
			// si le noeud n'a pas un enfant, on lève une exception
			if (ast.subTrees.size() != 1)
				throw new Exception("AST mal former");

			// on construit l'automate de l'enfant
			Automaton child = astToNDFA(ast.subTrees.get(0));

			// le noeud initial / final ne l'est plus
			int oldInitialStateId = child.getInitialStateId();
			child.removeInitialState(oldInitialStateId);
			int oldFinalStateId = child.getFinalStateId();
			child.removeFinalState(oldFinalStateId);
			// l'ancien etat final admet une epsilon transition vers l'ancien etat initial
			child.addTransition(oldFinalStateId, oldInitialStateId, Automaton.EPSILON);
			// on créer un nouvel automate contenant le fils + 2 noeuds
			Automaton emptyAutomaton = new Automaton(0);
			Automaton res = Automaton.mergeAutomatons(child, emptyAutomaton, 2);
			// le dernier etat devient final
			res.addFinalState(res.numberOfStates() - 1);
			// l'ancien etat final admet une transition vers le nouvel etat final
			res.addTransition(oldFinalStateId, res.getFinalStateId(), Automaton.EPSILON);
			// l'avant dernier etat devient initial et admet une epsilon transition vers le
			// nouvel etat final
			res.addInitialState(res.numberOfStates() - 2);
			res.addTransition(res.getInitialStateId(), oldFinalStateId, Automaton.EPSILON);
			return res;
		}

		throw new Exception("AST mal former, caractère non reconnu :" + (char) root);
	}

	/**
	 * Renvoie l'epsilon closure d'un etat dans un automate (tout les etats
	 * atteignables depuis l'état en le consummant que des epsilons)
	 * 
	 * @param automaton automate
	 * @param stateId   id de l'état donc on calcul l'espilon closure
	 * @return ensemble des etats atteignables en ne consummant que epsilon
	 */
	public static Set<Integer> epsilonClosure(Automaton automaton, int stateId) {
		// on initialise l'ensemble avec l'état lui même dedans
		Set<Integer> res = new HashSet<Integer>();
		res.add(stateId);
		// on récupère tout les etats atteignables avec espilon
		List<Integer> currentEpsilonTransitions = automaton.getTransitions()[stateId][Automaton.EPSILON];
		// on calcul leurs epsilon closure et les ajoutes dans le set resultat
		if (currentEpsilonTransitions != null)
			for (Integer id : currentEpsilonTransitions) {
				Set<Integer> epsilonClosure = epsilonClosure(automaton, id);
				for (Integer toAdd : epsilonClosure)
					res.add(toAdd);
			}
		return res;
	}

	/**
	 * Renvoie l'ensemble des états atteignables depuis l'état stateId et consummant
	 * un seul caractère c
	 * 
	 * @param automaton automate
	 * @param stateId   etat
	 * @param c         caractere
	 * @return l'ensemble des états atteignables depuis l'état stateId et consummant
	 *         un seul caractère c
	 */
	public static Set<Integer> reachableByChar(Automaton automaton, int stateId, int c) {
		Set<Integer> res = new HashSet<Integer>();
		// on calcul l'epsilon closure de stateId
		Set<Integer> epsClosure = epsilonClosure(automaton, stateId);
		// pour chaque etat dans l'epsilon closure, si il a une/des transition etiqueté
		// c
		// on ajoute l'état de destination de cette/ces transition dans l'ensemble
		// resultat
		for (Integer epsClosureState : epsClosure) {
			List<Integer> transitions = automaton.getTransitions()[epsClosureState][c];
			if (transitions != null)
				for (Integer id : transitions)
					res.add(id);
		}
		return res;
	}

	/**
	 * Transforme un ndfa avec epsilon transitions en dfa
	 * 
	 * @param automaton ndfa
	 * @return dfa
	 * @throws Exception
	 */
	public static Automaton ndfaToDFA(Automaton automaton) throws Exception {
		// le résultat, dans le pire des cas aura 2^n états, avec n = nombre d'états de
		// automaton
		Automaton res = new Automaton((int) Math.pow(2, automaton.numberOfStates()));
		// fais correspondre a un ensemble d'état un stateId dans l'automate final
		Map<Set<Integer>, Integer> bind = new HashMap<Set<Integer>, Integer>();
		int count = 0; // necessaire pour le bind

		// on se place sur l'état initial au debut et calcul son epsilon closure
		int current = automaton.getInitialStateId();
		Set<Integer> currentDFAState = epsilonClosure(automaton, current);
		// c'est le premier etat du nouvel automate, qui sera aussi initial, on le bind
		bind.put(currentDFAState, count);
		res.addInitialState(count);
		count++;

		while (currentDFAState != null) {

			// on parcours chaque carractère de l'alphabet reconnu par l'automate
			for (int i = 0; i < Automaton.AUTOMATON_NB_COLUMNS; i++) {
				if (RegEx.isLetter(i)) {

					Set<Integer> reachables = new HashSet<Integer>();
					// pour chaque etat du NFA contenu dans currentDFA, on recupere l'ensemble des
					// etats atteignables en lisant i et on ajoute tout les elements de cet ensemble
					// à reachables
					for (Integer stateId : currentDFAState) {
						Set<Integer> transitions = reachableByChar(automaton, stateId, i);
						for (Integer j : transitions)
							reachables.add(j);
					}
					// on créer un nouvel etat du DFA qui correspond à l'union de toute les epsilon
					// closure de reachables
					Set<Integer> newState = new HashSet<Integer>();
					for (Integer stateId : reachables) {
						Set<Integer> epsilonClosure = epsilonClosure(automaton, stateId);
						for (Integer j : epsilonClosure)
							newState.add(j);
					}

					// si le nouvel etat n'existe pas dans l'ensemble d'états du DFA, on l'ajoute
					if (!bind.containsKey(newState)) {
						// on bind le nouvel etat
						bind.put(newState, count);
						count++;
					}
					// on met une transition de currentDFAState vers newState etiqueté par i dans
					// DFA
					res.addTransition(bind.get(currentDFAState), bind.get(newState), i);

					// on passe au caractère suivant
				}
			}

			// on a fait toute les caractères pour currentDFAState, on passe à l'état
			// suivant du DFA qu'on à découvert
			// après currentDFAState si il y en a un
			Integer next = bind.get(currentDFAState) + 1;
			if (bind.containsValue(next)) { // un etat suivant existe
				// on récupère la clé qui correspond a next
				Stream<Set<Integer>> keysStream = bind.entrySet().stream().filter(entry -> next == entry.getValue())
						.map(Map.Entry::getKey);
				Set<Integer> nextDFAState = keysStream.findFirst().get();
				// si il contient un des etats finaux dans le NFA, il est final dans le DFA
				if (nextDFAState.contains(automaton.getFinalStateId()))
					res.addFinalState(bind.get(nextDFAState));
				currentDFAState = nextDFAState;
			} else // sinon, on a fini
				currentDFAState = null;
		}

		// on a plus qu'à retiré tout les etats inutiles dans res grace à count
		Automaton res2 = new Automaton(count);
		for (int i = 0; i < res2.numberOfStates(); i++) {
			for (int j = 0; j < Automaton.AUTOMATON_NB_COLUMNS; j++) {
				res2.getTransitions()[i][j] = res.getTransitions()[i][j];
			}
		}

		return res2;
	}

	private static Optional<Integer> getEquivalent(boolean[][] areEquivalent, int stateId) {
		for (int i = 1; i < areEquivalent.length; i++) {
			for (int j = 0; j < i; j++) {
				if (areEquivalent[i][j] && i == stateId)
					return Optional.of(j);
				else if (areEquivalent[i][j] && j == stateId)
					return Optional.of(i);
			}
		}
		return Optional.empty();
	}

	/**
	 * Minimise un dfa
	 * 
	 * @param automaton automate à minimiser
	 * @return automate minimal
	 */
	public static Automaton dfaToMin(Automaton automaton) throws Exception {
		// on construit une matrice n * n avec n = nombre d'états de l'automate
		boolean[][] areEquivalent = new boolean[automaton.numberOfStates()][automaton.numberOfStates()];
		// tout le long de l'algo on s'intéresse que à un coté de la matrice, et on
		// ignore les i et j tq i = j

		// on commence par tout mettre à true
		for (int i = 1; i < areEquivalent.length; i++)
			for (int j = 0; j < i; j++)
				areEquivalent[i][j] = true;

		// on parcours seulement un coté de la matrice, et pas les i, j tq i == j
		for (int i = 1; i < areEquivalent.length; i++)
			for (int j = 0; j < i; j++)
				// si les états i et j ne sont pas tout les deux initiaux ou tout les deux
				// finaux, ils ne sont pas équivalents
				if ((automaton.isInitial(i) && !automaton.isInitial(j))
						|| (!automaton.isInitial(i) && automaton.isInitial(j))
						|| (automaton.isFinal(i) && !automaton.isFinal(j))
						|| (!automaton.isFinal(i) && automaton.isFinal(j)))
					areEquivalent[i][j] = false;

		// on va marquer les etats non équivalents selon leurs transitions, on continue
		// tans que des etats ont été marqués non équivalent
		boolean hasChanged = true;
		while (hasChanged) {
			hasChanged = false;
			for (int i = 1; i < areEquivalent.length; i++) {
				for (int j = 0; j < i; j++) {
					// si ils ne sont pas deja marqués non équivalent
					if (areEquivalent[i][j])
						// on parcours l'ensemble des caractères :
						for (int k = 0; k < Automaton.AUTOMATON_NB_COLUMNS; k++)
							if (RegEx.isLetter(k)) {
								// si i et j on des transitions différentes pour k
								int destState1 = automaton.getTransitions()[i][k].get(0);
								int destState2 = automaton.getTransitions()[j][k].get(0);
								if (destState1 != destState2) {
									// pour regarder toujours le même coté de la matrice, il faut que l corresponde
									// a
									// max(destState1, destState2) et m au min , avec l = ligne et m = colonne.
									// Si la case est a false, alors on met aussi celle [i][j] à false
									if (!areEquivalent[Math.max(destState1, destState2)][Math.min(destState1,
											destState2)]) {
										areEquivalent[i][j] = false;
										hasChanged = true;
									}
								}
							}

				}
			}
		}

		// on a maintenant la table des équivalences, on fusionne les états equivalents
		// q1 q2 tq il ne restera que q1 dans l'automate, et toute les transitions
		// pointant vers q2 pointerons desormais vers q1

		// on construt les etat dans l'ordre, puis quand on voit un etat equivalent a un
		// deja construit
		// on signal qu'il pointe vers le meme etat dans le nouvel automate
		// ainsi on trouvera a chaque fois comment rediriger les transitions et quels
		// etats construire ou non
		Map<Integer, Integer> rename = new HashMap<Integer, Integer>();
		for (int i = 0; i < automaton.numberOfStates(); i++) {
			rename.put(i, i);
		}

		for (int i = 1; i < areEquivalent.length; i++) {
			for (int j = 0; j < i; j++) {
				// si ils sont équivalents, i devient le nouveau j dans le nouvel automate
				if (areEquivalent[i][j]) {
					rename.put(i, rename.get(j));
					// décrimente tout ceux > i car on a fusionné deux etats
					for (int k = i + 1; k < areEquivalent.length; k++)
						rename.put(k, rename.get(k) - 1);
					break;
				}
			}
		}

		// l'automate resultat aura pour nombre d'état le nombre d'états destinations
		// uniques
		// dans rename
		int nbEquivalentStates = new HashSet<Integer>(rename.values()).size();
		// on construit l'automate résultat
		Automaton res = new Automaton(nbEquivalentStates);

		// on construit le nouvel automate
		for (int i = 0; i < res.numberOfStates(); i++) {
			// on regarde quel etat est associé à i dans l'ancien automate
			final int newStateId = i;
			Stream<Integer> keysStream = rename.entrySet().stream().filter(entry -> newStateId == entry.getValue())
					.map(Map.Entry::getKey);
			Integer oldStateId = keysStream.findFirst().get();
			// on copie les transitions de l'ancien etat en les actualisant via rename
			for (int j = 0; j < Automaton.AUTOMATON_NB_COLUMNS; j++) {
				if (RegEx.isLetter(j))
					res.addTransition(i, rename.get(automaton.getTransitions()[oldStateId][j].get(0)), j);
			}
		}

		// fixe etat initiaux / finaux
		for (int i = 0; i < automaton.numberOfStates(); i++) {
			if (automaton.isInitial(i))
				res.addInitialState(rename.get(i));
			if (automaton.isFinal(i))
				res.addFinalState(rename.get(i));
		}

		return res;
	}

}
