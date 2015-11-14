
public class DecrementingEval implements EvaluationFunction {

	static double b= 0.0;
	@Override
	public double eval(StateInterface s, int currPlayer) {
		
		return b--;
	}
}
