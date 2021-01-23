import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Stack;

/**
 * @version 3
 * @date 31-Dec-2020
 * @name EmLang(Em for Mathematics)
 * @purpose language for compilation of mathematical expressions
 */
public class ExpressionCalc {
	double eval;
	private void display(String[] exp) {
		System.out.print("   ");
		for(String out: exp) {
			System.out.print(out + " ");
		}
		System.out.println();
	}
	/**
	 * Returns next index of the element passed. The function is such that it
	 * will return the next matching closing brace only during case "(" in
	 * function evaluate().
	 * @param from 
	 * 		The position from which searching will take place
	 * @param searchOnly
	 * 		If true the function only searches for the element. If false,
			the function checks for matching paranthesis. False mode is 
			used by the method simplify().
			@see simplify()For more details
	 */
	private int nextElement(String[] array, String element, int from, boolean searchOnly) {
		// Counts opened braces
		int openingBraceCount = 0;
		/*
		 * The 'from + 1' ensures that the method doesn't return back the index
		 * of the element whose index we sent in as 'from'
		 */
		for(int i = from; i < array.length; i++) {
			// If a brace opens it is counted
			if(array[i].equals("(")& !searchOnly) {
				openingBraceCount++;
			}
			/*
			 * If a brace closes, a count is removed. If count is zero it means
			 * the current closing brace is the brace that closes our current
			 * scope.
			 */
			else if(array[i].equals(")")& openingBraceCount > 0) {
				openingBraceCount--;
			}
			/*
			 * If any other element is searched for other than braces, the count
			 * always stays zero. Hence, this is the condition executed for
			 * non-brackets.
			 */
			else if(array[i].equals(element)& openingBraceCount == 0) {
				return i;
			}
		}
		/*
		 * An else part in the loop will be useless as the statement can be put
		 * outside only.
		 */
		return -1;
	}
	
	/**
	 * Just like substring but for an array
	 */
	private String[] subArray(String[] array, int from, int to) {
		String[] output = new String[to - from];
		for(int i = from, j = 0; i < to; i++, j++) {
			output[j] = array[i];
		}
		return output;
	}
	/**
	 * Joins two arrays with a string eval in b/w them
	 * @param eval The element sandwiched b/w the two arrays
	 */
	private String[] concatArrays(String[] A, String eval, String[] B) {
		String[] output = new String[A.length + B.length + 1];
		System.arraycopy(A, 0, output, 0, A.length);
		output[A.length] = eval;
		System.arraycopy(B, 0, output, A.length + 1, B.length);
		return output;
	}
	/**
	 * The Symbols analyzer:
	 * 
	 * This method generates a stack containing the symbols that have been used
	 * inside an expression scope.
	 * 
	 * An expression SCOPE is the part present b/w a pair of paranthesis
	 */
	private Stack Symbols(String[] exp)throws Exception {
		Stack Symbols = new Stack();
		/*
		 * Pushing takes place from the lowest BEDMAS operator This causes it to
		 * be placed at the end of the stack so that brackets are popped first.
		 */
		if(nextElement(exp, "-", 0, true)!= -1) {
			Symbols.push("-");
		}
		if(nextElement(exp, "+", 0, true)!= -1) {
			Symbols.push("+");
		}
		if(nextElement(exp, "*", 0, true)!= -1) {
			Symbols.push("*");
		}
		if(nextElement(exp, "/", 0, true)!= -1) {
			Symbols.push("/");
		}
		if(nextElement(exp, "^", 0, true)!= -1) {
			Symbols.push("^");
		}
		if(nextElement(exp, "(", 0, true)!= -1) {
			Symbols.push("(");
			/*
			 * THis part checks if the extracted scope has a closing
			 * paranthesis. If not, it throws an EXCEPTION and halts the
			 * execution completely.
			 */
			if(nextElement(exp, ")", 0, true)== -1) {
				throw new Exception("\nMISSING CLOSING PARANTHESIS");
			}
		}
		return Symbols;
	}
	
