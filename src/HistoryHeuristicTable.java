import java.util.Comparator;

public class HistoryHeuristicTable implements Comparator<Move>{
	private static int historyHeuristic[][] = new int[81][81];
	
	public HistoryHeuristicTable() {

		for (int i=0; i<81; i++) {
			for (int j=0; j<81; j++) {
				historyHeuristic[i][j] = 0; 
			}
		}
	}


	public int getHistoryHeuristic(Move m) {
		return historyHeuristic[m.from][m.to];	
	}

	public void updateHH_abCutOff(Move m, int depth) {
		try {
		historyHeuristic[m.from][m.to] += Math.pow(2, depth);
		} catch (Exception e)
		{
			System.err.println("Failed on move " + m.from + " to " + m.to);
		}
	}
	
	public void updateHH_selectMove(Move m, int depth) {
		
		try {
			historyHeuristic[m.from][m.to] += depth*depth;
			} catch (Exception e)
			{
				System.err.println("Failed on move " + m.from + " to " + m.to);
			}
	}
	

	@Override
	public int compare(Move lhs, Move rhs) {
		//since we want to sort the moves in decreasing order based on the HH values	
		// 1 if lhs < rhs
		// 0 zero if equal
		// -1 lhs > rhs
		
		if (historyHeuristic[lhs.from][lhs.to] < historyHeuristic[rhs.from][rhs.to])
			return 1;
		if (historyHeuristic[lhs.from][lhs.to] > historyHeuristic[rhs.from][rhs.to])
			return -1;
		
		return 0;
	}
}
