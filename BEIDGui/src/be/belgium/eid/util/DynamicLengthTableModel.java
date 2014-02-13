package be.belgium.eid.util;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

/**
 * Represents a table model with a dynamic row length
 * 
 * @author Kristof Overdulve 
 */
@SuppressWarnings("serial")
public class DynamicLengthTableModel extends AbstractTableModel {

	/**
	 * Contains the names of the columns
	 */
	private String[] fColumnNames;

	/**
	 * Contains the data in the table
	 */
	private ArrayList<ArrayList<Object>> data;

	/**
	 * Indicates whether the last column should contain checkboxes
	 */
	private boolean fImplementCheckBoxColumn;

	/**
	 * Constructor: Initializes the rental table model with the given column
	 * names
	 * 
	 * @param columnNames
	 *            contains the column names
	 * @param checkBoxCol
	 *            indicates whether the last column should contain checkboxes or
	 *            just strings
	 */
	public DynamicLengthTableModel(String[] columnNames, boolean checkBoxCol) {
		setColumnIdentifiers(columnNames);
		data = new ArrayList<ArrayList<Object>>();
		fImplementCheckBoxColumn = checkBoxCol;
	}

	/**
	 * Sets the identifiers of the columns
	 * 
	 * @param titles
	 *            contains the column identifiers
	 */
	public void setColumnIdentifiers(String[] titles) {
		fColumnNames = titles;
		this.fireTableDataChanged();
	}

	/**
	 * Adds a row to the table model
	 * 
	 * @param row
	 *            contains the data to enter in the row
	 */
	public void addRow(Object[] row) {
		ArrayList<Object> tmp = new ArrayList<Object>();
		for (Object element : row) {
			tmp.add(element);
		}
		data.add(tmp);
		this.fireTableRowsInserted(data.size() - 1, data.size() - 1);
	}

	/**
	 * Deletes the given row from the table model. Does nothing if the row
	 * doesn't exist
	 */
	public void deleteRow(int row) {
		if (!((row < 0) && (row > data.size())))
			data.remove(row);
		this.fireTableRowsDeleted(row, row);
	}

	/**
	 * Clears all the rows.
	 */
	public void clearRows() {
		data = new ArrayList<ArrayList<Object>>();
		this.fireTableDataChanged();
	}

	/**
	 * Deletes a few columns, for some reason
	 * 
	 * @return the columns to delete
	 */
	public ArrayList<ArrayList<Object>> getColumsToDelete() {
		ArrayList<ArrayList<Object>> colums = new ArrayList<ArrayList<Object>>();

		for (ArrayList<Object> currRow : data) {
			if ((Boolean) currRow.get(0)) {
				colums.add(currRow);
			}
		}

		return colums;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return fColumnNames.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return data.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	public String getColumnName(int col) {
		return fColumnNames[col];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int row, int col) {
		return data.get(row).get(col);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	@SuppressWarnings("unchecked")
	public Class getColumnClass(int c) {
		if (fImplementCheckBoxColumn && (c == 0)) {
			Boolean tmp = new Boolean(true);
			return tmp.getClass();
		} else {
			String tmp = "";
			return tmp.getClass();
		}
	}

	/**
	 * Indicates whether the given cell is editable
	 * 
	 * @param row
	 *            contains the row to check
	 * @param col
	 *            contains the column to check
	 * @return whether the given cell was editable
	 */
	public boolean isCellEditable(int row, int col) {
		if (fImplementCheckBoxColumn && (col == 0)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Sets the given value at the given position
	 * 
	 * @param value
	 *            contains the value to set
	 * @param row
	 *            contains the row to check
	 * @param col
	 *            contains the column to check
	 */
	public void setValueAt(Object value, int row, int col) {
		data.get(row).set(col, value);
		fireTableCellUpdated(row, col);
	}
}
