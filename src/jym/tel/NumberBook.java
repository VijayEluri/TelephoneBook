// CatfoOD 2008.2.24

package jym.tel;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.MenuEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import jym.lan.Lang;
import jym.wit.InputDialog;
import jym.wit.MenuListenerAdapter;
import jym.wit.Tools;

public class NumberBook extends JInternalFrame implements ActionListener {
	
	private static final long serialVersionUID = -8261975812454620998L;
	
	private JTable table;
	private TelTableModel telModel;
	private SortModel sortModel;
	private FilterModel filterModel;
	private TableDataPack tdp;
	private StateText stateText;
	
	private JPopupMenu popMenu = new JPopupMenu();
	private ML ml = new ML();
	
	
	public NumberBook(TableDataPack tdp) throws Exception {
		super("", true,true,true,true);

		setJMenuBar(creatMenu());
		creatPopMenu();
		addInternalFrameListener(new IFL());
		
		telModel = new TelTableModel(tdp);
		telModel.readData();
		setTitle(tdp.get().name);
		
		sortModel = new SortModel(telModel);
		filterModel =new FilterModel(sortModel);
		
		table = new JTable(filterModel);
		table.addMouseListener(ml);
		
		tdp.setEditor(new IEditorSet() {
			public void set(Class<?> c, TableCellEditor editor) {
				table.setDefaultEditor(c, editor);
			}
			public void set(Class<?> c, TableCellRenderer rand) {
				table.setDefaultRenderer(c, rand);
			}
		});
		
		JScrollPane pane = new JScrollPane(table);
		pane.addMouseListener(ml);
		stateText = new StateText();
		setLayout(new BorderLayout());
		
		add(pane);
		add(stateText, BorderLayout.SOUTH);
		
		this.tdp = tdp;
	}
	
	/** ��������ʽ�˵� */
	private void creatPopMenu() {
		padd  = creatMenu(Lang.get("num.menu.add"));
		pdel  = creatMenu(Lang.get("num.menu.del"));
		pcopy = creatMenu(Lang.get("num.menu.copy"));
		ppaste= creatMenu(Lang.get("num.menu.paste"));
		psetcols = creatMenu(Lang.get("num.menu.setcol"));
		
		popMenu.add(padd);
		popMenu.add(pcopy);
		popMenu.add(ppaste);
		popMenu.addSeparator();
		popMenu.add(pdel);
		popMenu.add(psetcols);
	}
	
	private JMenuItem padd;
	private JMenuItem pcopy;
	private JMenuItem ppaste;
	private JMenuItem pdel;
	private JMenuItem psetcols;
	
	/** �����˵��� */
	private JMenuBar creatMenu() {
		JMenuBar bar = new JMenuBar();
		
		file = new JMenu(Lang.get("num.menu.file"));
		save 		= creatMenu("num.menu.save", "Ctrl+S");
		changename	= creatMenu("num.menu.rename");
		changepw	= creatMenu("num.menu.repass");
		quitnotsave	= creatMenu("num.menu.qnsave");
		savequit	= creatMenu("num.menu.qsave", "Ctrl+Q");
		file.add(save);
		file.addSeparator();
		file.add(changename);
		file.add(changepw);
		file.addSeparator();
		file.add(quitnotsave);
		file.add(savequit);
		
		edit = new JMenu(Lang.get("num.menu.edit"));
		add  = creatMenu("num.menu.add");
		del  = creatMenu("num.menu.del");
		copy = creatMenu("num.menu.copy");
		paste= creatMenu("num.menu.paste");
		removeNull = creatMenu("num.menu.delnul");
		trim = creatMenu("num.menu.trim");
		find = creatMenu("num.menu.find", "Ctrl+F");
		findnext = creatMenu("num.menu.fnext", "F3");
		edit.add(add);
		edit.add(del);
		edit.add(copy);
		edit.add(paste);
		edit.addSeparator();
		edit.add(removeNull);
		edit.add(trim);
		edit.addSeparator();
		edit.add(find);
		edit.add(findnext);
		
		sort 	= new JMenu(Lang.get("num.menu.sort"));
		showall = creatMenu("num.menu.shall");
		sortfor = new JMenu(Lang.get("num.menu.colsor"));
		sortfor.addMenuListener(new MA());
		filter  = creatMenu("num.menu.inc");
		exclude = creatMenu("num.menu.exc");
		sort.add(showall);
		sort.add(filter);
		sort.add(exclude);
		sort.addSeparator();
		sort.add(sortfor);
		
		rowItem   = new JMenu(Lang.get("num.menu.colum"));
		addRow    = creatMenu("num.menu.addcol");
		removeRow = creatMenu("num.menu.delcol");
		setcols   = creatMenu("num.menu.setcol");
		rowItem.add(addRow);
		rowItem.add(removeRow);
		rowItem.addSeparator();
		rowItem.add(setcols);
		
		bar.add(file);
		bar.add(edit);
		bar.add(sort);
		bar.add(rowItem);
		
		return bar;
	}
	
