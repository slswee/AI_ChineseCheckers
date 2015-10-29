
public class UCB1Value {
	private int myTotalCount;
	private int numberOfWins;
	private double UCBscore;
	
	public UCB1Value(){
		myTotalCount = 0;
		numberOfWins = 0;
		UCBscore = 0.0;
	}
	
	public int getMyTotalCount() {
		return myTotalCount;
	}
	public int getNumberOfWins() {
		return numberOfWins;
	}
	public double getUCBscore() {
		return UCBscore;
	}
	
	public void increaseMyTotalCount() {
		myTotalCount++;
	}
	public void increaseNumberOfWins() {
		numberOfWins++;
	}
	
	public void updateUCBscore(double score) {
		UCBscore = score;
	}
	
}
