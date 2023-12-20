package ast;

import environment.RuntimeEnvironment;

/**
 * ForToDo defines a semantic object corresponding to a simple for loop with fixed increments.
 * Compilation not supported.
 * 
 * @author Jack Hsieh
 * with assistance from Clarice Wang
 * @version 2022/03/19
 */
public class ForToDo extends Statement
{
    private Variable index;             // the index variable
    private Expression lowerBound;      // the lower bound
    private Expression upperBound;      // the upper bound
    private Statement doStatement;      // the statement to conditionally repeatedly execute

    /**
     * Constructs a for-loop object with the provided index, lower bound, upper bound, 
     * and statement.
     *
     * @postcondition If the provided index, lower bound, upper bound are all correct,
     *                this for-loop is constructed with the provided index, 
     *                lower bound, upper bound, and statement.
     * 
     * @param index         the index loop variable
     * @param lowerBound    the lower bound for the index
     * @param upperBound    the upper bound for the index
     * @param doStatement   the statement to repeatedly execute
     * @throws SemanticErrorException if the index, lower bound, or upper bound 
     *         do not all evaluate to integers
     */
    public ForToDo(Variable index, Expression lowerBound, Expression upperBound, 
            Statement doStatement) throws SemanticErrorException
    {
        this.index = index;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.doStatement = doStatement;
        
        /**
         * Check for wrong evaluation types
         */
        if (!this.index.getEvaluationType().equals(Integer.class))
        {
            String message = "Error: expected integer index but got ";
            message += this.index.getEvaluationType() + " index instead";
            
            throw new SemanticErrorException(message);
        }
        
        else if (!this.lowerBound.getEvaluationType().equals(Integer.class))
        {
            String message = "Error: expected integer lower bound but got ";
            message += this.index.getEvaluationType() + " lower bound instead";
            
            throw new SemanticErrorException(message);
        }
        
        else if (!this.upperBound.getEvaluationType().equals(Integer.class))
        {
            String message = "Error: expected integer upper bound but got ";
            message += this.index.getEvaluationType() + " upper bound instead";
            
            throw new SemanticErrorException(message);
        } // else if
    } // public ForToDo
    
    /**
     * Executes the for loop.
     * 
     * @postcondition The index is initialized once. Then, the loop condition is checked,
     * the statement is executed, and the index incremented by one in order repeatedly 
     * until the upper bound is reached.
     * 
     * @param runtimeEnvironment    the runtime environment to execute the for loop within
     * @throws SemanticErrorException if the initial index or upper bound 
     *         cannot be evaluated or statement cannot be executed.
     */
    @Override
    public void execute(RuntimeEnvironment runtimeEnvironment) throws SemanticErrorException
    {        
        Literal one = new Literal(1);
        
        Assignment initialization = new Assignment(index, lowerBound);
        Assignment increment = new Assignment(index, new ArithmeticOperator("+", index, one));
        
        // Initialize
        initialization.execute(runtimeEnvironment);
        
        // Repeatedly execute the do statement and increment the index
        while ((int) index.evaluate(runtimeEnvironment) 
                <= (int) upperBound.evaluate(runtimeEnvironment))
        {
            doStatement.execute(runtimeEnvironment);
            increment.execute(runtimeEnvironment);
        }
    }

    /**
     * Returns a string representation of the AST for loop node
     * 
     * @return a labeled string with the node type, initialization, upper bound, and DO statement.
     */
    @Override
    public String toString()
    {
        String s = "FOR\n\t" + index.toString().replaceAll("\n", "\n\t");
        s += "\n=\n\t" + lowerBound.toString().replaceAll("\n", "\n\t");
        s += "\nTO\n\t" + upperBound.toString().replaceAll("\n", "\n\t");
        s += "\nDO\n\t" + doStatement.toString().replaceAll("\n", "\n\t");
        return s;   
    } // public String toString()
} // public class ForToDo