	private JMenu file;
	private JMenuItem save;
	private JMenuItem changepw;
	private JMenuItem changename;
	private JMenuItem savequit;
	private JMenuItem quitnotsave;
	
	private JMenu edit;
	private JMenuItem add;
	private JMenuItem del;
	private JMenuItem copy;
	private JMenuItem paste;
	private JMenuItem removeNull;
	private JMenuItem trim;
	private JMenuItem find;
	private JMenuItem findnext;
	
	private JMenu sort;
	private JMenuItem showall;
	private JMenu sortfor;
	private JMenuItem exclude;
	private JMenuItem filter;
	
	private JMenu rowItem;
	private JMenuItem addRow;
	private JMenuItem removeRow;
	private JMenuItem setcols;
	
	private JMenuItem creatMenu(String name, String...quick) {
		JMenuItem item = new JMenuItem(Lang.get(name));
		item.addActionListener(this);
		return item;
	}
	
	// ״̬��
	private class StateText extends JTextField implements Runnable {
		private static final long serialVersionUID = 5310188383572543784L;

		StateText() {
			this.setEditable(false);
			Thread t = new Thread(this);
			t.setDaemon(true);
			t.setPriority(Thread.MIN_PRIORITY);
			t.start();
		}
		
		public boolean stop = false;
		
		public void run() {
			while( !stop ) {
				try {
					String state = Lang.get("num.state", 
							telModel.getRowCount(), filterModel.getRowCount());

					int[] s = getSelectedRows();
					if( s!=null && s.length>0 ) {
						String select = Lang.get("num.selectline", s.length);
						this.setText(state + "  " + select);
					} else {
						this.setText(state);
					}
					
					Thread.sleep(800);
				} catch (InterruptedException e) {}
			}
		}
	}
	
	// ����˵�����
	private class MA extends MenuListenerAdapter implements ActionListener {
		public void menuSelected(MenuEvent e) {
			if(e.getSource()==sortfor) {
				Component[] coms = sortfor.getComponents();
				for(int i=0; i<coms.length; ++i) {
					((JMenuItem)(coms[i])).removeActionListener(this);
				}
				sortfor.removeAll();
				int colcont = telModel.getColumnCount();
				for(int i=0; i<colcont; ++i) {
					JMenuItem it = new JMItem( telModel.getColumnName(i), i);
					it.addActionListener(this);
					sortfor.add(it);
				}
			}
		}
		
		private class JMItem extends JMenuItem {
			private static final long serialVersionUID = -6308665539500668163L;
			int index;
			
			JMItem(String name, int itemIndex) {
				super(name);
				index = itemIndex;
			}
		}
		
		public void actionPerformed(ActionEvent e) {
			if( e.getSource() instanceof JMItem ) {
				sortModel.sort( ((JMItem)e.getSource()).index );
			}
		}
	}
	
	// ���ڹر��¼�
	private class IFL extends InternalFrameAdapter {
		public void internalFrameClosed(InternalFrameEvent e) {
			quit(true);
		}
	}
	
	/** �رմ��ڱ�������,�˳� */
	public void quit() { quit(true); }
	
