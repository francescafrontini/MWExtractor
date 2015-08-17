package reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import properties.ExtractorProperties;
import utilities.Utility;
import box.Token;
import extractor.ExtractorCollocationsAbstract;
import extractor.ExtractorCollocations_ITA;

public class PanaceaNetReader extends InputReaderAbstract {

	/**
	 * Reader to take in input a single file with a list of urls to the texts of the input corpus (in CoNLL format)
     * @author Valeria Quochi @author Francesca Frontini @author Francesco Rubino

	 */
	private String file_with_urls; 
	
	public PanaceaNetReader(ExtractorCollocationsAbstract extractor, String file_with_urls) {
		super(extractor);
		this.file_with_urls = file_with_urls;
	}

	@Override
	public void extract() {
		String line, url;
		BufferedReader in, readerURL;
		URL oracle = null;
		URLConnection yc = null;

		try {
			in = new BufferedReader(new FileReader(this.file_with_urls));
			while ((url = in.readLine()) != null) {
				//System.out.println("***** " + url + " *****");
				oracle = new URL(url);
				yc = oracle.openConnection();
				readerURL = new BufferedReader(new InputStreamReader(
						yc.getInputStream()));
				while ((line = readerURL.readLine()) != null) {
					if (line.equals("")) {
						extractor.extractMWfromPosTag(sentence);
						sentence.clear();
					} else {
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
				readerURL.close();
				oracle = null;
				yc = null;
				readerURL = null;
				
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: java -jar estrattoreMWv6.jar your_corpus file_properties [window] [aPOS] [bPOS]");
			System.exit(0);
		}

		
		String file_with_urls = args[0];
		String file_properties = args[1];
		
		ExtractorProperties.__FILECONF__ = file_properties;
		ExtractorProperties.loadProperties();
		int window = 0;
		String aPOS = null;
		String bPOS = null;
		
		if(args.length == 3){
			window = Integer.parseInt(args[2]);
			aPOS = ExtractorProperties.getStringProperty(ExtractorProperties.__APOS__);
			bPOS = ExtractorProperties.getStringProperty(ExtractorProperties.__BPOS__);
		}else{
			if(args.length == 4){
				window = Integer.parseInt(args[2]);
				aPOS = args[3];
				bPOS = args[4];
			}else{
				window = ExtractorProperties.getIntProperty(ExtractorProperties.__WINDOW__);
				aPOS = ExtractorProperties.getStringProperty(ExtractorProperties.__APOS__);
				bPOS = ExtractorProperties.getStringProperty(ExtractorProperties.__BPOS__);
			}
		}

		if (ExtractorProperties.getBooleanProperty(ExtractorProperties.__LOG__)) {
			System.out.println("Multiword Extraction in: " + file_with_urls);
			System.out.println("START " + Utility.getCurrentDate());
		}
		
		ExtractorCollocationsAbstract extractor = null;
		InputReaderAbstract reader;
		File file_descriptor = new File(file_with_urls);
		if(file_descriptor.isFile()){
			extractor = new ExtractorCollocations_ITA(aPOS, bPOS,window);
			reader = new PanaceaNetReader(extractor, file_with_urls);
			reader.extract();
			extractor.writeResult(file_with_urls);
		}
		
		if (ExtractorProperties.getBooleanProperty(ExtractorProperties.__LOG__)) {
			System.out.println("END " + Utility.getCurrentDate());
		}

	}

}
