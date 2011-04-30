package jym.vcf;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Type {
	
	private final static Map<String, String> map = new HashMap<String, String>();
	private final static Map<String, Integer> sorter = new HashMap<String, Integer>();
	private static Integer count = 0;
	
	static {
	//	set("N", 		"����"		);
		set("FN",		"����"		);
		set("TEL",		"�绰"		);
		set("X-MSN",	"MSN"		);
		set("X-AIM",	"QQ"		);
		set("NOTE",		"��ע"		);
		set("URL",		"��ַ"		);
		set("TITLE",	"����"		);
		set("ORG",		"��˾"		);
		set("ADR",		"��ַ"		);
		set("EMAIL",	"����"		);
		
		set("X-ANDROID-CUSTOM",			"�Զ���"			);
		set("X-PHONETIC-FIRST-NAME",	"first name"	);
		set("X-PHONETIC-MIDDLE-NAME",	"middle name"	);
		set("X-PHONETIC-LAST-NAME",		"last name"		);
		

		set("HOME",		"��ͥ"		);
		set("WORK",		"����"		);
		set("VOICE",	"����"		);
		set("CELL",		""			);
	}
	
	
	public static String get(String name) {
		return map.get(name);
	}
	
	/** ѹ���˳���Ӱ����ʾ��˳��  */
	private static void set(String name, String cname) {
		map.put(name, cname);
		sorter.put(cname, count);
		count = count + 1;
	}
	
	
	public static class Sorter implements Comparator<String> {

		public int compare(String a, String b) {
			Integer ai = sorter.get(a);
			Integer bi = sorter.get(b);
			
			if (ai!=null && bi!=null) {
				return ai.compareTo(bi);
			} else {
				return a.compareTo(b);
			}
		}
	}
	
}
