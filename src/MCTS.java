import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;


public class MCTS {
	
	public static ArrayList<MCTSNode> MonteCarloTree = new ArrayList<MCTSNode>();
	
	//method that is similar to iterative deepening, with time limit
	public static Move MCTSBestMove(ChineseCheckersState s, int time){
		
		MCTSNode rootNode = new MCTSNode();//root node has no parent
		Move myBestMove = new Move(-2,-2);
		rootNode.myParentIndex = 0; //set rootNode parent index to be itself
		MonteCarloTree.add(rootNode);
		
		AtomicBoolean timeUp = new AtomicBoolean(false);
		Alarm a = new Alarm(time, timeUp);
		a.start();
		  
		//recursively call GetBestMove() on the root until time runs out
	
		while (!timeUp.get()) {	
			myBestMove = getBestMove(s, rootNode);  
		}  
		
		return myBestMove;
	}
	
	//the high level function for computing the best move
	public static Move getBestMove(ChineseCheckersState s, MCTSNode aNode){
		Move m = new Move(-3, -3);
		//if leaf
		if(isLeaf(aNode)){
			//expand
			expand(s, aNode);	
			//playout each child
			int UCBSum = 0;
		
			for (int i=0; i<aNode.numChildren; i++) {
				MCTSNode currentChild = MonteCarloTree.get(aNode.childrenStartAtIndex + i);
				currentChild.totalPayoff= doPlayout(s, currentChild);
				currentChild.numSamples++;
				UCBSum += currentChild.UCBValue;
				aNode.numSamples++;
				updateUCB(currentChild);
			}
			//update score on node itself
			//aNode.UCBValue = UCBSum/aNode.numChildren;
			updateUCB(aNode);
		}
		else {
			//select best child
			MCTSNode bestChild = selectBestChild(aNode);
			//recurse
			s.applyMove(bestChild.myMove); 
			m = getBestMove(s, bestChild); //getBestMove should return a score, and this score should be returned
			s.undoMove(bestChild.myMove);
			
			//update score/move	for every node in the path //backpropogation		
			updateUCB(aNode);
	
		}	
		
		return m;	
	}
	
	//traverse down the tree (recursively), returning the value at the leaf of the sample
//	double selectLeaf(MCTSNode node){
//		
//	}

	// Use the UCB rule to find the best child
	static MCTSNode selectBestChild(MCTSNode aNode){
		//initialize bestChild to be the first child of the parent node
		MCTSNode bestChild = MonteCarloTree.get(aNode.childrenStartAtIndex);
		double tempUCBValue = bestChild.UCBValue;
		
		for (int i = 0; i<aNode.numChildren; i++) {
			MCTSNode currentChild = MonteCarloTree.get(aNode.childrenStartAtIndex + i);
			if (currentChild.UCBValue > tempUCBValue) {
				bestChild = currentChild;
			}				
		}
		return bestChild;
	}
	
	//update a node's UCB score after random playout
	static void updateUCB (MCTSNode aNode) {
		double score_avg = 0.0;
		score_avg = aNode.totalPayoff/(aNode.numSamples+1);
		aNode.UCBValue = score_avg + Math.sqrt(Math.log10(2*MonteCarloTree.get(aNode.myParentIndex).numSamples/aNode.numSamples));
	}
	
	// Get the UCB value of a given node
//	double getUCBVal(MCTSNode node, MCTSNode parent){
//	//	double score_avg = 0.0;
//	//	score_avg = score_avg * u3.getMyTotalCount()/(u3.getMyTotalCount()+1) + result/(u3.getMyTotalCount()+1);
//	}
//	
	 // is the designated node a leaf
	static boolean isLeaf(MCTSNode aNode){
		return aNode.numChildren==0;
	}
	
	// expand the designated node and add its children to the tree
	static void expand(ChineseCheckersState s, MCTSNode aNode){
		ArrayList<Move> moves = new ArrayList<Move>();
		s.getMoves(moves);
		aNode.numChildren = moves.size();
		aNode.childrenStartAtIndex = MonteCarloTree.size();
		//add its children to the tree
		for (int i=0; i<moves.size(); i++) {
			MCTSNode childNode = new MCTSNode();
			MonteCarloTree.add(childNode);
			childNode.myParentIndex = MonteCarloTree.indexOf(aNode);
		}
	}
	
	// play out the game, returning the evaluation at the end of the game
	static double doPlayout(ChineseCheckersState s, MCTSNode aNode){
		
		//epsilon 0,1, depth 40
		return UCB1.randomPlayout_2(s, 0.1, 40, s.currentPlayer);
		
	}
	
//	// return the UCB value of a given node
//	double GetUCBVal(MCTSNode node, MCTSNode parent) {
//		
//		
//		
//		return 0.0;
//	}
	
}
