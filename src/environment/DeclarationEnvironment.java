package environment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ast.ProcedureDeclaration;
import ast.SemanticErrorException;
import ast.Statement;
import ast.Variable;

/**
 * DeclarationEnvironment globally stores the definitions of variables and procedures.
 * DeclarationEnvironment should be changed during or after runtime.
 * 
 * @author Jack Hsieh
 * @version 2022/05/29
 */
public class DeclarationEnvironment
{
    // Stores the variables and their types post-declaration
    private Map<String, Variable> variables; 
    
    // Stores the procedures and their definitions post-declaration
    private Map<String, ProcedureDeclaration> procedures;
    
    /**
     * Constructs an empty declaration environment.
     * 
     * @postcondition The empty declaration environment is set 
     *                to store procedures and variables.
     */
    public DeclarationEnvironment()
    {
        variables = new HashMap<String, Variable>();
        procedures = new HashMap<String, ProcedureDeclaration>();
    }
    
    /**
     * Declares a variable with its identifier and type.
     * 
     * @postcondition If a variable with the given identifier does not yet exist,
     *                a new variable is declared within the declaration environment 
     *                with the given identifier and type.
     * 
     * @param identifier    the identifier of the new variable
     * @param type          the type of the new variable
     * @throws SemanticErrorException if the declaration environment already contains
     *         a variable with the given identifier
     */
    public void declareVariable(String identifier, Class<?> type) throws SemanticErrorException
    {
        if (variables.containsKey(identifier))
        {
            throw new SemanticErrorException("Error: Variable " + identifier + " already declared");
        }
        
        variables.put(identifier, new Variable(identifier, type));
    }
    
    /**
     * Retrieves a variable from its identifier.
     *  
     * @param identifier    the identifier of the desired variable
     * @return the variable corresponding to the identifier within the declaration environment
     *         if it exists
     * @throws SemanticErrorException if the declaration environment does not contain
     *         a variable with the given identifier
     */
    public Variable getVariable(String identifier) throws SemanticErrorException
    {
        if (!variables.containsKey(identifier))
        {
            String message = "Error: Variable " + identifier + " was not declared";
            throw new SemanticErrorException(message);
        }
        
        return variables.get(identifier);
    }
    
    /**
     * Returns whether the identifier names a variable declared in this declaration environment
     * 
     * @param identifier the identifier of the desired variable
     * @return true if the declaration environment contains a variable with the given identifier,
     *         false otherwise.
     */
    public boolean containsVariable(String identifier)
    {
        return variables.containsKey(identifier);
    }
    
    /**
     * Declares a new procedure with its identifier, parameters, and body.
     * 
     * @postcondition If a procedure with the given identifier does not yet exist,
     *                a new procedure is declared within the declaration environment 
     *                with the given identifier, parameters, and body.
     * 
     * @param identifier    the identifier of the new procedure
     * @param parameters    the parameters of the new procedure
     * @param nonParameters    the non-parameter local variables of the new procedure
     * @param body          the body of the new procedure
     * @throws SemanticErrorException if the declaration environment already contains
     *         a procedure with the given identifier
     */
    public void declareProcedure(String identifier, List<Variable> parameters,
            List<Variable> nonParameters, Statement body) throws SemanticErrorException
    {
        if (procedures.containsKey(identifier))
        {
            String message = "Error: Procedure " + identifier + " is already declared";
            throw new SemanticErrorException(message);
        }
        
        Variable returnVariable = this.getVariable(identifier);

        ProcedureDeclaration procedure = new ProcedureDeclaration(identifier, parameters, 
                returnVariable);
        
        procedures.put(identifier, procedure);
    }
    
    /**
     * Declares a new bodiless header with its identifier and parameters
     * 
     * @postcondition If a header with the given identifier does not yet exist,
     *                a new header is declared within the declaration environment 
     *                with the given identifier and parameters.
     * 
     * @param identifier    the identifier of the new procedure
     * @param parameters    the parameters of the new procedure
     * @throws SemanticErrorException if the declaration environment already contains
     *         a procedure with the given identifier
     */
    public void declareHeader(String identifier, List<Variable> parameters) 
            throws SemanticErrorException
    {
        this.declareProcedure(identifier, parameters, null, null);
    }

    /**
     * Retrieves a procedure from its identifier.
     *  
     * @param identifier    the identifier of the desired procedure
     * @return the procedure corresponding to the identifier within the declaration environment
     *         if it exists
     * @throws SemanticErrorException if the declaration environment does not contain
     *         a procedure with the given identifier
     */    
    public ProcedureDeclaration getProcedure(String identifier) throws SemanticErrorException
    {
        if (!procedures.containsKey(identifier))
        {
            String message = "Error: Procedure " + identifier + " was not declared";
            throw new SemanticErrorException(message);
        }
        
        return procedures.get(identifier);
    }
    
    /**
     * Returns all variables as a set of entries
     * 
     * @return all declared variables as a HashSet
     */
    public Set<Variable> getVariableSet()
    {
        return new HashSet<Variable>(variables.values());
    }
    
    /**
     * Returns all procedures.
     * 
     * @return all declared variables as a HashSet
     */
    public Set<ProcedureDeclaration> getProcedureSet()
    {
        return new HashSet<ProcedureDeclaration>(procedures.values());
    }
    /**
     * Returns a string representation of all declared variables and procedures
     * 
     * @return a string consisting of a list of printed variable declarations followed
     *         by a list of printed procedure declarations
     */
    public String toString()
    {
        String s = "Declaration environment\n\tVariables";

        for (Map.Entry<String, Variable> entry : variables.entrySet())
        {
            s += "\n\t\tIdentifier = " + entry.getKey();
            s += ", Type = " + entry.getValue().getEvaluationType().getSimpleName();
        }
        
        s += "\n\tProcedures";
        
        for (ProcedureDeclaration procedure : procedures.values())
        {
            s += "\n\t\t" + procedure.toString().replaceAll("\n","\n\t\t");
        }
        
        return s;
    } // public String toString()
} // public class DeclarationEnvironment
