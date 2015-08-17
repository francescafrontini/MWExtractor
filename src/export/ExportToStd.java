package export;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import properties.ExtractorProperties;
import sort.CollocationSort;
import utilities.Utility;

import box.Collocation;

/**
 * This class defines the standard export/output format.
 * @author Valeria Quochi @author Francesca Frontini @author Francesco Rubino
 * Istituto di linguistica Computazionale "A. Zampolli" - CNR Pisa - Italy
 * 
 */

public class ExportToStd extends ExportMWAbstract{

	@Override
	public void export(String filename, Hashtable<String, Collocation> multiwords, Hashtable<String, Long> single_freq, String label_a, String label_b, long num_tokens) {
		BufferedWriter out = null;
		
		int treshold = ExtractorProperties.getIntProperty(ExtractorProperties.__TRESHOLD__);
		boolean association_measures = ExtractorProperties.getBooleanProperty(ExtractorProperties.__ASSOCIATION_MEASURES__);

		String output_format = ExtractorProperties.getStringProperty(ExtractorProperties.__OUTPUT_FORMAT__);
		String column = ExtractorProperties.getStringProperty(ExtractorProperties.__ORDERBY__);

		Vector<Collocation> ordered_pattern = null;
		if (column.contains("noorder"))
			ordered_pattern = new Vector<Collocation>(multiwords.values());
		else
			ordered_pattern = CollocationSort.sort(multiwords, column);
		// STAMPO I RISULTATI
		try {

			if (output_format.equals("file"))
				out = new BufferedWriter(new FileWriter(filename+ ".tab"));
			else
				out = new BufferedWriter(new OutputStreamWriter(System.out));

			long total = multiwords.size();
			int count = 0;

			// stampo l'header
			out.write("##:: l1 = " + label_a + "\n");
			out.write("##:: l2 = " + label_b + "\n");
			out.write("##:: size = " + total + "\n");
			out.write("id\tl1\tl2\tf\tf1\tf2\tN\tfrel");

			if (association_measures){
				out.write("\tMI");
				out.write("\tLL");
				out.write("\tMLE");
			}
			out.write("\n");
			Iterator<Collocation> it = ordered_pattern.iterator();
			while (it.hasNext()) {
				Collocation pattern = it.next();
				Long freq_ab = pattern.getFrequency();
				Long freq_a = single_freq.get(pattern.getLemma_a() + Utility.__SPLITTER_X_MULTIWORDS__ + pattern.getPos_a());
				Long freq_b = single_freq.get(pattern.getLemma_b() + Utility.__SPLITTER_X_MULTIWORDS__ + pattern.getPos_b());

				if (freq_ab > treshold) {
					out.write(count + "\t" + pattern.collocationToString(freq_a, freq_b, num_tokens) + "\n");
					
					count = count + 1;
				}
				pattern = null;
				freq_ab = null;
				freq_a = null;
				freq_b = null;
			}

			out.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
