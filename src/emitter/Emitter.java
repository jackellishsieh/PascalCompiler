package emitter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import ast.ProcedureDeclaration;
import ast.Variable;

/**
 * Emitter represents a file writer specifically for writing MIPS code.
 * 
 * @author Anu Datar. Modified by Jack Hsieh.
 * @version 2022/05/29
 */
public class Emitter
{
    private PrintWriter out;               // the output

    private final static String DEFAULT_DESCRIPTION = "Auto-generated description";

    private int labelID;                       // the current id label
    
    ProcedureDeclaration procedureContext;  // the current procedure context
    private int numExcessWords;             // number of excess words pushed onto stack 
    
    private final static int WORD_SIZE = 4;
    
    /**
     * Constructs an emitter for writing to the provided file.
     * 
     * @postcondition The emitter is constructed write to the provided file.
     *                Internal counters and attributes are set to defaults.
     *                
     * @param outputFilename   the output filename
     */
    public Emitter(String outputFilename)
    { 
        try
        {
            out = new PrintWriter(new FileWriter(outputFilename), true);
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
        
        // Set internal counters and attributes to defaults
        labelID = 0;
        procedureContext = null;
        numExcessWords = 0;
    }

    /**
     * Writes one line of formatted code to the file.
     * 
     * @postcondition Writes the string to the with a possible preceding indentation. 
     *                Non-labels and headers are not indented, while all others are indented.
     * 
     * @param code the code to write to the file
     */
    public void emit(String code)
    {
        if (!(code.endsWith(":") || code.startsWith(".")))
        {
            code = "\t" + code;
        }
        out.println(code);
    }

    /**
     * Closes the file.
     * Should be called after all calls to emit.
     * 
     * @postcondition The file is closed.
     */
    public void close()
    {
        out.close();
    }
    
    /**
     * Emits a javadoc-style header
     * 
     * @postcondition This emitter writes a javadoc-style header
     *                with a blank description, author tag, and
     *                date of creation tag.
     * 
     * @param author    the author name
     */
    public void emitHeader(String author)
    {
        out.println("# " + Emitter.DEFAULT_DESCRIPTION);
        out.println("# @author " + author);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        out.println("# @version " + formatter.format(LocalDate.now()));
    }
    
    /**
     * Emits MIPS code that pushes the specified register onto the stack
     * 
     * @precondition The provided register is a valid register name.
     * @postcondition This emitter writes MIPS code that pushes the provided
     *                register onto the stack to the file.
     *                If there is a procedure context, the excess words counter is increased by one.
     * 
     * @param register  the register (with preceding $)
     */
    public void emitPush(String register)
    {
        this.emit("# push " + register + " onto the stack");
        this.emit("subu $sp $sp 4");
        this.emit("sw " + register + " ($sp)");
        this.emit("");
        
        // If in a procedure, increase excess word counter
        if (this.procedureContext != null)
        {
            this.numExcessWords++;
        } // if
    } // public void emitPush(String)
    
    /**
     * Emits MIPS code that pops from the stack to the specified register
     * 
     * @precondition The provided register is a valid register name.
     * @postcondition This emitter writes MIPS code that pops from the stack 
     *                to the provided register to the file.
     * 
     * @param register  the register (with preceding $)
     */
    public void emitPop(String register)
    {
        this.emit("# pop " + register + " from the stack");
        this.emit("lw " + register + " ($sp)");
        this.emit("addu $sp $sp 4");
        this.emit("");
        
        // If in a procedure, decrease excess word counter
        if (this.procedureContext != null)
        {
            this.numExcessWords--;
        } // if
    } // public void emitPop(String)
    
    /**
     * Returns the next label ID number
     * 
     * @postcondition The next label ID number is increased by one.
     * 
     * @return the next label ID (i.e. the number of times the method
     *         has been called)
     */
    public int nextLabelID()
    {
        labelID++;
        return labelID;
    }
    
    /**
     * Sets the current procedure context.
     * 
     * @postcondition The current procedure context is set to the provided procedure.
     *                The excess word counter is set to 0.
     * 
     * @param procedureContext  the new procedure context
     */
    public void setProcedureContext(ProcedureDeclaration procedureContext)
    {
        this.procedureContext = procedureContext;
        this.numExcessWords = 0;
    }
    
    /**
     * Clears the current procedure context.
     * 
     * @postcondition The current procedure context is set to null.
     *                The excess word counter is set to 0.
     */
    public void clearProcedureContext()
    {
        this.setProcedureContext(null);
        this.numExcessWords = 0;
    }
    
    /**
     * Returns whether the identifier identifies a local variable. 
     * NOT a return variable.
     * @param variable    the variable for which to evaluate scope
     * @return true if the identifier matches a local variable, false otherwise
     */
    public boolean isLocalVariable(Variable variable)
    {
        return this.isParameter(variable) || this.isReturnVariable(variable) 
                || this.isNonParameter(variable);
    }
    
    /**
     * Returns whether the identifier identifies a parameter in the procedure context.
     * 
     * @param variable    the variable for which to check whether it is a parameter in context
     * @return true if the identifier is a parameter, false otherwise
     */
    public boolean isParameter(Variable variable)
    {
        return procedureContext != null && procedureContext.getParameters().contains(variable);
    }
    
    /**
     * Returns whether the identifier identifies a return variable in the procedure context.
     * 
     * @param variable    the variable for which to check whether it is the return variable
     * @return true if the identifier is the return variable, false otherwise
     */
    public boolean isReturnVariable(Variable variable)
    {
        return procedureContext != null && procedureContext.getReturnVariable().equals(variable);
    }
    
    /**
     * Returns whether the identifier identifies a non-parameter local variable 
     * in the procedure context.
     * 
     * @param variable    the variable for which to check whether it is 
     *                    a non-parameter local variable
     * @return true if the identifier is a non-parameter local variable , false otherwise
     */
    public boolean isNonParameter(Variable variable)
    {
        return procedureContext != null && procedureContext.getNonParameters().contains(variable);
    }
    
    /**
     * Returns the byte offset for a variable in the given context.
     * 
     * @precondition The emitter has a non-null procedure context and
     *               the provided variable is a local variable of the current procedure context.
     * 
     * @param variable  the variable for which to calculate the offset
     * @return the byte offset for the variable on the stack
     */
    public int getByteOffset(Variable variable)
    {        
        List<Variable> nonParameters = procedureContext.getNonParameters();
        List<Variable> parameters = procedureContext.getParameters();
        
        int numOffsetWords = nonParameters.size();
        
        // If the local variable is a non-local parameter variable, account for the index
        if (this.isNonParameter(variable))
        {
            numOffsetWords -= (nonParameters.indexOf(variable) + 1);
        }
        
        // Otherwise, if the local variable is a parameter, account for the index
        else if (this.isParameter(variable))
        {
            numOffsetWords += (parameters.size() - parameters.indexOf(variable));
        }
        
        // Otherwise, the variable is the return variable and nothing else must be done
                
        // Add on excess words
        numOffsetWords += numExcessWords;
         
        // Convert to bytes
        return Emitter.WORD_SIZE * numOffsetWords;
    } // public int getOffset(Variable)
} // public class Emitter