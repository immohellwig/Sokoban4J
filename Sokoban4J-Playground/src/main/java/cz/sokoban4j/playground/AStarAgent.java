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
		LinkedList<EDirection> path = new LinkedList<EDirection>();
		Node<BoardCompact, EDirection> node = AStar.search(new SokobanProblem(board), new Stats());
		while (node != null) {
			path.addFirst(node.getLastAction());
			node = node.getFather();
		}
		return path;
	}

}
