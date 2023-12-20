package scanner;
/**
 * ScanErrorException is a sub class of Exception and is thrown to indicate a 
 * scanning error.  Usually, the scanning error is the result of an illegal 
 * character in the input stream.  The error is also thrown when the expected
 * value of the character stream does not match the actual value.
 * 
 * @author Anu Datar (main code)
 * with assistance from Jack Hsieh (documentation)
 * @version 2022/01/26
 *
 */
public class ScanErrorException extends Exception
{
    /**
     * Default constructor for ScanErrorExceptions
     */
    public ScanErrorException()
    {
        super();
    }
    /**
     * Constructor for ScanErrorExceptions that includes a reason for the error.
     * 
     * @param reason    the string reason for the error
     */
    public ScanErrorException(String reason)
    {
        super(reason);
    }
}