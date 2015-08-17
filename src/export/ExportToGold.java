package export;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import properties.ExtractorProperties;
import sort.CollocationSort;
import box.Collocation;

/**
 * Export the results in Gold format.
 * @author Valeria Quochi @author Francesca Frontini @author Francesco Rubino
 * Istituto di linguistica Computazionale "A. Zampolli" - CNR Pisa - Italy
 * 
 */

public class ExportToGold extends ExportMWAbstract {

	@Override
	public void export(String filename, Hashtable<String, Collocation> multiwords, Hashtable<String, Long> single_freq, String label_a, String label_b, long num_tokens) {
BufferedWriter out = null;
		
		Vector<Collocation> ordered_pattern = null;
		ordered_pattern = CollocationSort.sort(multiwords, "frequency");
		// prendo i primi 20 
		//????
		List<Collocation> global_collocation = new ArrayList<Collocation>();
		getCollocations(global_collocation, ordered_pattern);
		
		ordered_pattern = CollocationSort.sort(multiwords, "ll");
		getCollocations(global_collocation, ordered_pattern);
		
		ordered_pattern = CollocationSort.sort(multiwords, "mi");
		getCollocations(global_collocation, ordered_pattern);
		
		Vector<Collocation> global_multiword = CollocationSort.sort(global_collocation, "frequency");
		
		// STAMPO I RISULTATI
		try {
			out = new BufferedWriter(new FileWriter(filename+ ".gold"));
			long total = multiwords.size();
			int count = 0;
			// prendo i primi 20
			//????
			for(int i=0; i<global_multiword.size(); i++){
				Collocation pattern = global_multiword.get(i);
				Long freq_ab = pattern.getFrequency();
				String pattern_most_freq = pattern.getPatternMostFrequent();
				String key = pattern.getLemma_a() + "|XYZ|" + pattern.getLemma_b();
				//System.out.println(i + "\t" + key + "\t" +  printPatternMostFrequent(pattern_most_freq));
				out.write(i + "\t" + key + "\t" +  printPatternMostFrequent(pattern_most_freq) + "\n");
				pattern = null;
				freq_ab = null;
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private String printPatternMostFrequent(String max_pattern){
		//System.out.println(max_pattern);
		StringBuilder sentence = new StringBuilder();
		StringBuilder pos_sentence = new StringBuilder();
		if(!max_pattern.isEmpty()){
			String[] max_pattern_splitted = max_pattern.split("###");
			for(int i = 0; i<max_pattern_splitted.length; i++){
				String s = max_pattern_splitted[i];
				String[] s_splitted = s.split("@@@");
				String token = "";
				String pos = "";
				if(s_splitted.length >= 2){
					token = s_splitted[0];
					pos = s_splitted[1];
				}else{
					System.out.println(s + " " + s_splitted.length);
				}
				
				sentence.append(token);
				pos_sentence.append(pos);
				
				if(i != max_pattern_splitted.length - 1){
					sentence.append(" ");
					pos_sentence.append("_");
				}
			}
		}
		return sentence.toString() + "\t" + pos_sentence.toString();
	}
	private void getCollocations(List<Collocation> global_collocation, Vector<Collocation> ordered_pattern){
		//int size_part = ordered_pattern.size() * 20 / 100;
		
		int size_part = ExtractorProperties.getIntProperty(ExtractorProperties.__COLLECTIONS_SELECTED__);
		System.out.println("Size Part::"+size_part);
		if (size_part == 0)
			size_part = 1;
		for(int i=0; i<size_part; i++){
			if(!global_collocation.contains(ordered_pattern.get(i)))
				global_collocation.add(ordered_pattern.get(i));
		}
		int middle = ordered_pattern.size() / 2;
		int middle_size_part = size_part / 2;
		for(int i=middle-middle_size_part; i<middle+middle_size_part; i++){
			if(!global_collocation.contains(ordered_pattern.get(i)))
				global_collocation.add(ordered_pattern.get(i));
		}
		for(int i=ordered_pattern.size()-size_part; i<ordered_pattern.size(); i++){
			if(!global_collocation.contains(ordered_pattern.get(i)))
				global_collocation.add(ordered_pattern.get(i));
		}
	}

}
