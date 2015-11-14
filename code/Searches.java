import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.HashMap;

public class Searches {
	
	public static int cutOffCount = 0;
	public static HashMap<Long, TTEntry> zobristHash = new HashMap<Long, TTEntry>();

	public static Move abPruning (ChineseCheckersState state, EvaluationFunction ef, int myP, int time) {
			
		 //need HH object
		  HistoryHeuristicTable myHHTable = new HistoryHeuristicTable();
		
		  AtomicBoolean timeUp = new AtomicBoolean(false);
		  Alarm a = new Alarm(time, timeUp);
		  a.start();
		  Move lastCompletedMove = null;
		 
		  zobristHash.clear();
		  for (int depth = 1; ; ++depth) {
			  
		//	  System.err.println("Starting iterative deepening to depth "+depth);

			  CurrentBestMove temp = alphaBetaPruning(state, ef, myP, depth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, true, timeUp, myHHTable); 
			  
			  if (temp.score>10000) {
				  
				  System.err.println(temp.toString());
				  return temp.m;
			  }
			  
			  if (timeUp.get())
				  return lastCompletedMove;
			  lastCompletedMove = temp.m;
		  }
	
	  }
	
	public static boolean ttOn = false;
	public static CurrentBestMove alphaBetaPruning(ChineseCheckersState currentState, EvaluationFunction ef, int myPlayer, int depth, double alpha, double beta, boolean maximizingPlayer, AtomicBoolean timeUp, HistoryHeuristicTable myHHTable) {
		
		CurrentBestMove bestMove = new CurrentBestMove();
		if (depth == 0 || timeUp.get() || currentState.gameOver()) {
			
//			if (depth==0){
//				System.err.println("depth==0");
//			}
//			if (timeUp.get()) {
//				System.err.println("timer is up");
//			}
//			if (currentState.gameOver()) {
//				System.err.println("game over");
//			}
//			
			  bestMove.score = ef.eval(currentState, myPlayer); //currentState.currentPlayer);
			//  System.err.println(bestMove.toString());
			  return bestMove;
		}
		
		
		//if (is in TT)
		if (ttOn==true) {
			TTEntry ttEntry = zobristHash.get(currentState.getHash());

			if (ttEntry != null) { 	
					//if (debug transposition table) 
					//recalculate the value we looked up, and double check it matches		
					boolean debug = true;			
					if(debug){		
						ttOn = false;
						Move temp2 = alphaBetaPruning(currentState, ef, myPlayer, ttEntry.tt_depth, ttEntry.tt_alpha, ttEntry.tt_beta, maximizingPlayer, timeUp, myHHTable).m;
						if(temp2.equals(ttEntry.tt_move)) {
							System.err.println("TT is working correctly.");
						}
						else {
							System.err.println("The calcalated move is " + temp2);
							System.err.println("The entry in TT is "+ ttEntry.tt_move);
							System.err.println("......");
						}
						ttOn = true;
					}
					//if assumptions are valid (depth, alpha, beta)
					if (depth <= ttEntry.tt_depth && alpha <= ttEntry.tt_alpha && beta >= ttEntry.tt_beta) {		
						bestMove.score = ttEntry.tt_score;
						bestMove.m = ttEntry.tt_move;
						return bestMove;
					}
				}
		
		} 
		//if transposition table doesn't have entry 
		
			  if (maximizingPlayer) {
				  ArrayList<Move> moves = new ArrayList<Move>();
				  currentState.getFwdMoves(moves);
		  
				  // Move ordering by History Heuristic
				  //comment out the following line to test if history heuristic works or not
				  Collections.sort(moves, myHHTable);
			  
				  bestMove.score = Double.NEGATIVE_INFINITY;
				 
				  for (Move m : moves) {  //the moves are sorted 			  
					  					  
					  currentState.applyMove(m);	  
					  CurrentBestMove tmp;
					  if(moves.indexOf(m) > 0 ) { //if it's not the first node, then we do zero-window search it
						  tmp = alphaBetaPruning(currentState, ef, myPlayer, depth-1, beta-1, beta, false, timeUp, myHHTable);
						  if (alpha< tmp.score && tmp.score < beta) { //if it doesn't fail, then do a regular alpha-beta search
							  tmp = alphaBetaPruning(currentState, ef, myPlayer, depth-1, alpha, beta, false, timeUp, myHHTable); 
						  }
						  
					  }			  
					
					  else {
						  tmp = alphaBetaPruning(currentState, ef, myPlayer, depth-1, alpha, beta, false, timeUp, myHHTable);
					  }
					  
					  if(tmp.score > bestMove.score) {
						  bestMove.score = tmp.score;
						  bestMove.m = m;
					  }
					  
					  currentState.undoMove(m);
					  
					  alpha = Math.max(alpha, bestMove.score);
					  if (beta <= alpha) {					  
						  myHHTable.updateHH_abCutOff(bestMove.m, depth); //give it a big value
						  cutOffCount++;
						  break;
					  }
				  }	  
				  //return bestMove;  
			  }
			  else {
				  bestMove.score = Double.POSITIVE_INFINITY;
				  ArrayList<Move> movesSet2 = new ArrayList<Move>();
				  currentState.getFwdMoves(movesSet2);
				  
				  Collections.sort(movesSet2, myHHTable);
				  
				  for (Move m : movesSet2) {
					  currentState.applyMove(m);
					  
					  CurrentBestMove tmp;
					  tmp = alphaBetaPruning(currentState, ef, myPlayer, depth-1, alpha, beta, true, timeUp, myHHTable);
					  
					  if(tmp.score < bestMove.score) {
						  bestMove.score = tmp.score;
						  bestMove.m = m;
					  }
					  
					  currentState.undoMove(m);
					  beta = Math.min(beta, bestMove.score);
					  if (beta <= alpha) {
						//  System.err.println("Cutoff because beta <= alpha at depth = " + depth);
						  
						  myHHTable.updateHH_abCutOff(bestMove.m, depth);//give it a big value
						  cutOffCount++;
						  break;
					  }
				  }
			  }
			  
			  //insert into Transposition table the value we just calculated
			 if (ttOn == true) { 
				  TTEntry ttEntry = new TTEntry();
				  ttEntry.tt_alpha = alpha;
				  ttEntry.tt_beta = beta;
				  ttEntry.tt_depth = depth;
				  ttEntry.tt_move = bestMove.m;
				  ttEntry.tt_score = bestMove.score;
				  zobristHash.put(currentState.getHash(), ttEntry);
			 }
			 //udpate HH here for selecting this move
			 
		//	 System.err.println("Cut off " + cutOffCount);
			 myHHTable.updateHH_selectMove(bestMove.m, depth);
			 
////original to do list		  
		  //check transposition table 
/*		  
		  if (is in transposition table) {
			  if (debug transposition table)
				  recalculate the value we looked up, and double check it matches
			  
			  if (assumptions still valid (depth, alpha, beta, etc))
				  return lookup value
		  }
*/		  
		  return bestMove; 
	  }

	  
	  //HW2, minimax nextMove2 

