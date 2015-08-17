package properties;

/**
 * @author Valeria Quochi @author Francesca Frontini @author Francesco Rubino
 * 
 */

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import export.ExportToExt;
import export.ExportToLMF;

public class ExtractorProperties {
	
	private static Properties properties;
	
	public static String __FILECONF__ = "estrattore.properties";
	/**
	 *  The window size for collocations extraction and maximum lenght of MWEs.  
	 */
	public static final String __WINDOW__ = "window";
	/**
	 * Defines which association measure should be applied.  
	 * Is used in {@link ExportToExt}, {@link ExportToLMF}
	 */
	public static final String __ASSOCIATION_MEASURES__ = "association_measures";
	/**
	 * Defines the max number of MWEs to export as results. OPPURE E` UNA THRESHOLD SULLA FRQUENZA O LL??
	 * Is used in {@link ExportToExt}, {@link ExportToLMF}
	 */
	public static final String __TRESHOLD__ = "treshold";
	public static final String __FILTERNUMBER__ = "filter_number";
	public static final String __FILTERPUNCT__ = "filter_punctuation";
	/**
	 * List of stopwords used at collocation extraction step
	 */
	public static final String __STOP_WORDS__ = "stop_words";
	/**
	 * Part of Speech of the first item of the collocation and MWs. It is dependent of the POS tagset of the input corpus 
	 */
	public static final String __APOS__ = "pos1";
	/**
	 *  Part of Speech of the second item of the collocation and last of the MW. It is dependent of the POS tagset of the input corpus 
	 */
	public static final String __BPOS__ = "pos2";
	
	public static final String __BAD_WORDS__ = "bad_words";
	public static final String __BAD_MULTIWORDS__ = "bad_multiwords";
	public static final String __GOOD_WORDS__ = "words";
	
	/**
	 * Defines the type of output requested.Is used in Is used in {@link ExportToExt}, {@link ExportToLMF}
	 */
	public static final String __OUTPUT_FORMAT__ = "output_format";
	public static final String __OUTPUT_TYPE__ = "output_type";
	
	public static final String __LOG__ = "log";
	/**
	 * Choice of pre-filtering method 
	 */
	public static final String __PREFILTER__ = "prefilter";
	/**
	 * Choice of association measure for ordering of final results. 
	 * Is used in {@link ExportToExt}, {@link ExportToLMF}
	 */
	public static final String __ORDERBY__ = "orderby";
	public static final String __VECTORS_R_SCRIPT__ = "vectors_rscript";
	
	public static final String __EXTRACT_PATTERN_ = "extract_pattern";
	public static final String __MERGE_PATTERN_BY_LEMMA = "merge_pattern_by_lemma";
	
	public static final String __COLLECTIONS_SELECTED__ = "collections_selected";
	
	
	public static void loadProperties(){
		FileInputStream in;
		try {
			properties = new Properties();
			in = new FileInputStream(ExtractorProperties.__FILECONF__);
			properties.load(in);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void loadProperties(String file_properties){
		FileInputStream in;
		try {
			properties = new Properties();
			in = new FileInputStream(file_properties);
			properties.load(in);
			in.close();
		} catch (FileNotFoundException e) {
			System.out.println("File '"+file_properties+"' not found.");
			System.exit(0);
			//e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Unable to read file: '"+file_properties+"'.");
			System.exit(0);
			//e.printStackTrace();
		}
	}
	public static String getStringProperty(String property){
		if (properties.containsKey(property))
			return properties.getProperty(property);
		return null;
	}
	
	public static int getIntProperty(String property){
		if (properties.containsKey(property))
			return Integer.parseInt(properties.getProperty(property));
		return 0;
	}
	public static boolean getBooleanProperty(String property){
		if (properties.containsKey(property)){
			String fn = properties.getProperty(property);
			if(fn.equals("true"))
				return true;
			else
				return false;
		}
		return false;
	}
	
	public static String[] getArrayProperty(String property, String splitter){
		if (properties.containsKey(property))
			return properties.getProperty(property).split(splitter);
		return null;
	}
	
}