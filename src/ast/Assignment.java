package ast;

import emitter.Emitter;
import environment.RuntimeEnvironment;

/**
 * Assignment defines a semantic object corresponding to a variable assignment.
 * 
 * @author Jack Hsieh
 * with assistance from Clarice Wang
 * @version 2022/03/11
 */
public class Assignment extends Statement
{
    private Variable variable;
    private Expression expression;
    
    /**
     * Constructs an assignment object with the provided variable and expression.
     *  
     * @postcondition This assignment is constructed with the 
     * provided variable and expression.
     * 
     * @param variable      the variable to assign a value to
     * @param expression    the expression to assign the variable to
     * @throws SemanticErrorException if the variable type does not match the expression type
     */
    public Assignment(Variable variable, Expression expression) throws SemanticErrorException
    {
        // System.out.println("Created Assignment!");
        this.variable = variable;
        this.expression = expression;
        
        /**
         * Throw a semantic error if the variable type does not match
         * the expression type
         */
        if (!this.variable.getEvaluationType().equals(this.expression.getEvaluationType()))
        {
            String message = "Error! Attempted to assign variable of type ";
            message += this.variable.getEvaluationType() + " to expression of type ";
            message += this.expression.getEvaluationType();
            throw new SemanticErrorException(message);
        }
    }

    /**
     * Returns the variable of the assignment
     * 
     * @return the variable in the assignment
     */
    public Variable getVariable()
    {
        return this.variable;
    }

    /**
     * Executes the assignment by updating the variable and value during runtime
     * 
     * @postcondition The runtime environment in which the assignment is embedded inserts 
     * the variable if appropriate and/or updates the value associated with the variable.
     * 
     * @param runtimeEnvironment    the runtime environment to execute the assignment within
     * @throws SemanticErrorException if the assigned expression cannot be evaluated
     */
    @Override
    public void execute(RuntimeEnvironment runtimeEnvironment) throws SemanticErrorException
    {
        runtimeEnvironment.setVariableValue(variable, expression.evaluate(runtimeEnvironment));
    }

    /**
     * Compiles the assignment in MIPS assembly code to the provided emitter.
     * Supports both global and local variables.
     *
     * @precondition The variable and expression of are integer type.
     * @postcondition The emitter has emit code loading the variable into $v0
     *                converted to assembly code.
     * 
     * @param emitter   the emitter to output the MIPS assembly code to
     */
    public void compile(Emitter emitter)
    {
        // Evaluate the expression
        this.expression.compile(emitter);
        
        // If the variable is local,
        if (emitter.isLocalVariable(this.variable))
        {
            // Document
            emitter.emit("# load $v0 into local " + this.variable.getIdentifier());
            
            // Save to the stack with offset
            emitter.emit("sw $v0 " + emitter.getByteOffset(variable) + "($sp)");
        }
        
        // If the variable is global,
        else
        {
            // Document
            emitter.emit("# load $v0 into global " + this.variable.getIdentifier());
            
            // Save to the label
            emitter.emit("sw $v0 var" + this.variable.getIdentifier());
        }
        
        emitter.emit("");
    }
    
    /**
     * Returns a string representation of the AST assignment node
     * 
     * @return a labeled string with the node type, variable, and expression
     */
    @Override
    public String toString()
    {
        String s = "Assignment\n\t" + variable.toString().replaceAll("\n", "\n\t");
        s += "\n\t" + expression.toString().replaceAll("\n", "\n\t");
        return s; 
    } // public String toString()
} // public class Assignment
