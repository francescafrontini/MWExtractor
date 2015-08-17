package extractor;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import multiword.MultiwordExtractor;
import properties.ExtractorProperties;
import statistical.StatisticCalc;
import utilities.Utility;
import box.Collocation;
import box.Token;
import export.ExportMWAbstract;
import export.ExportToExt;
import export.ExportToGold;
import export.ExportToGold2;
import export.ExportToLMF;
import export.ExportToR;
import export.ExportToStd;
import filter.PreFilter;

/**
 * This class defines how collocations and their related word patterns are extracted 
 * from the input corpus.
 * @author Valeria Quochi @author Francesca Frontini @author Francesco Rubino
 * Istituto di linguistica Computazionale "A. Zampolli" - CNR Pisa - Italy
 * 
 */



public class ExtractorCollocations_ITA extends ExtractorCollocationsAbstract {

	private String aPOS = null; 
	private String bPOS = null; 
	private boolean aStartWith = false; //allow to search for POS tag starting with the char
	private boolean bStartWith = false;	
	private List<String> bad_multiwords = null; //collocations to be ignored
	private List<String> bad_words = null; //words to be ignored
	private List<String> good_words = null; //words to be extracted
	private String key_with_max_freq = null;
	private int window; 

	/**
	 * Three params are passed directly, others are read from files. The name of 
	 * these files in turn are contained in a file of properties
	 *  
	 * @param aPOS 1 char. The part-of-speech of the first item of the collocation. 
	 * This is dependent on the tagset used in the input tagged/parsed corpus
	 * 
	 * @param bPOS 1 char. The part-of-speech of second item of the collocation, 
	 * and last of the multiwords of length >2. This is dependent on the tagset 
	 * used in the input tagged/parsed corpus
	 * 
	 * @param window Number, positive integer. The Window-size for collocation 
	 * extraction and maximum lenght of corresponding multiwords
	 */
	public ExtractorCollocations_ITA(String aPOS, String bPOS, int window) {

		super();
		this.aPOS = aPOS;
		this.bPOS = bPOS;
		this.bad_multiwords = Utility.getWordListByFile(ExtractorProperties.getStringProperty(ExtractorProperties.__BAD_MULTIWORDS__));
		this.bad_words = Utility.getWordListByFile(ExtractorProperties.getStringProperty(ExtractorProperties.__BAD_WORDS__));
		this.good_words = Utility.getWordListByFile(ExtractorProperties.getStringProperty(ExtractorProperties.__GOOD_WORDS__));
// ma l'estrattore.properties che c'� qui non ha good_words!
		this.window = window;
//removes the + for the output lexicon
		if (this.aPOS.endsWith("+")) {
			this.aPOS = this.aPOS.replace("+", "");
			aStartWith = true;
		}
		if (this.bPOS.endsWith("+")) {
			this.bPOS = this.bPOS.replace("+", "");
			bStartWith = true;
		}

	}

	/**
	 * Only the window-size is passed directly, the others are read from the file of properties
	 * @param window Window-size for collocation extraction and maximum lenght of corresponding multiwords
	 */
	public ExtractorCollocations_ITA(int window) {
		super();

		this.aPOS = ExtractorProperties.getStringProperty(ExtractorProperties.__APOS__);
		this.bPOS = ExtractorProperties.getStringProperty(ExtractorProperties.__BPOS__);
		this.bad_multiwords = Utility.getWordListByFile(ExtractorProperties.getStringProperty(ExtractorProperties.__BAD_MULTIWORDS__));
		this.bad_words = Utility.getWordListByFile(ExtractorProperties.getStringProperty(ExtractorProperties.__BAD_WORDS__));
		this.window = window;

		if (this.aPOS.endsWith("+")) {
			this.aPOS = this.aPOS.replace("+", "");
			aStartWith = true;
		}
		if (this.bPOS.endsWith("+")) {
			this.bPOS = this.bPOS.replace("+", "");
			bStartWith = true;
		}

	}
	