	/**
	 * �˳�
	 * @param save - true,�������ݲ��˳�. false,�˳�����������
	 */
	public void quit(boolean save) {
		if(quited) return;
		
		popMenu.setVisible(false);
		setVisible(false);
		quited = true;
		stateText.stop=true;
		try{
			this.removeAll();
			table.setEnabled(false);
			
			if (save) {
				telModel.autoRemoveNullRows();
				telModel.saveData();
			}
			telModel.quit();
			//sortModel.quit();
			//filterModel.quit();
			
			table		= null;
			telModel	= null;
			sortModel	= null;
			filterModel	= null;
			tdp			= null;
			stateText	= null;
			popMenu		= null;
			ml			= null;
			
			this.dispose();
		}catch(Exception er){
			Tools.println(er);
		}
	}
	private boolean quited = false;
	
	/** ��������ɼ�,���ɼ�,����Ѿ��˳�,�����κ���Ӧ */
	public void setVisible(boolean aFlag) {
		if(quited) return;
		super.setVisible(aFlag);
	}
	
	// ��Ϣ��Ӧ
	public void actionPerformed(ActionEvent e) {
		popMenu.setVisible(false);
		// ����
		if( e.getSource()==save ) {
			try{
				telModel.saveData();
			}catch(Exception er){
				Tools.println(er);
			}
		}
		// �ı�����
		else if( e.getSource()==changename ) {
			InputDialog input = new InputDialog(null, Lang.get("num.newname"), false);
			input.setText(tdp.get().name);
			if( input.getInput()==InputDialog.OK ) {
				if( input.getResult().length()>1 ){
					tdp.get().name = input.getResult();
					setTitle(tdp.get().name);
				}else{
					Tools.message(Lang.get("num.namelen"));
				}
			}
		}
		// �ı�����
		else if( e.getSource()==changepw ) {
			InputDialog input = new InputDialog(null, Lang.get("num.newpass"), true);
			if( input.getInput()==InputDialog.OK ) {
				if( input.getResult().length()<1 ){
					Tools.message(Lang.get("num.clearpass"));
					tdp.get().password = TableDataPack.DEFAULTPASSWORD;
					return;
				}else{
					InputDialog rein = new InputDialog(null, Lang.get("num.reinput"), true);
					if( rein.getInput()==InputDialog.OK ) {
						if( rein.getResult().compareTo( input.getResult() )==0 ) {
							tdp.get().password = rein.getResult();
							Tools.message(Lang.get("num.passseted"));
							return;
						}else{
							Tools.message(Lang.get("num.repasserr"));
							return;
						}
					}else{
						Tools.message(Lang.get("num.nosetpass"));
						return;
					}
				}
			}
		}
		// �˳����ǲ���������
		else if( e.getSource()==quitnotsave) {
			quit(false);
		}
		// �����˳�
		else if( e.getSource()==savequit ) {
			quit(true);
		}
		// ���
		else if( e.getSource()==add || e.getSource()==padd ) {
			telModel.addRows(5);
		}
		// ɾ��
		else if( e.getSource()==del || e.getSource()==pdel ) {
			telModel.removeRows( getSelectedRows() );
		}
		// ����
		else if( e.getSource()==copy || e.getSource()==pcopy) {
			// {{��Ӵ���}}
			Tools.functionNotComplete();
		}
		// ճ��
		else if( e.getSource()==paste || e.getSource()==ppaste ) {
			// {{��Ӵ���}}
			Tools.functionNotComplete();
		}
		// �Ƴ�����
		else if( e.getSource()==removeNull ) {
			telModel.autoRemoveNullRows();
		}
		// �Ƴ���β���ַ�
		else if( e.getSource()==trim ) {
			telModel.trimAll();
		}
		// ����
		else if( e.getSource()==find ) {
			// {{��Ӵ���}}
			Tools.functionNotComplete();
		}
		// ������һ��
		else if( e.getSource()==findnext ) {
			// {{��Ӵ���}}
			Tools.functionNotComplete();
		}
		// ��ʾȫ��
		else if( e.getSource()==showall ) {
			filterModel.allDisplay();
		}
		// ����
		else if( e.getSource()==filter ) {
			InputDialog in = new InputDialog(null, Lang.get("num.in.inc"));
			if( in.getInput()==InputDialog.OK ) {
				filterModel.IncludeWord( in.getResult() );
			}
		}
		//
		else if( e.getSource()==exclude ) {
			InputDialog in = new InputDialog(null, Lang.get("num.in.exc"));
			if( in.getInput()==InputDialog.OK ) {
				filterModel.ExcludeWord( in.getResult() );
			}
		}
		// �����
		else if( e.getSource()==addRow ) {
			InputDialog in = new InputDialog(null, Lang.get("num.in.newcoln"));
			if( in.getInput()==InputDialog.OK ) {
				String newcolumn = in.getResult().trim();
				if (newcolumn!=null && newcolumn.length()>0) {
					if ( !telModel.columnNameExits(newcolumn) ) {
						telModel.addColumn(newcolumn);
						table.setModel(telModel);
					} else {
						Tools.message(Lang.get("num.colnexist", newcolumn));
					}
				} else {
					Tools.message(Lang.get("num.reincname"));
				}
			}
		}
		// ɾ����
		else if( e.getSource()==removeRow ) {
			if (telModel.getColumnCount()<=1) {
				Tools.message(Lang.get("num.nodelcol"));
				return;
			}

			ButtonDialog bdg = new ButtonDialog(null, Lang.get("num.sldelcol"));
			bdg.addButtons(telModel);
			
			if ( bdg.showButtonDialog()==bdg.OK ) {
				ValidateDialog vd = new ValidateDialog(null, 
						Lang.get("num.confirmdel", bdg.getSelectedName()) );
				
				if (vd.getResult()==ValidateDialog.OK) {
					telModel.removeColumn(bdg.getSelectedName());
					table.setModel(telModel);
				}
			}
		}
		// ����ѡ����Ŀ������
		else if( e.getSource()==setcols || e.getSource()==psetcols ) {
			int[] select = getSelectedRows();
			if(select.length<1) {
				Tools.message(Lang.get("num.slcolval"));
				return;
			}
			// ѡ��Ҫ���ĵ���
			ButtonDialog bdg = new ButtonDialog(null);
			bdg.addButtons(telModel);
			
			if ( bdg.showButtonDialog()==bdg.OK ) {
				int c = bdg.getSelected();
				// �ж��Ƿ�Ҫ����
				for(int x=0; x<select.length; ++x) {
					String ts = (String)telModel.getValueAt(select[x], c);
					if( ts!=null && ts.trim().length()>0 ) {
						ValidateDialog vd = 
							new ValidateDialog(null, Lang.get("num.cfmrelse"));
						if (vd.getResult()==ValidateDialog.OK) {
							break;
						}else{
							return;
						}
					}
				}
				// �����е�����
				InputDialog ind = new InputDialog(null, Lang.get("num.colnewval"));
				if ( ind.getInput()==InputDialog.OK ) {
					String news = ind.getResult();
					for(int x=0; x<select.length; ++x) {
						telModel.setValueAt(news, select[x], c);
					}
					telModel.fireTableDataChanged();
					Tools.message(Lang.get("num.colvalok"));
				}else{
					Tools.message(Lang.get("num.colvalcal"));
				}
			}else{
				return;
			}
		}
	}
	
	/**
	 * ����ѡ�����
	 * @return int[] - ��Եײ�Table��ӳ��
	 */
	private int[] getSelectedRows() {
		int[] s = table.getSelectedRows();
		for(int i=0; i<s.length; ++i) {
			s[i] = sortModel.getRow( filterModel.getRow(s[i]) );
		}
		return s;
	}
	
	/**
	 * ����ָ�����ļ��Ƿ��Ѿ�����ǰ���ڴ�
	 * @param f - Ҫ���Ե��ļ�
	 * @return �Ѿ��򿪷���true
	 */
	public boolean isFileOpened(File f) {
		if (quited) return false;
		return tdp.get().file.equals(f);
	}
	
	/**
	 * ��д����ķ���,�����Ѿ��رշ���true
	 */
	public boolean isClosed() {
		return quited;
	}
	
	private class ML extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			if ( e.getButton()==MouseEvent.BUTTON3 ) {
				popMenu.show((Component)e.getSource(), e.getX(), e.getY());
			}else{
				popMenu.setVisible(false);
			}
		}
	}
}
