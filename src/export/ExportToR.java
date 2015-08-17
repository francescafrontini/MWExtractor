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

import box.Collocation;



/**
 * This class converts the set of word patterns with frequencies 
 * for collocation pair into R vectors for calculation of standard deviation. 
 * @author Valeria Quochi @author Francesca Frontini @author Francesco Rubino
 * Istituto di linguistica Computazionale "A. Zampolli" - CNR Pisa - Italy
 * 
 */

public class ExportToR extends ExportMWAbstract {

	private StringBuffer x = null;
	private StringBuffer y = null;

	public ExportToR() {
		this.x = new StringBuffer();
		this.y = new StringBuffer();
	}

	@Override
	public void export(String filename, Hashtable<String, Collocation> multiwords, Hashtable<String, Long> single_freq, String label_a, String label_b, long num_tokens) {
		BufferedWriter out;

		int treshold = ExtractorProperties.getIntProperty(ExtractorProperties.__TRESHOLD__);
		String output_format = ExtractorProperties.getStringProperty(ExtractorProperties.__OUTPUT_FORMAT__);
		String[] columns_rscript = ExtractorProperties.getArrayProperty(ExtractorProperties.__VECTORS_R_SCRIPT__, ",");
		String column = ExtractorProperties.getStringProperty(ExtractorProperties.__ORDERBY__);

		Vector<Collocation> ordered_pattern = null;
		if (column.contains("noorder"))
			ordered_pattern = new Vector<Collocation>(multiwords.values());
		else
			ordered_pattern = CollocationSort.sort(multiwords, column);

		if (ExtractorProperties.getBooleanProperty(ExtractorProperties.__LOG__))
			System.out.println("Export R Script");
		
		Iterator<Collocation> it = ordered_pattern.iterator();
		while (it.hasNext()) {
			Collocation pattern = it.next();
			Long freq_ab = pattern.getFrequency();
			if (freq_ab > treshold) 
				insertInVectors(pattern, columns_rscript);
		}
		try {
			if (output_format.equals("file"))
				out = new BufferedWriter(new FileWriter(filename + ".R"));
			else
				out = new BufferedWriter(new OutputStreamWriter(System.out));
			
			x.replace(x.length() - 1, x.length(), "");
			y.replace(y.length() - 1, y.length(), "");
			out.write("x<-c(" + x.toString() + ")\n");
			out.write("y<-c(" + y.toString() + ")\n");
			// out.write("cor.test(x,y,method=\"spearman\")");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void insertInVectors(Collocation multiword, String[] columns) {
		if (columns[0].equals("frequency"))
			x.append(multiword.getFrequency());
		if (columns[0].equals("frelative"))
			x.append(multiword.getRelativefrequency());
		if (columns[0].equals("ll"))
			x.append(multiword.getLoglikelihood());
		if (columns[0].equals("mi"))
			x.append(multiword.getMutualinformation());

		if (columns[1].equals("frequency"))
			y.append(multiword.getFrequency());
		if (columns[1].equals("frelative"))
			y.append(multiword.getRelativefrequency());
		if (columns[1].equals("ll"))
			y.append(multiword.getLoglikelihood());
		if (columns[1].equals("mi"))
			y.append(multiword.getMutualinformation());
		x.append(",");
		y.append(",");
	}

}
