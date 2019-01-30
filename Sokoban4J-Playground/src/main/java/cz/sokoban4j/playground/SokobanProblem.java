package cz.sokoban4j.playground;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

import cz.sokoban4j.simulation.actions.EDirection;
import cz.sokoban4j.simulation.actions.compact.CMove;
import cz.sokoban4j.simulation.actions.compact.CPush;
import cz.sokoban4j.simulation.board.compact.BoardCompact;
import cz.sokoban4j.simulation.board.compact.CTile;
import cz.sokoban4j.simulation.board.oop.EEntity;
import cz.sokoban4j.simulation.board.oop.EPlace;
import generic.Problem;

public class SokobanProblem implements Problem<BoardCompact, EDirection> {

	class Coordinate implements Comparable<Coordinate> {
		int cX;
		int cY;
		int boxNum;
		int id;
		int rectangleDistance;

		public Coordinate(int x, int y, int boxNum, int id) {
			cX = x;
			cY = y;
			this.id = id;
			this.boxNum = boxNum;
		}

		public Coordinate(int x, int y) {
			cX = x;
			cY = y;
		}

		@Override
		public String toString() {
			return cX + "|" + cY;
		}

		@Override
		public int compareTo(Coordinate o) {
			return Integer.compare(id, o.id);
		}

		@Override
		public int hashCode() {
			return cX + cY * 50;
		}

		@Override
		public boolean equals(Object obj) {
			Coordinate c = (Coordinate) obj;
			return cX == c.cX && cY == c.cY;
		}
	}

	final BoardCompact initialBoard;
	HashSet<Coordinate> deadTiles;

	public SokobanProblem(BoardCompact board) {
		System.out.println(board);
		initialBoard = board;
		initDeadTiles();
		System.out.println("Prunned: " + deadTiles);
		System.out.println("dead squares: ");
		for (int y = 0; y < board.height(); ++y) {
			for (int x = 0; x < board.width(); ++x)
				System.out.print(
						CTile.isWall(board.tile(x, y)) ? '#' : (deadTiles.contains(new Coordinate(x, y)) ? 'X' : '_'));
			System.out.println();
		}

	}

	private void initDeadTiles() {
		deadTiles = new HashSet<Coordinate>();
		for (int x = 0; x < initialBoard.width(); x++) {
			for (int y = 0; y < initialBoard.height(); y++) {
				int currentTile = initialBoard.tile(x, y);
				if (CTile.forSomeBox(currentTile))
					continue;
				boolean isWall = CTile.isWall(currentTile);
				EDirection directionOfWall = isNextToWall(x, y);
				if (!isWall && directionOfWall == null) { // Corner
					deadTiles.add(new Coordinate(x, y));
				} else if (!isWall) {
					isDead(x, y, directionOfWall);
				}
			}
		}
	}

	/**
	 * Checks if box is stuck in a wall
	 * 
	 * @param x              x of tile that should be checked
	 * @param y              x of tile that should be checked
	 * @param checkDirection
	 * @return
	 */

