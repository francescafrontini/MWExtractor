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
 * This class defines the export to LMF.
 * The use of this class is deprecated. 
 * For export to LMF it is recommended to use an external converter, as this is not up to date 
 * and maintained.
 * @author Valeria Quochi @author Francesca Frontini @author Francesco Rubino
 * Istituto di linguistica Computazionale "A. Zampolli" - CNR Pisa - Italy
 * 
 */

public class ExportToLMF extends ExportMWAbstract {

	/**
	 * Defines the LMF feature structures
	 * @param att Attribute 
	 * @param val Value
	 * @return The XML tag syntax.
	 */
	private String feature(String att, String val){
		return "<feat att=\"" + att + "\" val=\"" + val + "\"/>";
	}
	
	@Override
	public void export(String filename, Hashtable<String, Collocation> multiwords, Hashtable<String, Long> single_freq, String label_a, String label_b, long num_tokens) {
		BufferedWriter out = null;
		
		int treshold = ExtractorProperties.getIntProperty(ExtractorProperties.__TRESHOLD__);
		boolean association_measures = ExtractorProperties.getBooleanProperty(ExtractorProperties.__ASSOCIATION_MEASURES__);

		String output_format = ExtractorProperties.getStringProperty(ExtractorProperties.__OUTPUT_FORMAT__);
		String column = ExtractorProperties.getStringProperty(ExtractorProperties.__ORDERBY__);

		Vector<Collocation> ordered_collocations = null;
		if (column.contains("noorder"))
			ordered_collocations = new Vector<Collocation>(multiwords.values());
		else
			ordered_collocations = CollocationSort.sort(multiwords, column);

		// PRINTING RESULTS
		try {

			if (output_format.equals("file"))
				out = new BufferedWriter(new FileWriter(filename+ ".lmf"));
			else
				out = new BufferedWriter(new OutputStreamWriter(System.out));

			long total = multiwords.size();
			int count = 0;

			// stampo l'header
			out.write("<LexicalResource dtdVersion=\"16\">"+ "\n");
			out.write("<!-- metadata for genaral info, to make compliant to MetaShare metadata -->"+ "\n");
			
			out.write("\t" + "<GlobalInformation>"+ "\n");
			out.write("\t\t" +feature("originalSource","corpus")+ "\n");
			out.write("\t\t" +feature("domain","-")+ "\n");
			out.write("\t\t" +feature("author","MW_Extractor")+ "\n");
			out.write("\t\t" +feature("creationMode","automatic")+ "\n");
			out.write("\t\t" +feature("creationModeDetails","acquisition")+ "\n");
			out.write("\t\t" +feature("creationTool","MW_Extractor")+ "\n");
			out.write("\t\t" +feature("creationDate","2013")+ "\n");
			out.write("\t\t" +feature("size",String.valueOf(multiwords.size()))+ "\n");
			out.write("\t\t" +feature("sizeUnit","LexicalEntries")+ "\n");
			out.write("\t" + "</GlobalInformation>"+ "\n");
			
			out.write("\t" + "<Lexicon>"+ "\n");
			out.write("\t\t" +feature("type","MWE")+ "\n");
			out.write("\t\t" +feature("language","Italian")+ "\n");
			
//counter for total number of MW entries			
			long mwe_counter = 0;
			Iterator<Collocation> it = ordered_collocations.iterator();
			while (it.hasNext()) {
				Collocation collocation = it.next();
				Long freq_ab = collocation.getFrequency();
				Long freq_a = single_freq.get(collocation.getLemma_a() + Utility.__SPLITTER_X_MULTIWORDS__ + collocation.getPos_a());
				Long freq_b = single_freq.get(collocation.getLemma_b() + Utility.__SPLITTER_X_MULTIWORDS__ + collocation.getPos_b());
				
				String pmf = collocation.getPatternMostFrequent();
				Long pmf_freq = collocation.getPattern().get(pmf);
				double pmf_relative_frequency = (double) pmf_freq / (double) num_tokens; 
				
				String token_pattern = Utility.patternToString(pmf);
				String pos_pattern = Utility.patternToPosString(pmf);
				String[] pmf_splitted = pmf.split(Utility.__S_X_PATTERN__);
				
				// PRINT COLLOCATION
				out.write("\t\t" + "<LexicalEntry id=\"" + (mwe_counter++) + "\">"+ "\n");
				out.write("\t\t\t" +feature("entryType","collocation")+ "\n");
				
				out.write("\t\t\t" +feature("POS_Pattern",collocation.getPos_a() + "+" + collocation.getPos_b())+ "\n");
				out.write("\t\t\t" +feature("relativeFrequency",String.valueOf(freq_ab))+ "\n");
				out.write("\t\t\t" +feature("logLikelihood",String.valueOf(collocation.getLoglikelihood()))+ "\n");
				
				out.write("\t\t\t" + "<Lemma>"+ "\n");
				out.write("\t\t\t\t" +feature("writtenform",collocation.getLemma_a() + " " + collocation.getLemma_b()) + "\n");
				out.write("\t\t\t" + "</Lemma>"+ "\n");
				out.write("\t\t\t" + "<ListOfComponents>"+ "\n");
				out.write("\t\t\t" + "</ListOfComponents>"+ "\n");
				out.write("\t\t" + "</LexicalEntry>"+ "\n");
				
				// PRINT MULTIWORD
				out.write("\t\t" + "<LexicalEntry id=\"" + (mwe_counter++) + "\">"+ "\n");
				
				if(collocation.getPos_a().equals("SP") || collocation.getPos_b().equals("SP"))
					out.write("\t\t\t" +feature("entryType","NamedEntity")+ "\n");
				else
					out.write("\t\t\t" +feature("entryType","Multiword")+ "\n");
				
				out.write("\t\t\t" +feature("POS_Pattern",pos_pattern)+ "\n");
				out.write("\t\t\t" +feature("relativeFrequency",String.valueOf(pmf_relative_frequency))+ "\n");
				out.write("\t\t\t" +feature("logLikelihood",String.valueOf(collocation.getLoglikelihood()))+ "\n");
				
				
				out.write("\t\t\t" + "<Lemma>"+ "\n");
				out.write("\t\t\t\t" +feature("writtenform",token_pattern)+ "\n");
				out.write("\t\t\t" + "</Lemma>"+ "\n");
				out.write("\t\t\t" + "<ListOfComponents>"+ "\n");
				for(int i=0; i<pmf_splitted.length; i++){
					String s = pmf_splitted[i];
					String[] s_splitted = s.split(Utility.__S_X_PART__);
					out.write("\t\t\t\t" + "<Component entry=\"idEntry_" + s_splitted[0].toLowerCase() + "\">"+ "\n");
					out.write("\t\t\t\t\t" + feature("rank",String.valueOf(i)) + "\n");
					out.write("\t\t\t\t\t" + feature("pos",s_splitted[1])+ "\n");
					out.write("\t\t\t\t\t" + feature("lemma",s_splitted[2])+ "\n");
					out.write("\t\t\t\t\t" + feature("writtenform",s_splitted[0])+ "\n");
					//out.write("\t\t\t\t\t" + feature("inflection","TODO")+ "\n");
					out.write("\t\t\t\t\t" + feature("frequency",String.valueOf(pmf_freq))+ "\n");
					out.write("\t\t\t\t" + "</Component>"+ "\n");
				}
				out.write("\t\t\t" + "</ListOfComponents>"+ "\n");
				out.write("\t\t" + "</LexicalEntry>"+ "\n");
			
				
				collocation = null;
				freq_ab = null;
				freq_a = null;
				freq_b = null;
			}
			out.write("\t" + "</Lexicon>"+ "\n");
			out.write("</LexicalResource>"+ "\n");

			out.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
