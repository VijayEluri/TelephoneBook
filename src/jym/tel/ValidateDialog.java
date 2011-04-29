package jym.tel;
// CatfoOD 2008.2.26

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

/**
 * ѡ�� ��/�� �Ի���
 */
public class ValidateDialog extends JDialog {
	
	private static final long serialVersionUID = -2207991857109781194L;
	
	private JButton ok = new JButton("   ��   ");
	private JButton cancel = new JButton("   ��   ");
	
	public static final int OK = 1;
	public static final int CANCEL = 2;
	public static final int ERROR =3;
	
	private WA wa = new WA();
	private int result = ERROR;
	
	
	/**
	 * ��һ��ȷ��ȡ���Ի���
	 * @param message - Ҫ��ʾ����Ϣ
	 */
	public ValidateDialog(Frame f, String message) {
		super(f, true);
		setTitle("Are you sure..");
		setResizable(false);
		addWindowListener(wa);
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int width = 300;
		int height= 100;
		int x = (int)( (dim.width-width)/2 );
		int y = (int)( (dim.height-height)/2);
		setBounds(x, y, width, height);
		
		ok.addActionListener(wa);
		cancel.addActionListener(wa);
		Panel pan = new Panel(new FlowLayout(FlowLayout.CENTER,8,10));
		pan.add(ok);
		pan.add(cancel);
		
		Panel pan2 = new Panel(new FlowLayout(FlowLayout.CENTER,8,10));
		pan2.add( new JLabel(message) );
		
		setLayout(new BorderLayout());
		add(pan2, 	BorderLayout.CENTER);
		add(pan,	BorderLayout.SOUTH);
		
		setVisible(true);
	}
	
	/**
	 * ���ؽ��
	 * @return int - OK, CANCEL
	 */
	public int getResult() {
		return result;
	}
	
	private class WA extends WindowAdapter implements ActionListener {
		public void windowClosing(WindowEvent e) {
			setVisible(false);
			result=CANCEL;
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
}
