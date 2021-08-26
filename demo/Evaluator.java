package demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 *  _____________________________________________
 *  Rules for determining true or false or errors
 *  _____________________________________________
 *  
 * 	1] &, |, - can't be present same one in a row. ex) && or || -> false.
 *	2] &, |  - must be in the middle of true and false
 *	3] &, |  - can't be present mixed |& &|
 *	4] !     - must be present before true or false
 *	5] (     - In present, must prior to any until ) shows up
 *  6] Anything not able to handle, throws an exception instead of returns false;
 *  
 * @author hyungjoonkim
 *
 */
public class Evaluator implements IEvaluator {

	public boolean evaluate(String input) throws SyntaxException {

		final char[] TO_BE_CONSIDERED = {'&', '|', '!', '(', ')', 't', 'f'};
		
		String inputTrimmed = input.replaceAll("\\s+","");

		return processInput(inputTrimmed, TO_BE_CONSIDERED);
	}
	
	//Recursive approaches due to the parenthesis, which can be many opens / closes
	//Always goes from most inner one.
	private boolean processInput(String trimmedInput, char[] TO_BE_CONSIDERED) throws SyntaxException {

		char[] input = trimmedInput.toCharArray();
		
		List<Integer> inputList = new ArrayList<Integer>();
		
		for (int i = 0; i < input.length; i++) {
			
			//Filters out only required characters 
			if (input[i] != TO_BE_CONSIDERED[0] &&
					input[i] != '&' &&
					input[i] != '|' &&
					input[i] != '!' &&
					input[i] != '(' &&
					input[i] != ')' &&
					input[i] != 't' &&
					input[i] != 'r' &&
					input[i] != 'u' &&
					input[i] != 'e' &&
					input[i] != 'f' &&
					input[i] != 'a' &&
					input[i] != 'l' &&
					input[i] != 's' &&
					input[i] != 'e')
				throw new SyntaxException("Wrong Expression -" + trimmedInput);
				
			for (char tbc : TO_BE_CONSIDERED) {
				
				if (tbc == input[i]) {
					
					if (tbc == '&' || tbc == '|' ||tbc == '!' || tbc == '(' || tbc == ')') {
						
						if (tbc == '(') {
							
							inputList.add(i);
							
						}
						
						if (tbc == ')') {
							
							try {
								
								int from = inputList.get(inputList.size() - 1);
								
								boolean result = evaluateExpression(buildEvaluationInput(TO_BE_CONSIDERED, Arrays.copyOfRange(input, from, i + 1)));
								
								trimmedInput = trimmedInput.substring(0, from) + result + trimmedInput.substring(i + 1);
								inputList.remove(inputList.size() - 1);
								
								return processInput(trimmedInput, TO_BE_CONSIDERED);
								
							} catch (java.lang.IndexOutOfBoundsException e) {
								
								 throw new SyntaxException("Wrong Expression -" + trimmedInput);
								 
							}
							
															
						}							
					}
				}
			}
		}
		
		boolean result = evaluateExpression(buildEvaluationInput(TO_BE_CONSIDERED, trimmedInput.toCharArray()));

		return result;
	}
	
	private List<String> buildEvaluationInput(char[] TO_BE_CONSIDERED, char[] input) throws SyntaxException {
		
		List<String> preparedInput = new ArrayList<String>();
		
		for (int i = 0; i < input.length; i++) {

			for (char tbc : TO_BE_CONSIDERED) {
				
				if (tbc == input[i]) {

					if (tbc == '&' || tbc == '|' ||tbc == '!' || tbc == '(' || tbc == ')') {

						preparedInput.add(String.valueOf(tbc));							

					} else if (tbc == 't' && input[i + 1] == 'r' && input[i + 2] == 'u' && input[i + 3] == 'e' ) {

						preparedInput.add("true");

					} else if (tbc == 'f' && input[i + 1] == 'a' && input[i + 2] == 'l' && input[i + 3] == 's' && input[i + 4] == 'e' ) {

						preparedInput.add(String.valueOf("false"));

					}
				}
			}
		}		
		
		return preparedInput;
	}
	
	private boolean evaluateExpression (List<String> preparedInput) throws SyntaxException {

		int parenthesis = 0;
		int flip = 0;
		int cont = 0;
		boolean result = false;
		boolean safeToGoBack = false;
		
		for (int i = 0; i < preparedInput.size(); i++ ) {

			if (cont > 0) {
				cont = 0;
				continue;
			}

			if (preparedInput.get(i).equals("(")) {
				parenthesis++;
			} else if (preparedInput.get(i).equals(")")) {
				parenthesis--;
			} else if (preparedInput.get(i).equals("&") || preparedInput.get(i).equals("|")) {
				
				int index = 1;
				if (i < preparedInput.size() - 1 && preparedInput.get(i + 1).equals("!")) {
					index = 2;
				}
				
				if (i == 0 || i == preparedInput.size() -1 || preparedInput.get(i - 1).equals("(") || preparedInput.get(i + 1).equals(")")) {

					throw new SyntaxException("Wrong Expression -" + preparedInput);

				} else if (preparedInput.get(i).equals("&")) {

					if (result && preparedInput.get(i + index).equals("true")) {

						result = true;
						cont++;

					} else if (result && preparedInput.get(i + index).equals("false")) {

						result = false;
						cont++;

					} else if (!result && preparedInput.get(i + index).equals("true")) {

						result = false;
						cont++;

					} else if (!result && preparedInput.get(i + index).equals("false")) {

						result = false;
						cont++;
					}
					
				} else {
					
					if (result || preparedInput.get(i + index).equals("true")) {

						result = true;
						result = simpleFlip(result, flip);
						cont++;

					} else if (result || preparedInput.get(i + index).equals("false")) {;

						result = true;
						cont++;
						result = simpleFlip(result, flip);

					} else if (!result || preparedInput.get(i + index).equals("true")) {

						result = true;
						cont++;
						result = simpleFlip(result, flip);

					} else if (!result || preparedInput.get(i + index).equals("false")) {

						result = false;
						cont++;
						result = simpleFlip(result, flip);

					}

				}
			} else if (preparedInput.get(i).equals("!")) {

				if (i == preparedInput.size() -1 || preparedInput.get(i + 1).equals(")") || preparedInput.get(i + 1).equals("&") || preparedInput.get(i + 1).equals("|")) {

					throw new SyntaxException("Wrong Expression -" + preparedInput);

				} else {

					flip++;
					safeToGoBack = true;

				}
					
			} else if (preparedInput.get(i).equals("true")) {

				result = true;
				result = simpleFlip(result, flip);

			} else if (preparedInput.get(i).equals("false")) {

				result = false;
				result = simpleFlip(result, flip);

			} else {
				
				throw new SyntaxException("Wrong Expression -" + preparedInput);

			}
		}

		if (parenthesis != 0) {

			throw new SyntaxException("Wrong Expression -" + preparedInput);

		}
		
		return result;
	}

	private boolean simpleFlip(boolean result, int flip) {

		if (flip > 0) {
			for (int j = 0; j < flip; j++) 
				result = !result;
			flip = 0;
		}

		return result;
	}

}
