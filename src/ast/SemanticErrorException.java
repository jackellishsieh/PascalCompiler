package ast;
/**
 * SemanticErrorException is a subclass of Exception and is thrown to indicate a 
 * semantic analysis error. The exception is thrown when a parse tree's semantics fail, 
 * potentially due to an unrecognized identifier.
 * 
 * @author Jack Hsieh
 * with assistance from Clarice Wang
 * @version 2022/03/06
 */
public class SemanticErrorException extends Exception
{
    /**
     * Default constructor for SemanticErrorException
     */
    public SemanticErrorException()
    {
        super();
    }
    
    /**
     * Constructor for SemanticErrorException that includes a reason for the error.
     * 
     * @param reason    the string reason for the error
     */
    public SemanticErrorException(String reason)
    {
        super(reason);
    }
}