import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Random;


public class UCB1 {
	
	private static ArrayList<UCB1Value> UCBscore = new ArrayList<UCB1Value>();
	
	public ArrayList<UCB1Value> getUCBscore(){
		return UCBscore;
	}
		
	public static Move UCB1Search(ChineseCheckersState currentState, int time, boolean Random_Playout1 ) {
		
	//	int totalCount = 0;
	//	ArrayList<UCB1Value> UCBscore = new ArrayList<UCB1Value>();
		
		//timer
		AtomicBoolean timeUp = new AtomicBoolean(false);
		Alarm a = new Alarm(time, timeUp);
		a.start();
		
		ArrayList<Move> moves = new ArrayList<Move>();
		currentState.getMoves(moves);
		
		//store who the currentPlayer is
		int rootPlayer = currentState.currentPlayer; 
		
		// explore each move once
		
		for (Move m: moves) {
			
			ChineseCheckersState cloneOfCurrentState = new ChineseCheckersState();
			cloneOfCurrentState.loadState(currentState.dumpState());
			
			cloneOfCurrentState.applyMove(m);
			
			int result = randomPlayout_1(cloneOfCurrentState, 0.1, rootPlayer);
			
			UCB1Value u1 = new UCB1Value();
			
			if (result == 1) {
				u1.increaseNumberOfWins();
			}

			u1.increaseMyTotalCount();
			
			if (Random_Playout1) {
			
				double score1 = u1.getNumberOfWins()/u1.getMyTotalCount() + Math.sqrt(2* Math.log10(u1.getMyTotalCount()));
				u1.updateUCBscore(score1);
				UCBscore.add(u1);
			}
			
			double score2 = u1.getNumberOfWins()/u1.getMyTotalCount() + Math.sqrt(2* Math.log10(u1.getMyTotalCount()));
			u1.updateUCBscore(score2);
			UCBscore.add(u1);
		
	//		score.add(new Result(result, 1));
			
		}
		
		// explore move based on which move maximizes the formula
		Move bestMove = new Move(0,0);
		while (!timeUp.get()) {
	//		bestUCBMove = Double.NEGATIVE_INFINITY ; //this needs to be a double
			
			double bestUCBscore = Double.NEGATIVE_INFINITY;
			int bestIndex = 0;
				
			for (Move m:moves) {
				
			//	if UCBValue for move m > bestUCBMove
			//	   update
				int moveIndex = moves.indexOf(m);
				double currentUCBscore = UCBscore.get(moveIndex).getUCBscore();
				
				if (currentUCBscore > bestUCBscore) {
					
					bestUCBscore = currentUCBscore;
					bestIndex = moveIndex;				
				}
				
			}
			
//			bestMove = bestUCBMove;
//						make clone of state
//						randomplayout for bestUCBMove
			
	//		bestMove = moves.get(UCBscore.indexOf(bestUCBscore));
			
			bestMove = moves.get(bestIndex);
			
			
			ChineseCheckersState stateCopy = new ChineseCheckersState();
			stateCopy.loadState(currentState.dumpState());
			
			stateCopy.applyMove(bestMove);
			
			if(Random_Playout1) {
			
				int result1 = randomPlayout_1(stateCopy, 0.1, rootPlayer);
				updateUCBValue_RandomPlayout_1(result1, UCBscore);
			}
		/*	
			UCB1Value u2 = new UCB1Value();
			
			if (result == 1) {
				u2.increaseNumberOfWins();
			}

			u2.increaseMyTotalCount();
			
			double score = u2.getNumberOfWins()/u2.getMyTotalCount() + Math.sqrt(2* Math.log10(u2.getMyTotalCount()));
			u2.updateUCBscore(score);
		
	//		score.add(new Result(result, 1));
			UCBscore.add(u2);
		*/			
			
			double result2 = randomPlayout_2(stateCopy, 0.1, 40, rootPlayer);
			updateUCBValue_RandomPlayout_2(result2, UCBscore);
			
		}
		return bestMove;
	
	}
	