	private boolean isDead(int x, int y, EDirection checkDirection) {
		int currentX = x;
		int currentY = y;
		List<Coordinate> potentialDeadTiles = new ArrayList<Coordinate>();
		potentialDeadTiles.add(new Coordinate(currentX, currentY));
		switch (checkDirection) {
		case DOWN:
			while (!CTile.isWall(initialBoard.tile(--currentX, currentY))) {
				potentialDeadTiles.add(new Coordinate(currentX, currentY));
				if (CTile.forSomeBox(initialBoard.tile(currentX, currentY)))
					return false;
				if (!CTile.isWall(initialBoard.tile(currentX, currentY + 1)))
					return false;
			}
			currentX = x;
			while (!CTile.isWall(initialBoard.tile(++currentX, currentY))) {
				potentialDeadTiles.add(new Coordinate(currentX, currentY));
				if (CTile.forSomeBox(initialBoard.tile(currentX, currentY)))
					return false;
				if (!CTile.isWall(initialBoard.tile(currentX, currentY + 1)))
					return false;
			}
			break;
		case UP:
			while (!CTile.isWall(initialBoard.tile(--currentX, currentY))) {
				potentialDeadTiles.add(new Coordinate(currentX, currentY));
				if (CTile.forSomeBox(initialBoard.tile(currentX, currentY)))
					return false;
				if (!CTile.isWall(initialBoard.tile(currentX, currentY - 1)))
					return false;
			}
			currentX = x;
			while (!CTile.isWall(initialBoard.tile(++currentX, currentY))) {
				potentialDeadTiles.add(new Coordinate(currentX, currentY));
				if (CTile.forSomeBox(initialBoard.tile(currentX, currentY)))
					return false;
				if (!CTile.isWall(initialBoard.tile(currentX, currentY - 1)))
					return false;
			}
			break;
		case LEFT:
			while (!CTile.isWall(initialBoard.tile(currentX, --currentY))) {
				potentialDeadTiles.add(new Coordinate(currentX, currentY));
				if (CTile.forSomeBox(initialBoard.tile(currentX, currentY)))
					return false;
				if (!CTile.isWall(initialBoard.tile(currentX - 1, currentY)))
					return false;
			}
			currentY = y;
			while (!CTile.isWall(initialBoard.tile(currentX, ++currentY))) {
				potentialDeadTiles.add(new Coordinate(currentX, currentY));
				if (CTile.forSomeBox(initialBoard.tile(currentX, currentY)))
					return false;
				if (!CTile.isWall(initialBoard.tile(currentX - 1, currentY)))
					return false;
			}
			break;
		case RIGHT:
			while (!CTile.isWall(initialBoard.tile(currentX, --currentY))) {
				potentialDeadTiles.add(new Coordinate(currentX, currentY));
				if (CTile.forSomeBox(initialBoard.tile(currentX, currentY)))
					return false;
				if (!CTile.isWall(initialBoard.tile(currentX + 1, currentY)))
					return false;
			}
			currentY = y;
			while (!CTile.isWall(initialBoard.tile(currentX, ++currentY))) {
				potentialDeadTiles.add(new Coordinate(currentX, currentY));
				if (CTile.forSomeBox(initialBoard.tile(currentX, currentY)))
					return false;
				if (!CTile.isWall(initialBoard.tile(currentX + 1, currentY)))
					return false;
			}
			break;
		case NONE:
			return false;
		}
		for (Coordinate c : potentialDeadTiles)
			deadTiles.add(c);
		return true;
	}

	private EDirection isNextToWall(int x, int y) {
		int counter = 0;
		int vertCounter = 0;
		int horiCounter = 0;
		EDirection dir = EDirection.NONE;
		if (x < initialBoard.width() - 1 && CTile.isWall(initialBoard.tile(x + 1, y))) {
			dir = EDirection.RIGHT;
			counter++;
			horiCounter++;
		}
		if (x > 0 && CTile.isWall(initialBoard.tile(x - 1, y))) {
			dir = EDirection.LEFT;
			counter++;
			horiCounter++;
		}
		if (y < initialBoard.height() - 1 && CTile.isWall(initialBoard.tile(x, y + 1))) {
			dir = EDirection.DOWN;
			counter++;
			vertCounter++;
		}
		if (y > 0 && CTile.isWall(initialBoard.tile(x, y - 1))) {
			dir = EDirection.UP;
			counter++;
			vertCounter++;
		}
		if (counter == 2 && (vertCounter == 2 ^ horiCounter == 2))
			return EDirection.NONE; // May ignore squares that are dead, but those can be reached over dead squares
									// so that does not make difference
		if (counter > 1)
			return null;
		return dir;
	}

	@Override
	public BoardCompact initialState() {
		return initialBoard.clone();
	}

	@Override
	public List<EDirection> actions(BoardCompact state) {
		List<EDirection> result = new ArrayList<EDirection>();
		for (EDirection current : EDirection.arrows()) {
			Coordinate target = new Coordinate(state.playerX + current.dX, state.playerY + current.dY);
			if (CPush.isPushPossible(state, state.playerX, state.playerY, current) && !deadTiles.contains(target)
					&& !isBoxBlockMove(state, current)) {
				result.add(current);
			} else if (CTile.isFree(state.tile(target.cX, target.cY))) {
				result.add(current);
			}
		}
		return result;
	}

