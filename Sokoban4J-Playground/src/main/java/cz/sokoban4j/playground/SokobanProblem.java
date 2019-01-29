package cz.sokoban4j.playground;

import java.util.ArrayList;
import java.util.List;

import cz.sokoban4j.simulation.actions.EDirection;
import cz.sokoban4j.simulation.actions.compact.CMove;
import cz.sokoban4j.simulation.actions.compact.CPush;
import cz.sokoban4j.simulation.board.compact.BoardCompact;
import cz.sokoban4j.simulation.board.compact.CTile;
import cz.sokoban4j.simulation.board.oop.EPlace;
import generic.Problem;

public class SokobanProblem implements Problem<BoardCompact, EDirection> {

	final BoardCompact initialBoard;

	public SokobanProblem(BoardCompact board) {
		initialBoard = board;
	}

	@Override
	public BoardCompact initialState() {
		return initialBoard.clone();
	}

	@Override
	public List<EDirection> actions(BoardCompact state) {
		List<EDirection> result = new ArrayList<EDirection>();
		for (EDirection current : EDirection.arrows()) {
			if (CPush.isPushPossible(state, state.playerX, state.playerY, current) ||
					CTile.isFree(state.tile(state.playerX + current.dX, state.playerY + current.dY)))
				result.add(current);
		}
		return result;
	}
	
	@Override
	public BoardCompact result(BoardCompact state, EDirection action) {
		BoardCompact resultBoard = state.clone();
		if (CPush.isPushPossible(resultBoard, resultBoard.playerX, resultBoard.playerY, action))
			CPush.getAction(action).perform(resultBoard);
		else
			CMove.getAction(action).perform(resultBoard);
		return resultBoard;
	}

	@Override
	public boolean isGoal(BoardCompact state) {
		return state.isVictory();
	}

	@Override
	public int cost(BoardCompact state, EDirection action) {
		return 1;
	}

	@Override
	public int estimate(BoardCompact state) {
		
		class Coordinates {
			int fromX;
			int fromY;
			int toX;
			int toY;
			
			int getDistance() {
				return Math.abs(fromX - toX) + Math.abs(fromY - toY);
			}
			
			int getPlayerDistance() {
				return Math.abs(fromX - state.playerX) + Math.abs(fromY - state.playerY) - 1;
			}
		}
		
		Coordinates[] positions = new Coordinates[state.boxCount];
		
		for (int i = 0; i < positions.length; i++) {
			positions[i] = new Coordinates();
		}	
		
		for (int x = 0; x < state.width(); x++) {
			for (int y = 0; y < state.width(); y++) {
				int currentTile = state.tile(x, y);
				if (CTile.isSomeBox(currentTile)) {
					int boxNum = CTile.getBoxNum(currentTile) - 1;
					System.out.println(x + "|" + y + " " + boxNum);
					positions[boxNum].fromX = x;
					positions[boxNum].fromY = y;
				}
				if (CTile.forSomeBox(currentTile)) {
					int boxNum = EPlace.fromFlag(currentTile).getBoxNum() - 1;
					System.out.println(x + "|" + y + " " + boxNum);
					positions[boxNum].toX = x;
					positions[boxNum].toY = y;
				}
			}
		}
		int estimation = 0;
		int smallestPlayerDistance = Integer.MAX_VALUE;
		for (Coordinates current : positions) {
			estimation += current.getDistance();
			if (smallestPlayerDistance > current.getPlayerDistance()) {
				smallestPlayerDistance = current.getPlayerDistance();
			}
		}
		System.out.println("Estimation: " + estimation);
		return estimation + smallestPlayerDistance;
	}

}
