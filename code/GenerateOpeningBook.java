import java.io.PrintWriter;
import java.io.FileNotFoundException;

public class GenerateOpeningBook {

	public static void main(String[] args) throws FileNotFoundException{
					
			PrintWriter myWriter = new PrintWriter("src/OpeningBook.java");
			
			myWriter.println("public class OpeningBook {");
			
			
			myWriter.println("public static Move firstMove = new Move(3,12);");
			myWriter.println("public static Move secondMove = new Move(1, 21);");
			myWriter.println("public static Move thirdMove = new Move(18, 22);");
			myWriter.println("public static Move fourthMove = new Move(22, 31);");
			

			
			myWriter.println("public static Move openingMoves[] = {");
			myWriter.println("	firstMove,"); // this is a player 1 move
			myWriter.println("	secondMove,");
			myWriter.println("	thirdMove,");
			myWriter.println("	fourthMove");
			
			
			myWriter.println("};");
			myWriter.println("}");
			myWriter.close();
	}		

}







