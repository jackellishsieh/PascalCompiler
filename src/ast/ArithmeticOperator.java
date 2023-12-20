package ast;

import emitter.Emitter;
import environment.RuntimeEnvironment;

/**
 * BinaryIntegerOperator defines a semantic object corresponding to 
 * an arithmetic binary operator expression
 * 
 * @author Jack Hsieh
 * with assistance from Clarice Wang
 * @version 2022/03/11
 */
public class ArithmeticOperator extends Expression
{
    private String operator;        // the operator
    private Expression operand1;    // first operand
    private Expression operand2;    // second operand
        
    private Class<?> type1;
    private Class<?> type2;
    
    /**
     * Constructs an arithmetic operator object with the provided expressions and operator
     *  
     * @precondition the provided operator is a valid operator among +, -, *, /, and mod.
     * @postcondition This arithmetic operator object is constructed with the 
     *                provided expressions and operator.
     * 
     * @param operator  the binary operator string
     * @param operand1  the first operand
     * @param operand2  the second operand
     * @throws SemanticErrorException if the expression evaluation types
     * are not compatible with the provided operator
     */
    public ArithmeticOperator(String operator, Expression operand1, 
            Expression operand2) throws SemanticErrorException
    {   
        // System.out.println("Created Binary Operator!");
        this.operator = operator;
        this.operand1 = operand1;
        this.operand2 = operand2;
        

        type1 = this.operand1.getEvaluationType();
        type2 = this.operand2.getEvaluationType();
        
        if (type1.equals(Integer.class) && type2.equals(Integer.class))
        {
            super.evaluationType = Integer.class;
        }
        else
        {
            String message = "Error: Operand types ";
            message += type1.toString() + " and " + type2.toString();
            message += " are incompatible with operator " + this.operator;  
            throw new SemanticErrorException(message);
        } // else
    } // public ArithmeticOperator
    
    /**
     * Returns the evaluated integer value of the arithmetic operator
     * 
     * @precondition operand1 and operand2 are of types compatible with the operator
     *               and the operator is valid.
     * 
     * @param runtimeEnvironment    the runtime environment to evaluate the expression within
     * @return the Integer value resulting from the operation and operands
     * @throws SemanticErrorException if the operator is invalid
     */
    @Override
    public Object evaluate(RuntimeEnvironment runtimeEnvironment) throws SemanticErrorException
    {     
        Object value = null;

        if (type1.equals(Integer.class) && type2.equals(Integer.class))
        {
            Integer value1 = (Integer) operand1.evaluate(runtimeEnvironment);
            Integer value2 = (Integer) operand2.evaluate(runtimeEnvironment);
                
            switch (operator)
            {
                case ("+"): 
                    value = value1 + value2;
                    break;
                case ("-"): 
                    value = value1 - value2;
                    break;
                case ("*"): 
                    value = value1 * value2;
                    break;
                case ("/"): 
                    value = value1 / value2;
                    break;
                case ("mod"): 
                    value = value1 % value2;
                    break;
            }
        }
            
        return value;
    } 
    
    /**
     * Compiles the arithmetic operator and writes the MIPS assembly code 
     * to the provided emitter.
     * 
     * @precondition If the operator is multiplication, the product of the operands does not exceed 
     *               the integer limit.
     * @postcondition The emitter has emit the arithmetic operator converted to assembly code.
     * 
     * @param emitter   the emitter to output the MIPS assembly code to
     */
    public void compile(Emitter emitter)
    {
        // Document
        emitter.emit("# compute " + operator + " operator");
        
        // Evaluate the first operand and push onto the stack
        operand1.compile(emitter);
        emitter.emitPush("$v0");
        
        // Evaluate the second operand
        operand2.compile(emitter);
        
        // Pop the first operand from the stack into $t0
        emitter.emitPop("$t0");
        
        // Evaluate based on the operator
        switch (operator)
        {
            case ("+"): 
                emitter.emit("addu $v0 $t0 $v0");
                break;
            case ("-"): 
                emitter.emit("subu $v0 $t0 $v0");
                break;
            case ("*"): 
                emitter.emit("mult $t0 $v0");
                emitter.emit("mflo $v0");
                break;
            case ("/"): 
                emitter.emit("div $t0 $v0");
                emitter.emit("mflo $v0");
                break;
            case ("mod"): 
                emitter.emit("div $t0 $v0");
                emitter.emit("mfhi $v0");
                break;
        }
        
        emitter.emit("");
        
    } // public void compile
    
    /**
     * Returns a string representation of the AST arithmetic operator node
     * 
     * @return a labeled string with the node type, the operator, and the operands
     */
    @Override
    public String toString()
    {
        String s = "Arithmetic Operator: " + operator;
        s += "\n\t" + operand1.toString().replaceAll("\n", "\n\t");
        s += "\n\t" + operand2.toString().replaceAll("\n", "\n\t");
        
        return s;
    } // public String toString()
} // public class ArithmeticOperator
