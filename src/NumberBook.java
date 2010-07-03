// CatfoOD 2008.2.24

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

public class NumberBook extends JInternalFrame implements ActionListener {
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
		
		sortModel= new SortModel(telModel);
		filterModel=new FilterModel(sortModel);
		
		table = new JTable(filterModel);
		table.addMouseListener(ml);
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
		padd  = creatMenu("���");
		pdel  = creatMenu("ɾ��");
		pcopy = creatMenu("����");
		ppaste= creatMenu("ճ��");
		psetcols = creatMenu("����ѡ����Ŀ������..");
		
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
		
		file = new JMenu("�ļ�");
		save = creatMenu("���� Ctrl+S");
		changename=creatMenu("�ı�绰��������..");
		changepw = creatMenu("�ı�����..");
		quitnotsave = creatMenu("�˳�,��������ǰ�޸ĵ�����");
		savequit = creatMenu("���沢�˳� Ctrl+Q");
		file.add(save);
		file.addSeparator();
		file.add(changename);
		file.add(changepw);
		file.addSeparator();
		file.add(quitnotsave);
		file.add(savequit);
		
		edit = new JMenu("�༭");
		add  = creatMenu("���");
		del  = creatMenu("ɾ��");
		copy = creatMenu("����");
		paste= creatMenu("ճ��");
		removeNull = creatMenu("�Ƴ�����");
		trim = creatMenu("�Ƴ�������Ŀ����β���ַ�");
		find = creatMenu("����.. Ctrl+F");
		findnext = creatMenu("������һ�� F3");
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
		
		sort = new JMenu("����/ɸѡ");
		showall = creatMenu("��ʾȫ��");
		sortfor = new JMenu("��������");
			sortfor.addMenuListener(new MA());
		filter  = creatMenu("��ʾ�������ʵ���Ŀ");
		exclude = creatMenu("�˳��������ʵ���Ŀ");
		sort.add(showall);
		sort.add(filter);
		sort.add(exclude);
		sort.addSeparator();
		sort.add(sortfor);
		
		rowItem   = new JMenu("��/��Ŀ");
		addRow    = creatMenu("���һ����Ŀ��");
		removeRow = creatMenu("�Ƴ�һ����Ŀ��");
		setcols   = creatMenu("����ѡ����Ŀ������..");
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
	
	private JMenuItem creatMenu(String name) {
		JMenuItem item = new JMenuItem(name);
		item.addActionListener(this);
		return item;
	}
	
	// ״̬��
	private class StateText extends JTextField implements Runnable {
		StateText() {
			this.setEditable(false);
			Thread t = new Thread(this);
			t.setDaemon(true);
			t.setPriority(t.MIN_PRIORITY);
			t.start();
		}
		
		public boolean stop = false;
		
		public void run() {
			while( !stop ) {
				try {
					int[] s = getSelectedRows();
					String select = "";
					if( s!=null && s.length>0 ) {
						select = "\tѡ����:"+s.length+"��.";
					}
					String state = 	" ������:"+telModel.getRowCount()+
									"\t��ʾ������:"+filterModel.getRowCount();
					this.setText(state+select);
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
			
			table = null;
			telModel = null;
			sortModel = null;
			filterModel = null;
			tdp = null;
			stateText = null;
			popMenu = null;
			ml = null;
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
			InputDialog input = new InputDialog(null, "�µ�����", false);
			input.setText(tdp.get().name);
			if( input.getInput()==input.OK ) {
				if( input.getResult().length()>1 ){
					tdp.get().name = input.getResult();
					setTitle(tdp.get().name);
				}else{
					Tools.message("���ֱ������1����ĸ.");
				}
			}
		}
		// �ı�����
		else if( e.getSource()==changepw ) {
			InputDialog input = new InputDialog(null, "�µ�����", true);
			if( input.getInput()==input.OK ) {
				if( input.getResult().length()<1 ){
					Tools.message("�����Ѿ���ɾ��,��ʹ������.");
					tdp.get().password = tdp.DEFAULTPASSWORD;
					return;
				}else{
					InputDialog rein = new InputDialog(null, "��������һ��", true);
					if( rein.getInput()==rein.OK ) {
						if( rein.getResult().compareTo( input.getResult() )==0 ) {
							tdp.get().password = rein.getResult();
							Tools.message("�����Ѿ�����,�´ε�¼��ʹ���µ�����.");
							return;
						}else{
							Tools.message("������������벻ͬ,������..");
							return;
						}
					}else{
						Tools.message("������������");
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
			InputDialog in = new InputDialog(null, "��������ĵ���");
			if( in.getInput()==in.OK ) {
				filterModel.IncludeWord( in.getResult() );
			}
		}
		//
		else if( e.getSource()==exclude ) {
			InputDialog in = new InputDialog(null, "����Ҫ�˳��ĵ���");
			if( in.getInput()==in.OK ) {
				filterModel.ExcludeWord( in.getResult() );
			}
		}
		// �����
		else if( e.getSource()==addRow ) {
			InputDialog in = new InputDialog(null, "���е�����");
			if( in.getInput()==in.OK ) {
				String newcolumn = in.getResult().trim();
				if (newcolumn!=null && newcolumn.length()>0) {
					if ( !telModel.columnNameExits(newcolumn) ) {
						telModel.addColumn(newcolumn);
						table.setModel(telModel);
					} else {
						Tools.message("'"+newcolumn+"' ���Ѿ����ڣ���ʹ�ò�ͬ������.");
					}
				} else {
					Tools.message("��������Ч������.");
				}
			}
		}
		// ɾ����
		else if( e.getSource()==removeRow ) {
			if (telModel.getColumnCount()<=1) {
				Tools.message("��Ч��������ֻ��һ�У����ܼ���ɾ��.");
				return;
			}

			ButtonDialog bdg = new ButtonDialog(null, "ѡ��Ҫɾ������");
			bdg.addButtons(telModel);
			
			if ( bdg.showButtonDialog()==bdg.OK ) {
				ValidateDialog vd = new ValidateDialog(null, "'"+
						bdg.getSelectedName()+ "' �е��������ݶ��ᶪʧ��ȷ��ô��");
				
				if (vd.getResult()==vd.OK) {
					telModel.removeColumn(bdg.getSelectedName());
					table.setModel(telModel);
				}
			}
		}
		// ����ѡ����Ŀ������
		else if( e.getSource()==setcols || e.getSource()==psetcols ) {
			int[] select = getSelectedRows();
			if(select.length<1) {
				Tools.message("����ѡ��Ҫ���ĵ���Ŀ");
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
							new ValidateDialog(null,"ѡ�����Ŀ��������,Ҫ����ô?");
						if (vd.getResult()==vd.OK) {
							break;
						}else{
							return;
						}
					}
				}
				// �����е�����
				InputDialog ind = new InputDialog(null, "�µ�����");
				if ( ind.getInput()==ind.OK ) {
					String news = ind.getResult();
					for(int x=0; x<select.length; ++x) {
						telModel.setValueAt(news, select[x], c);
					}
					telModel.fireTableDataChanged();
					Tools.message("�޸ĳɹ�!");
				}else{
					Tools.message("ȡ���޸�..");
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
			if ( e.getButton()==e.BUTTON3 ) {
				popMenu.show((Component)e.getSource(), e.getX(), e.getY());
			}else{
				popMenu.setVisible(false);
			}
		}
	}
}
