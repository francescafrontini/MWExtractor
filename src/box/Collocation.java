package box;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import statistical.StatisticCalc;
import utilities.Utility;

/**
 * This class defines the structure for collocation extraction. 
 * 
 * The class stores information of two items within the window size as set in the properties:
 * lemma,POS,token, frequency of the collocation, association measures. 

 * @author Valeria Quochi @author Francesca Frontini @author Francesco Rubino
 * Istituto di linguistica Computazionale "A. Zampolli" - CNR Pisa - Italy
 * 
 */


public class Collocation {

	private String lemma_a = null;
	private String lemma_b = null;

	private String pos_a = null;
	private String pos_b = null;

	private String token_a = null;
	private String token_b = null;

	private long frequency;

	private double maximumlikelihood;
	private double loglikelihood;
	private double mutualinformation;
	private double relativefrequency;

	/**
	 * Pattern is the sequence of word tokens internal to collocations for distance >2.
	 */
	private Hashtable<String, Long> pattern = null;
	/**
	 * Multiword is the full sequence of word tokens forming multiword candidates.
	 */
	private List<Multiword> multiwords = null;

	/**
	 * A Collocation is defined by a pair of lemmas
	 * @param lemma_a The lemma of the first item of the collocation
	 * @param lemma_b The lemma of the second item of the collocation
	 */
	public Collocation(String lemma_a, String lemma_b) {
		super();
		this.lemma_a = lemma_a;
		this.lemma_b = lemma_b;
		this.frequency = (long) 1;
		this.maximumlikelihood = 0;
		this.loglikelihood = 0;
		this.mutualinformation = 0;
		this.relativefrequency = 0;
	}

	public void addFrequency() {
		this.frequency++;
	}

	public void addMultiword(Multiword m) {
		if (this.multiwords == null)
			this.multiwords = new ArrayList<Multiword>();
		this.multiwords.add(m);
	}
	
	public void addPattern(String current_pattern) {
		if (this.pattern == null)
			this.pattern = new Hashtable<String, Long>();
		if (this.pattern.containsKey(current_pattern)) {
			this.pattern.put(current_pattern, this.pattern.get(current_pattern) + 1);
		} else {
			this.pattern.put(current_pattern, (long) 1);
		}
	}

	public String patternToR() {
		// transforms patterns in R vectors for statistics
		String r_vector = "";

		Enumeration<String> ks_pat = this.pattern.keys();
		while (ks_pat.hasMoreElements()) {
			String k_pat = ks_pat.nextElement();
			long f_pat = this.pattern.get(k_pat);

			r_vector = r_vector + f_pat;
			if (ks_pat.hasMoreElements())
				r_vector = r_vector + ",";
		}

		return r_vector;
	}


	/**
	 * This implements the simplest/baseline filter for MWEs extraction. For each collocation 
	 * (lemma) pair, selects as the candidate MWEs the most frequent pattern 
	 * (=the most frequent token frequency)
	 * 
	 * @return  The most frequent pattern (=MWEs) for a given collocation pair. 
	 * 
	 */
	public String getPatternMostFrequent() {
		// select the most frequent pattern = Multiword form. 

		String max_pattern = "";
		long max_frequency = 0;
		Enumeration<String> ks_pat = this.pattern.keys();
		while (ks_pat.hasMoreElements()) {
			String k_pat = ks_pat.nextElement();
			long f_pat = this.pattern.get(k_pat);
			if (f_pat > max_frequency) {
				max_pattern = k_pat;
				max_frequency = f_pat;
			}
		}
		return max_pattern;
	}
	
	
	/**
	 * @return The frequency value of the most frequent pattern (=MWEs) for a given collocation pair. 
	 * 
	 */
	public long getFrequencyPatternMostFrequent() {
		// select the highest pattern freq

		long max_frequency = 0;
		Enumeration<String> ks_pat = this.pattern.keys();
		while (ks_pat.hasMoreElements()) {
			String k_pat = ks_pat.nextElement();
			long f_pat = this.pattern.get(k_pat);
			if (f_pat > max_frequency) {
				max_frequency = f_pat;
			}
		}
		return max_frequency;
	}
	

	

