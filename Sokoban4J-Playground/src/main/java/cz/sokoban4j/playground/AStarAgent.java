package cz.sokoban4j.playground;

import java.util.LinkedList;
import java.util.List;

import cz.sokoban4j.agents.ArtificialAgent;
import cz.sokoban4j.simulation.actions.EDirection;
import cz.sokoban4j.simulation.board.compact.BoardCompact;
import generic.AStar;
import generic.Node;
import generic.Stats;

public class AStarAgent extends ArtificialAgent  {

	@Override
	protected List<EDirection> think(BoardCompact board) {
		System.out.println("THINK!");
		LinkedList<EDirection> path = new LinkedList<EDirection>();
		Stats stats = new Stats();
		Node<BoardCompact, EDirection> node = AStar.search(new SokobanProblem(board), stats);
		while (node.getLastAction() != null) {
			path.addFirst(node.getLastAction());
			node = node.getFather();
		}
		System.out.println("Expanded: " + stats.expanded);
		return path;
	}

}
