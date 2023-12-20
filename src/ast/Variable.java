package ast;

import emitter.Emitter;
import environment.RuntimeEnvironment;

/**
 * Variable defines a semantic object corresponding to a variable with a unique identifier.
 * 
 * @author Jack Hsieh
 * with assistance from Clarice Wang
 * @version 2022/05/29
 */
public class Variable extends Expression
{
    private String identifier;      // the string identifier
    
    /**
     * Constructs a variable object with the provided identifier.
     *  
     * @postcondition This variable object is constructed with the provided identifier
     *                and type.
     * 
     * @param identifier        the variable's identifier.
     * @param evaluationType    the data type the variable should store
     */
    public Variable(String identifier, Class<?> evaluationType)
    {
        // System.out.println("Created Variable!");
        this.identifier = identifier;
        super.evaluationType = evaluationType;
    }

    /**
     * Returns the variable's identifier
     *
     * @return the variable's identifier as a string
     */
    public String getIdentifier()
    {
        return identifier;
    }
    
    /**
     * Returns the evaluated value associated with the variable
     * 
     * @param runtimeEnvironment    the runtime environment to evaluate the expression within
     * @return the value associated with this variable the in the provided environment
     */
    @Override
    public Object evaluate(RuntimeEnvironment runtimeEnvironment)
    {
        return runtimeEnvironment.getVariableValue(this);
    }

    /**
     * Compiles the variable in MIPS assembly code to the provided emitter.
     * Supports both global and local variables.
     * 
     * @precondition The variable is of integer type.
     * @postcondition The emitter has emit code loading the variable into $v0
     *                converted to assembly code.
     * 
     * @param emitter   the emitter to output the MIPS assembly code to
     */
    public void compile(Emitter emitter)
    {
        // If the variable is a local variable
        if (emitter.isLocalVariable(this))
        {
            // Document
            emitter.emit("# load local " + this.identifier);

            // Load from stack with offset
            emitter.emit("lw $v0 " + emitter.getByteOffset(this) + "($sp)");
        }
        
        // If the variable is a global variable,
        else
        {
            // Document
            emitter.emit("# load global " + this.identifier);
            
            // Load from label
            emitter.emit("la $t0 var" + this.identifier);
            emitter.emit("lw $v0 ($t0)");
        }
        
        emitter.emit("");
    }
    
    /**
     * Returns a string representation of the AST variable node
     * 
     * @return a labeled string with the node type and the identifier
     */
    @Override
    public String toString()
    {
        return "Variable = " + identifier + ", Type = " + super.evaluationType.getSimpleName();
    } // public String toString()
} // public class Variable
