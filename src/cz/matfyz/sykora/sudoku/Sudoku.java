/*
    This file is part of Sudoku Solver.

    Sudoku Solver is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Sudoku Solver is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Sudoku Solver.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.matfyz.sykora.sudoku;

import java.awt.Point;
import java.util.*;

/**
 * The core of the sudoku solver. Maintains the game state, the backtracking
 * stack and implements the search and constraint propagation algorithms.
 * 
 * @author Ondrej Sykora
 */
public class Sudoku {
	/**
	 * The size of an edge of a square group. This is square root of
	 * the size of a group in the game. 
	 */
	public static final int GAME_SQUARE_SIZE = 3;
	/**
	 * The size of the game board (the number of cells in a row/column).
	 */
	public static final int GAME_SIZE = GAME_SQUARE_SIZE * GAME_SQUARE_SIZE;
	/**
	 * Contains the list adjacent groups for each cell in the game. An
	 * adjacent group is a group of cells in the same column, row, or
	 * square as the given cell.
	 */
	private List<Point[]>[][] adjacentGroups;
	/**
	 * Contains the current game state. The current game state is always
	 * a reference to a value in {@link gameStateStack}.
	 * 
	 * @see #gameStateStack
	 * @see #stackPosition
	 */
	private Field[][] currentGameState;
	/**
	 * The stack used for backtracking. This stack is preallocated and
	 * re-used to minimize the impact of garbage collection. Reference
	 * to the current item on the stack is maintained in {@link #currentGameState}.
	 * 
	 * @see #currentGameState
	 */
	private Field[][][] gameStateStack;
	/**
	 * The current position in {@link #gameStateStack}. This is the index
	 * of the value stored in {@link #currentGameState}. 
	 */
	private int stackPosition;
	/**
	 * The list of cells, whose value has changed, and the constraints
	 * arising from these changes need to be propagated through the
	 * game board.
	 */
	private Queue<Point> propagateList;
	/**
	 * The list of groups on the game board. This is a complete list
	 * of groups references from {@link #adjacentGroups}.
	 */
	private List<Point[]> sudokuGroups;
	
	private void addGroup(Point[] group) {
		sudokuGroups.add(group);
	}
	
	/**
	 * Clears the value of the cell at the given position.
	 * 
	 * @param x the X position of the cell.
	 * @param y the Y position of the cell.
	 */
	public void clearValue(int x, int y) {
		currentGameState[x][y].clearValue();
	}
	
	private void cloneGameState() {
		Field[][] source = gameStateStack[stackPosition - 1];
		Field[][] clone = gameStateStack[stackPosition];
		for(int x=0; x < GAME_SIZE; x++)
			for(int y=0; y < GAME_SIZE; y++)
				clone[x][y].assign(source[x][y]);
	}
	
	/**
	 * Prints the current state of the game to standard output (for
	 * debugging purposes).
	 */
	public void debugOutput() {
		for(int y=0; y < GAME_SIZE; y++) {
			for(int x=0; x < GAME_SIZE; x++) {
				System.out.print(currentGameState[x][y].getValue());
				if(x < GAME_SIZE)
					System.out.print(" ");
			}
			System.out.println();
		}
	}
	
	/**
	 * Returns the value assigned to the cell at the given position.
	 * 
	 * @param x the X position of the cell.
	 * @param y the Y position of the cell.
	 * @return the value of the cell at the given position.
	 */
	public int getValue(int x, int y) {
		return currentGameState[x][y].getValue();
	}
	
	@SuppressWarnings("unchecked")
	private void initialize() {
		// Create the stack of state representations
		gameStateStack = new Field[GAME_SIZE * GAME_SIZE][][];
		for(int z = 0; z < GAME_SIZE * GAME_SIZE; z++) {
			gameStateStack[z] = new Field[GAME_SIZE][];
			for(int x = 0; x < GAME_SIZE; x++) {
				gameStateStack[z][x] = new Field[GAME_SIZE];
				for(int y = 0; y < GAME_SIZE; y++)
					gameStateStack[z][x][y] = new Field();
			}
		}
		currentGameState = gameStateStack[0];

		// Initialize the propagation list
		propagateList = new LinkedList<Point>();

		// Create representations of the adjacent groups
		sudokuGroups = new LinkedList<Point[]>();
		adjacentGroups = new List[GAME_SIZE][];
		for(int x=0; x < GAME_SIZE; x++) {
			adjacentGroups[x] = new List[GAME_SIZE];
			for(int y=0; y < GAME_SIZE; y++)
				adjacentGroups[x][y] = new ArrayList<Point[]>();
		}
		
		for(int i=0; i < GAME_SIZE; i++) {
			Point[] new_group = new Point[GAME_SIZE];
			for(int j=0; j < GAME_SIZE; j++) {
				new_group[j] = new Point(i, j);
				adjacentGroups[i][j].add(new_group);
			}
			addGroup(new_group);
		}
		for(int i=0; i < GAME_SIZE; i++) {
			Point[] new_group = new Point[GAME_SIZE];
			for(int j=0; j < GAME_SIZE; j++) {
				new_group[j] = new Point(j, i);
				adjacentGroups[j][i].add(new_group);
			}
			addGroup(new_group);
		}
		for(int xg=0; xg < GAME_SQUARE_SIZE; xg++)
			for(int yg=0; yg < GAME_SQUARE_SIZE; yg++) {
				Point[] new_group = new Point[GAME_SIZE];
				int pos = 0;
				for(int x=0; x < GAME_SQUARE_SIZE; x++)
					for(int y = 0; y < GAME_SQUARE_SIZE; y++) {
						new_group[pos++] = new Point(xg*GAME_SQUARE_SIZE + x,yg*GAME_SQUARE_SIZE + y);
						adjacentGroups[xg*GAME_SQUARE_SIZE + x][yg*GAME_SQUARE_SIZE + y].add(new_group);
					}
				addGroup(new_group);
			}
	}
	
