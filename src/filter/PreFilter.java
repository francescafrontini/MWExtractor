package filter;

import box.Collocation;

/**
 * This class defines the possible prefiltering methods, i.e. the filter that applies 
 * to the extracted collocation pairs. Patterns are not considered here.
 * @author Valeria Quochi @author Francesca Frontini @author Francesco Rubino
 * Istituto di linguistica Computazionale "A. Zampolli" - CNR Pisa - Italy
 * 
 */

public class PreFilter {

	/**
	 * This implements the AverageF prefilter: it filters away all collocation pairs whose frequency
	 *  is below the average.
	 *  
	 * @param current The collocation (lemma) pair 
	 * @param freq_a The frequency of the first lemma
	 * @param freq_b The frequency of the second lemma
	 * @param averageFreq The value of the average frequency of the sample of collocation pairs 
	 * extracted from the corpus.
	 *  
	 * @return Boolean
	 */
	public static boolean isFilteredByAverageFrequency(Collocation current, Long freq_a, Long freq_b, Long averageFreq) {
		// long averageFreq = getAverageFrequency(num_multiwords, num_tokens);
		Long f = current.getFrequency();
		if (f <= averageFreq)
			return true;
		return false;
	}
	/**
	 * This implements the MaxF prefilter: if filters away all collocation pairs whose frequency
	 *  is below 1/10 of the maximum frequency.
	 *  
	 * @param current The collocation (lemma) pair 
	 * @param freq_a The frequency of the first lemma
	 * @param freq_b The frequency of the second lemma
	 * @param maxFreq The value of the maximum frequency within the sample of collocation pairs 
	 * extracted from the corpus.
	 * @return Boolean
	 */
	public static boolean isFilteredByMaxFrequency(Collocation current, Long freq_a, Long freq_b, Long maxFreq) {
		Long f = current.getFrequency();
		if (!(f >= maxFreq / 10))
			return true;
		return false;
	}
}
