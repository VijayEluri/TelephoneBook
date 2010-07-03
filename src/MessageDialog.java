// CatfoOD 2008.2.7

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

/**
 * �����ʾ��Ϣ����,��������� show(Title, Text) ����.
 * @author CatfoOD
 */
public class MessageDialog extends JDialog {
	private JLabel  label  = new JLabel();
	private JButton button = new JButton("     ȷ��     ");
	private JButton about  = new JButton(" ��ϸ��Ϣ ");
	private final WA wa = new WA(); 
	
	/**
	 * ����һ����Ϣ�Ի���
	 */
	public MessageDialog() {
		this(null);
	}
	/**
	 * ʹ�ö��㴰�ڳ�ʼ����ģʽ��MessageDialog
	 * @param f - �ϲ㴰��
	 */
	public MessageDialog(Frame f) {
		super(f,true);
		setResizable(false);
		addWindowListener(wa);
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int width = 330;
		int height= 110;
		int x = (int)( (dim.width-width)/2 );
		int y = (int)( (dim.height-height)/2);
		setBounds(x, y, width, height);
		
		button.addActionListener(wa);
		about.addActionListener(wa);
		Panel pan = new Panel(new FlowLayout(FlowLayout.CENTER,8,10));
		pan.add(button);
		pan.add(about);
		
		Panel pan2= new Panel(new FlowLayout(FlowLayout.CENTER,8,10));
		pan2.add(label);
		
		setLayout(new BorderLayout());
		add(pan2, 	BorderLayout.CENTER);
		add(pan,	BorderLayout.SOUTH);
	}
	
	/**
	 * ��ʾ�Ի���
	 * @param title - ����String
	 * @param message - ��ϢString,����>20,�����ϸ��Ϣ�Ի���
	 */
	public void show(String title, String message) {
		mge = message;
		int enter = message.indexOf('\n');
		if (enter<=0 || enter>20) enter = 20;
		if (message.length()>enter ) {
			message = message.substring(0, enter)+"        . . .";
			about.setVisible(true);
		}else{
			about.setVisible(false);
		}
		setTitle(title);
		label.setText(message);
		validate();
		setVisible(true);
	}
	public void show(String message) {
		show("Message.",message);
	}
	public void show(Exception e) {
		StackTraceElement[] list = e.getStackTrace();
		String text = e.getMessage()+'\n';
		text+="\n"+e.toString();
		for (int i=0; i<list.length; ++i) {
			text = text + "\n\tat "+list[i];
		}
		show("Error.",text);
	}
	
	private class WA extends WindowAdapter implements ActionListener {
		public void windowClosing(WindowEvent e) {
			setVisible(false);
		}
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()==button) {
				setVisible(false);
			}else if(e.getSource()==about) {
				showMore();
			}
		}
	}
	
	public void showMore() {
		md.show(mge);
	}
	private String mge;
	private MoreDialog md = new MoreDialog(this);
}

class MoreDialog extends JDialog {
	private JTextArea t = new JTextArea();
	
	MoreDialog(Dialog d) {
		super(d, "��ϸ��Ϣ", true);
		setResizable(false);
		WA wa = new WA();
		addWindowListener(wa);
		JButton b = new JButton("�ر�");
		b.addActionListener(wa);
		t.setEditable(false);
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int)(dim.width/2.3f);
		int height= (int)(dim.height/3.5f);
		int x = (int)( (dim.width-width)/2 );
		int y = (int)( (dim.height-height)/2);
		setBounds(x, y, width, height);
		
		JScrollPane s = new JScrollPane(t);
		s.setHorizontalScrollBarPolicy( s.HORIZONTAL_SCROLLBAR_NEVER );
		s.setVerticalScrollBarPolicy( s.VERTICAL_SCROLLBAR_AS_NEEDED );
		setLayout(new BorderLayout());
		add(s, BorderLayout.CENTER);
		add(b, BorderLayout.SOUTH);
	}
	public void show(String text) {
		t.setText(text);
		validate();
		setVisible(true);
	}
	private class WA extends WindowAdapter implements ActionListener {
		public void windowClosing(WindowEvent e) {
			setVisible(false);
		}
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
		}
	}
	public int getColumns() {
		return t.getColumns();
	}
}

