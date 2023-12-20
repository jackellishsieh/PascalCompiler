package ast;

import emitter.Emitter;
import environment.DeclarationEnvironment;
import environment.RuntimeEnvironment;

/**
 * Program defines a semantic object corresponding to a complete Pascal program
 * as well as the parsed declaration environment.
 * 
 * @author Jack Hsieh
 * @version 2022/05/29
 */
public class Program extends Statement
{
    private DeclarationEnvironment declarationEnvironment;  // variable and procedure declarations
    private Statement body;                                 // the program to execute
    
    // Compile constants
    public final static String NEWLINE_LABEL = "newline";   // what to call the newline string
    public final static int DEFAULT_INTEGER_VALUE = 0;      // default integer .data value
    private String author = "Jack Hsieh";                   // javadoc author
    
    /**
     * Constructs a program.
     * 
     * @param declarationEnvironment the environment of declarations of variables and procedures
     *                               that may be used in the body
     * @param body the statement that forms the body of the program
     */
    public Program(DeclarationEnvironment declarationEnvironment, Statement body)
    {
        this.declarationEnvironment = declarationEnvironment;
        this.body = body;
    }
    
    /**
     * Executes the program.
     * 
     * @param runtimeEnvironment    the environment in which to execute the program
     * @throws SemanticErrorException if the body statement cannot be executed
     */
    public void execute(RuntimeEnvironment runtimeEnvironment) throws SemanticErrorException
    {
        this.body.execute(runtimeEnvironment);
    }
    
    /**
     * Compiles the program and writes the MIPS assembly code to the provided emitter.
     * Includes a javadoc header, .data, .text, and the main code.
     * 
     * @postcondition The emitter has emit the program converted to assembly code.
     * 
     * @param emitter   the emitter to output the MIPS assembly code to
     */
    public void compile(Emitter emitter)
    {
        // Javadoc header
        emitter.emitHeader(this.author);
        emitter.emit("");
        
        // Data
        emitter.emit(".data");
        
        // Compile variables
        this.compileVariableDeclarations(emitter);
        
        // Include newline by default
        emitter.emit(Program.NEWLINE_LABEL + ": .asciiz \"\\n\"");
        emitter.emit("");
        
        // Text 
        emitter.emit(".text");
        emitter.emit("");
        
        // Global
        emitter.emit(".globl main");
        emitter.emit("");
        
        // Main
        emitter.emit("main:");
        
        // Code body
        this.body.compile(emitter);
        
        // Termination
        emitter.emit("# terminate execution");
        emitter.emit("li $v0 10");
        emitter.emit("syscall");
        emitter.emit("");
        
        // Compile procedures
        this.compileProcedureDeclarations(emitter);
        
    } // public void compile
    
    /**
     * Compiles the variable declarations and writes the MIPS assembly code to the provided emitter
     * 
     * @precondition The emitter is writing the .data section.
     * @postcondition The emitter has emit the variable declarations converted to 
     *                MIPS assembly
     * 
     * @param emitter   the emitter to output the MIPS assembly code to
     */
    private void compileVariableDeclarations(Emitter emitter)
    {
        for (Variable variable : declarationEnvironment.getVariableSet())
        {
            String declaration = "var" + variable.getIdentifier() + ": ";
            
            // Variable declaration
            if (variable.getEvaluationType().equals(Integer.class))
            {
                declaration += ".word " + Program.DEFAULT_INTEGER_VALUE;
            }
            
            emitter.emit(declaration);
        } // for
    } // private void compileVariableDeclarations(Emitter)
    
    
    /**
     * Compiles the procedure declarations and writes the MIPS assembly code to the provided emitter
     * 
     * @precondition The emitter is writing after main label.
     * @postcondition The emitter has emit the procedure declarations converted to 
     *                MIPS assembly
     * 
     * @param emitter   the emitter to output the MIPS assembly code to
     * @throws SemanticErrorException 
     */
    private void compileProcedureDeclarations(Emitter emitter)
    {
        for (ProcedureDeclaration procedureDeclaration : declarationEnvironment.getProcedureSet())
        {
            procedureDeclaration.compile(emitter);
        } // for
    } // private void compileProcedureDeclarations(Emitter)

    /**
     * Returns a string representation of the program
     * 
     * @return a labeled string including the declarations followed by the body
     */
    public String toString()
    {
        String s = "Program\n\t" + declarationEnvironment.toString().replaceAll("\n", "\n\t");
        s += "\n\t" + body.toString().replaceAll("\n","\n\t");
        
        return s;
    } // public String toString
    
} // public class Program
