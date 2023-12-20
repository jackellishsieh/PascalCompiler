package ast;

import java.util.Iterator;
import java.util.List;

import emitter.Emitter;
import environment.RuntimeEnvironment;

/**
 * ProcedureCall defines a semantic object corresponding to the call
 * of a procedure.
 * 
 * @author Jack Hsieh
 * @version 2022/05/29
 */
public class ProcedureCall extends Expression
{
    private ProcedureDeclaration procedure;     // the procedure being called
    private List<Expression> arguments;         // the arguments given to the called procedure

    /**
     * Constructs a procedure call with the provided procedure and arguments.
     * 
     * @precondition All arguments and parameters evaluate to integers (for now).
     * @postcondition If the arguments match the parameters,
     *                this procedure call is constructed with the given procedure and arguments.
     * 
     * @param procedure the procedure being called
     * @param arguments the arguments given to the called procedure
     * @throws SemanticErrorException if the number of arguments does not match
     *         the number of parameters.
     */
    public ProcedureCall(ProcedureDeclaration procedure, List<Expression> arguments)
            throws SemanticErrorException
    {
        this.procedure = procedure;
        this.arguments = arguments;

        // Check that the number of arguments and parameters match
        if (arguments.size() != procedure.getParameters().size())
        {
            String message = "Error: expected " + procedure.getParameters().size();
            message += " arguments but found " + arguments.size();
            throw new SemanticErrorException(message);
        }

        // Set the evaluation type to integer
        super.evaluationType = Integer.class;
    }

    /**
     * Executes the procedure and returns the output.
     * 
     * @precondition The argument types match the parameter types in order.
     * @postcondition The body of the procedure is executed with the given arguments
     *                in a local environment that is the child of a global environment.
     *                Local variables are attempted to match parameters, then global variables,
     *                and finally the local environment.
     * 
     * @return the return value of the procedure evaluated within the provided runtime environment
     * @throws SemanticErrorException if the arguments cannot be evaluated, the body cannot be 
     *                                executed, or the procedure is a bodiless header.
     */
    @Override
    public Object evaluate(RuntimeEnvironment runtimeEnvironment) throws SemanticErrorException
    {
        // Check that the procedure is not a bodiless header
        if (procedure.isHeader())
        {
            String message = "Error: called bodiless procedure header";
            
            System.out.println("\t" + procedure);
            System.out.println("\tBody = " + procedure.getBody());
            
            throw new SemanticErrorException(message);
        }
        
        /*
         * Create a new local environment that is a child of the global environment
         * in which the procedure body (barring calls to other procedures) is to be evaluated.
         * The only parent of the child environment is NOT the current environment.
         */
        RuntimeEnvironment globalEnvironment = RuntimeEnvironment.getGlobalEnvironment();
        RuntimeEnvironment localEnvironment = new RuntimeEnvironment(globalEnvironment);

        // Introduce the procedure return value as a local variable with the default value
        localEnvironment.introduceProcedureValue(procedure);

        /*
         * Introduce each parameter within the local environment
         * and set each to the corresponding argument.
         * Parameters and arguments must be processed in the same order.
         */
        Iterator<Variable> parameterIterator = procedure.getParameters().iterator();
        Iterator<Expression> argumentIterator = arguments.iterator();

        Variable parameter;
        Expression argument;
        Object argumentValue;

        while (parameterIterator.hasNext() && argumentIterator.hasNext())
        {
            parameter = parameterIterator.next();
            argument = argumentIterator.next();
            argumentValue = argument.evaluate(runtimeEnvironment);

            // Introduce the parameter with the argument value as a local variable
            localEnvironment.introduceLocalVariable(parameter, argumentValue);
        }

        // Now execute the body in the local environment
        this.procedure.getBody().execute(localEnvironment);

        // Return the value the procedure return value was set to
        return localEnvironment.getProcedureValue(procedure);
    }

    /**
     * Compiles the procedure call and writes the MIPS assembly code to the provided emitter.
     * 
     * @postcondition The emitter has emit the procedure call converted to 
     *                MIPS assembly.
     * 
     * @param emitter   the emitter to output the MIPS assembly code to
     */
    public void compile(Emitter emitter)
    {
        // Push the return address onto the stack
        emitter.emitPush("$ra");
        
        // Pushes arguments onto the stack
        for (Expression argument: arguments)
        {
            argument.compile(emitter);
            emitter.emitPush("$v0");
        }
        
        // Jump and link to label
        emitter.emit("# jump to procedure " + procedure.getIdentifier());
        emitter.emit("jal proc" + procedure.getIdentifier());
        emitter.emit("");
        
        // Pops arguments from stack
        for (Expression argument: arguments)
        {
            emitter.emitPop("$t0");
        }
        
        // Pop the return address from the stack
        emitter.emitPop("$ra");
    }
    
    /**
     * Returns a string representation of the AST procedure call node
     * 
     * @return a labeled string with the procedure identifier and the arguments
     */
    public String toString()
    {
        String s = "Procedure call = " + this.procedure.getIdentifier();  

        for (Expression argument : arguments)
        {
            s += "\n\t" + argument.toString().replaceAll("\n", "\n\t");
        }

        return s;
    } // public String toString
} // public class ProcedureCall
