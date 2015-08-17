package reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import properties.ExtractorProperties;
import utilities.Utility;
import box.Token;
import extractor.ExtractorCollocationsAbstract;
import extractor.ExtractorCollocations_ITA;

/**
 * Reads from input corpus format like Corpus La Repubblica
 * 
 * @author Valeria Quochi @author Francesca Frontini @author Francesco Rubino
 *
 */
public class CLRReader extends InputReaderAbstract {

	/**
	 * document delimiter (only for CLR)
	 */
	private final static String __OPEN_DOC__ = "<text";
	private final static String __CLOSE_DOC__ = "</text>";
	
	/**
	 * phrase delimiter (only for CLR)
	 */
	private final static String __OPEN_SENTENCE__ = "<s>";
	private final static String __CLOSE_SENTENCE__ = "</s>";
	
	private String filename = null;
	
	public CLRReader(ExtractorCollocationsAbstract extractor, String filename) {
		super(extractor);
		this.filename = filename;
	}


	@Override
	public void extract() {
		String line;
		BufferedReader in;

		boolean inDoc = false;
		boolean inSentence = false;

		try {
			in = new BufferedReader(new FileReader(filename));
			while ((line = in.readLine()) != null) {
				if (line.startsWith(__CLOSE_DOC__)) {
					inDoc = false;
				}

				if (line.startsWith(__CLOSE_SENTENCE__)) {
					extractor.extractMWfromPosTag(sentence);
					inSentence = false;
					sentence.clear();
				}

				if (inDoc && inSentence) {
					//System.out.println(line);
					if (!line.startsWith("<")) {
						String[] token_splitted = line.split(Utility.__SPLITTER__);
						int idx = Integer.parseInt(token_splitted[Utility.__IDLINE__]);
						String token = token_splitted[Utility.__WORD__];
						String lemma = token_splitted[Utility.__LEMMA__];
						String post = token_splitted[Utility.__WPOS__];
						String posl = token_splitted[Utility.__POS__];
						String morpho = token_splitted[Utility.__MORPHO__];
						int hrel = Integer.parseInt(token_splitted[Utility.__POINTER__]);
						String relation = token_splitted[Utility.__RELATION__];
						Token the_token = new Token(idx, token, lemma, post, posl, morpho, hrel, relation);
						sentence.add(the_token);
					}
				}

				if (line.startsWith(__OPEN_DOC__)) {
					inDoc = true;
				}

				if (inDoc && line.startsWith(__OPEN_SENTENCE__)) {
					inSentence = true;
				}
			}

			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	
	public static void main(String[] args){
		if (args.length < 1) {
			System.out.println("Usage: java -jar estrattoreMWv6.jar your_corpus [window] [aPOS] [bPOS]");
			System.exit(0);
		}

		ExtractorProperties.loadProperties();
		String filename = args[0];
		int window = 0;
		String aPOS = null;
		String bPOS=null;
		
		if(args.length == 2){
			window = Integer.parseInt(args[1]);
			aPOS = ExtractorProperties.getStringProperty(ExtractorProperties.__APOS__);
			bPOS = ExtractorProperties.getStringProperty(ExtractorProperties.__BPOS__);
		}else{
			if(args.length == 4){
				window = Integer.parseInt(args[1]);
				aPOS = args[2];
				bPOS = args[3];
			}else{
				window = ExtractorProperties.getIntProperty(ExtractorProperties.__WINDOW__);
				aPOS = ExtractorProperties.getStringProperty(ExtractorProperties.__APOS__);
				bPOS = ExtractorProperties.getStringProperty(ExtractorProperties.__BPOS__);
			}
		}

		if (ExtractorProperties.getBooleanProperty(ExtractorProperties.__LOG__)) {
			System.out.println("Multiword Extraction in: " + filename);
			System.out.println("START " + Utility.getCurrentDate());
		}
		
		ExtractorCollocationsAbstract extractor = null;
		InputReaderAbstract reader;
		File file_descriptor = new File(filename);
		if(file_descriptor.isFile()){
			extractor = new ExtractorCollocations_ITA(aPOS, bPOS,window);
			reader = new CLRReader(extractor, filename);
			reader.extract();
			extractor.writeResult(filename);
		}
		
		if (ExtractorProperties.getBooleanProperty(ExtractorProperties.__LOG__)) {
			System.out.println("END " + Utility.getCurrentDate());
		}
	}

}
