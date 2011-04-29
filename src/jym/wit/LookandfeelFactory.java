package jym.wit;
// CatfoOD 2008.5.28

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.UIManager;

/**
 * ʹ����substance.jar�������
 * ��֤������·�������ҵ�����
 */
public class LookandfeelFactory {
	private LookandfeelFactory() {}
	
	private static final String setfile = ".lookandfeel";
	
	/**
	 * ���ؿ�����۵��ַ�����ʾ
	 */
	public static String[] getLookandFeel() {
		return lookandfeel;
	}
	
	/**
	 * ��ȡ���������
	 */
	public static void loadConfig() throws IOException {
		FileInputStream in = new FileInputStream(setfile);
		byte[] b = new byte[150];
		int c = in.read(b);
		set( new String(b,0,c) );
	}
	
	/**
	 * ������۵������ļ�
	 */
	public static void saveConfig(String name) throws IOException {
		FileOutputStream out = new FileOutputStream(setfile);
		out.write( name.getBytes() );
		out.flush();
		out.close();
	}
	
	/**
	 * ��ǰ����۱��浽�����ļ�
	 */
	public static void saveConfig() {
		try {
			String s = UIManager.getLookAndFeel().getClass().getName();
			if (s!=null) saveConfig( s );
		} catch (IOException e) {}
	}
	
	/**
	 * ��ָ����������ı䵱ǰ���̵�Ƥ��
	 * @param subname - ����۵�����
	 */
	public static void setStanceFeel(String subname) {
		String subskin = "Substance"+subname+"LookAndFeel";
		String sublook = "org.jvnet.substance.skin."+subskin;
		// String defaultlook = "org.jvnet.substance.SubstanceLookAndFeel";
		set(sublook);
	}

	/**
	 * ��ȡ������۵Ĳ˵���ı�ݷ���
	 * @return JMenuItem[] - ������۵Ĳ˵�����
	 */
	public static JMenuItem[] getLookandFeelMenuItem() {
		JMenuItem[] jm = new JMenuItem[lookandfeel.length];
		MItemGroup group = new MItemGroup();
		for (int i=0; i<jm.length; ++i) {
			jm[i] = new MItem(lookandfeel[i]);
			group.add((MItem)jm[i]);
		}
		return jm;
	}
	
	/**
	 * �����ѡ��һ���
	 */
	public static void setRandomLookandFeel() {
		setStanceFeel( lookandfeel[(int)(Math.random()*lookandfeel.length)] );
	}
	
	private static String[] lookandfeel = {
		"Autumn", "BusinessBlackSteel", "BusinessBlueSteel", "Business",
		"ChallengerDeep", "CremeCoffee", "Creme", "EmeraldDusk",
		"FieldOfWheat", "FindingNemo", "GreenMagic", "Magma", "Mango",
		"MistAqua", "MistSilver", "Moderate", "NebulaBrickWall",
		"Nebula", "OfficeBlue2007", "OfficeSilver2007", "RavenGraphiteGlass",
		"RavenGraphite", "Raven", "Sahara",
	};
	
	/**
	 * �������ΪSwing��Ĭ�����
	 */
	public static void setSwingLookandFeel() {
		set("javax.swing.plaf.metal.MetalLookAndFeel");
	}
	
	/**
	 * �������Ϊ����ϵͳ�����
	 */
	public static void setNativeLookandFeel() {
		set(UIManager.getSystemLookAndFeelClassName());
	}
	
	private static void set(String s) {
		try {
			UIManager.setLookAndFeel(s);
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
}


class MItemGroup {
	private ArrayList<MItem> list = new ArrayList<MItem>();
	
	public void add(MItem i) {
		list.add(i);
		i.addGroup(this);
	}
	
	protected void inform(MItem item) {
		for (int i=0; i<list.size(); ++i) {
			if (list.get(i)!=item) {
				list.get(i).inform();
			}
		}
	}
}