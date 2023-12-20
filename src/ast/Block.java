package ast;

import java.util.List;

import emitter.Emitter;
import environment.RuntimeEnvironment;

/**
 * Block defines a semantic object corresponding to a block of statements.
 * 
 * @author Jack Hsieh
 * with assistance from Clarice Wang
 * @version 2022/03/11
 */
public class Block extends Statement
{
    private List<Statement> statementList;      // Ordered list of statements
    
    /**
     * Constructs a Block object with the provided list of statements.
     *  
     * @postcondition This Block object is constructed with the 
     * provided statements.
     * 
     * @param statementList the statements out of which a block is formed
     */
    public Block(List<Statement> statementList)
    {
        // System.out.println("Created Block!");
        this.statementList = statementList;
    }

    /**
     * Executes the block by executing all embedded statements in order
     * 
     * @postcondition Each statement in the block is executed in order 
     * 
     * @param runtimeEnvironment    the runtime environment to execute the block within
     * @throws SemanticErrorException if a component statement fails to execute
     */
    @Override
    public void execute(RuntimeEnvironment runtimeEnvironment) throws SemanticErrorException
    {
        for (Statement statement : statementList)
        {
            statement.execute(runtimeEnvironment);
        } // for
    } // public void execute

    /**
     * Compiles the block and writes the MIPS assembly code 
     * to the provided emitter.
     * 
     * @postcondition The emitter has emit the block converted to assembly code.
     * 
     * @param emitter   the emitter to output the MIPS assembly code to
     */
    public void compile(Emitter emitter)
    {
        for (Statement statement : statementList)
        {
            statement.compile(emitter);
        } // for
    } // public void compile
            
    /**
     * Returns a string representation of the AST block node
     * 
     * @return a labeled string with the node type and each statement in order
     */
    @Override
    public String toString()
    {
        String s = "Block";
        
        for (Statement statement : statementList)
        {
            s += "\n\t" + statement.toString().replaceAll("\n","\n\t");
        }
        
        return s;
    } // public String toString()
} // public class Block
