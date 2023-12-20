package ast;

import emitter.Emitter;
import environment.RuntimeEnvironment;

/**
 * Writeln defines a semantic object corresponding to a print statement.
 * 
 * @author Jack Hsieh
 * with assistance from Clarice Wang
 * @version 2022/03/11
 */
public class Writeln extends Statement
{
    private Expression expression;  // Expression to print
    
    /**
     * Constructs a Writeln object with the given expression
     *  
     * @postcondition This Writeln object is constructed with the provided expression.
     * 
     * @param expression    the expression to print
     */
    public Writeln(Expression expression)
    {
        // System.out.println("Created Writeln!");
        this.expression = expression;
    }

    /**
     * Executes the Writeln by printing the expression to the console
     * 
     * @postcondition The expression, if able to be evaluated, is printed to the console.
     * 
     * @param runtimeEnvironment    the runtime environment to execute the assignment within
     * @throws SemanticErrorException if the assigned expression cannot be evaluated
     */
    @Override
    public void execute(RuntimeEnvironment runtimeEnvironment) throws SemanticErrorException
    {
        System.out.println(expression.evaluate(runtimeEnvironment));
    }

    /**
     * Compiles the Writeln and writes the MIPS assembly code to the provided emitter
     * 
     * @precondition The Writeln contains an integer expression.
     * @postcondition The emitter has emit the Writeln converted to assembly code.
     * 
     * @param emitter   the emitter to output the MIPS assembly code to
     */
    public void compile(Emitter emitter)
    {
        if (expression.getEvaluationType().equals(Integer.class))
        {
            // Compile the literal
            expression.compile(emitter);
            
            // Print the literal
            emitter.emit("# print $v0");
            emitter.emit("move $a0 $v0");
            emitter.emit("li $v0 1");
            emitter.emit("syscall");
            
            // Print a new line
            emitter.emit("# print newline");
            emitter.emit("la $a0 " + Program.NEWLINE_LABEL);
            emitter.emit("li $v0 4");
            emitter.emit("syscall");
            emitter.emit("");
            
        } // if
        
    } // public void compile
    
    /**
     * Returns a string representation of the AST Writeln node
     * 
     * @return a labeled string with the node type and the expression to print
     */
    @Override
    public String toString()
    {
        return "Writeln\n\t" + expression.toString().replaceAll("\n", "\n\t");
    } // public String toString()
} // public class Writeln
