// CatfoOD 2011-4-30 ����06:56:06

package jym.vcf;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import jym.tel.TableDataPack;

public class QuotedCoder {
	
	/** �����ַ��� */
	public static final int INSERT_ENTER_CNT = 20;
	
	/**
	 * �����ַ���, ���ر������µ��ַ�������, ����ַ����������, �򷵻�values����
	 * @param props
	 * @param values
	 */
	public static String[] encode(Map<String, String> props, String[] values) {
		
		if (needEncode(values)) {
			String charset = getCharset(props);
			props.put("ENCODING", "QUOTED-PRINTABLE");
			props.put("CHARSET", charset);
			
			String[] ns = new String[values.length];
			
			for (int i=0; i<ns.length; ++i) {
				if (values[i]!=null) {
					ns[i] = string2quoted(values[i], charset);
				}
			}
			return ns;
			
		} else {
			props.remove("ENCODING");
			props.remove("CHARSET");
			return values;
		}
	}

	/**
	 * �����ַ���, �ú������غ�, values���ӽ������ַ���<br>
	 * ����ַ�����Ҫ����, props�ᱻ�޸�
	 * @param props
	 * @param values
	 */
	public static void decode(Map<String, String> props, String[] values) {
		if (isQuotedCoding(props)) {
			String charset = getCharset(props);
			
			for (int i=0; i<values.length; ++i) {
				if (values[i]!=null) {
					values[i] = quoted2string(values[i], charset);
				}
			}
		}
	}
	

	/** ���ص��ַ������ݳ��Ȼ���뻻�� */
	private static String string2quoted(String str, String charset) {
		try {
			byte[] _b = str.getBytes(charset);
			StringBuilder out = new StringBuilder();
			
			for (int i=0; i<_b.length; ++i) {
				out.append('=');
				out.append( Integer
						.toHexString( TableDataPack.toInt(_b[i]) )
						.toUpperCase() );
				
				if (_b.length > INSERT_ENTER_CNT) {
					if (i % INSERT_ENTER_CNT == INSERT_ENTER_CNT-1) {
						out.append('\n');
					}
				}
			}
			
			str = out.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return str;
	}
	
	private static String quoted2string(String str, String charset) {
		String[] _t = str.split("=", 0);
		byte[] _b = new byte[_t.length];
		int bi = 0;
		
		for (int i=0; i<_t.length; ++i) {
			try {
				if (_t[i].length()>0) {
					_b[bi] = (byte) Integer.parseInt(_t[i], 16);
					bi++;
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		try {
			str = new String(_b, 0, bi, charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}
	
	private static boolean needEncode(String[] s) {
		boolean need = false;
		for (int i=0; i<s.length; ++i) {
			if (s[i]==null) continue;
			need = (s[i].length() != s[i].getBytes().length);
			if (need) break;
		}
		return need;
	}
	
	private static boolean isQuotedCoding(Map<String, String> props) {
		return "QUOTED-PRINTABLE".equalsIgnoreCase(  props.get("ENCODING") );
	}
	
	private static String getCharset(Map<String, String> props) {
		String charset = props.get("CHARSET");
		if (charset==null) {
			charset = "UTF-8";
		}
		return charset;
	}
}
