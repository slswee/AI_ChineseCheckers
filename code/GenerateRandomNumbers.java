import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.util.Random;

public class GenerateRandomNumbers {

	public static void main(String[] args) throws FileNotFoundException {
			
		int numOfBoardPositions = 81;
		int numVal = 3; //each board position can be 0, 1 or 2
			
		PrintWriter myWriter = new PrintWriter("src/RandomNumbers.java");
		myWriter.println("public class RandomNumbers {");
		myWriter.println("public static long randomNumbers[] = { ");
		Random myNumber = new Random();
		
		for (int i = 0; i < numOfBoardPositions; i++) {
			for (int j = 0; j < numVal; j++){
				myWriter.println(myNumber.nextLong() + "l,");
			}
		}
		
		myWriter.println("};");
		myWriter.println("}");
		myWriter.close();
	}	
	
}
