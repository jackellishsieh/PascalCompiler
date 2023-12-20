package ast;

import emitter.Emitter;
import environment.RuntimeEnvironment;

/**
 * Literal defines a semantic object corresponding to a literal value.
 * 
 * @author Jack Hsieh
 * @version 2022/03/25
 */
public class Literal extends Expression
{
    private Object value;               // Internal value
    
    /**
     * Constructs a Literal object with the provided value.
     *  
     * @postcondition This Literal object is constructed with the provided value and type.
     * 
     * @param value The value of this Literal object.
     */
    public Literal(Object value)
    {
        // System.out.println("Created Literal!");
        this.value = value;
        this.evaluationType = value.getClass();
    }

    /**
     * Returns the evaluated value of the Literal
     * 
     * @param runtimeEnvironment    the runtime environment to evaluate the expression within
     * @return the value of the Literal
     */
    @Override
    public Object evaluate(RuntimeEnvironment runtimeEnvironment)
    {
        return this.value;
    }

    /**
     * Compiles the literal and writes the MIPS assembly code to the provided emitter
     * 
     * @precondition the literal is an integer value
     * @postcondition The emitter has emit the literal converted to assembly code.
     * 
     * @param emitter   the emitter to output the MIPS assembly code to
     */
    public void compile(Emitter emitter)
    {
        if (this.getEvaluationType().equals(Integer.class))
        {
            emitter.emit("# loads integer literal");
            emitter.emit("li $v0 " + this.value);
            emitter.emit("");
        } // if
    } // public void compile
    
    /**
     * Returns a string representation of the AST literal node
     * 
     * @return a labeled string with the node type and the literal value
     */
    @Override
    public String toString()
    {
        return "Literal = " + this.value;
    } // public String toString()
} // public class Literal
