package box;

/**
* This class represents the final multiword candidates to be extracted and output

 * @author Valeria Quochi @author Francesca Frontini @author Francesco Rubino
 * Istituto di linguistica Computazionale "A. Zampolli" - CNR Pisa - Italy
 * 
 */

public class Multiword {

	private String multiword; 
	private long frequency;
	private boolean flex;
	
	public Multiword(String multiword, long frequency) {
		super();
		this.multiword = multiword;
		this.frequency = frequency;
	}

	public String toString(){
		return multiword + "(" + frequency + ")";
	}
	
	public String getMultiword() {
		return multiword;
	}
	public void setMultiword(String multiword) {
		this.multiword = multiword;
	}
	public long getFrequency() {
		return frequency;
	}
	public void setFrequency(long frequency) {
		this.frequency = frequency;
	}
	public boolean isFlex() {
		return flex;
	}
	public void setFlex(boolean flex) {
		this.flex = flex;
	} 
	
	
}