	/**
	 * This method takes in two numbers and the operator code and performs a
	 * binary operation.
	 */
	private double calculate(String operator, double oprnd1, double oprnd2) {
		/*
		 * No default case is needed as invalid operators are checked by the
		 * symbol analyzer.
		 */
		switch(operator) {
			case "^":
				return Math.pow(oprnd1, oprnd2);
			case "/":
				return oprnd1 / oprnd2;
			case "*":
				return oprnd1 * oprnd2;
			case "+":
				return oprnd1 + oprnd2;
			case "-":
				return oprnd1 - oprnd2;
		}
		return 0;
	}
	/**
	 * This is, as I like to call it, 'THE CORE', method evaluate(). This method
	 * evaluates the scope and returns the resulting expression. 
	 * @param exp
	 *            The expression ie the part inside current scope
	 * @param Symbols
	 *            Symbols stack for current scope
	 * @param operator
	 *            Operator value from { "^", "/", "*", "+", "-" }
	 * @param eval
	 *            Passed in so that incase operator is not found the function
	 *            returns 'eval'
	 * @return String[] expression
	 */
	private String[] evaluate(String[] exp, Stack Symbols, String operator) {
		for(int i = 0; i < exp.length; i++) {
			if(exp[i].equals(operator)) {
				// evaluate the operator.
				this.eval = calculate(operator, Double.parseDouble(exp[i - 1]), Double.parseDouble(exp[i + 1]));
				/*
				 * Expression is modified by replacing the 'operator' and it's
				 * operands with the result 'eval'.
				 */
				exp = concatArrays(subArray(exp, 0, i - 1), Double.toString(eval), subArray(exp, i + 2, exp.length));
				/*
				 * This makes the loop restart. This is done because the length
				 * of 'exp' changes every time the previous statement is
				 * executed.
				 */
				i = 0;
				display(exp);
			}
		}
		/*
		 * Ensures 'operator' is popped if no more 'operator' remain in the
		 * expression.
		 */
		try {
			if (Symbols.peek().equals(operator)) {
				Symbols.pop();
			}
		}
		catch(Exception e) {
		}
		return exp;
	}
	/**
	 * Expression simplification takes place here
	 * 
	 * The Symbol analyzer produces a fresh stack of symbols for the scope
	 * passed in through variable 'exp'.
	 * 
	 * For every scope, the part inside brackets is extracted using subarray.
	 * This is then passed to simplify()recursively.
	 * 
	 * Again a symbol stack is generated for the scope. As long as a bracket
	 * pair stays inside the scope, another scope is generated for the part
	 * inside the brackets. When no brackets remain, evaluation begins.
	 */
	private double simplify(String[] exp)throws Exception {
		// displays current state of evaluation
		System.out.println("\n>> Selected Scope:");
		display(exp);
		// A symbol stack is generated for scope inside 'exp'
		Stack Symbols = Symbols(exp);
		// eval carries the resultant value of any binary operation
		double eval = 0.0;
		/*
		 * Variable 'next' stores index of next operator/brace. by default it is
		 * set to -1, in case the symbol is not found at all.
		 */
		int i = 0, next = -1;
		// Until the table is empty
		while(!Symbols.isEmpty()) {
			/*
			 * Selects top symbol from stack. When no more of that symbol remain
			 * in the scope, that top is popped. Then the next symbol gets
			 * evaluated till the stack empties.
			 */
			switch(Symbols.peek().toString()) {
				case "(":
					/*
					 * NOTE that value of exp.length changes as more and more
					 * scopes are evaluated.
					 * 
					 * As more scopes get evaluated, the length of the
					 * expression becomes smaller.
					 */
					for(i = 0; i < exp.length; i++) {
						// If an opening brace is found
						if(exp[i].equals("(")) {
							// next stores index of next closing
							// brace
							next = nextElement(exp, ")", i + 1, false);
							// if brace is closed
							if(next != -1) {
								/*
								 * recursive call that sends a part of 'exp'
								 * from i+1 (ie position after opening brace)
								 * till position before the closing brace to
								 * evaluate()as the new scope.
								 */
								eval = simplify(subArray(exp, i + 1, next));
								/*
								 * Once that sent scope gets evaluated, the
								 * result in eval is put in the middle of the
								 * parent expression (from which the scope was
								 * cut and sent).
								 * 
								 * The braces are excluded during concatination
								 * process. This forms a new expression, ready
								 * to be evaluated agoin during next while
								 * iteration.
								 */
								exp = concatArrays(subArray(exp, 0, i), Double.toString(eval), subArray(exp, next + 1, exp.length));
								System.out.println(">> Left scope\n");
								// displays the new expression
								// as a step of evaluation
								display(exp);
							}
							else {
								break;
							}
						}
					}
					/*
					 * Stack.peek()and Stack.pop()methods throws a stack
					 * underflow exception.
					 * 
					 * Although we have the throws clause at the method
					 * declaration, we catch this exception so that it doesn't
					 * halt the progarm.
					 */
					try {
						/*
						 * A safety measure that ensures brackets are popped if
						 * no open bracket remain in the expression.
						 * 
						 * This situation arises when all scopes of a scope have
						 * been evaluated and numbers have been put in place of
						 * the evaluated scopes, and the brackets have been
						 * excluded.
						 */
						if(Symbols.peek().equals("(")) {
							Symbols.pop();
						}
					}
					catch(Exception e) {
					}
					break;
				/*
				 * If you've understood how braces are handled in the first case
				 * you can understand easily how other operators are being
				 * handled.
				 * 
				 * Difference is that no new scopes are generated in the
				 * following cases. Ie, no recursive calls in the following. All
				 * recursive calls are for the brackets.
				 */
				case "^":
					exp = evaluate(exp, Symbols, "^");
					eval = this.eval;
					break;
				case "/":
					exp = evaluate(exp, Symbols, "/");
					eval = this.eval;
					break;
				case "*":
					exp = evaluate(exp, Symbols, "*");
					eval = this.eval;
					break;
				case "+":
					exp = evaluate(exp, Symbols, "+");
					eval = this.eval;
					break;
				case "-":
					exp = evaluate(exp, Symbols, "-");
					eval = this.eval;
					break;
			}
		}
		// returns the evaluated value after case completion
		return eval;
	}
	public static void main(String[] args)throws Exception {
		EmLang em = new EmLang();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("REMEMBER to put a space b/w every ELEMENT\nEnter expression: ");
		String expression = br.readLine();
		System.out.println();
		System.out.print("Tokenized expression: ");
		// Print tokenized expression
		for(String out: expression.split(" ")) {
			System.out.print(out + "  ");
		}
		System.out.println();
		System.out.print("Evaluate (Y/N)?: ");
		if(br.readLine().equalsIgnoreCase("Y")) {
			/*
			 * CAST TO FLOAT FOR BETTER LOOKING OUTPUT Use double for more
			 * prescision.
			 */
			System.out.println("\nRESULT: " +(double)(em.simplify(expression.split(" "))));
		}
		else {
			System.err.println("HALTED BY USER!");
			System.exit(5);
		}
	}
}
