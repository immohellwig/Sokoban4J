package generic;

import java.util.HashMap;
import java.util.PriorityQueue;

public class AStar<S, A> {
	public static <S, A> Node<S, A> search(Problem<S, A> prob, Stats stats) {
		PriorityQueue<Node<S, A>> frontier = new PriorityQueue<Node<S, A>>(); // frontier
		HashMap<S, Node<S, A>> explored = new HashMap<S, Node<S, A>>();
		Node<S, A> initialNode = new Node<S, A>(prob);
		frontier.add(initialNode);
		explored.put(initialNode.getState(), initialNode);
		while (!frontier.isEmpty()) {
			Node<S, A> currentNode = frontier.poll();
			if (prob.isGoal(currentNode.getState())) {
				System.out.println("Solution: " + currentNode);
				return currentNode;
			}
			stats.expanded++;
			for (Node<S, A> currentChild : currentNode.getListOfChildren(prob)) {
				double currentCost = currentChild.getPathCost();
				boolean isExplored = explored.containsKey(currentChild.getState());
				boolean isInFrontier = explored.get(currentChild.getState()) != null;
				if (isExplored && isInFrontier) {
					double formerCost = explored.get(currentChild.getState()).getPathCost();
					if (currentCost < formerCost) {
						frontier.remove(currentChild);
						frontier.offer(currentChild);
					}
				} else if (!isExplored) {
					frontier.offer(currentChild);
					explored.put(currentChild.getState(), currentChild);
				}
			}
			explored.replace(currentNode.getState(), null);
		}
		return null;
	}
}