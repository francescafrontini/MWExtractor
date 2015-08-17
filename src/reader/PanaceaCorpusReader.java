package reader;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import box.Token;

import properties.ExtractorProperties;
import utilities.Utility;
import extractor.ExtractorCollocationsAbstract;
import extractor.ExtractorCollocations_ITA;
/**
 * Reads from CoNLL 10 columns format; used in PANACEA project
 * @author Valeria Quochi @author Francesca Frontini @author Francesco Rubino
 * Istituto di linguistica Computazionale "A. Zampolli" - CNR Pisa - Italy


 * @param parameter file
 * @param dirname  Name of the directory with corpus files
 */
public class PanaceaCorpusReader extends InputReaderAbstract {

	private String dirname;	
	
	public PanaceaCorpusReader(ExtractorCollocationsAbstract extractor, String dirname) {
		super(extractor);
		this.dirname = dirname;
	}

	@Override
	public void extract() {
		String line;
		BufferedReader in;

		try {
			File dir = new File(dirname);
			File[] files=new File[0];
			if (dir.isDirectory()) {				
				files = dir.listFiles();
			}else if (dir.isFile()){
				//check if file contains a list of URLs
				boolean isFileURL=false;  
				try{
					  FileInputStream fstream = new FileInputStream(dir);
					  DataInputStream instream = new DataInputStream(fstream);
					  BufferedReader br = new BufferedReader(new InputStreamReader(instream));
					  String strLine = br.readLine();
					  try {
						    URL url = new URL(strLine);
						    URLConnection conn = url.openConnection();
						    conn.connect();
						    isFileURL=true;
						} catch (Exception e) {
							isFileURL=false;
						}
					  //Close the input stream
					  instream.close();
					    }catch (Exception e){//Catch exception if any
					  System.err.println("Error checking URL file: " + e.getMessage());
					  }
			    ////
				if(isFileURL){
					// URLs file
					try{
						  FileInputStream fstream = new FileInputStream(dir);
						  DataInputStream instream = new DataInputStream(fstream);
						  BufferedReader br = new BufferedReader(new InputStreamReader(instream));
						  String strLine;
						  ArrayList<URL> vetfiles = new ArrayList<URL>();
						while ((strLine = br.readLine()) != null)   
						{
								URL url = new URL(strLine);  
								vetfiles.add(url);
								in = new BufferedReader(new InputStreamReader(url.openStream()));
								String[] token_splitted;
								while ((line = in.readLine()) != null) {
									if (line.equals("")) {
										extractor.extractMWfromPosTag(sentence);
										sentence.clear();
									} else {
										//System.out.println(line);
										token_splitted = line.split(Utility.__SPLITTER__);
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
								in.close();
								in = null;
						}  
						//Close the input stream
						  instream.close();
						
					}catch (Exception e){//Catch exception if any
						  System.err.println("Error opening files from URLs: " + e.getMessage());
						  e.printStackTrace();}
					
				}
				else{
					//single file
					files=new File[1];
					files[0]=dir;
				}				
			}
			int fileslen=files.length;
			int counterfile=0;
			for (File f : files) {
				//counterfile++; System.out.println("File "+counterfile+" of "+fileslen+";");
				if (f.isFile()) {
					in = new BufferedReader(new FileReader(f));
					//in = new BufferedReader(new InputStreamReader(url));
				    //System.out.println(">>"+f.getName());
					String[] token_splitted;
					int idx;
					String lemma;
					String post;
					String posl;
					String morpho;
					int hrel;
					String relation;
					Token the_token;
					while ((line = in.readLine()) != null) {
						if (line.equals("")) {
							extractor.extractMWfromPosTag(sentence);
							sentence.clear();
						} else {
							token_splitted = line.split(Utility.__SPLITTER__);
							idx = Integer.parseInt(token_splitted[Utility.__IDLINE__]);
							String token = token_splitted[Utility.__WORD__];
							lemma = token_splitted[Utility.__LEMMA__];
							post = token_splitted[Utility.__WPOS__];
							posl = token_splitted[Utility.__POS__];
							morpho = token_splitted[Utility.__MORPHO__];
							hrel = Integer.parseInt(token_splitted[Utility.__POINTER__]);
							relation = token_splitted[Utility.__RELATION__];
							the_token = new Token(idx, token, lemma, post, posl, morpho, hrel, relation);
							sentence.add(the_token);
						}
					}
				
					in.close();
					in = null;
					
				}
			
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {

		if (args.length < 1) {
			System.out.println("Usage: java -jar estrattoreMWv6.jar your_corpus file_properties [window] [aPOS] [bPOS]");
			System.exit(0); // perche v5?
		}
		try{
			ExtractorProperties.loadProperties(args[1]);
		}catch(Exception e){
			System.out.println("Usage: java -jar estrattoreMWv6.jar your_corpus file_properties [window] [aPOS] [bPOS]");
			System.exit(0);
		}
		String dirname = args[0];
		int window = 0;
		String aPOS = null;
		String bPOS=null;
		
		if(args.length == 3){
			window = Integer.parseInt(args[2]);
			aPOS = ExtractorProperties.getStringProperty(ExtractorProperties.__APOS__);
			bPOS = ExtractorProperties.getStringProperty(ExtractorProperties.__BPOS__);
		}else{
			if(args.length == 5){
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
			System.out.println("Multiword Extraction in: " + dirname);
			System.out.println("START " + Utility.getCurrentDate());
		}	
		ExtractorCollocationsAbstract extractor = null;
		InputReaderAbstract reader;
		File file_descriptor = new File(dirname);
		if(file_descriptor.isDirectory() || file_descriptor.isFile()){
			extractor = new ExtractorCollocations_ITA(aPOS, bPOS,window);
			reader = new PanaceaCorpusReader(extractor, dirname);
			reader.extract();
			//System.out.println("END " + Utility.getCurrentDate());
			extractor.writeResult(dirname);
		}
		else{
			System.out.println("Problem reading "+dirname);
		}
		if (ExtractorProperties.getBooleanProperty(ExtractorProperties.__LOG__)) {
			System.out.println("END " + Utility.getCurrentDate());
		}
	}
}
