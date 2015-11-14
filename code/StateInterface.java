import java.util.ArrayList;

public interface StateInterface {
		
  public long getHash();
  public void getMoves(ArrayList<Move> moves);
  public void getFwdMoves(ArrayList<Move> moves);
  public boolean applyMove(Move m);
  public boolean undoMove(Move m);
  public boolean gameOver();
}
