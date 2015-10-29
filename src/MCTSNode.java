
public class MCTSNode {
	
	//all the info the nodes need to store
	public double UCBValue;
	public int numChildren;
	public int childrenStartAtIndex;
	public int numSamples;
	public int myParentIndex;
	public double totalPayoff;
	public Move prev_m;
	public Move myMove;
	
	public MCTSNode () {
		UCBValue = 0.0;
		numChildren = 0;
		childrenStartAtIndex = 0;		
		numSamples = 0;
		totalPayoff = 0.0;
		prev_m = new Move(0,0);
		myMove = new Move(0,0);
	}
	
//	public void updateState(MCTSNode parent) {
//		//initialize the child Node's current state, should be the state after apply the move in parent node
//		parent.currentState.applyMove(parent.myMove);
//		currentState.loadState(parent.currentState.dumpState());
//		parent.currentState.undoMove(parent.myMove);
//	//currentState = MCTS.MonteCarloTree.get(myParentIndex).currentState;
//	}
	
}
