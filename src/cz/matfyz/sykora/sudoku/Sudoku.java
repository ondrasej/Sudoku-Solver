package cz.matfyz.sykora.sudoku;

import java.awt.Point;
import java.util.*;

public class Sudoku {
	public static final int GAME_SIZE = 25;
	
	public static final int GAME_SQUARE_SIZE = 5;
	
	private List[][] adjacentGroups;
	
	private Field[][] currentGameState;
	
	//private Stack<Field[][]> gameStateStack;
	private Field[][][] gameStateStack;
	private int stackPosition;
	
	private Queue<Point> propagateList;
	
	private List<Point[]> sudokuGroups;
	
	private void addGroup(Point[] group) {
		sudokuGroups.add(group);
	}
	
	public void clearValue(int _x, int _y) {
		currentGameState[_x][_y].clearValue();
	}
	
	private void cloneGameState() {
		Field[][] source = gameStateStack[stackPosition - 1];
		Field[][] clone = gameStateStack[stackPosition];
		for(int x=0; x < GAME_SIZE; x++)
			for(int y=0; y < GAME_SIZE; y++)
				clone[x][y].assign(source[x][y]);
	}
	
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
	
	public int getValue(int _x, int _y) {
		return currentGameState[_x][_y].getValue();
	}
	
	private void initialize() {
		//gameStateStack = new Stack<Field[][]>();
		
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
		
		sudokuGroups = new LinkedList<Point[]>();
		propagateList = new LinkedList<Point>();
		
		adjacentGroups = new List[GAME_SIZE][];
		for(int x=0; x < GAME_SIZE; x++) {
			adjacentGroups[x] = new List[GAME_SIZE];
			for(int y=0; y < GAME_SIZE; y++)
				adjacentGroups[x][y] = new LinkedList();
		}
		
		currentGameState = new Field[GAME_SIZE][];
		for(int i=0; i < GAME_SIZE; i++)
			currentGameState[i] = new Field[GAME_SIZE];
		for(int x=0; x < GAME_SIZE; x++)
			for(int y=0; y < GAME_SIZE; y++) {
				currentGameState[x][y] = new Field();
			}
		for(int i=0; i < GAME_SIZE; i++) {
			Point[] new_group = new Point[GAME_SIZE];
			for(int j=0; j < GAME_SIZE; j++) {
				new_group[j] = new Point(i, j);
				adjacentGroups[i][j].add((Object)new_group);
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
	
	public boolean isValueSet(int _x, int _y) {
		return currentGameState[_x][_y].hasFixedValue();
	}
	
	public void loadGameState(String _source) {
		for(int x=0; x < GAME_SIZE; x++)
			for(int y=0; y < GAME_SIZE; y++) {
				char val = _source.charAt(x + y*GAME_SIZE);
				if('x' != val) {
					int ival = Character.getNumericValue(val) - Character.getNumericValue('0');
					currentGameState[x][y].setValue(ival);
					propagateList.add(new Point(x, y));
				}
				else
					currentGameState[x][y].clearValue();
			}
		propagateAll();
	}
	
	public void setupSearchState() {
		for(int x=0; x < GAME_SIZE; x++)
			for(int y=0; y < GAME_SIZE; y++) {
				if(y == 0) {
					currentGameState[x][y].setValue(x + 1);
					propagateList.add(new Point(x, y));
				}
				else
					currentGameState[x][y].clearValue();
			}
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
	
	public void setValue(int _x, int _y, int _value) {
		currentGameState[_x][_y].setValue(_value);
	}
	
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
	
	public boolean validate() {
		for(Point[] current : sudokuGroups) {
			if(!validateGroup(current))
				return false;
		}
		return true;
	}
	
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
	
	public Sudoku() {
		initialize();
	}
}
