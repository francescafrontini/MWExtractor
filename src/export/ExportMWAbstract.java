package export;

import java.util.Hashtable;

import box.Collocation;

/**
 * This class defines the information type to be represented in the output lexicon
 * @author Valeria Quochi @author Francesca Frontini @author Francesco Rubino
 * Istituto di linguistica Computazionale "A. Zampolli" - CNR Pisa - Italy
 * 
 */

public abstract class ExportMWAbstract {

	public abstract void export(String filename, Hashtable<String, Collocation> multiwords, Hashtable<String, Long> single_freq, String label_a, String label_b, long num_tokens);
	
}
