
public class CurrentBestMove {
	public double score = 0; //static means there's one score for everything everywhere 
	public Move m = new Move(-1,-1);
	
	public String toString() {
		return "move from " + m.from + "to " + m.to +" " + "score is " + this.score + " ";
	
	}
}