	  public static Move iterativeDeepening (ChineseCheckersState state, EvaluationFunction ef, int myPlayer, int time) {
		
		  AtomicBoolean timeUp = new AtomicBoolean(false);
		  Alarm a = new Alarm(time, timeUp);
		  a.start();
		  Move lastCompletedMove = new Move(-1,-1);
		  
		  for (int depth = 1; ; ++depth) {
	//		  System.err.println("Starting iterative deepening to depth "+depth); 
			  Move temp = maxPlayer(state, ef, myPlayer, depth, timeUp, true).m;
			  if (timeUp.get())
			  {
				  return lastCompletedMove;
			  }
			  lastCompletedMove = temp;
		  }
	  }
	  
	  
	  public static CurrentBestMove maxPlayer(ChineseCheckersState currentState, EvaluationFunction ef, int myPlayer, int depth, AtomicBoolean timeUp, boolean debug) {
		  CurrentBestMove bestMove = new CurrentBestMove();
		  if (depth == 0 || timeUp.get() || currentState.gameOver()) {
			  bestMove.score = ef.eval(currentState, myPlayer); //currentState.currentPlayer);
			  return bestMove;
		  }
		  ArrayList<Move> moves = new ArrayList<Move>();
		  currentState.getMoves(moves);
		  
		  bestMove.score = Double.NEGATIVE_INFINITY;
		  
		  for (Move m : moves) {
			  
			  currentState.applyMove(m);

			  CurrentBestMove temp = minPlayer(currentState, ef, myPlayer, depth-1, timeUp);
//			  if (debug)
//			  {
//				  System.err.print(m+" score: "+Math.round(temp.score*100)/100+" ");
//			  }
			  if (temp.score > bestMove.score) {
				  bestMove.score = temp.score;
				  bestMove.m = m;
//				  if (debug)
//					  System.err.print("(new best)");
			  }
			  
		//	  if (debug) System.err.println("");
			  
			  currentState.undoMove(m);
			  
		  } //end of for 
	//	  if (debug) System.err.println("Returning " + bestMove);
		  return bestMove;
	 }

	  public static CurrentBestMove minPlayer(ChineseCheckersState currentState, EvaluationFunction ef, int myPlayer, int depth, AtomicBoolean timeUp) {
		  CurrentBestMove bestMove = new CurrentBestMove();
		  if (depth == 0 || timeUp.get() || currentState.gameOver()) {
			  bestMove.score = ef.eval(currentState, myPlayer); //currentState.currentPlayer);
			  return bestMove;
		  }
		  ArrayList<Move> moves = new ArrayList<Move>();
		  currentState.getMoves(moves);
		  
		  bestMove.score = Double.POSITIVE_INFINITY;
		  
		  for (Move m : moves) {
			  currentState.applyMove(m);

			  CurrentBestMove temp = maxPlayer(currentState, ef, myPlayer, depth-1, timeUp, false);
			  if (temp.score < bestMove.score) {
				  bestMove.score = temp.score;
				  bestMove.m = m;
			  }
			  currentState.undoMove(m);
					  
		  }
		    
		  return bestMove;
	  }
	  


}
