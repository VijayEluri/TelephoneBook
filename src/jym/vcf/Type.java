package jym.vcf;

import java.util.HashMap;
import java.util.Map;

public class Type {
	
	private final static Map<String, String> map = new HashMap<String, String>();
	
	static {
		set("N", 						"����");
		set("FN",						"����");
		set("TEL",						"�绰");
		set("X-MSN",					"MSN");
		set("X-AIM",					"QQ");
		set("NOTE",						"��ע");
		set("URL",						"��ַ");
		set("TITLE",					"����");
		set("ORG",						"��˾");
		set("ADR",						"��ַ");
		set("EMAIL",					"����");
		set("X-ANDROID-CUSTOM",			"�Զ���");
		set("X-PHONETIC-LAST-NAME",		"last name");
		set("X-PHONETIC-MIDDLE-NAME",	"middle name");
		set("X-PHONETIC-FIRST-NAME",	"first name");
		

		set("HOME",		"��ͥ");
		set("WORK",		"����");
		set("VOICE",	"����");
		set("CELL",		"");
	}
	
	
	public static String get(String name) {
		return map.get(name);
	}
	
	private static void set(String name, String cname) {
		map.put(name, cname);
	}
}
