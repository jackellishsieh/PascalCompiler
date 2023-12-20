package ast;

import emitter.Emitter;
import environment.RuntimeEnvironment;

/**
 * Statement defines an abstract statement semantic action.
 * 
 * @author Jack Hsieh
 * with assistance from Clarice Wang
 * @version 2022/03/09
 */
public abstract class Statement implements Compileable
{
    public abstract void execute(RuntimeEnvironment runtimeEnvironment) 
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
}
