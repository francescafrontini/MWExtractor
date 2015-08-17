package sort;

import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import box.Collocation;

import comparator.PatternComparator;

/**
 * Defines the different sorting criteria of the final MWEs list to be output. 
 * @author Valeria Quochi @author Francesca Frontini @author Francesco Rubino
 * Istituto di linguistica Computazionale "A. Zampolli" - CNR Pisa - Italy
 * 
 */


public class CollocationSort {
	public static Vector<Collocation> sort(Hashtable<String, Collocation> multiwords, String column){
		PatternComparator pc = new PatternComparator(column);
		
		Collection<Collocation> patterns = multiwords.values();
		Vector<Collocation> vector = new Vector<Collocation>(patterns);
		Collections.sort(vector, pc);
		return vector;

	}
	public static Vector<Collocation> sort(List<Collocation> multiwords, String column){
		PatternComparator pc = new PatternComparator(column);
		
		Collection<Collocation> patterns = multiwords;
		Vector<Collocation> vector = new Vector<Collocation>(patterns);
		Collections.sort(vector, pc);
		return vector;
	}

}