	private boolean isBoxBlockMove(BoardCompact state, EDirection current) {
		BoardCompact newState = state.clone();
		CPush.getAction(current).perform(newState);
		int baseTile = newState.tile(newState.playerX + current.dX, newState.playerY + current.dY);
		int deepTile = newState.tile(newState.playerX + current.dX * 2, newState.playerY + current.dY * 2);
		int leftTile = newState.tile(newState.playerX + current.dX + current.dY, newState.playerY + current.dY + current.dX);
		int leftDeepTile = newState.tile(newState.playerX + 2 * current.dX + current.dY,
				newState.playerY + 2 * current.dY + current.dX);
		int rightTile = newState.tile(newState.playerX + current.dX - current.dY, newState.playerY + current.dY - current.dX);
		int rightDeepTile = newState.tile(newState.playerX + 2 * current.dX - current.dY,
				newState.playerY + 2 * current.dY - current.dX);
		boolean target1 = CTile.forSomeBox(baseTile) && (CTile.isWall(deepTile) ^ (CTile.forSomeBox(deepTile) && CTile.isSomeBox(deepTile)));
		boolean target2;
//		System.out.println("BLOCKMOVE?");
//		System.out.println(newState);		
		if (CTile.isWall(deepTile) || CTile.isSomeBox(deepTile)) {
			if ((CTile.isSomeBox(leftTile) || CTile.isWall(leftTile))
					&& (CTile.isSomeBox(leftDeepTile) || CTile.isWall(leftDeepTile))) {
				target2 = (CTile.isWall(leftTile) ^ (CTile.isSomeBox(leftTile) && CTile.forSomeBox(leftTile)))
						&& (CTile.isWall(leftDeepTile) ^ (CTile.isSomeBox(leftDeepTile) && CTile.forSomeBox(leftDeepTile)));
			} else if ((CTile.isSomeBox(rightTile) || CTile.isWall(rightTile))
					&& (CTile.isSomeBox(rightDeepTile) || CTile.isWall(rightDeepTile))) {
				target2 = (CTile.isWall(rightTile) ^ (CTile.isSomeBox(rightTile) && CTile.forSomeBox(rightTile)))
						&& (CTile.isWall(rightDeepTile) ^ (CTile.isSomeBox(rightDeepTile) && CTile.forSomeBox(rightDeepTile)));
			} else {
//				System.out.println("2x Formation");
				return false;
			}
//			System.out.println("4x Formation");
//			System.out.println(newState);
			return !(target2 && target1);
		} else {
//			System.out.println("No Formation");
			return false;
		}
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

		PriorityQueue<Coordinate> boxes = new PriorityQueue<Coordinate>();
		PriorityQueue<Coordinate> targets = new PriorityQueue<Coordinate>();
		// Get all boxes and targets and get Sokobandistance
		int sokobanDistance = Integer.MAX_VALUE;
		int boxID = 0, targetID = 0;
		for (int x = 0; x < state.width(); x++) {
			for (int y = 0; y < state.height(); y++) {
				int currentTile = state.tile(x, y);
				if (CTile.isSomeBox(currentTile)) {
					int boxNum = EEntity.fromFlag(state.tile(x, y)).getBoxNum();
					Coordinate newBox = new Coordinate(x, y, boxNum, boxID++);
					boxes.add(newBox);
					int currentSokobanDistance = Math.abs(newBox.cX - state.playerX)
							+ Math.abs(newBox.cY - state.playerY) - 1;
					sokobanDistance = sokobanDistance < currentSokobanDistance ? sokobanDistance
							: currentSokobanDistance;
				}
				if (CTile.forSomeBox(currentTile)) {
					int targetNum = EPlace.fromFlag(state.tile(x, y)).getBoxNum();
					targets.add(new Coordinate(x, y, targetNum, targetID++));
				}
			}
		}

		double[][] distances = new double[targetID][boxID];
		for (Coordinate target : targets) {
			for (Coordinate box : boxes) {
				if (target.boxNum == 0 || target.boxNum == box.boxNum) {
					distances[target.id][box.id] = getRealDistance(state, box, target);
				} else {
					distances[target.id][box.id] = Double.MAX_VALUE;
				}
			}
		}

		// Hungerian Algorithm, copied from Kevin Stern (e.g. Documentation)
		HungarianAlgorithm alg = new HungarianAlgorithm(distances);
		int[] bestCombination = alg.execute();
		double estimate = 0;
		for (int i = 0; i < bestCombination.length; i++) {
			estimate += distances[i][bestCombination[i]];
		}

		return (int) estimate + sokobanDistance;
	}

	private int getRealDistance(BoardCompact state, Coordinate box, Coordinate target) {
		return Math.abs(box.cX - target.cX) + Math.abs(box.cY - target.cY);
	}

}