	public static void updateUCBValue_RandomPlayout_1(int result, ArrayList<UCB1Value> UCBscore) {

		UCB1Value u2 = new UCB1Value();
		
		if (result == 1) {
			u2.increaseNumberOfWins();
		}

		u2.increaseMyTotalCount();
		
		double score = u2.getNumberOfWins()/u2.getMyTotalCount() + Math.sqrt(2* Math.log10(u2.getMyTotalCount()));
		u2.updateUCBscore(score);
	
//		score.add(new Result(result, 1));
		UCBscore.add(u2);
	}

	public static void updateUCBValue_RandomPlayout_2(double result, ArrayList<UCB1Value> UCBscore) {
		UCB1Value u3 = new UCB1Value();
		
		u3.increaseMyTotalCount();
		
		double score_avg = 0.0;
		score_avg = score_avg * u3.getMyTotalCount()/(u3.getMyTotalCount()+1) + result/(u3.getMyTotalCount()+1);
		u3.updateUCBscore(score_avg);
		UCBscore.add(u3);
		
	}
	
	public static int randomPlayout_1(ChineseCheckersState s1, double epsilon, int currentPlayer) {
		
		ArrayList<Move> moves1 = new ArrayList<Move>();
		s1.getMoves(moves1);
		
		while(!s1.gameOver()) {
			Random rand = new Random(System.currentTimeMillis());
			if (rand.nextDouble() < 0.1) {
				//randomly pick a move from the list of moves
				int i = rand.nextInt(moves1.size());
				Move mv = moves1.get(i);
				s1.applyMove(mv);
			//	randomPlayout_1(s1, 0.1, currentPlayer);
			}
			else { //90% of the time, pick the move that makes the most fwd progress
				Move mv2 = getMostFwdMove(s1, moves1);
				s1.applyMove(mv2);
			//	randomPlayout_1(s1, 0.1, currentPlayer);
			}
		}
		
		if(s1.winner()==currentPlayer) {
			return 1;
		}
		
		return 0;
	}
	
	public static double randomPlayout_2(ChineseCheckersState s2, double epsilon, int depth, int currentPlayer) {
		
		
		ArrayList<Move> moves2 = new ArrayList<Move>();
		s2.getMoves(moves2);
		
		while(!s2.gameOver() && depth >0) {
			Random rand = new Random(System.currentTimeMillis());
			if (rand.nextDouble() < 0.1) {
				//randomly pick a move from the list of moves
				int i = rand.nextInt(moves2.size());
				Move mv = moves2.get(i);
				s2.applyMove(mv);
			//	randomPlayout_2(s2, 0.1, depth, currentPlayer);
			}
			else {
				Move mv2 = getMostFwdMove(s2, moves2);
				s2.applyMove(mv2);
			//	randomPlayout_2(s2, 0.1, depth, currentPlayer);
			}
			depth--;
		}
		
		EvaluationFunction ef = new SecondEval();
		return ef.eval(s2, currentPlayer);
		
	}
	
	
	public static Move getMostFwdMove(ChineseCheckersState cloneOfCurrentState, ArrayList<Move> moves) {
		  
		//The following code is HW1, return the move that moves the most difference in hex-rows	  
		    int selected = 0;

			int diff_max_1 = 0;
			int diff_max_2 = 0;
			
			cloneOfCurrentState.getMoves(moves);
			Collections.shuffle(moves);
			
			    for (int i=0; i<moves.size(); i++) {
			    
			    	int hex_row1 = moves.get(i).from / 9 + moves.get(i).from % 9; //hex row index is row index + col index
			    	int hex_row2 = moves.get(i).to / 9 + moves.get(i).to % 9;
			    	
			    	if (cloneOfCurrentState.currentPlayer == 1) {
			    		int diff_1 = hex_row2 - hex_row1;
			    		if (diff_1 >= diff_max_1) {
			    			diff_max_1 = diff_1;
			    			selected = i;
			    		}
			    	}
			    	
			    	if (cloneOfCurrentState.currentPlayer == 2) {
			    		int diff_2 = hex_row1 - hex_row2;
			    		if (diff_2 >= diff_max_2) {
			    			diff_max_2 = diff_2;
			    			selected = i;
			    		}
			    	}
			    } //end of for loop	
		    return moves.get(selected);
		 
		    //return moves.get(0); //pick the first one, but we want to pick the move with the most forward progress
	}
	
}
