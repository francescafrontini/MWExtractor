package box;

import properties.ExtractorProperties;
import utilities.Utility;

/**
 * This class defines the notion of token and the associated data structure.
 * For each token, the class stores info about its form, lemma, simple POS tag, complex POS tag, morphology, 
 * hrel and relation
 * The token is read from the input, which is the CoNLL 10 column format (from dependency parsing tasks).
 * @author Valeria Quochi @author Francesca Frontini @author Francesco Rubino
 * Istituto di linguistica Computazionale "A. Zampolli" - CNR Pisa - Italy
 * 
 */

public class Token {
	private long iddoc;
	private int idx;
	private String token;
	private String lemma;
	private String post;
	private String posl;
	private String morpho;
	private int hrel;
	private String relation;
	
	/**
	 * @param idx Sentence index, or id
	 * @param token token string  
	 * @param lemma lemma string
	 * @param post Part of speech tag of the token
	 * @param posl Part of speech tag of the lemma
	 * @param morpho Morphological features of the token
	 * @param hrel pointer to the head of the relation/dependency
	 * @param relation label of the grammatical relation
	 */
	public Token(int idx, String token, String lemma, String post, String posl, String morpho, int hrel, String relation) {
		super();
		this.idx = idx;
		this.token = token;
		this.lemma = lemma.toUpperCase();
		this.post = post;
		this.posl = posl;
		this.morpho = morpho;
		this.hrel = hrel;
		this.relation = relation;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append(idx);
		sb.append("\t");
		sb.append(token);
		sb.append("\t");
		sb.append(lemma);
		sb.append("\t");
		sb.append(post);
		sb.append("\t");
		sb.append(posl);
		sb.append("\t");
		sb.append(morpho);
		sb.append("\t");
		sb.append(hrel);
		sb.append("\t");
		sb.append(relation);
		
		return sb.toString();
	}
	
	public boolean isChild(int parent){
		if(hrel == parent)
			return true;
		return false;
	}
	
	/**
	 * Finds the sentence Root element in a dependency structure
	 * 
	 */
	public boolean isRoot(){
		if(relation.equals("ROOT"))
			return true;
		return false;
	}
	
	/**
	 * Stops search/extract if token is punctuation mark. 
	 * 
	 * @param pos First char of POS tag 
	 * @param startWith Boolean value
	 * @return Boolean If the token is a punctuation mark it stops the search and re-start the window size 
	 */
	public boolean isPos(String pos, boolean startWith){
		String[] punct = { ".", "-", "--", "\"", ":", "?", "!", ";", ",", "#" };
		if(startWith){
			if(posl.startsWith(pos)){
				for (String p : punct)
					if (lemma.contains(p))
						return false;
				return true;
			}
		}else{
			if(posl.equals(pos)){
				for (String p : punct)
					if (lemma.contains(p))
						return false;
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Check if the item is an auxiliary and ignored it.
	 * 
	 * @return Boolean
	 */
	public boolean isAux(){
		if(relation.equals("aux"))
			return true;
		return false;
	}
	
	/**
	 * Identifies the stopwords defined in the properties
	 * 
	 */
	public boolean isStopWords(){
		String[] stop_words = ExtractorProperties.getArrayProperty(ExtractorProperties.__STOP_WORDS__, Utility.__SPLITTER_X_STOPWORDS__);
		for (String sw : stop_words){
			//System.out.print(sw + "_");
			if (lemma.contains(sw))
				return true;
		}
		//System.out.println();
		return false;
	}
	
	/**
	 * Identifies punctuation 
	 */
	public boolean isPoint(){
		String[] punct = { ".", "-", "--", "\"", ":", "?", "!", ";", ",", "#" };
		for (String p : punct)
			if (lemma.contains(p))
				return true;
		return false;
	}
	////////////////////////////////////////////////
	// GETTER

	public long getIddoc() {
		return iddoc;
	}

	public int getIdx() {
		return idx;
	}

	public String getToken() {
		return token;
	}

	public String getLemma() {
		return lemma;
	}

	public String getPost() {
		return post;
	}

	public String getPosl() {
		return posl;
	}

	public String getMorpho() {
		return morpho;
	}

	public int getHrel() {
		return hrel;
	}

	public String getRelation() {
		return relation;
	}
}
