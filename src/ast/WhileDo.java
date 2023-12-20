package ast;

import emitter.Emitter;
import environment.RuntimeEnvironment;

/**
 * WhileDo defines a semantic object corresponding to a while loop.
 * 
 * @author Jack Hsieh
 * with assistance from Clarice Wang
 * @version 2022/03/19
 */
public class WhileDo extends Statement
{
    private Expression condition;       // the boolean condition
    private Statement doStatement;      // the statement to repeatedly execute

    /**
     * Constructs a while loop object with the provided condition and statement.
     *  
     * @postcondition If the condition evaluates to a boolean,
     *                this while loop is constructed with the given condition and statement.     * 
     * @param condition     the condition to evaluate to determine whether to loop
     * @param doStatement the statement to conditionally repeatedly execute
     * @throws SemanticErrorException if the condition does not evaluate to a boolean
     */
    public WhileDo(Expression condition, Statement doStatement) throws SemanticErrorException
    {
        this.condition = condition;
        this.doStatement = doStatement;

        // Check type
        if (!condition.getEvaluationType().equals(Boolean.class))
        {
            String message = "Error: expected Boolean expression but found ";
            message += condition.getEvaluationType() + " expression instead";
            throw new SemanticErrorException(message);
        } // if
    } // public WhileDo

    /**
     * Executes the while-do by evaluating the boolean condition and 
     * repeatedly executing the statement until the boolean condition evaluates to false.
     * 
     * @postcondition The statement is executed repeatedly until the condition evaluates to false.
     * 
     * @param runtimeEnvironment    the runtime environment to execute the while loop within
     * @throws SemanticErrorException if the statement cannot be executed
     */
    @Override
    public void execute(RuntimeEnvironment runtimeEnvironment) throws SemanticErrorException
    {
        while ((boolean) condition.evaluate(runtimeEnvironment))
        {
            doStatement.execute(runtimeEnvironment);
        } // while
    } // public void execute

    /**
     * Compiles a WhileDo statement.
     * 
     * @postcondition The emitter has emit assembly code that 
     *                when run will conditionally execute the do statement
     *                while the condition is true.
     *                The condition is a relative operator comparison.
     *                The emitter's label ID is increased once.
     * 
     * @param emitter   the emitter to output the MIPS assembly code to
     */
    public void compile(Emitter emitter)
    {
        // Compiles the start and end loop labels
        int labelID = emitter.nextLabelID();
        String startLabel = "startWhile" + labelID;
        String endLabel = "endWhile" + labelID;

        // Compiles the start label
        emitter.emit("");
        emitter.emit(startLabel + ":");
        
        // Compiles the condition with the next label
        ((RelativeOperator) condition).compile(emitter, endLabel);

        // Compiles then statement
        this.doStatement.compile(emitter);

        // Compiles the jump
        emitter.emit("j " + startLabel);

        // Compiles the end label
        emitter.emit("");
        emitter.emit(endLabel + ":");
    }

    /**
     * Returns a string representation of the AST while-do node
     * 
     * @return a labeled string with the node type, while condition, and loop statement.
     */
    @Override
    public String toString()
    {
        String s = "WHILE\n\t" + condition.toString().replaceAll("\n", "\n\t");
        s += "\nDO\n\t" + doStatement.toString().replaceAll("\n", "\n\t");
        return s;
    } // public String toString()
} // public class WhileDo
