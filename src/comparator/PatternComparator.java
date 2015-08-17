package comparator;



import java.util.Comparator;

import box.Collocation;

public class PatternComparator implements Comparator<Collocation> {

	private String column;

	/**
	 * @author Valeria Quochi @author Francesca Frontini @author Francesco Rubino
	 * Istituto di linguistica Computazionale "A. Zampolli" - CNR Pisa - Italy
	 * 
	 */
	
	public PatternComparator(String column) {
		super();
		this.column = column;
	}

	@Override
	public int compare(Collocation arg0, Collocation arg1) {
		//cosa sono arg0 e arg1?
		if (column.equals("frequency")) {
			if (arg1.getFrequency() < arg0.getFrequency())
				return -1;
			if (arg1.getFrequency() > arg0.getFrequency())
				return 1;
		}
		if (column.equals("frelative")) {
			if (arg1.getRelativefrequency() < arg0.getRelativefrequency())
				return -1;
			if (arg1.getRelativefrequency() > arg0.getRelativefrequency())
				return 1;
		}
		if (column.equals("mi")) {
			if (arg1.getMutualinformation() < arg0.getMutualinformation())
				return -1;
			if (arg1.getMutualinformation() > arg0.getMutualinformation())
				return 1;
		}
		if (column.equals("ll")) {
			if (arg1.getLoglikelihood() < arg0.getLoglikelihood())
				return -1;
			if (arg1.getLoglikelihood() > arg0.getLoglikelihood())
				return 1;
		}
		return 0;
	}

}
