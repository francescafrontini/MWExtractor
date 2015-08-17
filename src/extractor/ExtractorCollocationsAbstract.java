package extractor;

/**
 * @author Valeria Quochi @author Francesca Frontini @author Francesco Rubino
 * Istituto di linguistica Computazionale "A. Zampolli" - CNR Pisa - Italy
 * 
 */

import java.util.Hashtable;
import java.util.List;

import box.Collocation;
import box.Token;

public abstract class ExtractorCollocationsAbstract {
	
	protected Hashtable<String, Collocation> collocations = null;
	protected Hashtable<String, Long> single_freq = null;
	protected long num_tokens = 0; //the total number of extracted collocation pairs
	
	/**
	 * Extract collocation pairs
	 */
	public ExtractorCollocationsAbstract() {
		this.collocations = new Hashtable<String, Collocation>();
		this.single_freq = new Hashtable<String, Long>();
		this.num_tokens = 0;
	}

	/**
	 * Extract multiwords from collocations pattern sets 
	 * 
	 * @param multiwords Multiwords are the full sequence(s) of tokens of collocation pair
	 * @param single_freq Frequency of the full token pattern
	 * @param num_tokens Total number of collocation pairs or total number of multiwords
	 */
	public ExtractorCollocationsAbstract(Hashtable<String, Collocation> multiwords, Hashtable<String, Long> single_freq, long num_tokens) {
		super();
		this.collocations = multiwords;
		this.single_freq = single_freq;
		this.num_tokens = num_tokens;
	}
	
	public abstract void writeResult(String filename);
	public abstract void extractMWfromPosTag(List<Token> sentence);
	protected abstract void insert(Token t1, Token t2, String pattern, int type);
	protected abstract void putInSingleHash(Token t1);
	protected abstract void putInHashPair(Token t1, Token t2, String pattern, int type);
	
	
	
	
	

	
}
