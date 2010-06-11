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

/**
 * Represents a single cell in the game.
 * 
 * @author Ondrej Sykora
 */
public class Field {
	/**
	 * Set to <code>true</code> if a value is assigned to the cell.
	 * 
	 * @see #assignedValue
	 * @see #clearValue()
	 * @see #hasFixedValue()
	 * @see #setValue(int)
	 */
	private boolean valueFixed;
	/**
	 * Contains the value assigned to the cell.
	 * 
	 * @see #valueFixed
	 * @see #getValue()
	 * @see #setValue(int)
	 */
	private int assignedValue;
	/**
	 * A bit mask that contains the list of all values that can be
	 * assigned to this cell.
	 * 
	 * @see #valueFixed
	 * @see #clearPossibleValue(int)
	 */
	private int possibleValues;
	
	/**
	 * Copies all values (assignment, possible values) from the given cell.
	 * 
	 * @param source the cell, from which the values are copied.
	 */
	public final void assign(Field source) {
		valueFixed = source.valueFixed;
		assignedValue = source.assignedValue;
		possibleValues = source.possibleValues;
	}
	
	/**
	 * If there is only one value that can be assigned to the cell, assigns
	 * it to the cell.
	 */
	public final void assignSinglePossibleValue() {
		int pos_val = possibleValues;
		int val = 1;
		while(pos_val != 0 && ((pos_val & 1) == 0)) {
			pos_val >>= 1;
			val++;
		}
		assignedValue = val;
		valueFixed = true;
	}
	
	/**
	 * Removes the given value from the list of possible values for this cell.
	 * 
	 * @param value the value that is removed from the list.
	 */
	public final void clearPossibleValue(int value) {
		possibleValues &= ~(1 << (value - 1));
	}
	
	/**
	 * Removes the value assigned to the cell.
	 */
	public final void clearValue() {
		valueFixed = false;
		possibleValues = (1 << Sudoku.GAME_SIZE) - 1;
	}
	
	/**
	 * Returns the value assigned to the cell.
	 * 
	 * @return the value assigned to the cell.
	 */
	public final int getValue() {
		return assignedValue;
	}
	
	/**
	 * Checks if a value is assigned to the cell.
	 * 
	 * @return <code>true</code> if a value is assigned to the cell; otherwise,
	 * 			<code>false</code>.
	 */
	public final boolean hasFixedValue() {
		return valueFixed;
	}
	
	/**
	 * Checks that the list of possible values for this cell is not empty.
	 * 
	 * @return <code>true</code> if the list is not empty; otherwise, <code>false</code>.
	 */
	public final boolean hasPossibleValues() {
		return possibleValues != 0;
	}
	
	/**
	 * Checks that there is a single value that can be assigned to the cell.
	 * 
	 * @return <code>true</code> if there is just a single value that can be
	 * 			assigned to the cell; otherwise, <code>false</code>.
	 */
	public final boolean hasSinglePossibleValue() {
		int pos_val = possibleValues;
		while(pos_val != 0 && ((pos_val & 1) == 0))
			pos_val >>= 1;
		return pos_val == 1;
	}
	
	/**
	 * Checks whether <code>value</code> can be assigned to the cell.
	 * 
	 * @param value the tested value.
	 * @return <code>true</code> if <code>value</code> can be assigned to the
	 * 			cell; otherwise, <code>false</code>.
	 */
	public final boolean isPossibleValue(int value) {
		int mask = 1 << (value - 1);
		return 0 != (possibleValues & mask);
	}
	
	/**
	 * Assigns the given value to the cell.
	 * 
	 * @param value the value assigned to the cell.
	 */
	public final void setValue(int value) {
		valueFixed = true;
		assignedValue = value;
	}
	
	/**
	 * Creates a new unassigned cell.
	 */
	public Field() {
		clearValue();
	}
	
	/**
	 * Creates a clone of the given cell.
	 * 
	 * @param source the cell, from which the values are cloned.
	 */
	public Field(Field source) {
		assign(source);
	}
}
