// CatfoOD 2008.2.24

public class Tools {
	private Tools() {}
	private static MessageDialog md = new MessageDialog();
	
	/**
	 * һ���Ի��������Ϣ
	 * @param e - Ҫ��ʾ�Ĵ���
	 */
	public static void println(Exception e) {
		md.show(e);
	}
	public static void message(String s) {
		md.show(s);
	}
	public static void p(Object o) {
		System.out.println(o);
	}
	
	public static void functionNotComplete() {
		md.show("����̫����������δʵ��.\n��Ҫ��ȡ���๦�ܣ�����������ϵ:" +
				"yanming-sohu@sohu.com\nqq:412475540");
	}
}
