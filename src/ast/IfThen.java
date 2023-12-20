package ast;

import emitter.Emitter;
import environment.RuntimeEnvironment;

/**
 * IfThen defines a semantic object corresponding to an if-then conditional.
 * 
 * @author Jack Hsieh
 * with assistance from Clarice Wang
 * @version 2022/03/19
 */
public class IfThen extends Statement
{
    private Expression condition;           // the boolean condition
    private Statement thenStatement;        // the statement to conditionally execute

    /**
     * Constructs an if-then object with the provided condition and statement.
     *  
     * @postcondition If the condition evaluates to a boolean,
     *                this if statement is constructed with the given condition and statement.
     * 
     * @param condition     the condition to evaluate to determine whether to execute
     * @param thenStatement the statement to conditionally execute
     * @throws SemanticErrorException if the condition does not evaluate to a boolean
     */
    public IfThen(Expression condition, Statement thenStatement) throws SemanticErrorException
    {
        this.condition = condition;
        this.thenStatement = thenStatement;

        if (!condition.getEvaluationType().equals(Boolean.class))
        {
            String message = "Error: expected Boolean expression but found ";
            message += condition.getEvaluationType() + " expression instead";
            throw new SemanticErrorException(message);
        }
    }

    /**
     * Executes the if-then by evaluating the boolean condition and 
     * conditionally executing the statement.
     * 
     * @postcondition The statement is executed if and only if the condition evaluates to true;
     *                otherwise, nothing happens.
     * 
     * @param runtimeEnvironment    the runtime environment to execute the if conditional within
     * @throws SemanticErrorException if the statement cannot be executed
     */
    @Override
    public void execute(RuntimeEnvironment runtimeEnvironment) throws SemanticErrorException
    {
        if ((boolean) condition.evaluate(runtimeEnvironment))
        {
            thenStatement.execute(runtimeEnvironment);
        } // if
    } // public void execute

    /**
     * Compiles an If statement.
     * 
     * @postcondition The emitter has emit assembly code that 
     *                when run will conditionally execute the then statement
     *                if the condition is true.
     *                The condition is a relative operator comparison.
     *                The emitter's label ID is increased once.
     * 
     * @param emitter   the emitter to output the MIPS assembly code to
     */
    public void compile(Emitter emitter)
    {
        // Compiles the condition with the next label
        String endLabel = "endIf" + emitter.nextLabelID();
        ((RelativeOperator) condition).compile(emitter, endLabel);

        // Compiles then statement
        this.thenStatement.compile(emitter);

        // Compiles the end label
        emitter.emit("");
        emitter.emit(endLabel + ":");
    }

    /**
     * Returns a string representation of the AST if-then node.
     * 
     * @return a labeled string with the node type, if condition, and then statement.
     */
    @Override
    public String toString()
    {
        String s = "IF\n\t" + condition.toString().replaceAll("\n", "\n\t");
        s += "\nTHEN\n\t" + thenStatement.toString().replaceAll("\n","\n\t");
        return s;
    } // public String toString()
} // public class IfThen
