package ast;

import emitter.Emitter;
import environment.RuntimeEnvironment;

/**
 * RelativeOperator defines a semantic object corresponding to 
 * a relative operator expression
 * 
 * @author Jack Hsieh
 * with assistance from Clarice Wang
 * @version 2022/03/19
 */
public class RelativeOperator extends Expression
{
    private String operator;
    private Expression operand1;
    private Expression operand2;
    
    private Class<?> type1;
    private Class<?> type2;
    
    /**
     * Constructs a relative operator object with the provided expressions and operator
     *  
     * @postcondition This relative operator object is constructed with the 
     *                provided expressions and operator.
     * 
     * @param operator  the binary operator string
     * @param operand1  the first operand
     * @param operand2  the second operand
     * @throws SemanticErrorException if the operands do not both evaluate to integers
     */
    public RelativeOperator(String operator, Expression operand1, Expression operand2) 
            throws SemanticErrorException
    {
        // Initialize the operator
        // System.out.println("Created Relative Operator!");
        this.operator = operator;
        this.operand1 = operand1;
        this.operand2 = operand2;
    
        // Set the evaluation type and check types
        type1 = this.operand1.getEvaluationType();
        type2 = this.operand2.getEvaluationType();
    
        if (type1.equals(Integer.class) && type2.equals(Integer.class))
        {
            super.evaluationType = Boolean.class;
        }
        else
        {
            String message = "Error: Operand types ";
            message += type1.toString() + " and " + type2.toString();
            message += " are incompatible with operator " + this.operator;  
            throw new SemanticErrorException(message);
        } // else    
    } // public RelativeOperator
        
    /**
     * Returns the evaluated boolean value of the relative operator
     * 
     * @precondition operand1 and operand2 evaluate to integer objects 
     *               and the internal operator symbol matches a relative operator
     * 
     * @param runtimeEnvironment    the runtime environment to evaluate the expression within
     * @return the Integer value resulting from the operation and operands
     * @throws SemanticErrorException if the operator is invalid.
     */
    @Override
    public Boolean evaluate(RuntimeEnvironment runtimeEnvironment) throws SemanticErrorException
    {
        Boolean value = null;
        
        Integer value1 = (Integer) operand1.evaluate(runtimeEnvironment);
        Integer value2 = (Integer) operand2.evaluate(runtimeEnvironment);   

        switch (operator)
        {
            case ("="): 
                value = value1.equals(value2);
                break;
            case ("<>"): 
                value = !value1.equals(value2);
                break;
            case ("<"): 
                value = value1 < value2;
                break;
            case (">"): 
                value = value1 > value2;
                break;
            case ("<="): 
                value = value1 <= value2;
                break;
            case (">="): 
                value = value1 >= value2;
                break;
        }
        
        return value;
    }
    
    /**
     * Compiles conditonal jump code using the given label
     * 
     * @precondition The operands are of integer type.
     * @postcondition The emitter has emit code that conditionally jumps
     *                to the label if the condition is FALSE
     *                converted to assembly code.
     * 
     * @param emitter   the emitter to output the MIPS assembly code to
     * @param label     the label to write a conditional jump to
     */
    public void compile(Emitter emitter, String label)
    {
        // Evaluate the first operand
        this.operand1.compile(emitter);
        
        // Load into stack
        emitter.emitPush("$v0");
        
        // Evaluate the second operand
        this.operand2.compile(emitter);
        
        // Pop stack into $t0
        emitter.emitPop("$t0");
        
        // Choose the right branch
        String branch = "";
        
        switch (operator)
        {
            case ("<="): 
                branch += "bgt";
                break;
            case ("<"): 
                branch += "bge";
                break;
            case (">="): 
                branch += "blt";
                break;
            case (">"): 
                branch += "ble";
                break;
            case ("<>"): 
                branch += "beq";
                break;
            case ("="):
                branch += "bne";
                break;
        }
        
        // Complete the branch
        branch += " $t0 $v0 " + label;
        emitter.emit(branch);
        emitter.emit("");
    }
    
    /**
     * Returns a string representation of the AST relative operator node
     * 
     * @return a labeled string with the node type, the operator, and the operands
     */    
    @Override
    public String toString()
    {
        String s = "Relative Operator: " + operator;
        s += "\n\t" + operand1.toString().replaceAll("\n", "\n\t");
        s += "\n\t" + operand2.toString().replaceAll("\n", "\n\t");
        
        return s;
    } // public String toString()
} // public class RelativeOperator