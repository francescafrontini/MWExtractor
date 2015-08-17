package export;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import properties.ExtractorProperties;
import sort.CollocationSort;
import utilities.Utility;
import box.Collocation;
import box.Multiword;

/**
 * Defines the final export in tabbed format.
 * @author Valeria Quochi @author Francesca Frontini @author Francesco Rubino
 * Istituto di linguistica Computazionale "A. Zampolli" - CNR Pisa - Italy
 * 
 */

public class ExportToExt extends ExportMWAbstract {

	@Override
	public void export(String filename, Hashtable<String, Collocation> collocations, Hashtable<String, Long> single_freq, String label_a, String label_b, long num_tokens) {
		BufferedWriter out_ext = null;

		int treshold = ExtractorProperties.getIntProperty(ExtractorProperties.__TRESHOLD__);
		boolean association_measures = ExtractorProperties.getBooleanProperty(ExtractorProperties.__ASSOCIATION_MEASURES__);
		String output_format = ExtractorProperties.getStringProperty(ExtractorProperties.__OUTPUT_FORMAT__);
		String column = ExtractorProperties.getStringProperty(ExtractorProperties.__ORDERBY__);

		Vector<Collocation> ordered_collocations = null;
		if (column.contains("noorder"))
			ordered_collocations = new Vector<Collocation>(collocations.values());
		else
			ordered_collocations = CollocationSort.sort(collocations, column);

		try {

			if (output_format.equals("file"))
				out_ext = new BufferedWriter(new FileWriter(filename + ".ext"));
			else {
				out_ext = new BufferedWriter(new OutputStreamWriter(System.out));
			}

			if (ExtractorProperties.getBooleanProperty(ExtractorProperties.__LOG__))
				System.out.println("Print Human Readable Version");

			Iterator<Collocation> it = ordered_collocations.iterator();
			while (it.hasNext()) {
				Collocation collocation = it.next();
				Long freq_ab = collocation.getFrequency();
				Long freq_a = single_freq.get(collocation.getLemma_a() + Utility.__SPLITTER_X_MULTIWORDS__ + collocation.getPos_a());
				Long freq_b = single_freq.get(collocation.getLemma_b() + Utility.__SPLITTER_X_MULTIWORDS__ + collocation.getPos_b());
				boolean estrai_pattern = ExtractorProperties.getBooleanProperty(ExtractorProperties.__EXTRACT_PATTERN_);

				if (freq_ab > treshold) {
					// STAMPO I RISULTATI
					out_ext.write("Key = " + collocation.getLemma_a() + "|XYZ|" + collocation.getLemma_b() + "\n");
					out_ext.write("---------------------------------------\n");
					out_ext.write("\n");
					out_ext.write("Lemma 1: " + collocation.getLemma_a() + " - " + collocation.getPos_a() + " (" + freq_a + ")\n");
					out_ext.write("Lemma 2: " + collocation.getLemma_b() + " - " + collocation.getPos_b() + " (" + freq_b + ")\n");
					out_ext.write("Frequency: " + collocation.getFrequency() + "\n");
					if (estrai_pattern) {
						out_ext.write("Patterns: " + collocation.patternsToString() + "\n");

						List<Multiword> multiwords = collocation.getMultiwords();
						if (multiwords != null) {
							out_ext.write("Multiwords: ");
							for (Multiword mw : multiwords) {
								out_ext.write(mw.toString() + ", ");
							}
							out_ext.write("\n");
						}
						
						String r_vector = collocation.patternToR();
						out_ext.write("R-vector: \"pattern <- c(" + r_vector + ")\"\n");

					} else
						out_ext.write("Pattern: not extracted\n");

					if (association_measures) {
						out_ext.write("MI: " + collocation.getMutualinformation() + "\n");
						out_ext.write("LL: " + collocation.getLoglikelihood() + "\n");
						out_ext.write("MLE: " + collocation.getMaximumlikelihood() + "\n");
					}
					out_ext.write("\n");
				}
			}
			out_ext.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
