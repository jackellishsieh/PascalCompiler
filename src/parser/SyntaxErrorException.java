package parser;
/**
 * SyntaxErrorException is a subclass of Exception and is thrown to indicate a 
 * parsing error. The exception is thrown when a stream fails
 * to match a Parser's grammar.
 * 
 * @author Jack Hsieh
 * @version 2022/03/06
 */
public class SyntaxErrorException extends Exception
{
    /**
     * Default constructor for SyntaxErrorException
     */
    public SyntaxErrorException()
    {
        super();
    }
    /**
     * Constructor for SyntaxErrorException that includes a reason for the error.
     * 
     * @param reason    the string reason for the error
     */
    public SyntaxErrorException(String reason)
    {
        super(reason);
    }
}