	/**
	 * Checks if a value is assigned to the cell at the given position.
	 * 
	 * @param x the X position of the cell.
	 * @param y the Y position of the cell.
	 * @return <code>true</code> if a value is assigned to the cell; otherwise,
	 * 			<code>false</code>. 
	 */
	public boolean isValueSet(int x, int y) {
		return currentGameState[x][y].hasFixedValue();
	}
	
	/**
	 * Loads the state of the game from a textual representation. Propagates
	 * all constraints after loading the state.
	 * 
	 * @param _source the string, from which the state is loaded.
	 */
	public void loadGameState(String _source) {
		for(int x=0; x < GAME_SIZE; x++)
			for(int y=0; y < GAME_SIZE; y++) {
				char val = _source.charAt(x + y*GAME_SIZE);
				if('0' <= val && '9' >= val) {
					int ival = Character.getNumericValue(val) - Character.getNumericValue('0');
					currentGameState[x][y].setValue(ival);
					propagateList.add(new Point(x, y));
				}
				else if('x' == val)
					currentGameState[x][y].clearValue();
			}
		propagateAll();
	}
	
	private void popGameState() {
		stackPosition--;
		currentGameState = gameStateStack[stackPosition];
	}
	
	private void pushGameState() {
		stackPosition++;
		cloneGameState();
		currentGameState = gameStateStack[stackPosition];
	}
	
	private boolean propagate(int _value, Point[] _group) {
		for(int i=0; i<_group.length; i++) {
			Point pos = _group[i];
			Field current = currentGameState[pos.x][pos.y];
			if(!current.hasFixedValue()) {
				current.clearPossibleValue(_value);
				if(current.hasSinglePossibleValue()) {
					current.assignSinglePossibleValue();
					propagateList.add(new Point(pos.x, pos.y));
				}
				else if(!current.hasPossibleValues())
					return false;
			}
		}
		return true;
	}
	
	private boolean propagateAll() {
		while(!propagateList.isEmpty()) {
			Point pos = propagateList.remove();
			Field propagated = currentGameState[pos.x][pos.y];
			for(Object cur_object : adjacentGroups[pos.x][pos.y]) {
				Point[] current_group = (Point[])cur_object;
				if(!propagate(propagated.getValue(), current_group))
					return false;
			}
		}
		return true;
	}
	
	/**
	 * Assigns the given value to the cell at the given position.
	 * 
	 * @param x the X position of the cell.
	 * @param y the Y position of the cell.
	 * @param value the value assigned to the cell.
	 */
	public void setValue(int x, int y, int value) {
		currentGameState[x][y].setValue(value);
	}
	
	/**
	 * Runs the propagation and search algorithm to assign values to
	 * all cells. The solution will be stored in {@link #currentGameState},
	 * and accessible through {@link #getValue(int, int)}.
	 * 
	 * @return <code>true</code> if a solution was found; otherwise,
	 * 			<code>false</code>.
	 */
	public boolean solve() {
		for(int x=0; x < GAME_SIZE; x++)
			for(int y=0; y < GAME_SIZE; y++) {
				if(!currentGameState[x][y].hasFixedValue()) {
					if(!currentGameState[x][y].hasPossibleValues())
						return false;
					for(int val=1; val <= GAME_SIZE; val++) {
						if(!currentGameState[x][y].isPossibleValue(val))
							continue;
						pushGameState();
						currentGameState[x][y].setValue(val);
						propagateList.add(new Point(x, y));
						if(propagateAll() && validate() && solve())
							return true;
						popGameState();
					}
					return false;
				}
			}
		return true;
	}
	
	/**
	 * Checks if the current game state is valid, i.e. that there are
	 * no collisions, and all cells are either assigned to, or there is
	 * at lease one value that can be assigned to them.
	 * 
	 * @return <code>true</code> if there are no collisions; otherwise,
	 * 			<code>false</code>.
	 */
	public boolean validate() {
		for(Point[] current : sudokuGroups) {
			if(!validateGroup(current))
				return false;
		}
		return true;
	}
	
	/**
	 * Checks if the given group is valid, i.e. that it contains no
	 * collisions, and that all cells are either assigned to, or there is
	 * at least one value that can be assigned to them.
	 * 
	 * @param _fields the list of fields in the group.
	 * @return <code>true</code> if there are no collisions; otherwise,
	 * 			<code>false</code>.
	 */
	public boolean validateGroup(Point[] _fields) {
		for(int i=0; i<_fields.length; i++) {
			Field current = currentGameState[_fields[i].x][_fields[i].y];
			if(current.hasFixedValue()) {
				for(int j=i+1; j<_fields.length; j++) {
					Field next = currentGameState[_fields[j].x][_fields[j].y];
					if(next.hasFixedValue() && current.getValue() == next.getValue())
						return false;
				}
			}
			else {
				if(!current.hasPossibleValues())
					return false;
			}
		}
		return true;
	}
	
	/**
	 * Creates and initializes a new Sudoku solver.
	 */
	public Sudoku() {
		initialize();
	}
}