	@Override
	public void writeResult(String filename) {
		String field_A = aPOS;
		String field_B = bPOS;
		if (aStartWith)
			field_A += "plus";
		if (bStartWith)
			field_B += "plus";

		if (ExtractorProperties.getBooleanProperty(ExtractorProperties.__LOG__))
			System.out.println("End of Extraction!");

		// System.out.println(multiwords.get(this.key_with_max_freq).getLemma_a()
		// + " " + multiwords.get(this.key_with_max_freq).getLemma_b() + " " +
		// multiwords.get(this.key_with_max_freq).getFrequency());
		
		addLastInformationAndFilter();
		
		// Token sequences extraction. Extracts the sequences of tokens related to the collocations, i.e. MWEs candidates
		if(ExtractorProperties.getBooleanProperty(ExtractorProperties.__MERGE_PATTERN_BY_LEMMA)){
			MultiwordExtractor me = new MultiwordExtractor();
			me.extractByPattern(collocations); 
		}
		
		if (ExtractorProperties.getBooleanProperty(ExtractorProperties.__LOG__))
			System.out.println("Print Multiword " + field_A + "-" + field_B);

		ExportMWAbstract export = null;
		String output_type = ExtractorProperties.getStringProperty(ExtractorProperties.__OUTPUT_TYPE__);
		if (output_type.equals("tab"))
			export = new ExportToStd();
		if (output_type.equals("lmf"))
			export = new ExportToLMF();
		if (output_type.equals("ext"))
			export = new ExportToExt();
		if (output_type.equals("r"))
			export = new ExportToR();
		if (output_type.equals("gold"))
			export = new ExportToGold();
		if (output_type.equals("gold2"))
			export = new ExportToGold2();
export.export(filename, collocations, single_freq, field_A, field_B, num_tokens);
	}

	@Override
	public void extractMWfromPosTag(List<Token> sentence) {
		// search words by tag
		
		List<String> idx_found = new ArrayList<String>();

		for (int i = 0; i < sentence.size(); i++) {
			Token current_token = sentence.get(i);

			// if (current_token.isPos(aPOS) || current_token.isPos(bPOS)) {
			if (current_token.isPos(aPOS, aStartWith)) {
				if ((good_words.isEmpty()) || (good_words.contains(current_token.getLemma()))) {
					// go on searching
					int k = i + 1;
					while (k < sentence.size()) {
						if (k - i >= this.window)
							break;

						Token tok_succ = sentence.get(k);
						if (tok_succ.isStopWords())
							break;

						// if (current_token.isPos(aPOS) &&
						// tok_succ.isPos(bPOS)) {
						if (tok_succ.isPos(bPOS, bStartWith)) {
							if (!bad_multiwords.contains(current_token.getLemma() + Utility.__SPLITTER_X_MULTIWORDS__ + tok_succ.getLemma()) && !bad_words.contains(current_token.getLemma())
									&& !bad_words.contains(tok_succ.getLemma())) {
								if (filterNumber(current_token, tok_succ)) {
									// System.out.println("multiword: " +
									// current_token.getLemma() +
									// Utility.__SPLITTER_X_MULTIWORDS__ +
									// tok_succ.getLemma());
									String pattern = Utility.getPattern(current_token, tok_succ, sentence);
									// normalize all to lowercase
									pattern = pattern.toLowerCase();
									insert(current_token, tok_succ, pattern, 0); //PATTERN HERE IS THE FULL WORD SEQUENCE BETWEEN A AND B

									String key_single = current_token.getLemma() + Utility.__SPLITTER_X_MULTIWORDS__ + current_token.getPosl() + Utility.__SPLITTER_X_MULTIWORDS__
											+ current_token.getIdx();
									// System.out.println("current_token_single: "
									// +
									// key_single);
									if (!idx_found.contains(key_single)) {
										putInSingleHash(current_token);
										idx_found.add(key_single);
									}

									key_single = tok_succ.getLemma() + Utility.__SPLITTER_X_MULTIWORDS__ + tok_succ.getPosl() + Utility.__SPLITTER_X_MULTIWORDS__ + tok_succ.getIdx();
									// System.out.println("tok_succ_single: " +
									// key_single);
									if (!idx_found.contains(key_single)) {
										putInSingleHash(tok_succ);
										idx_found.add(key_single);
									}

									key_single = null;
								}
							}
						}

						k = k + 1;
						tok_succ = null;
					}
				}

			}
			current_token = null;
		}
		idx_found = null;
		sentence = null;
		idx_found = null;

	}

