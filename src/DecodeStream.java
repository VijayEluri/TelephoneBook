// CatfoOD 2008.2.7  �����һ :)

import javax.sound.sampled.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * ����������
 * ʹ��ʱ��DecodeStream�滻InputStream
 */
public class DecodeStream extends InputStream{
	private String bookname;
	private long fileLength = 0;
	private boolean closed = false;
	
	public DecodeStream(File f, String password) throws IOException {
		fi = new FileInputStream(f);
		fileLength = f.length();
		pw = password.getBytes();
		init();
		fileLength = (fileLength*8/9) + (fileLength%9) - 1;
	}
	public DecodeStream(String s, String password) throws IOException {
		this(new File(s), password);
	}

	public String getBookName() {
		return bookname;
	}
	
	public static String getBookName(File f) throws IOException {
		String bname = null;
		FileInputStream in = new FileInputStream(f);

		byte[] scr = new byte [SECERN.length];
		in.read(scr, 0, SECERN.length);
		if ( !MessageDigest.isEqual(scr,SECERN) ) {
			throw new IOException("������Ч�ĵ绰���ļ�.\n�ļ������Ѿ��𻵻�ʧ");
		}

		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IOException("MD5 Error.\n"+e);
		}
		byte[] md5 = md.digest("jym".getBytes());
		byte[] fmd = new byte[md5.length];
		in.read(fmd, 0, md5.length);
		
		int i = in.read();
		byte[] _n = new byte[i];
		in.read(_n,0,i);
		bname = new String(_n);
		in.close();
		return bname;
	}
	
	private int rlength = 0;
	private int nowRead = 0;
	public int read() throws IOException {
		if (closed) throw new IOException("InputStream is closed.");
		//����reset()�ӻ����ȡ�ֽ�
		if( reset && marks!=null ) {
			if(markcont<marks.length) {
				return marks[markcont++];
			}else{
				reset=false;
				marks=null;
			}
		}
		//���ļ���ȡ�ֽ�
		if (nowRead>=rlength) {
			rlength = read_unencode();
			nowRead = 0;
		}
		if (rlength==-1) {
			return rlength;
		}else{
			if( marks!=null ) {
				if (markcont<marks.length) {
					marks[markcont++] = w[nowRead];
				}else{
					marks=null;
				}
			}
			return w[nowRead++];
		}
	}
	
	public void close() throws IOException {
		fi.close();
		closed = true;
	}
	
	public boolean markSupported() {
		return true;
	}
	
	private boolean reset = false; 
	public void reset() throws IOException {
		if ((marks==null)||(markcont>=marks.length+1)) {
			// ������reset()
			throw new IOException("reset()error."+marks.length+" "+markcont);
		} else {
			// ����reset()
			reset=true;
			markcont=0;
		}
	}

	private int markcont = 0;
	private int[] marks = null;
	public void mark(int readlimit){
		if( marks!=null && (readlimit<=marks.length) ) {
			return;
		}
		marks = null;
		marks = new int[readlimit];
		markcont = 0;
	}
	
	/**
	 * �ļ��ĳ���
	 * @return long
	 */
	public long length() {
		return this.fileLength;
	}
	
	//----------------------------------------------------------------------------------------
	
	private FileInputStream fi;
	private byte[] pw;
	private static byte[] SECERN = 
		{(byte)0xef,(byte)0xaa,0x3f,(byte)0x86,0x1a,(byte)0xff,(byte)0xff,0x00};
	
	private void init() throws IOException {
		//�ж��Ƿ�Ϊ���ܵ��ļ�
		byte[] scr = new byte [SECERN.length];
		fileLength -= fi.read(scr, 0, SECERN.length);
		if ( !MessageDigest.isEqual(scr,SECERN) ) {
			throw new IOException("������Ч�ĵ绰���ļ�.\n�ļ������Ѿ��𻵻�ʧ");
		}
		
		//�ж���������
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IOException("MD5 Error.\n"+e);
		}
		byte[] md5 = md.digest(pw);
		byte[] fmd = new byte[md5.length];
		fileLength -= fi.read(fmd, 0, md5.length);
		
		
		//ȷ��ԭʼ�ļ���
		int i = fi.read();
		byte[] _n = new byte[i];
		fileLength -= fi.read(_n,0,i);
		bookname = new String(_n);
		
		if ( !MessageDigest.isEqual(md5, fmd) ) {
			throw new IOException("�������.");
		}
		
		plen	= pw.length;
	}
	
	private final int UNIT = 8;
	private int[] w 	= new int[UNIT]; // ���ǽ���Ľ��
	private byte[] data = new byte[UNIT+1];
	private int readcon = 0;
	private int   _pw 	= 0;
	private int   plen	= 0;
	private int   bit	= 0x01;	
	private int   il	= 0;	
	private int read_unencode() throws IOException {
		//������ܵ��ļ�
		readcon = 0;
		bit	= 0x01;		
		readcon = fi.read(data, 0, UNIT+1);
		if (readcon!=-1) {
			for (int n=0; n<readcon-1; n++) {
				if ((data[0] & bit)!=0) {
					il =  (0x0100 | toInt(data[n+1])) - toInt(pw[_pw]);
				}else{
					il =   toInt( data[n+1] ) - toInt( pw[_pw] );
				}
				w[n] = il;
				bit = bit<<1;
				if (++_pw>=plen) _pw = 0;
			}
		}
		//�������
		//����Ч���ݷ���-1
		return readcon>1? readcon-1: -1;
	}
	
	/**
	 * byte �з���������<p>
	 *
	 *	00 = 00<p>
	 *	01 = 01<p>
	 *	7f = 127<p>
	 *	80 = -128	128<p>
	 *	81 = -127	129<p>
	 *	ff = -1		255<p>
	 *
	 * @param b
	 * @return r
	 * @author CatfoOD
	 */
	private int toInt(byte b){
		int r = 0;
		if (b<0) r = 256 + b;
		else r = (int)b;
		return r;
	}
}
