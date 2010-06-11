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
package cz.matfyz.sykora.sudoku.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import cz.matfyz.sykora.sudoku.*;

public class MainFrame extends JFrame {
	
	static final long serialVersionUID = 0;
	
	private class SudokuTableModel implements TableModel {
		public void addTableModelListener(TableModelListener _listener) {
			
		}
		
		public Class<Integer> getColumnClass(int _col) {
			return Integer.class;
		}
		
		public int getColumnCount() {
			return Sudoku.GAME_SIZE;
		}
		
		public String getColumnName(int _col) {
			return "column";
		}
		
		public int getRowCount() {
			return Sudoku.GAME_SIZE;
		}
		
		public Object getValueAt(int _row, int _col) {
			if(sudoku.isValueSet(_row, _col))
				return sudoku.getValue(_row, _col);
			return null;
		}
		
		public boolean isCellEditable(int _row, int _col) {
			return true;
		}
		
		public void removeTableModelListener(TableModelListener _listener) {
			
		}
		
		public void setValueAt(Object _value, int _row, int _col) {
			if(_value == null)
				sudoku.clearValue(_row, _col);
			else {
				int value = ((Integer)_value).intValue();
				if((0 >= value) || (Sudoku.GAME_SIZE < value)) {
					JOptionPane.showMessageDialog(MainFrame.this, "The value is outside the bounds 1 - " + Sudoku.GAME_SIZE,
												"Sudoku solver", JOptionPane.ERROR_MESSAGE);
					return;
				}
				sudoku.setValue(_row, _col, value);
			}
		}
	}
	
	private Sudoku sudoku;
	
	private JTable sudokuTable;
	
	private TableModel sudokuTableModel;
	
	private void initializeControls() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(300, 300));
		setMaximumSize(new Dimension(300, 300));
		
		// tabulka pro zadavani dat
		sudokuTableModel = new SudokuTableModel();
		sudokuTable = new JTable(sudokuTableModel);
		for(int i = 0; i < sudokuTable.getColumnCount(); i++)
			sudokuTable.getColumnModel().getColumn(i).setPreferredWidth(30);
		
		JPanel button_pane = new JPanel();
		button_pane.setLayout(new BoxLayout(button_pane, BoxLayout.X_AXIS));
		button_pane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		JButton solve_button = new JButton("Solve");
		solve_button.setActionCommand("solve");
		solve_button.addActionListener(new ActionListener() {
											public void actionPerformed(ActionEvent _action) {
												if(_action.getActionCommand().equals("solve")) {
													solveSudoku();
												}
											}
		});
		button_pane.add(solve_button);
		button_pane.add(Box.createRigidArea(new Dimension(10, 0)));
		
		JButton clear_button = new JButton("Clear");
		clear_button.setActionCommand("clear");
		clear_button.addActionListener(new ActionListener() {
											public void actionPerformed(ActionEvent _action) {
												if(_action.getActionCommand().equals("clear")) {
													clearSudoku();
												}
											}
		});
		button_pane.add(clear_button);
		
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(sudokuTable);
		getContentPane().add(button_pane);
		pack();
	}
	
	private void clearSudoku() {
		if(sudokuTable.isEditing())
			return;
		for(int x = 0; x < Sudoku.GAME_SIZE; x++)
			for(int y = 0; y < Sudoku.GAME_SIZE; y++) {
				sudoku.clearValue(x, y);
			}
		sudokuTable.updateUI();
	}
	
	private void solveSudoku() {
		if(sudokuTable.isEditing())
			return;
		if(!sudoku.solve()) {
			JOptionPane.showMessageDialog(this, "There is no solution to this problem", "Sudoku solver", JOptionPane.ERROR_MESSAGE);
		}
		sudokuTable.updateUI();
	}
	
	public MainFrame() {
		super("Sudoku Solver");
		
		sudoku = new Sudoku();
		initializeControls();
	}
	
	public static void main(String[] _args) {
		JFrame main_window = new MainFrame();
		main_window.setVisible(true);
	}
}
