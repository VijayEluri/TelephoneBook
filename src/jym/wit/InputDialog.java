package jym.wit;
// CatfoOD 2008.2.25

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import jym.lan.Lang;

/**
 * ����Ի���
 */
public class InputDialog extends JDialog {
	
	private static final long serialVersionUID = -9142246621039344159L;
	
	private JButton ok = new JButton(Lang.get("all.cancel"));
	private JButton cancel = new JButton(Lang.get("all.cancel"));
	private JTextField text;

	private WA wa = new WA();
	private Panel pan2;
	private int result = WAIT_INPUT;
	
	public static final int WAIT_INPUT = 0;
	public static final int OK = 1;
	public static final int CANCEL = 2;
	public static final int TEXT_WEIGHT = 15;
	
	
	/** �½�һ����ģʽ�Ի��� */
	public InputDialog(Frame f, String message) {
		this(f,message,false);
	}
	/**
	 * �½�һ����ģʽ�Ի��� 
	 * @param f - ������
	 * @param message - ��Ϣ
	 * @param password - ����ģʽ
	 */
	public InputDialog(Frame f, String message, boolean password) {
		super(f, true);
		setTitle("Wait you Input...");
		setResizable(false);
		addWindowListener(wa);
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int width = 300;
		int height= 110;
		int x = (int)( (dim.width-width)/2 );
		int y = (int)( (dim.height-height)/2);
		setBounds(x, y, width, height);
		
		if( password ) {
			text = new JPasswordField(TEXT_WEIGHT);
		}else{
			text = new JTextField(TEXT_WEIGHT);
		}
		pan2 = new Panel(null);
		JLabel jl = new JLabel(message);
		jl.setHorizontalAlignment(SwingConstants.RIGHT);
		jl.setBounds(10, 10, 110, 20);
		text.setBounds(130, 10, 140, 20);
		pan2.add(jl);
		pan2.add(text);
		
		ok.addActionListener(wa);
		cancel.addActionListener(wa);
		Panel pan = new Panel(new FlowLayout(FlowLayout.CENTER,8,10));
		pan.add(ok);
		pan.add(cancel);
		
		setLayout(new BorderLayout());
		add(pan2, 	BorderLayout.CENTER);
		add(pan,	BorderLayout.SOUTH);
	}
	
	/**
	 * ��ʼ�ȴ�����
	 * @return int - OK, CANCEL, WAIT_INPUT,
	 */
	public int getInput() {
		if( result!=WAIT_INPUT ) return CANCEL;
		this.setVisible(true);
		return result;
	}
	
	/**
	 * ��������Ľ��
	 * @return - String
	 */
	public String getResult() {
		return text.getText().trim();
	}
	
	private class WA extends WindowAdapter implements ActionListener {
		public void windowClosing(WindowEvent e) {
			setVisible(false);
		}
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()==ok) {
				setVisible(false);
				result=OK;
			}else if(e.getSource()==cancel) {
				setVisible(false);
				result=CANCEL;
			}
		}
	}
	
	/**
	 * ���������Ĭ�ϵ���ʾ����
	 * @param t - Ҫ��ʾ������
	 */
	public void setText(String t) {
		text.setText(t);
		text.selectAll();
	}
}
