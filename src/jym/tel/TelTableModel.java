package jym.tel;
// CatfoOD 2008.2.27

import java.io.IOException;
import java.util.Arrays;

import javax.swing.table.AbstractTableModel;

/**
 * ����ں�ģ��
 */
public class TelTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 1365614389441957465L;
	
	private Object[][] tableData;
	private String[] ColumnName;
	private volatile int row;
	private volatile int col;

	private TableDataPack tdp;
	
	
	public TelTableModel(TableDataPack tdp) {
		this.tdp = tdp;
	}
	
	/** �е����� */
	public int getColumnCount() {
		return col;
	}

	/** �����е����� */
	public String getColumnName(int columnIndex) {
		return ColumnName[columnIndex];
	}
	
	/** ָ���������Ƿ���� */
	public boolean columnNameExits(String columnname) {
		columnname = columnname.trim();
		for (int i=0; i<ColumnName.length; ++i) {
			if (ColumnName[i].compareToIgnoreCase(columnname)==0) 
				return true;
		}
		return false;
	}
	
	/** �е��������� */
	public int getColumnIndex(String CName) {
		int i=0;
		boolean finded = false;
		
		for(i=0; i<col; ++i) {
			if( ColumnName[i].compareToIgnoreCase(CName)==0 ) {
				finded=true;
				break;
			}
		}
		return finded? i: -1;
	}
	
	/** �е����� */
	public int getRowCount() {
		return row;
	}

	/** �õ� ��/�� ������ */
	public Object getValueAt(int rowIndex, int columnIndex) {
		return tableData[rowIndex][columnIndex];
	}
	
	/** ��ȡ���� */
	public void readData() throws IOException {
		TableDataPack.Data d = tdp.read();
		tableData = d.data;
		ColumnName = d.columnName;
		row = d.rowCount;
		col = d.columnCount;
	}
	
	/** �������� */
	public void saveData() throws IOException {
		TableDataPack.Data d = tdp.get();
		d.data = tableData;
		d.columnName = ColumnName;
		d.columnCount= col;
		d.rowCount   = row;
		tdp.write(d);
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}
	
	/** �ı� ��/�� ������ */
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		tableData[rowIndex][columnIndex] = aValue;
	}
	
	/** ����� */
	public void addRows(int crow) {
		Object[][] newData = new Object[row+crow][col];
		for(int r=0; r<row; ++r) {
			for(int c=0; c<col; ++c) {
				newData[r][c] = tableData[r][c];
			}
		}
		tableData=newData;
		row+=crow;
		
		fireTableDataChanged();
	}
	
	/** �Ƴ�ѡ����� */
	public void removeRows(int[] rows) {
		Object[][] newData = new Object[row-rows.length][col];
		int new_r = 0;
		for(int r=0; r<row; ++r) {
			if( includeNum(rows, r) ) continue;
			for(int c=0; c<col; ++c) {
				newData[new_r][c] = tableData[r][c];
			}
			++new_r;
		}
		tableData=newData;
		row = new_r;
		
		fireTableDataChanged();
	}
	
	/** arr[] �а��� num ������,����Ϊ�� */
	private boolean includeNum(int[] arr, int num) {
		for(int i=0; i<arr.length; ++i) {
			if(arr[i]==num) return true;
		}
		return false;
	}
	
	/** �����,�ͻ���֤��������Ч�� */
	public void addColumn(String name) {
		ColumnName = Arrays.copyOf(ColumnName, ColumnName.length+1);
		ColumnName[ColumnName.length-1] = name;
		
		Object[][] newdate = new Object[row][tableData[0].length+1];
		for(int r=0; r<row; ++r) {
			for(int c=0; c<col; ++c) {
				newdate[r][c] = tableData[r][c];
			}
			newdate[r][newdate[0].length-1] = null;
		}
		tableData = newdate;
		col = ColumnName.length;
		
		fireTableStructureChanged();
	}
	
	/** �Ƴ��У��ͻ���֤��������Ч�� */
	public void removeColumn(String name) {
		final int deletecol = getColumnIndex(name);
		if (deletecol<0) return;
		--col;
		
		String[] newcolname = new String[ColumnName.length-1];
		int oldindex = 0;
		for (int i=0; i<newcolname.length; ++i) {
			if (deletecol == oldindex) {
				++oldindex;
			}
			newcolname[i] = ColumnName[oldindex];
			++oldindex;
		}
		ColumnName = newcolname;
		
		Object[][] newdate = new Object[row][col];
		for(int r=0; r<row; ++r) {
			oldindex = 0;
			for(int c=0; c<col; ++c) {
				if (deletecol == oldindex) {
					++oldindex;
				}
				newdate[r][c] = tableData[r][oldindex];
				++oldindex;
			}
		}
		tableData = newdate;
		
		fireTableStructureChanged();
	}
	
	@SuppressWarnings("unused")
	private void printTable() {
		for (int i=0; i<this.ColumnName.length; ++i) {
			System.out.print(" "+ ColumnName[i]+"\t");
		}System.out.println();
		
		for(int r=0; r<row; ++r) {
			for(int c=0; c<col; ++c) {
				System.out.print(" "+tableData[r][c]+"\t");
			}
			System.out.println();
		}
	}
	
	/** �Ƴ����� */
	public void autoRemoveNullRows() {
		int[] deleteLine = new int[row];
		int deleteIndex = 0;

		for(int r=0; r<row; ++r) {
			for(int c=0; c<col; ++c) {
				if(tableData[r][c]==null || 
						tableData[r][c].toString().trim().length()<1) 
				{
					if(c==col-1) deleteLine[deleteIndex++] = r;
				}else{
					break;
				}
			}
		}
		
		int[] delRow = new int[deleteIndex];
		for(int i=0; i<deleteIndex; ++i) {
			delRow[i] = deleteLine[i];
		}
		
		removeRows(delRow);
	}
	
	/** �Ƴ�������Ŀ����β���ַ� */
	public void trimAll() {
		for(int r=0; r<row; ++r) {
			for(int c=0; c<col; ++c) {
				if( tableData[r][c] instanceof String ) {
					tableData[r][c] = ((String)tableData[r][c]).trim();
				}
			}
		}
		fireTableDataChanged();
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (tableData!=null && tableData[0]!=null && tableData[0][columnIndex]!=null) {
			return tableData[0][columnIndex].getClass();
		} else {
			return Object.class;
		}
	}

	/** ������ж���,�ͷ��ڴ� */
	public void quit() {
		tableData = null;
		ColumnName = null;
		row = -1;
		col = -1;
		tdp.quit();
	}
}
