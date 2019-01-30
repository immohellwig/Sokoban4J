package generic;

import java.util.ArrayList;
import java.util.List;

public class Node<S, A> implements Comparable<Node<S,A>> {
	private S state;
	private Node<S, A> father; // parent node, or null if this is the start node
	final private A lastAction; // the action we took to get here from the parent
	private double pathCost;
	private double estimation;
	
	//root constructor
	
	public Node(Problem<S, A> problem) {
		state = problem.initialState();
		pathCost = 0;
		lastAction = null;
		estimation = problem.estimate(state);
	}
	
	private Node(A action, Node<S, A> father, Problem<S, A> problem) {
		lastAction = action;
		pathCost = father.getPathCost() + problem.cost(father.getState(), lastAction);
		state = problem.result(father.getState(), action);
		estimation = problem.estimate(state);
		this.father = father;
	}
	
	// Getter + Setter
	
	public Node<S, A> getFather() {
		return father;
	}
	
	public List<Node<S, A>> getListOfChildren(Problem<S, A> problem) {
		ArrayList<Node<S, A>> children = new ArrayList<Node<S, A>>();
		for (A currentAction : problem.actions(getState())) {
			children.add(new Node<S, A>(currentAction, this, problem));
		}
		return children;
	}
	
	public A getLastAction() {
		return lastAction;
	}
	
	public S getState() {
		return state;
	}
	
	public double getCombinedCost() {
		return pathCost + estimation;
	}
	
	public double getPathCost() {
		return pathCost;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Node<?,?>) {
			Node<?, ?> node = (Node<?, ?>) obj;
			return getState().equals(node.getState());
		} else {
			System.err.println("Tried to compare another datatype with Node");
			return false;
		}
	}

	@Override
	public int compareTo(Node<S, A> node) {
		return Double.compare(getCombinedCost(), node.getCombinedCost());
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return state + " " + getCombinedCost();
	}
}