	/**
	 * Selects and stores pattern with frequency above the mean
	 * @return The list of patterns (token sequences).  
	 */
	public String patternsToString() {
		StringBuffer sb = new StringBuffer();
		double mean = StatisticCalc.mean(pattern);

		Enumeration<String> ks_pat = this.pattern.keys();
		while (ks_pat.hasMoreElements()) {
			String k_pat = ks_pat.nextElement();
			long f_pat = this.pattern.get(k_pat);
			if (f_pat > mean) {
				String token_pattern = Utility.patternToString(k_pat);
				sb.append(token_pattern + "(" + f_pat + ")");
				if (ks_pat.hasMoreElements())
					sb.append(",");
			}
		}
		return sb.toString();
	}

	/**
	 * Defines the data structure for the retrieved collocations. For each given collocation 
	 * the following data is stored: collocation lemma pair, collocation frequency, lemmas frequency, 
	 * collocation sample size, collocation relative frequency, MI, LL, MLE.
	 * 
	 * @param freq_a Frequency of the first lemma
	 * @param freq_b Frequency of the second lemma
	 * @param num_tokens  Total numebr of collocations (lemma pairs)
	 * @return A string with all this information concatenated.
	 */
	public String collocationToString(long freq_a, long freq_b, long num_tokens) {
		StringBuilder builder = new StringBuilder();
		builder.append(lemma_a);
		builder.append("\t");
		builder.append(lemma_b);
		builder.append("\t");
		builder.append(frequency);
		builder.append("\t");
		builder.append(freq_a);
		builder.append("\t");
		builder.append(freq_b);
		builder.append("\t");
		builder.append(num_tokens);
		builder.append("\t");
		builder.append(relativefrequency);
		builder.append("\t" + mutualinformation);
		builder.append("\t" + loglikelihood);
		builder.append("\t" + maximumlikelihood);
		return builder.toString();
	}

	public String getLemma_a() {
		return lemma_a;
	}

	public void setLemma_a(String lemma_a) {
		this.lemma_a = lemma_a;
	}

	public String getLemma_b() {
		return lemma_b;
	}

	public void setLemma_b(String lemma_b) {
		this.lemma_b = lemma_b;
	}

	public String getPos_a() {
		return pos_a;
	}

	public void setPos_a(String pos_a) {
		this.pos_a = pos_a;
	}

	public String getPos_b() {
		return pos_b;
	}

	public void setPos_b(String pos_b) {
		this.pos_b = pos_b;
	}

	public String getToken_a() {
		return token_a;
	}

	public void setToken_a(String token_a) {
		this.token_a = token_a;
	}

	public String getToken_b() {
		return token_b;
	}

	public void setToken_b(String token_b) {
		this.token_b = token_b;
	}

	public Hashtable<String, Long> getPattern() {
		return pattern;
	}

	public void setPattern(Hashtable<String, Long> pattern) {
		this.pattern = pattern;
	}

	public long getFrequency() {
		return frequency;
	}

	public void setFrequency(long frequency) {
		this.frequency = frequency;
	}

	public double getMaximumlikelihood() {
		return maximumlikelihood;
	}

	public void setMaximumlikelihood(double maximumlikelihood) {
		this.maximumlikelihood = maximumlikelihood;
	}

	public double getLoglikelihood() {
		return loglikelihood;
	}

	public void setLoglikelihood(double loglikelihood) {
		this.loglikelihood = loglikelihood;
	}

	public double getMutualinformation() {
		return mutualinformation;
	}

	public void setMutualinformation(double mutualinformation) {
		this.mutualinformation = mutualinformation;
	}

	public void setRelativefrequency(double relativefrequency) {
		this.relativefrequency = relativefrequency;
	}

	public double getRelativefrequency() {
		return relativefrequency;
	}

	public List<Multiword> getMultiwords() {
		return multiwords;
	}

	public void setMultiwords(List<Multiword> multiwords) {
		this.multiwords = multiwords;
	}
	
	
}