	private boolean filterNumber(Token t1, Token t2) { //filters out numbers on digits or words 
		if (ExtractorProperties.getBooleanProperty(ExtractorProperties.__FILTERNUMBER__)) {

			String[] number = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
			String[] num_in_word = { "UNO", "DUE", "TRE", "QUATTRO", "CINQUE", "SEI", "SETTE", "OTTO", "NOVE", "DIECI", "UNDICI", "DODICI", "TREDICI", "QUATTORDICI", "QUINDICI", "SEDICI",
					"DICIASSETTE", "DICIOTTO", "DICIANNOVE", "VENTI", "TRENTA", "QUARANTA", "CINQUANTA", "SESSANTA", "SETTANTA", "OTTANTA", "NOVANTA", "CENTO" };

			for (int i = 0; i < number.length; i++) {
				if (t1.getLemma().contains(number[i]))
					return false;
				if (t2.getLemma().contains(number[i]))
					return false;
			}
			for (int i = 0; i < num_in_word.length; i++) {
				if (t1.getLemma().contains(num_in_word[i]))
					return false;
				if (t2.getLemma().contains(num_in_word[i]))
					return false;
			}

			number = null;
			num_in_word = null;
		}
		return true;
	}

	@Override
	protected void insert(Token t1, Token t2, String pattern, int type) {
		putInHashPair(t1, t2, pattern, type); 
	}

	@Override
	protected void putInSingleHash(Token t1) { //constructs hash for frequency of single items
		String key = t1.getLemma() + Utility.__SPLITTER_X_MULTIWORDS__ + t1.getPosl();
		if (this.single_freq.containsKey(key)) {
			long freq = this.single_freq.get(key) + 1;
			this.single_freq.put(key, freq);
		} else {
			this.single_freq.put(key, (long) 1);
		}

	}

	@Override
	protected void putInHashPair(Token t1, Token t2, String pattern, int type) { //constructs hash of for frequency of collocation pairs. Stores Lemma, POS, form for both items. Stores the key as lemma a<>lemma b
		String lemma_a = t1.getLemma();
		String lemma_b = t2.getLemma();
		String pos_a = t1.getPosl();
		String pos_b = t2.getPosl();
		String token_a = t1.getToken();
		String token_b = t2.getToken();
		String key = lemma_a + Utility.__SPLITTER_X_MULTIWORDS__ + lemma_b;
		boolean estrai_pattern = false;
		if (ExtractorProperties.getBooleanProperty(ExtractorProperties.__EXTRACT_PATTERN_))
			estrai_pattern = true;
		if (collocations.containsKey(key)) {
			Collocation p = collocations.get(key);
			p.addFrequency();

			if (estrai_pattern)
				p.addPattern(pattern);
			collocations.put(key, p);

			// check and set max frequency
			long frequency = p.getFrequency();
			if (frequency > collocations.get(key_with_max_freq).getFrequency())
				key_with_max_freq = key;

			p = null;
		} else {
			Collocation p = new Collocation(lemma_a, lemma_b);
			p.setPos_a(pos_a);
			p.setPos_b(pos_b);
			p.setToken_a(token_a);
			p.setToken_b(token_b);
			if (estrai_pattern)
				p.addPattern(pattern);

			collocations.put(key, p);

			// check and set max frequency
			if (key_with_max_freq == null)
				key_with_max_freq = key;
			p = null;
		}
		num_tokens++;
		key = null;
		lemma_a = null;
		lemma_b = null;
		pos_a = null;
		pos_b = null;
		token_a = null;
		token_b = null;

	}

