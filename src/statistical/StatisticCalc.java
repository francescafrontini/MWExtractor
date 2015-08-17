package statistical;

import java.util.Enumeration;
import java.util.Hashtable;

import box.Collocation;

/**
 * Defines the statistical functions for the measures and methods used in the tool
 * @author Valeria Quochi @author Francesca Frontini @author Francesco Rubino
 * Istituto di linguistica Computazionale "A. Zampolli" - CNR Pisa - Italy
 * 
 */

public class StatisticCalc {

	private static double logaritmo2(double value) {
		return (double) (Math.log(value) / Math.log(2));
	}
	private static double logaritmo10(double value) {
		return (double) (Math.log(value) / Math.log(10));
	}
	private static double logaritmoLN(double value) {
		return (double) Math.log(value);
	}
	
	/**
	 * @param freq_ab  Absolute frequency of collocations (lemma pairs)
	 * @param num_tokens Dataset size (total number of collocations)
	 * @return Returns relative frequency of collocation pairs
	 * 
	 */
	public static double relativeFrequency(long freq_ab, long num_tokens){
		return (double) freq_ab / (double) num_tokens;
	}

	
	/**
	 * Calculate Mutual Information for the collocations
	 * @param freq_coppia Absolute frequency of collocations (lemma pairs)
	 * @param freq_a  Absolute frequency of first lemma of collocation
	 * @param freq_b  Absolute frequency of second lemma of collocation
	 * @param num_tokens  Dataset size (total number of collocations)
	 * @return Returns the MI value of the collocation
	 */
	public static double mutualInformation(long freq_coppia, long freq_a, long freq_b, long num_tokens) {
		return logaritmo10((double) ((double) freq_coppia / (double) (num_tokens - 1)) / (((double) freq_a / (double) num_tokens) * ((double) freq_b / (double) num_tokens)));
	}
	
	public static double maximumLikelihoodEstimate(long freq_coppia, long freq_verb) {
		return (double) ((double) freq_coppia / (double) freq_verb);
	//This seems to be not in use in this software. 
		
	}
	
	
	/**
	 * Optimised.
	 * Calculate loglikelihood for collocations
	 * @param key Lemma pair of collocation
	 * @param freq_a_b Frequency of the collocation lemma
	 * @param freq_a Frequency of the first lemma of collocation
	 * @param freq_b Frequency of the second lemma of collocation
	 * @param N  Dataset size (total number of collocations, lemma pairs)
	 * @return Returns the LL value of the collocation
	 */
	public static double loglikelihood(String key, long freq_a_b, long freq_a, long freq_b, long N){
		// Calculate contingency matrix 	
		// Ottimizzata. Vedi appunti.
		// data
		long freq_A_notB = (freq_a > freq_a_b)?(freq_a - freq_a_b):(freq_a_b - freq_a);
		long freq_notA_B = (freq_b > freq_a_b)?(freq_b - freq_a_b):(freq_a_b - freq_b);
		
		long C1 = freq_a_b + freq_notA_B;
		long C2 = N - C1;
		long R1 = freq_a_b + freq_A_notB;
		long R2 = N - R1;
		
		long freq_notA_notB = R2 - freq_notA_B;
		
		
		// expected values
		double E11 = (double) ((double) R1 * (double) C1) / ((double) N);
		double E12 = (double) ((double) R1 * (double) C2) / ((double) N);
		double E21 = (double) ((double) R2 * (double) C1) / ((double) N);
		double E22 = (double) ((double) R2 * (double) C2) / ((double) N);
		
		/*if(key.equals("MILIARDO_LIRA")){
		System.out.println(freq_a_b + " " + freq_A_notB + " " + freq_notA_B + " " + freq_notA_notB);
		System.out.println(C1 + " " + C2 + " " + R1 + " " + R2);
		System.out.println(E11 + " " + E12 + " " + E21 + " " + E22);
		System.out.println(((freq_a_b > 0) ? (double) freq_a_b * logaritmoLN((double)freq_a_b / (double) E11) : 0));
		System.out.println(((freq_A_notB > 0) ? (double) freq_A_notB * logaritmoLN((double)((double)freq_A_notB / (double) E12)) : 0));
		System.out.println(((freq_notA_B > 0) ? (double) freq_notA_B * logaritmoLN((double)freq_notA_B / (double) E21) : 0));
		System.out.println(((freq_notA_notB > 0) ? (double) freq_notA_notB * logaritmoLN((double)freq_notA_notB /(double)  E22) : 0));
		
		}*/
		double loglikelihood = 2*( 
								((freq_a_b > 0) ? (double) freq_a_b * logaritmoLN((double)freq_a_b / (double) E11) : 0) + 
								((freq_A_notB > 0) ? (double) freq_A_notB * logaritmoLN((double)freq_A_notB / (double) E12) : 0) +
								((freq_notA_B > 0) ? (double) freq_notA_B * logaritmoLN((double)freq_notA_B / (double) E21) : 0) + 
								((freq_notA_notB > 0) ? (double) freq_notA_notB * logaritmoLN((double)freq_notA_notB /(double)  E22) : 0)
							);
		
		/*if(key.equals("MILIARDO_LIRA"))
			System.out.println(key + " " + loglikelihood);*/
		return loglikelihood;
	}
	
	/**
	 * @param patterns 
	 * @param mean  mean frequency value of patterns
	 * @return Returns standard deviation for pattern frequency distribution of collocation pairs 
	 */
	public static double standardDeviation(Hashtable<String, Long> patterns, double mean) {
		// calculate standard deviation 
		double current_dev = 0;
		Enumeration<String> pkeys = patterns.keys();
		while (pkeys.hasMoreElements()) {
			String pkey = pkeys.nextElement();
			double fpattern = (double) patterns.get(pkey);
			current_dev = current_dev + (double) ( (fpattern - mean) * (fpattern - mean));
		}
		return (double) Math.sqrt((double) ((double)current_dev / (double)patterns.size()));
	}
	
	public static double mean(Hashtable<String, Long> patterns) {
		// calculate mean frequency of patterns within collocation pairs
		double sum = 0;
		Enumeration<String> pkeys = patterns.keys();
		while (pkeys.hasMoreElements()) {
			String pkey = pkeys.nextElement();
			double fpattern = (double) patterns.get(pkey);
			sum = sum + fpattern;
		}
		return (double) (sum / (double)patterns.size());
	}
	
}