package environment;

import java.util.HashMap;
import java.util.Map;

import ast.ProcedureDeclaration;
import ast.Variable;

/**
 * RuntimeEnvironment keeps track of variable values during runtime execution and evaluation.
 * RuntimeEnvironment differs from DeclarationEnvironment in that it is a dynamic construct.
 * 
 * @author Jack Hsieh
 * with assistance from Clarice Wang
 * @version 2022/03/19
 */
public class RuntimeEnvironment
{
    // Must be of String keys to get the variables associated with procedures
    private Map<String, Object> variableMap;        // actual values during execution
    
    private RuntimeEnvironment parentEnvironment;   // null if this environment is global
    
    private static RuntimeEnvironment globalEnvironment; 
    
    private final static Object DEFAULT_VALUE = 0;   // default variable value is 0 
    
    /**
     * 
     * @precondition only one globalEnvironment
     *  @postcondition the runtime environment is ready to associate variables and values
     */
    
    /**
     * Constructs an empty runtime environment.
     * 
     * @postcondition The empty runtime environment is set to store variable values and its
     *                parent environment, if it exists, is identified.
     *                If the parent environment does not exist, then the global runtime environment
     *                is set to this environment.
     * 
     * @param parentEnvironment the environment of which this runtime environment is a child;
     *        null if this environment is the global environment
     */
    public RuntimeEnvironment(RuntimeEnvironment parentEnvironment)
    {
        variableMap = new HashMap<String, Object>();
        this.parentEnvironment = parentEnvironment;
        
        /*
         * Extracts the global environment
         * If the current runtime environment is not the global environment,
         * then the global environment must be the current runtime environment's parent
         */        
        if (parentEnvironment == null)
        {
            RuntimeEnvironment.globalEnvironment = this;
        }
    }

    /**
     * Declares and initializes a local variable within the local scope.
     * 
     * @postcondition The  given variable is initialized in this environment
     *                with the given value.
     * 
     * @param variable the variable that should be inserted into this local environment
     * @param value the value to set the local variable to
     */
    public void introduceLocalVariable(Variable variable, Object value)
    {
        variableMap.put(variable.getIdentifier(), value);
    }
    
    /**
     * Sets the value of a variable within the appropriate scope.
     * 
     * @postcondition If the provided variable exists in the current local scope,
     *                the variable is set to the provided value in the local scope.
     *                If the provided variable exists only within the global scope,
     *                the variable is set to the provided value within the global scope.
     *                If the the provided variable does not exist yet in either scope,
     *                the variable is introduced to the local scope with default value 0. 
     * 
     * @param variable  the variable to set
     * @param value     the value to set the variable to
     */
    public void setVariableValue(Variable variable, Object value)
    {   
        // If this environment contains this variable (e.g. parameter), change the value locally
        if (variableMap.containsKey(variable.getIdentifier()))
        {
            variableMap.put(variable.getIdentifier(), value);
        }
        
        // If this environment doesn't contain this variable, see if the parent contains it
        else if (parentEnvironment != null 
                && parentEnvironment.variableMap.containsKey(variable.getIdentifier()))
        {
            parentEnvironment.setVariableValue(variable, value);
        }
        
        // Otherwise, introduce the variable to this scope alone
        else
        {
            introduceLocalVariable(variable, value);
        } // else
    } // public void setVariableValue
    
    /**
     * Returns the value associated with a variable.
     *  
     * @postcondition If the provided variable exists in neither the current local 
     *                or global scope, the variable is introduced to the global scope
     *                with default value 0.
     *  
     * @param variable  the variable whose value is to be retrieved
     * @return If the provided variable exists in the current local scope,
     *         the associated value in the local scope is returned.
     *         
     *         If the provided variable exists only within the global scope,
     *         the associated value within the global scope is returned.
     *         If the the provided variable does not exist yet in either scope,
     *         the variable is introduced to the local scope with default value 0
     *         and the value 0 is returned.         
     */
    public Object getVariableValue(Variable variable)
    {   
        Object value = null;
        
        // If this environment associates a value with this variable, return that value
        if (variableMap.containsKey(variable.getIdentifier()))
        {
            value = variableMap.get(variable.getIdentifier());
        }
        
        // If this environment doesn't contain this variable, see if the parent contains it
        else if (parentEnvironment != null 
                && parentEnvironment.variableMap.containsKey(variable.getIdentifier()))
        {
            value = parentEnvironment.getVariableValue(variable);
        }
        
        // Otherwise, add it to the local environment as the default value and return it
        else
        {
            introduceLocalVariable(variable, RuntimeEnvironment.DEFAULT_VALUE);
            value = variableMap.get(variable.getIdentifier());
        }
                
        return value;
    }
    
    /**
     * Declares and initializes a local return variable for a procedure within the local scope.
     * 
     * @postcondition The given procedure's return variable is initialized in this environment
     *                with the default value 0.
     * 
     * @param procedure the procedure for which a local return variable must be introduced
     */
    
    public void introduceProcedureValue(ProcedureDeclaration procedure)
    {
        variableMap.put(procedure.getIdentifier(), RuntimeEnvironment.DEFAULT_VALUE);
    }
    
    /**
     * Returns the return value of a procedure.
     *  
     * @precondition The return variable for the given procedure has been previously introduced
     *               as a local variable.
     *  
     * @param procedure the procedure whose return value is to be retrieved
     * @return the value of the procedure return variable within the local scope
     */
    public Object getProcedureValue(ProcedureDeclaration procedure)
    {
        return variableMap.get(procedure.getIdentifier());
    }
    
    /**
     * Returns the parent of this runtime environment
     * 
     * @return the parent environment, or null if this environment is the global environment 
     */
    public RuntimeEnvironment getParentEnvironment()
    {
        return parentEnvironment;
    }
    
    /**
     * Returns the global environment
     * 
     * @precondition A global environment has been created.
     * 
     * @return the global environment
     */
    public static RuntimeEnvironment getGlobalEnvironment()
    {
        return RuntimeEnvironment.globalEnvironment;
    }
    
    /**
     * Returns a string representation of all variables and values
     * 
     * @return a string consisting of a list of printed variables and their assigned values
     */
    public String toString()
    {
        String s = "Runtime environment\n\tVariables";

        for (Map.Entry<String, Object> entry : variableMap.entrySet())
        {
            s += "\n\t\tIdentifier = " + entry.getKey();
            s += ", Value = " + entry.getValue();
        }
        
        return s;
    } // public String toString()
} // public class RuntimeEnvironment