	/**
	 * This methods determines average frequency and defines the possible prefilters that can be applied
	 */
	public void addLastInformationAndFilter() {

		boolean association_measures = ExtractorProperties.getBooleanProperty(ExtractorProperties.__ASSOCIATION_MEASURES__);
		long averageFreq = (long) (num_tokens / collocations.size()) + 1;
		if (ExtractorProperties.getBooleanProperty(ExtractorProperties.__LOG__)) {
			if (ExtractorProperties.getStringProperty(ExtractorProperties.__PREFILTER__).equals("averagef"))
				System.out.println("PreFiltering By Average ");
			if (ExtractorProperties.getStringProperty(ExtractorProperties.__PREFILTER__).equals("maxf"))
				System.out.println("PreFiltering By Max ");
			if (ExtractorProperties.getStringProperty(ExtractorProperties.__PREFILTER__).equals("nofilter"))
				System.out.println("No PreFiltering");
		}

		Enumeration<String> keys = collocations.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			//System.out.println(">>"+key+"<<");

			Collocation pattern = collocations.get(key);
			Long freq_ab = pattern.getFrequency();
			Long freq_a = this.single_freq.get(pattern.getLemma_a() + Utility.__SPLITTER_X_MULTIWORDS__ + pattern.getPos_a());
			Long freq_b = this.single_freq.get(pattern.getLemma_b() + Utility.__SPLITTER_X_MULTIWORDS__ + pattern.getPos_b());
			// System.out.println(pattern.getLemma_a() + "_" +
			// pattern.getPos_a() + " " + pattern.getLemma_a() + "_" +
			// pattern.getPos_b()); System.out.println(freq_a + " " + freq_b);

			// System.out.println(key + " " + freq_ab + " " + freq_a + " " +
			// freq_b);
			
			// //// FILTERS
			boolean isfiltered = false;
			if (ExtractorProperties.getStringProperty(ExtractorProperties.__PREFILTER__).equals("averagef")) {
				if (PreFilter.isFilteredByAverageFrequency(pattern, freq_a, freq_b, averageFreq))
					isfiltered = true;
			}
			if (ExtractorProperties.getStringProperty(ExtractorProperties.__PREFILTER__).equals("maxf")) {
				long maxFreq = collocations.get(this.key_with_max_freq).getFrequency();
				if (PreFilter.isFilteredByMaxFrequency(pattern, freq_a, freq_b, maxFreq))
					isfiltered = true;
			}
			// //// END FILTERS

			if (isfiltered) {
				// freq_aPOS.put(a,freq_aPOS.get(a) - f);
				// freq_bPOS.put(b,freq_bPOS.get(b) - f);
				// num_tokens = num_tokens - f;
				collocations.remove(key);
			} else {
				pattern.setRelativefrequency(StatisticCalc.relativeFrequency(freq_ab, num_tokens));
				if (association_measures) {
					pattern.setMutualinformation(StatisticCalc.mutualInformation(freq_ab, freq_a, freq_b, num_tokens));
					pattern.setLoglikelihood(StatisticCalc.loglikelihood(key, freq_ab, freq_a, freq_b, num_tokens));
					pattern.setMaximumlikelihood(StatisticCalc.maximumLikelihoodEstimate(freq_ab, freq_a));
				}
				collocations.put(key, pattern);
			}

			key = null;
			pattern = null;
			freq_a = null;
			freq_b = null;
			freq_ab = null;
		}
	}

}
