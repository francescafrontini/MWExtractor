package utilities;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import properties.ExtractorProperties;
import box.Token;

/**
 * Contains a series of utility functions. 
 * @author Valeria Quochi @author Francesca Frontini @author Francesco Rubino
 * Istituto di linguistica Computazionale "A. Zampolli" - CNR Pisa - Italy
 * 
 */
public class Utility {

	/**
	 * SPLITTER.
	 * Defines how info is separated in (printed) data structures 
	 */
	
	public final static String __SPLITTER__ = "\t";
	/**
	 * Morphological person, gender and number features separated by | in CoNLL/TANL input format 
	 */
	public final static String __SPLITTER_X_NUMORGEN__ = "|";
	public final static String __SPLITTER_X_NUMORGEN_SPLIT__ = "\\|";
	/**
	 * Stopwords in stopword file are separated by space
	 */
	public final static String __SPLITTER_X_STOPWORDS__ = " ";
	/**
	 * Information about multiwords in the internal data structure are separated by <> 
	 */
	public final static String __SPLITTER_X_MULTIWORDS__ = "<>";

	// for patterns
	
	/**
	 * Information about patterns in the internal data structure are separated by <xyz> 
	 */
	public final static String __S_X_PATTERN__ = "<xyz>";
	/**
	 * (POS and Freq)Information of components of the multiwords are separated by <123>
	 */
	public final static String __S_X_PART__ = "<123>";

	/**
	 * Empty field in input text is represented with _
	 */
	public final static String __NOT_INS__ = "_";

	/**
	 * Sentence Index 
	 */
	public final static int __IDLINE__ = 0;

	/**
	 * word della frase
	 * WHAT'S THIS, the word index?
	 */
	public final static int __WORD__ = 1;

	/**
	 * Key = Lemma
	 */
	public final static int __LEMMA__ = 2;

	/**
	 * POS of word token
	 */
	public final static int __WPOS__ = 3;

	/**
	 * POS of lemma
	 */
	public final static int __POS__ = 4;

	/**
	 * Morphological features of the token
	 */
	public final static int __MORPHO__ = 5;

	/**
	 * Pointer.
	 */
	public final static int __POINTER__ = 6;

	/**
	 * con, prep, comp, det...
	 */
	public final static int __RELATION__ = 7;

	public final static String __NUMBER__ = "num";
	public final static String __SINGULAR__ = "s";
	public final static String __PLURAL__ = "p";
	public final static String __EQUAL__ = "=";

	public static final String __MOD__ = "mod";
	public static final String __SUB__ = "subj";
	public static final String __OBJ__ = "obj";
	public static final String __DET__ = "det";

	/**
	 * Extracts the POS sequence instantiated by the multiword
	 * 
	 * @param mypattern 
	 * @return The pos sequence (or POS pattern)
	 */
	public static String patternToPosString(String mypattern) {
		String pos_pattern = null;
		String[] k_pat_splitted = mypattern.split(Utility.__S_X_PATTERN__);
		// System.out.println(pmf_splitted[0] + " " + pmf_splitted[1]);
		for (String s : k_pat_splitted) {
			// System.out.println(s);
			String[] s_splitted = s.split(Utility.__S_X_PART__);
			if (pos_pattern != null) {
				pos_pattern = pos_pattern + "+" + s_splitted[1];
			} else {
				pos_pattern = s_splitted[1];
			}
		}
		return pos_pattern;
	}
	
	/**
	 * @param mypattern 
	 * @return The sequence of tokens 
	 */
	public static String patternToString(String mypattern) {
		String token_pattern = null;
		String pos_pattern = null;
		String[] k_pat_splitted = mypattern.split(Utility.__S_X_PATTERN__);
		// System.out.println(pmf_splitted[0] + " " + pmf_splitted[1]);
		for (String s : k_pat_splitted) {
			// System.out.println(s);
			String[] s_splitted = s.split(Utility.__S_X_PART__);
			if (pos_pattern != null) {
				pos_pattern = pos_pattern + "+" + s_splitted[1];
				token_pattern = token_pattern + " " + s_splitted[0];
			} else {
				pos_pattern = s_splitted[1];
				token_pattern = s_splitted[0];
			}
		}
		return token_pattern;
	}

	/**
	 * @param filename  
	 * @return Multiword list
	 */
	public static List<String> getWordListByFile(String filename) {
		List<String> mwlist = new ArrayList<String>();
		if (filename == null || filename.isEmpty() || filename.equals("-"))
			return mwlist;

		BufferedReader in = null;
		String line = null;
		try {
			in = new BufferedReader(new FileReader(filename));
			while ((line = in.readLine()) != null) {
				mwlist.add(line.toUpperCase());
			}
			in.close();
			line = null;
			in = null;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mwlist;
	}

	public static String getValueFromXML(String line, String key) {
		int idx_emo = line.indexOf(key);
		StringBuffer sb = new StringBuffer();
		boolean stampa = false;
		for (int i = idx_emo; i < line.length(); i++) {
			if (stampa) {
				if (line.charAt(i) != '\"')
					sb.append(line.charAt(i));
			}
			if (line.charAt(i) == '\"') {
				stampa = !stampa;
			}
			if (line.charAt(i) == ' ') {
				break;
			}
		}
		// System.out.println(sb.toString());
		return sb.toString();
	}

	public static String getCurrentDate() {
		GregorianCalendar gc = new GregorianCalendar();
		return gc.get(Calendar.YEAR) + "/" + gc.get(Calendar.MONTH) + "/" + gc.get(Calendar.DAY_OF_MONTH) + " " + gc.get(Calendar.HOUR_OF_DAY) + ":" + gc.get(Calendar.MINUTE) + ":"
				+ gc.get(Calendar.SECOND);
	}

	/**
	 * Retrieves the tokens intervening between the 2 collocation items.
	 * 
	 * 
	 * @param t1 First token of collocation
	 * @param t2 Second token of collocation
	 * @param sentences 
	 * @return String representing the full token pattern: token-pos-lemma of all components of the pattern
	 */
	public static String getPattern(Token t1, Token t2, List<Token> sentences) {
		int start = 0;
		int end = 0;
		if (t1.getIdx() < t2.getIdx()) {
			start = t1.getIdx(); // NB. the two keys are NOT counted in the pattern.  
			end = t2.getIdx() - 1;
		} else {
			start = t2.getIdx(); // NB. the two keys are NOT counted in the pattern.  
			end = t1.getIdx() - 1;
		}
		if (end - start == 0)
			return t1.getToken() + __S_X_PART__ + t1.getPosl() + __S_X_PART__ + t1.getLemma() + __S_X_PATTERN__ + t2.getToken() + __S_X_PART__ + t2.getPosl() + __S_X_PART__ + t2.getLemma();

		StringBuffer sb = new StringBuffer();
		sb.append(t1.getToken() + __S_X_PART__ + t1.getPosl() + __S_X_PART__ + t1.getLemma() + __S_X_PATTERN__);
		for (int i = start; i < end; i++) {
			sb.append(sentences.get(i).getToken() + __S_X_PART__ + sentences.get(i).getPosl() + __S_X_PART__ + sentences.get(i).getLemma());
			if (i != end - 1)
				sb.append(__S_X_PATTERN__);
		}
		sb.append(__S_X_PATTERN__ + t2.getToken() + __S_X_PART__ + t2.getPosl() + __S_X_PART__ + t2.getLemma());
		return sb.toString();
	}

}
