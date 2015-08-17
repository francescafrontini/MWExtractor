package multiword;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import statistical.StatisticCalc;
import utilities.Utility;
import box.Collocation;
import box.Multiword;


/**
 * This class extracts multi-words from word patterns corresponding related to the filtered collocations.
 * @author Valeria Quochi @author Francesca Frontini @author Francesco Rubino
 * Istituto di linguistica Computazionale "A. Zampolli" - CNR Pisa - Italy
 * 
 */

public class MultiwordExtractor {

	public Hashtable<String, List<String>> unifyPatternsWithSameLemma(Hashtable<String, Long> patterns, Hashtable<String, Long> good_patterns) {
		Hashtable<String, List<String>> mapper = new Hashtable<String, List<String>>();
		List<String> same_pattern = new ArrayList<String>();
		List<String> equal2current = new ArrayList<String>();
		
		Enumeration<String> ks_pat = patterns.keys();
		while (ks_pat.hasMoreElements()) {
			String current = ks_pat.nextElement();
			Long current_freq = patterns.get(current);
			
			long global_freq = current_freq;
			String most_freq_pattern = current;
			
			
			equal2current.add(current);
			
			if(!same_pattern.contains(current)){
				same_pattern.add(current);
				String[] current_parts = current.split(Utility.__S_X_PATTERN__);
				
				Enumeration<String> sub_ks_pat = patterns.keys();
				while (sub_ks_pat.hasMoreElements()) {
					String sub_current = sub_ks_pat.nextElement();
					Long sub_current_freq = patterns.get(sub_current);
					
					
					String[] sub_current_parts = sub_current.split(Utility.__S_X_PATTERN__);
					
					if (!current.equals(sub_current) && current_parts.length == sub_current_parts.length) {
						boolean uguali = true;
						for (int j = 0; j < current_parts.length; j++) {
							String[] p1_infos = current_parts[j].split(Utility.__S_X_PART__);
							String[] p2_infos = sub_current_parts[j].split(Utility.__S_X_PART__);
							if (!p1_infos[2].equals(p2_infos[2]))
								uguali = false;
						}
						if(uguali){
							global_freq = global_freq + sub_current_freq;
							equal2current.add(sub_current);
							same_pattern.add(sub_current);
							if(sub_current_freq > current_freq){
								most_freq_pattern = sub_current;
							}
						}
					}
				}
				good_patterns.put(most_freq_pattern, global_freq);
				mapper.put(most_freq_pattern, equal2current);
				equal2current.clear();
			}
		}
		return mapper;
	}

	/**
	 * Selects word patterns related to collocations on statistical and/or length basis
	 * 
	 * 
	 * @param collocations Collocation (Lemma) pairs
	 */
	public void extractByPattern(Hashtable<String, Collocation> collocations){
		Enumeration<String> keys = collocations.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			Collocation collocation = collocations.get(key);

			Hashtable<String, Long> patterns = collocation.getPattern();
			if (patterns == null)
				return;
			
			Hashtable<String, Long> new_patterns = new Hashtable<String, Long>();
			Hashtable<String, List<String>> mapper = unifyPatternsWithSameLemma(patterns, new_patterns);
			
			// CASE 1 (BY STD DEV)
			double mean = StatisticCalc.mean(patterns);
			double std_dev = StatisticCalc.standardDeviation(patterns, mean);
			
			Enumeration<String> ks_pat = new_patterns.keys();
			while (ks_pat.hasMoreElements()) {
				String k_pat = ks_pat.nextElement();
				long f_pat = new_patterns.get(k_pat);
				if (f_pat >= mean + std_dev){
					Multiword m = new Multiword(k_pat, f_pat);
					if(mapper.containsKey(k_pat)){
						if(mapper.get(k_pat).size() > 1)
							m.setFlex(true);
					}
					collocation.addMultiword(m);
					//System.out.println(Utility.patternToString(k_pat, f_pat) + "(" + std_dev + ")");
				}
			}
			
			// CASE 2 (BY LENGTH)
			ks_pat = new_patterns.keys();
			while (ks_pat.hasMoreElements()) {
					String k_pat = ks_pat.nextElement();
					long f_pat = new_patterns.get(k_pat);
					String[] k_splitted = k_pat.split(Utility.__S_X_PATTERN__);

					// find patterns with given length and filter on frequency distribution by same lenght groups
					int len = k_splitted.length;
					long count = f_pat;
					Enumeration<String> ks_subpat = new_patterns.keys();
					while (ks_subpat.hasMoreElements()) {
						String k_subpat = ks_subpat.nextElement();
						long f_subpat = new_patterns.get(k_subpat);
						String[] k_subsplitted = k_subpat.split(Utility.__S_X_PATTERN__);
						if (k_subsplitted.length == len && !k_subpat.equals(k_pat)) {
							count = count + f_subpat;
						}
					}
					double freq_rel = (double) f_pat / (double) count;
					if (freq_rel > 0.05){ 
						Multiword m = new Multiword(k_pat, f_pat);
						if(mapper.containsKey(k_pat)){
							if(mapper.get(k_pat).size() > 1)
								m.setFlex(true);
						}
						collocation.addMultiword(m);
						//System.out.println(Utility.patternToString(k_pat, f_pat) + "(" + freq_rel + "," + count + ")");
					}
						
				}
			
			collocation.setPattern(new_patterns);
		}
	}
}
