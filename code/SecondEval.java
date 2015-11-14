
public class SecondEval implements EvaluationFunction{
	//Important: the currentPlayer here must be us! we could be doing a 2 ply search and evaluating our opponents' leaves and maximizing that 
	// and use it as ours. That's why it's going backwards. 
	
	@Override
	public double eval (StateInterface state, int currentPlayer) {
		ChineseCheckersState s = (ChineseCheckersState) state;
		int winner = s.winner();
		if (currentPlayer == winner) { //if we are the winner
			return Double.POSITIVE_INFINITY;
		} 
		else if (winner != -1) {  //game is over and we lost
			return Double.NEGATIVE_INFINITY;
		}
				
			double sumPlayer1 = 0.0;
			double sumPlayer2 = 0.0;

			for (int i=0; i<81; ++i) {
				if (s.getBoard()[i] == 1) {
					sumPlayer1 += 16 - (i%9 + i/9);
				}
				if (s.getBoard()[i] == 2) {
					sumPlayer2 += i%9 + i/9;
				}
			}

			if (currentPlayer == 1) {
				return sumPlayer2 - sumPlayer1 + Math.random();
			}
			if (currentPlayer == 2) {
				return sumPlayer1 - sumPlayer2 + Math.random();
			}
		
		
		return 0.0 ;
}
}