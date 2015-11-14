/* Sally Li 
 * COMP4703-HW1
 * 4/2/2015 */
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Agent {
  public Agent() {
    name = "Sally";
    stdin = new Scanner(System.in);
  }


  public Move myOpeningMoves[] = OpeningBook.openingMoves;
  
  
  public void playGame() {
    // Identify myself
    System.out.println("#name " + name);
    System.out.flush();

    // Wait for start of game
    waitForStart();

    // Main game loop
    for (;;) {
      if (current_player == my_player) {
        // My turn
    	  
        // Check if game is over
        if (state.gameOver()) {
          System.err.println("I, " + name + ", have lost");
          System.err.flush();
          switchCurrentPlayer();
          continue;
        }

        // Determine next move
        Move m = nextMove();

        // Apply it locally
        state.applyMove(m);

        // Tell the world
        printAndRecvEcho(m.toString());

        // It is the opponents turn
        switchCurrentPlayer();
      } else {
        // Wait for move from other player
        // Get server's next instruction
        String server_msg = readMessage();
        String[] tokens = server_msg.split(" ");

        //if (tokens.length == 5 && tokens[0] == "MOVE") {
        if (isValidMoveMessage(tokens)) {
          // Translate to local coordinates and update our local state
          Move m = state.translateToLocal(tokens);
          state.applyMove(m);

          // It is now my turn
          switchCurrentPlayer();
        } else if (tokens.length == 4 && tokens[0].equals("FINAL") &&
                   tokens[2].equals("BEATS")) {
          // Game over
          if (tokens[1].equals(name) && tokens[3].equals(opp_name)) {
            System.err.println("I, " + name + ", have won!");
            System.err.flush();
          } else if (tokens[3].equals(name) && tokens[1].equals(opp_name)) {
            System.err.println("I, " + name + ", have lost.");
            System.err.flush();
          } else {
            System.err.println(
                "Did not find expected players in FINAL command.\n"
                + "Found '" + tokens[1] + "' and '" + tokens[3] + "'. "
                + "Expected '" + name + "' and '" + opp_name + "'.\n"
                + "Received message '" + server_msg + "'");
            System.err.flush();
          }
          break;
        } else {
          // Unknown command
          System.err.println("Unknown command of '" + server_msg +
                             "' from the server");
          System.err.print("Tokens: ("+ tokens.length +")");
          for (String s : tokens)
            System.err.print("'" + s + "' ");
          System.err.print("\n");
          System.err.flush();
        }
      }
    }
  }

  private Move nextMove() {


//	  return Searches.iterativeDeepening(state, new SecondEval(), (my_player == Players.player1) ? 1 : 2, 1); 
	  
	  /* The following code is for opening book
	   * 
	   */
	  
	  //need to check if within 4 moves
	 
	 
	  if (state.getTurnCount()/2 <4 && state.currentPlayer == 1) {
		  return myOpeningMoves[state.getTurnCount()/2];
	  }
	  if (state.getTurnCount()/2 <4 && state.currentPlayer == 2) {
		  Move turnBoardMove = new Move (0,0);
		  turnBoardMove.from = 80 - myOpeningMoves[state.getTurnCount()/2].from;
		  turnBoardMove.to = 80 - myOpeningMoves[state.getTurnCount()/2].to;
		  return turnBoardMove;
	  }
	  
	  
	  
	  //do I wanna reset HH for every depth? this should be done in Agent, as HH Table needs to passed in. 
	
	  //The following code is for HW3 and 4	  Searches.cutOffCount = 0;
	  return Searches.abPruning(state, new SecondEval(), state.currentPlayer, 10);
  
  } //end of nextMove()
  
  
  
  // Sends a msg to stdout and verifies that the next message to come in is it
  // echoed back. This is how the server validates moves
  private void printAndRecvEcho(String msg) {
    System.out.println(msg);
    System.out.flush();

    String echo_recv = readMessage();
    if (!msg.equals(echo_recv)) {
      System.err.println("Expected echo of '" + msg + "'. Received '" +
                         echo_recv + "'");
      System.err.flush();
    }
  }

  // Reads a line, up to a newline from the server
  private String readMessage() { return stdin.nextLine().trim(); }

  private String[] tokenize(String s) { return s.split(" "); }

  private void waitForStart() {
    for (;;) {
      String response = readMessage();
      String[] tokens = tokenize(response);

      if (tokens.length == 4 && tokens[0].equals("BEGIN") &&
          tokens[1].equals("CHINESECHECKERS")) {
        // Found BEGIN GAME message, determine if we play first
        if (tokens[2].equals(name)) {
          // We go first!
          opp_name = tokens[3];
          my_player = Players.player1;
          break;
        } else if (tokens[3].equals(name)) {
          // They go first
          opp_name = tokens[2];
          my_player = Players.player2;
          break;
        } else {
          System.err.println(
              "Did not find '" + name + "', my name, in the BEGIN command.\n"
              + "# Found '" + tokens[2] + "' and '" + tokens[3] + "'"
              + " as player names. Received message '" + response + "'");
          System.err.flush();
          System.out.println("#quit");
          System.out.flush();
        }
      } else if (response.equals("DUMPSTATE")) {
        System.out.println(state.dumpState());
        System.out.flush();
      } else if (tokens[0].equals("LOADSTATE")) {
        String newState = response.substring(10);
        if (!state.loadState(newState)) {
          System.err.println("Unable to load '" + newState + "'");
          System.err.flush();
        }
      } else if (response.equals("LISTMOVES")) {
        ArrayList<Move> moves = new ArrayList<Move>();
        state.getMoves(moves);
        for (Move m : moves) {
          System.out.print(m.from + ", " + m.to + "; ");
        }
        System.out.print("\n");
        System.out.flush();
      }  else if (tokens[0].equals("MOVE")) {
          Move m = state.translateToLocal(tokens);
          if (!state.applyMove(m)) {
            System.err.println("Unable to apply move " + m);
            System.err.flush();
          }
        } else if (tokens[0].equals("UNDO")){
      	  tokens[0] = "MOVE";
      	  Move m = state.translateToLocal(tokens);
      	  if(!state.undoMove(m)){
      		  System.out.println("Unable to undo move" + m );
      	  }
        } else if (response.equals("NEXTMOVE")){
      	  Move m = nextMove();
      	  System.out.println(m.from + ", " + m.to);
        } else if (response.equals("EVAL")) {
        	EvaluationFunction g = new SecondEval();
      	  System.out.println(g.eval(state, state.currentPlayer));
        } else {
          System.err.println("Unexpected message '" + response + "'");
          System.err.flush();
        }
      }
/*
 else if (tokens[0].equals("MOVE")) {
        Move m = state.translateToLocal(tokens);
        if (!state.applyMove(m)) {
          System.err.println("Unable to apply move " + m);
          System.err.flush();
        }
      } else if (tokens[0].equals("UNDO")){
    	  tokens[0] = "MOVE";
    	  Move m = state.translateToLocal(tokens);
    	  if(!state.undoMove(m)){
    		  System.out.println("Unable to undo move" + m );
    	  }
      } else if (response.equals("NEXTMOVE")){
    	  Move m = nextMove();
    	  System.out.println(m.from + ", " + m.to);
      } else if (response.equals("EVAL")) {
    	  System.out.println(Evaluation(state,state.getCurrentPlayer()));
      } else {
        System.err.println("Unexpected message '" + response + "'");
        System.err.flush();
      }
 * */
    // Game is about to begin, restore to start state in case
    // DUMPSTATE/LOADSTATE/LISTMOVES
    // were used
    state.reset();

    // Player 1 goes first
    current_player = Players.player1;
  }

  private void switchCurrentPlayer() {
    current_player = (current_player == Players.player1) ? Players.player2 : Players.player1;
  }

  private boolean isValidStartGameMessage(String[] tokens) {
    return tokens.length == 4 && tokens[0].equals("BEGIN") &&
        tokens[1].equals("CHINESECHECKERS");
  }
  private boolean isValidMoveMessage(String[] tokens) {
    return tokens.length == 5 && tokens[0].equals("MOVE") &&
        tokens[1].equals("FROM") && tokens[3].equals("TO");
  }

  private ChineseCheckersState state = new ChineseCheckersState();
  private enum Players { player1, player2 }
  private Players current_player;
  private Players my_player;
  private String name;
  private String opp_name;
  private Scanner stdin;
  
  public Players getCurrentPlayer() {
	  return current_player;
  }

}
