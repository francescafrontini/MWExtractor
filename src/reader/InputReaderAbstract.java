package reader;

/**
 * 
 * @author Valeria Quochi @author Francesca Frontini @author Francesco Rubino
 * Istituto di linguistica Computazionale "A. Zampolli" - CNR Pisa - Italy
 * 
 */
import java.util.ArrayList;
import box.Token;
import extractor.ExtractorCollocationsAbstract;

public abstract class InputReaderAbstract {
	protected ArrayList<Token> sentence = null;
	protected ExtractorCollocationsAbstract extractor = null;
	
	public InputReaderAbstract(ExtractorCollocationsAbstract extractor) {
		super();
		this.extractor = extractor;
		this.sentence = new ArrayList<Token>();
	}
	
	public abstract void extract();
	
	
}
