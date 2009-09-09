package net.sf.okapi.lib.search.lucene.stemmers; // DWH 5-18-05 added org.ldschurch.trl.

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import javax.swing.JFrame;

public class EnglishListStemmer {
	private HashMap hmStemList;
	private int iNited = 0;
	private String fileName;
	private JFrame topFrame;
	private PorterRockwellStemmer prs; // DWH 3-14-06

	public EnglishListStemmer(String filename) {
		hmStemList = new HashMap();
		this.fileName = filename;
		initPRS(""); // DWH 6-16-06, 12-12-06
		if (filename == null || filename.equals("")) // DWH 12-12-06 added ""
			iNited = 2; // can't be initialized
	}

	public EnglishListStemmer(String filename, String sSourceLocaleName) {
		hmStemList = new HashMap();
		this.fileName = filename;
		initPRS(sSourceLocaleName); // DWH 6-16-06, 12-12-06 sSourceLanguage
		if (filename == null || filename.equals("")) // DWH 12-12-06 added ""
			iNited = 2; // can't be initialized
	}

	public EnglishListStemmer(String filename, JFrame jf) {
		hmStemList = new HashMap();
		this.fileName = filename;
		initPRS(""); // DWH 6-16-06 12-12-06
		if (filename == null || filename.equals("")) // DWH 12-12-06 added ""
			iNited = 2; // can't be initialized
		topFrame = jf;
	}

	public static void main(String args[]) {
		EnglishListStemmer els = new EnglishListStemmer(args[0]);
	}

	public String initEnglishList() {
		File theFile = new File(fileName);
		BufferedReader in = null;
		int i;
		String s, ss, sss, result;
		if (iNited == 1)
			return (null); // already initialized
		if (!theFile.exists()) {
			return ("Source List for Stemming " + fileName + " could not be Found");
		} else if (!theFile.canRead()) {
			return ("Source List for Stemming " + fileName + " cannot be Read");
		}
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(
					theFile), "UTF-8"));
			// DWH 12-18-06 assume the file is encoded in UTF-8
			// in = new BufferedReader(new FileReader(theFile)); DWH 12-18-06
		} catch (IOException e) {
			return ("Source List for Stemming " + fileName + " cannot be Buffered");
		}
		try {
			while (true) {
				s = in.readLine();
				if (s == null)
					break;
				if (s.equals(""))
					continue;
				i = s.indexOf('	'); // tab
				if (i > -1 && s.length() > i + 1) {
					ss = s.substring(0, i);
					sss = s.substring(i + 1);
					hmStemList.put(ss, sss);
				}
			}
		} catch (EOFException e) {
		} catch (IOException e) {
			return ("Can't read Source List for Stemming " + fileName);
		} finally {
			try {
				in.close();
			} catch (IOException e2) {
				return ("Can't process Source List for Stemming " + fileName);
			}
		}
		iNited = 1; // initialized
		return null;
	}

	public String getBaseForm(String s) {
		String ss = null;
		if (iNited == 0) // not initialized yet; never loads file unless it is
							// needed
		{
			ss = initEnglishList();
			if (ss == null)
				iNited = 1;
			else {
				iNited = 2; // can't be initialized, will default to Porter
							// Stemmer, SHOULD show error message
				// if (topFrame==null)
				// JOptionPane.showMessageDialog(topFrame,ss+": Source stemming will default to Porter Stemmer","Java Workbench Engine",JOptionPane.WARNING_MESSAGE);
				ss = null;
			}
		}
		ss = (String) hmStemList.get(s);
		if (ss == null || ss.equals("")) // DWH 6-16-06 used to return ""
		{
			ss = getPRSStem(s);
			if (ss == null || ss.equals(""))
				return (s);
			else
				return (ss);
		} else
			return (ss);
	}

	public boolean initPRS(String sSourceLanguage) {
		if (sSourceLanguage.equals("EN-US")) // DWH 12-12-06 added
												// sSourceLanguage if
			prs = new PorterRockwellStemmer();
		else
			prs = null;
		return true;
	}

	public String getPRSStem(String s) {
		if (prs == null)
			return (s);
		else
			return (prs.stem(s));
	}

	public String getFileName() {
		return (fileName);
	}
}
