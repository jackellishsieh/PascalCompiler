package ast;

import java.util.List;

import emitter.Emitter;

/**
 * ProcedureDeclaration defines a generalized procedure that can be called.
 * The procedure may be initialized as a bodiless header to which a body can be added
 * or a bodied procedure.
 *  
 * @author Jack Hsieh
 * @version 2022/05/29
 */
public class ProcedureDeclaration
{
    private String identifier;              // the procedure identifier
    
    private List<Variable> parameters;      // the list of parameters    
    private Variable returnVariable;        // the return variable
    
    private List<Variable> nonParameters;   // the list of non-parameter local variables
    private Statement body;                 // the statement to execute
    
    private boolean isHeader;               // whether the procedure declaration is a bodiless

    /**
     * Constructs a procedure declaration with the provided identifier, parameters, 
     * return variable, non-parameter local variables, and body.
     * 
     * @precondition All parameters evaluate to integers (for now).
     * @postcondition If the parameters are acceptable, this procedure declaration is constructed 
     *               with the given identifier, parameters, return variable, and body.
     * 
     * @param identifier        the identifier of the procedure
     * @param parameters        the ordered list of procedure parameters. May be empty.
     * @param returnVariable    the return variable
     * @param nonParameters     the ordered list of non-parameter local variables. May be empty.
     * @param body              the statement to execute
     */
    public ProcedureDeclaration(String identifier, List<Variable> parameters,
            Variable returnVariable, List<Variable> nonParameters, Statement body)
    {
        this.identifier = identifier;
        this.parameters = parameters;
        this.returnVariable = returnVariable;
        this.nonParameters = nonParameters;
        this.body = body;
        
        this.isHeader = false;
    }

    /**
     * Constructs a procedure declaration with the provided identifier, parameters, 
     * and return value but NO non-parameter local variables or body.
     * 
     * @precondition All parameters evaluate to integers (for now).
     * @postcondition If the parameters are acceptable, this procedure declaration is constructed 
     *                as a pure header with the given identifier and parameters but no body.
     * 
     * @param identifier        the identifier of the procedure
     * @param parameters        the ordered list of procedure parameters. May be empty.
     * @param returnVariable    the return variable
     */
    public ProcedureDeclaration(String identifier, List<Variable> parameters, 
            Variable returnVariable)
    {
        this(identifier, parameters,returnVariable, null, null);
        this.isHeader = true;
    }

    /**
     * Returns the identifier of the procedure
     * 
     * @return the identifier of the procedure as a string
     */
    public String getIdentifier()
    {
        return this.identifier;
    }

    /**
     * Returns the parameters of the procedure
     * 
     * @return the parameters of the procedure as a list of variables
     */
    public List<Variable> getParameters()
    {
        return this.parameters;
    }
    
    /**
     * Returns the return variable of the procedure
     * 
     * @return the return variable
     */
    public Variable getReturnVariable()
    {
        return this.returnVariable;
    }
    
    /**
     * Returns whether the procedure is a bodiless header
     * 
     * @return true if the procedure is a bodiless header, false otherwise
     */
    public boolean isHeader()
    {
        return this.isHeader;
    }

    /**
     * Returns the non-parameter local variables of the procedure
     * 
     * @return the non-parameter local variables of the procedure as a list of variables
     */
    public List<Variable> getNonParameters()
    {
        return this.nonParameters;
    }
    
    /**
     * Returns the body of the procedure
     * 
     * @return the identifier of the procedure as a statement
     * @throws SemanticErrorException if the procedure is a bodiless header
     */
    public Statement getBody() throws SemanticErrorException
    {
        if (isHeader)
        {
            String message = "Error: Procedure " + this.toString() + " is a bodiless procedure";
            throw new SemanticErrorException(message);
        }

        return this.body;
    }

    /**
     * Adds the non-parameters local variables and a body to a header.
     * 
     * @postcondition If the procedure was previously a bodiless header, 
     *                the local non-parameters variables body is added
     *                and the procedure is no longer a header.
     *                Otherwise, an exception is thrown.
     *     
     * @param newNonParameters  the non-parameter local variables to add
     * @param newBody           the body to add
     * @throws SemanticErrorException if the procedure is not a bodiless header
     */
    public void fill(List<Variable> newNonParameters, Statement newBody)
            throws SemanticErrorException
    {
        // If a body can be added (i.e. the procedure is a bodiless header)
        if (isHeader)
        {
            this.nonParameters = newNonParameters;
            this.body = newBody;
            isHeader = false;
        }

        // If the procedure is not a bodiless header
        else
        {
            String message = "Error: Attempted to fill bodied procedure";
            throw new SemanticErrorException(message);
        } // else
    } // public void fill(List<Variable>)

    /**
     * Compiles the procedure declaration and writes the MIPS assembly code to the provided emitter.
     * 
     * @postcondition The emitter has emit the procedure declaration converted to 
     *                MIPS assembly
     * 
     * @param emitter   the emitter to output the MIPS assembly code to
     */
    public void compile(Emitter emitter)
    {
        // Write the label
    	emitter.emit("proc" + this.identifier + ":");
    	
    	// Push the return variable (default 0) onto the stack
    	emitter.emit("# set default value for return variable " + this.getIdentifier());
    	emitter.emit("li $v0 0");
    	emitter.emitPush("$v0");
    	
    	// Push the non-parameter local variables (default 0) onto the stack
    	for (Variable nonParameter : nonParameters)
    	{
    	    emitter.emit("# set default value for non-parameter " + nonParameter.getIdentifier());
            emitter.emit("li $v0 0");
            emitter.emitPush("$v0");
    	}
        
    	// Sets the procedure context for the emitter
        emitter.setProcedureContext(this);
        
    	// Write the body
    	body.compile(emitter);
    	
    	// Clears the procedure context for the emitter
        emitter.clearProcedureContext();
        
        // Pops the non-parameter local variables from the stack
        for (Variable nonParameter : nonParameters)
        {
            emitter.emit("# pop non-parameter local " + nonParameter.getIdentifier());
            emitter.emitPush("$t0");
        }
    	
        // Pops return variable from the stack
        emitter.emit("# pop var" + this.getIdentifier());
        emitter.emitPop("$v0");
        
    	// Write return
        emitter.emit("# return");
    	emitter.emit("jr $ra");
    	emitter.emit("");
    }
    
    /**
     * Returns a string representation of the procedure declaration.
     * 
     * @return a labeled string with the procedure identifier, 
     *         parameters, and body if the procedure is not a header
     */
    public String toString()
    {
        String s = "Procedure ";

        if (isHeader)
        {
            s += "header";
        }
        else
        {
            s += "definition";
        }

        s += " = " + this.identifier;

        s += "\n\tParameters: " + parameters.toString();
        
        if (!isHeader)
        {
            s += "\n\tNon-parameters: " + nonParameters.toString();
            s += "\n\t" + this.body.toString().replaceAll("\n", "\n\t");
        }

        return s;
    } // public String toString
} // public class ProcedureDeclaration
