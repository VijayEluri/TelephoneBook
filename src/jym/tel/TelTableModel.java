package jym.tel;
// CatfoOD 2008.2.27

import java.io.IOException;
import java.util.Arrays;

import javax.swing.table.AbstractTableModel;

import jym.tel.TableDataPack.Data;

/**
 * ����ں�ģ��
 */
public class TelTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 1365614389441957465L;

	private TableDataPack tdp;
	private Data d;
	
	
	public TelTableModel(TableDataPack tdp) {
		this.tdp = tdp;
		d = tdp.get();
	}
	
	/** �е����� */
	public int getColumnCount() {
		return d.columnCount;
	}

	/** �����е����� */
	public String getColumnName(int columnIndex) {
		return d.columnName[columnIndex];
	}
	
	/** ָ���������Ƿ���� */
	public boolean columnNameExits(String columnname) {
		columnname = columnname.trim();
		for (int i=0; i<d.columnName.length; ++i) {
			if (d.columnName[i].compareToIgnoreCase(columnname)==0) 
				return true;
		}
		return false;
	}
	
	/** �е��������� */
	public int getColumnIndex(String CName) {
		int i=0;
		boolean finded = false;
		
		for(i=0; i<d.columnCount; ++i) {
			if( d.columnName[i].compareToIgnoreCase(CName)==0 ) {
				finded=true;
				break;
			}
		}
		return finded? i: -1;
	}
	
	/** �е����� */
	public int getRowCount() {
		return d.rowCount;
	}

	/** �õ� ��/�� ������ */
	public Object getValueAt(int rowIndex, int columnIndex) {
		return d.data[rowIndex][columnIndex];
	}
	
	/** ��ȡ���� */
	public void readData() throws IOException {
		d = tdp.read();
	}
	
	/** �������� */
	public void saveData() throws IOException {
		tdp.write(d);
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}
	
	/** �ı� ��/�� ������ */
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		d.data[rowIndex][columnIndex] = aValue;
	}
	
	/** ����� */
	public void addRows(int crow) {
		Object[][] newData = new Object[d.rowCount+crow][d.columnCount];
		for(int r=0; r<d.rowCount; ++r) {
			for(int c=0; c<d.columnCount; ++c) {
				newData[r][c] = d.data[r][c];
			}
		}
		
		for (int r=d.rowCount; r<d.rowCount+crow; ++r) {
			newData[r] = tdp.addRow(r);
		}
		
		d.data = newData;
		d.rowCount += crow;
		
		fireTableDataChanged();
	}
	
	/** �Ƴ�ѡ����� */
	public void removeRows(int[] rows) {
		Object[][] newData = new Object[d.rowCount-rows.length][d.columnCount];
		int new_r = 0;
		for(int r=0; r<d.rowCount; ++r) {
			if( includeNum(rows, r) ) {
				tdp.removeRow(r);
				continue;
			}
			for(int c=0; c<d.columnCount; ++c) {
				newData[new_r][c] = d.data[r][c];
			}
			++new_r;
		}
		d.data = newData;
		d.rowCount = new_r;
		
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
		d.columnName = Arrays.copyOf(d.columnName, d.columnName.length + 1);
		d.columnName[d.columnName.length-1] = name;
		
		Object[][] newdate = new Object[d.rowCount][d.data[0].length+1];
		for(int r=0; r<d.rowCount; ++r) {
			for(int c=0; c<d.columnCount; ++c) {
				newdate[r][c] = d.data[r][c];
			}
			newdate[r][newdate[0].length-1] = null;
		}
		d.data = newdate;
		d.columnCount = d.columnName.length;
		
		fireTableStructureChanged();
	}
	
	/** �Ƴ��У��ͻ���֤��������Ч�� */
	public void removeColumn(String name) {
		final int deletecol = getColumnIndex(name);
		if (deletecol<0) return;
		--d.columnCount;
		
		String[] newcolname = new String[d.columnName.length-1];
		int oldindex = 0;
		for (int i=0; i<newcolname.length; ++i) {
			if (deletecol == oldindex) {
				++oldindex;
			}
			newcolname[i] = d.columnName[oldindex];
			++oldindex;
		}
		d.columnName = newcolname;
		
		Object[][] newdate = new Object[d.rowCount][d.columnCount];
		for(int r=0; r<d.rowCount; ++r) {
			oldindex = 0;
			for(int c=0; c<d.columnCount; ++c) {
				if (deletecol == oldindex) {
					++oldindex;
				}
				newdate[r][c] = d.data[r][oldindex];
				++oldindex;
			}
		}
		d.data = newdate;
		
		fireTableStructureChanged();
	}
	
	@SuppressWarnings("unused")
	private void printTable() {
		for (int i=0; i<d.columnName.length; ++i) {
			System.out.print(" "+ d.columnName[i]+"\t");
		}System.out.println();
		
		for(int r=0; r<d.rowCount; ++r) {
			for(int c=0; c<d.columnCount; ++c) {
				System.out.print(" "+ d.data[r][c] +"\t");
			}
			System.out.println();
		}
	}
	
	/** �Ƴ����� */
	public void autoRemoveNullRows() {
		int[] deleteLine = new int[d.rowCount];
		int deleteIndex = 0;

		for(int r=0; r<d.rowCount; ++r) {
			for(int c=0; c<d.columnCount; ++c) {
				if(d.data[r][c]==null || 
						d.data[r][c].toString().trim().length()<1) 
				{
					if(c==d.columnCount-1) deleteLine[deleteIndex++] = r;
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
		for(int r=0; r<d.rowCount; ++r) {
			for(int c=0; c<d.columnCount; ++c) {
				if( d.data[r][c] instanceof String ) {
					d.data[r][c] = ((String)d.data[r][c]).trim();
				}
			}
		}
		fireTableDataChanged();
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (d.data!=null && d.data[0]!=null && d.data[0][columnIndex]!=null) {
			return d.data[0][columnIndex].getClass();
		} else {
			return Object.class;
		}
	}

	/** ������ж���,�ͷ��ڴ� */
	public void quit() {
		tdp.quit();
		d = null;
	}
}
