package ast;

import emitter.Emitter;
import environment.RuntimeEnvironment;

/**
 * Abstract Expression class for integer and boolean expressions.
 * 
 * @author Jack Hsieh
 * with assistance from Clarice Wang
 * @version 2022/03/19
 */
public abstract class Expression implements Compileable
{
    /**
     * The type that the expression evaluates to
     */
    protected Class<?> evaluationType;
    
    public abstract Object evaluate(RuntimeEnvironment runtimeEnvironment) 
            throws SemanticErrorException;
    
    /**
     * Default compile method for the statement.
     * Intended to be overridden by subclasses.
     * 
     * @postcondition A runtime exception labeled "Implement me!!!!!" is thrown.
     * 
     * @param emitter the emitter to compile to
     */
    public void compile(Emitter emitter)
    {
        throw new RuntimeException("Implement me!!!!!");
    }
    
    public abstract String toString();
    
    /**
     * Returns the evaluation type of the expression.
     * 
     * @return the data type the expression evaluates to
     */
    public Class<?> getEvaluationType()
    {
        return evaluationType;
    } // public Class<?> getEvaluationType()
} // public abstract class Expression
