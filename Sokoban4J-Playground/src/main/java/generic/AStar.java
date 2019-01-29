package generic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;

public class AStar<S, A> {
	public static <S, A> Node<S, A> search(Problem<S, A> prob, Stats stats) {
		PriorityQueue<Node<S, A>> frontier = new PriorityQueue<Node<S, A>>(); // frontier
		ArrayList<Node<S, A>> closed = new ArrayList<Node<S, A>>(); // explored
		Node<S, A> initialNode = new Node<S, A>(prob);
		frontier.add(initialNode);

		while (!frontier.isEmpty()) {
			Node<S, A> currentNode = frontier.poll();
			System.out.println(currentNode);
			if (prob.isGoal(currentNode.getState()))
				return currentNode;
			stats.expanded++;
			for (Node<S, A> currentChild : currentNode.getListOfChildren(prob)) {
				double currentCost = currentChild.getPathCost();
				if (frontier.contains(currentChild)) {
					double formerCost = getCostfromQueue(frontier, currentChild);
					if (currentCost < formerCost) {
						frontier.remove(currentChild);
						frontier.offer(currentChild);
					}
				} else if (!closed.contains(currentChild)) {
					frontier.offer(currentChild);
				}
			}
			closed.add(currentNode);
		}
		return null;
	}

	private static <S, A> double getCostfromQueue(PriorityQueue<Node<S, A>> queue, Node<S, A> element) {
		Iterator<Node<S, A>> iter = queue.iterator();
		while (iter.hasNext()) {
			Node<S, A> current = iter.next();
			if (element.equals(current)) {
				return current.getPathCost();
			}
		}
		System.err.println("Queue does not contain element!");
		return -1;
	}
}