package scanner;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Scanner is a simple scanner for Compilers and Interpreters 2022.
 * A Scanner can tokenize a stream of characters
 * into a series of number, identifier, and operator lexemes.
 * 
 * @author Jack Hsieh
 * with assistance from: Anu Datar, Matthew Lau, Ritu Belani
 * @version 2022/01/26
 */
public class Scanner
{
    /*
     * Stream reading
     */
    public BufferedReader in;       // the input stream
    private char currentChar;       // the lookahead
    private boolean eof;            // the flag denoting end of file
    
    /*
     * For resetting
     */
    private char markedCurrentChar;
    private boolean markedEof;
    private boolean isMarked;
    
    /*
     * Enumerator to help process single line comments
     * as a finite state machine process
     */
    private static enum SingleLineCommentState 
    {
        DEFAULT_STATE,          // default state: no slash recently
        ONE_SLASH_STATE,        // one slash read: not determined if the next character is a slash
        TWO_SLASH_STATE,        // two consecutive slashes read, beginning a comment
        ALONE_SLASH_STATE       // one slash and one NON-slash character was read
    }
    
    /*
     * For matching scanned tokens to certain types
     */
    private static String numberRegex = "[0-9]+";                       // number regex
    private static String identifierRegex = "[a-zA-Z][a-zA-Z0-9_]*";    // identifier regex
    private static String arithmeticOperatorRegex = "+|-|/|\\*|mod";    // arithmetic operator regex
    private static String relativeOperatorRegex = "=|<>|<|>|<=|>=";     // relative operator regex
    // private static String binaryBooleanOperatorRegex = ""
    
    /*
     * Set of Pascal keywords
     */
    private static Set<String> keywordSet = new HashSet<String>(Arrays.asList(
            "BEGIN", "END","IF", "THEN", "WHILE", "DO", "FOR", "TO", "WRITELN", "VAR", 
            "PROCEDURE", "integer", "boolean"));
    
    /**
     * Tests whether a token constitutes a number
     * 
     * @param token     the token to test 
     * @return true if the input matches regex [0-9]+, false otherwise
     */
    public static boolean isNumber(String token)
    {
        return token.matches(Scanner.numberRegex);
    }
    
    /**
     * Tests whether a token constitutes a identifier that is not a keyword
     * 
     * @param token     the token to test 
     * @return true if the input matches regex [a-zA-Z][a-zA-Z0-9_]*, false otherwise
     */
    public static boolean isKeyword(String token)
    {
        return keywordSet.contains(token);
    }
    
    /**
     * Tests whether a token constitutes a Pascal keyword
     * 
     * @param token     the token to test 
     * @return true if the input matches regex [a-zA-Z][a-zA-Z0-9_]* and is not a Pascal keyword,
     *         false otherwise
     */
    public static boolean isIdentifier(String token)
    {
        return token.matches(Scanner.identifierRegex) && !isKeyword(token);
    }
    
    /**
     * Tests whether a token constitutes an arithmetic operator
     * 
     * @param token     the token to test 
     * @return true if the input matches +|-|/|\\*|mod, false otherwise
     */
    public static boolean isArithmeticOperator(String token)
    {
        return token.matches(Scanner.arithmeticOperatorRegex);
    }
    
    /**
     * Tests whether a token constitutes a relative operator
     * 
     * @param token     the token to test 
     * @return true if the input matches =|<>|<|>|<=|>=, false otherwise
     */
    public static boolean isRelativeOperator(String token)
    {
        return token.matches(Scanner.relativeOperatorRegex);
    }
    
    /**
     * Constructs a Scanner using the given input stream.
     * 
     * @precondition The provided input stream is initialized to a valid stream.
     *               The input stream permits unlimited mark range.
     * 
     * @postcondition This scanner is set to tokenize the provided input stream
     * and has processed the first character in the stream if it exists with the 
     * end-of-file flag set appropriately.
     * 
     * @param inStream  the input stream to be tokenized by this scanner
     */
    public Scanner(InputStream inStream)
    {
        in = new BufferedReader(new InputStreamReader(inStream));
        eof = false;
        isMarked = false;
        getNextChar();
    }
    
    /**
     * Constructs a Scanner using the given string as an input stream.
     * 
     * @precondition The provided string is initialized to a valid string.
     * 
     * @postcondition This scanner is set to tokenize the input string
     * and has processed the first character in the string if it exists with the 
     * end-of-file flag set appropriately.
     * 
     * @param inString  the input string to be tokenized by this scanner
     */
    public Scanner(String inString)
    {
        in = new BufferedReader(new StringReader(inString));
        eof = false;
        isMarked = false;
        getNextChar();
    }
    
    /**
     * Reads the next character from the internal input stream.
     * 
     * @precondition The internal input stream is set to a valid input stream. 
     * 
     * @postcondition If reading the input stream fails, the error is printed to the console 
     *                and program execution is aborted with return code -1.
     *                Otherwise, if the end of the stream is reached or a period is read, 
     *                the end-of-file flag is set true and the internal input stream is closed. 
     *                Otherwise, the scanner stores the next character in the stream
     *                and the internal input stream's reading position is advanced one character.
     */
    private void getNextChar()
    {        
        try
        {
            int nextInt = in.read();            // the next integer to be read from the stream
                        
            // If the end of the stream is reached OR 
            // a period is read ("."), set the end-of-file flag
            // and close the stream.
            if (nextInt == -1 || nextInt == (int) '.')
            {
                eof = true;
                
                // If isMarked, expected that will not be closed
                if (!isMarked)
                {
                    in.close();
                }
            }
            
            else
            {
                currentChar = (char) nextInt;
            }
        } // try
        
        catch (IOException exception)
        {
            exception.printStackTrace();
            
            // Datar said to comment out
            // in.close();
            
            // Exit the program. Exit code doesn't matter as long as it's not zero.
            System.exit(-1);
        }
    }
    
    /**
     * Reads the next letter and checks consistency
     * 
     * @precondition The internal input stream is set to a valid input stream.  
     * 
     * @postcondition If the expected character matches the scanner's current character, 
     *                load the next character into the scanner.
     *                Otherwise, throw a ScanErrorException
     *                
     * @param expected  the expected character
     * @exception ScanErrorException if the current lookahead read character does not match the 
     *            expected character.
     */
    private void eat(char expected) throws ScanErrorException
    {
        if (expected != currentChar)
        {
            String exceptionMessage = "Illegal character - expected \"";
            exceptionMessage += currentChar + "\" and found \"" + expected + "\"";
            throw new ScanErrorException(exceptionMessage);
        }
        else
        {
            this.getNextChar();
        }
    }
    
    /**
     * Marks position
     * 
     * @postcondition the scanner stream reading position is marked at the current position,
     *                and the current char and eof are stored in anticipation
     */
    public void markPosition()
    {
        // Marks position and stores eof and char
        try
        {
            in.mark(0);
            
            markedCurrentChar = currentChar;
            markedEof = eof;
            isMarked = true;
                        
            // TODO 
            // System.out.println("MARK: Marked character " + markedCurrentChar + " with EOF " + markedEof);
        }
        
        catch (Exception e)
        {
            // This will not occur
        }
    } // public void markPosition()
    
    
    /**
     * Returns to the marked scanner position
     * 
     * @postcondition the scanner stream reading position is reset to the last marked position
     *                and the marked char and eof are restored.
     *                The mark is consumed.
     *  
     * @throws IllegalStateException if the scanner was not marked before
     */
    public void returnToMark() throws IllegalStateException
    {
        // Throw an exception if the scanner is not marked
        if (!isMarked)
        {
            throw new IllegalStateException("Error: Scanner not marked");
        }
        
        isMarked = false;
        
        // Rewinds to mark
        try
        {
            in.reset();
        }
        
        catch (Exception e)
        {
            // Will not execute
        }
                        
        // Rewinds current char and eof
        currentChar = markedCurrentChar;
        eof = markedEof;

        // TODO
        // System.out.println("RETURNED: Returned to character " + currentChar + " with EOF " + eof);
    }
    
    /**
     * Returns whether the scanner has a next token
     * 
     * @return whether there are more characters to read in the file
     */
    public boolean hasNext()
    {
        return ! eof;
    }
    
    /**
     * Returns whether a character is a digit
     * 
     * @param testChar  the character to be tested
     * @return true if the input character is a digit, false otherwise
     */
    public static boolean isDigit(char testChar)
    {
        return ('0' <= testChar && testChar <= '9');
    }
    
    /**
     * Returns whether a character is a letter
     * 
     * @param testChar  the character to be tested
     * @return true if the input character is a letter, false otherwise
     */
    public static boolean isLetter(char testChar)
    {
        return (('A' <= testChar && testChar <= 'Z') || ('a' <= testChar && testChar <= 'z'));
    }
    /**
     * Returns whether a character is whitespace
     * 
     * @param testChar  the character to be tested
     * @return true if the input character is whitespace (' ', '\t', '\r', or '\n'), false otherwise
     */
    public static boolean isWhitespace(char testChar)
    {        
        return (testChar == ' ' || testChar == '\t' || testChar == '\r' || testChar == '\n');
    }
    
    /**
     * Returns whether a character is a single non-slash operator 
     * (i.e. an operator that is not a slash or the start of a two-character operator) 
     * 
     * @param testChar  the character to be tested
     * @return true if the input character is an =, +, -, *, %, 
     *         left parenthesis, or right parenthesis, false otherwise
     */
    public static boolean isSingleOperator(char testChar)
    {
        return (testChar == '=' || testChar ==  '+' || testChar ==  '-' || testChar ==  '*' 
                || testChar == '%' || testChar == '(' || testChar == ')');
    }
    
    /**
     * Returns whether a character may be the start of a double operator 
     * (i.e. an operator that is the start of a two-character operator) 
     * 
     * @param testChar  the character to be tested
     * @return true if the input character is an >, <, :, false otherwise
     */
    public static boolean isPotentialDoubleOperator(char testChar)
    {
        return (testChar == '>' || testChar ==  '<' || testChar ==  ':');
    }
    
    /**
     * Returns whether a character is a separator
     * 
     * @param testChar  the character to be tested
     * @return true if the input character is a ; or ,
     *         false otherwise
     */
    public static boolean isSeparator(char testChar)
    {
        return (testChar == ';' || testChar == ',');
    }
    
    /**
     * Scans and returns number 
     * (a sequence beginning with a digit optionally followed by more digits).
     * 
     * @precondition The input stream contains a next character that is a digit.
     * 
     * @postcondition The scanner position is advanced to the next
     *                non-digit character in the stream. 
     * 
     * @return the tokenized number lexeme
     * @exception ScanErrorException if a read value fails to match the expected value
     */
    private String scanNumber() throws ScanErrorException
    {
        String lexeme = "";         // the read lexeme
        
        // Begin with a digit
        lexeme += currentChar;
        eat(currentChar);
        
        // Continuing adding digits until there are no more contiguously
        while (hasNext() && Scanner.isDigit(currentChar))
        {
            lexeme += currentChar;
            this.eat(currentChar);
        }
        
        return lexeme;
    }
    
    /**
     * Scans and returns an identifier (a sequence beginning with a letter
     * optionally followed by alphanumeric characters)
     * 
     * @precondition The input stream contains a next character that is a letter.
     * 
     * @postcondition The scanner position is advanced to the next 
     *                non-alphanumeric character in the stream. 
     * 
     * @return the tokenized identifier lexeme
     * @exception ScanErrorException if a read value fails to match the expected value
     */
    private String scanIdentifier() throws ScanErrorException
    {
        String lexeme = "";         // the read lexeme
        
        // Begin with a letter
        lexeme += currentChar;
        eat(currentChar);
        
        // Continuing adding alphanumeric characters until there are no more contiguously
        while (hasNext() && (Scanner.isLetter(currentChar) || Scanner.isDigit(currentChar)))
        {
            lexeme += currentChar;
            this.eat(currentChar);
        }
        
        return lexeme;
    }
    
    /**
     * Scans and returns a single operator that is NOT a slash and cannot be part of a 
     * two-character operator. (slash needs to be treated as a special case due to the possibility
     * it begins a comment)
     * 
     * @precondition The input stream contains a next character that is a 
     * single non-slash operator character (exactly one of the following:
     * =, +, -, *, %, left parenthesis, or right parenthesis)
     * 
     * @postcondition The scanner position is advanced one character in the stream. 
     * 
     * @return the tokenized operator lexeme
     * @exception ScanErrorException if a read value fails to match the expected value
     */
    private String scanSingleOperator() throws ScanErrorException
    {
        String lexeme = "";         // the read lexeme
        
        // Begin with an operator
        lexeme += currentChar;
        eat(currentChar);
        
        return lexeme;
    }

    /**
     * Scans and returns a single or a double operator where the first character may
     * or may not be part of a two-character operator. 
     * 
     * @precondition The input stream contains a next character that is an
     * operator character that can be followed by another character to form a different operator
     * (exactly one of the following: <, >, :)
     * 
     * @postcondition If the scanner reads a single operator, the scanner position
     * is advanced two characters forward. in the stream
     * If the scanner reads a double operator, the scanner position 
     * is advanced three characters forward.
     * 
     * @return the single operator (<, >, :) if the following character does not 
     * complete a double operator and a double operator (<=, >=, <>, :=) if appropriate.
     * 
     * @throws ScanErrorException if a read value fails to match the expected value or
     * a colon (:) is NOT followed by a "="
     */
    private String scanPotentialDoubleOperator() throws ScanErrorException
    {
        String lexeme = "";         // the read lexeme
        
        // Begin with the first character of the operator
        char firstChar = currentChar;
        lexeme += currentChar;
        eat(currentChar);
        
        /*
         * If the first character is a <, check for = or > after to determine
         * whether to scan a single or double operator
         */
        if (firstChar == '<')
        {
            /*
             * If = or > follows the <, it's a double operator. 
             * Move forward and store both in the lexeme
             */
            if (currentChar == '>' || currentChar == '=')
            {
                lexeme += currentChar;
                eat(currentChar); 
            }
            
            /*
             * Otherwise, it's a one-character operator. Store only the first character.
             * Don't move forward since the scanner is one ahead.
             */
        }
        
        /*
         * If the first character is a >, check for = after
         */
        else if (firstChar == '>')
        {
            /*
             * If = follows the >, it's a double operator. 
             * Move forward and store both in the lexeme
             */
            if (currentChar == '=')
            {
                lexeme += currentChar;
                eat(currentChar);
            }
            
            /*
             * Otherwise, it's a one-character operator. Store only the first character.
             * Don't move forward since the scanner is one ahead.
             */
        }
        
        /*
         * If the first character is a :, check for = after
         */
        else if (firstChar == ':')
        {
            /**
             * If = follows the :, it's a double operator.
             * Move forward and store both in the lexeme.
             */
            if (currentChar == '=')
            {
                lexeme += currentChar;
                eat(currentChar);
            }
            
            /*
             * Otherwise, it's a one-character operator. Store only the first character.
             * Don't move forward since the scanner is one ahead.
             */
        } // else if for colon case
        
        return lexeme;
    }
    
    /**
     * Scans and returns a separator (a sequence containing exactly one semicolon or comma)
     * 
     * @precondition The input stream contains a next character that is a separator character.
     * 
     * @postcondition The scanner position is advanced one character in the stream. 
     * 
     * @return the tokenized operator lexeme
     * @exception ScanErrorException if a read value fails to match the expected value
     */
    private String scanSeparator() throws ScanErrorException
    {
        String lexeme = "";         // the read lexeme
        
        // Begin with an operator
        lexeme += currentChar;
        eat(currentChar);
        
        return lexeme;
    }
    
    /**
     * Returns the next token from the input stream
     * 
     * @precondition The input stream is initialized.
     * 
     * @postcondition The scanner position is moved after the next token if the end of the file 
     *                is not yet reached and the following non-whitespace/comment is tokenizable.
     *                If the following non-whitespace/comment is not tokenizable, the error 
     *                is printed to the console and the scanner is advanced one character.
     *                The end-of-file flag is updated appropriately.
     * 
     * @return the next number, identifier, operator, separator, or END token as appropriate
     * @exception ScanErrorException if a read character does not match the expected value
     */
    public String nextToken() throws ScanErrorException
    {
        try
        {
            String lexeme;      // the lexeme to be returned
            
            // Tracks the scanner's state regarding comment processing
            // Begins in the default state
            SingleLineCommentState commentState = SingleLineCommentState.DEFAULT_STATE;
            
            /*
             * Skip whitespace and comments
             * Moves between four states (default, one slash, two slash, alone slash)
             * to track whether a read forward slash (/) begins a comment or is its own operator.
             * Loop until the end of file, an isolated slash, or a non-whitespace character
             * that is not part of a comment.
             */
            while (hasNext() 
                    && commentState != SingleLineCommentState.ALONE_SLASH_STATE
                    && (Scanner.isWhitespace(currentChar)
                            || currentChar == '/'
                            || commentState == SingleLineCommentState.ONE_SLASH_STATE 
                            || commentState == SingleLineCommentState.TWO_SLASH_STATE)) 
            {
                /*
                 *  If we're in the default state, 
                 *  transition to the one slash state if and only if the character is a slash.
                 *  Otherwise, the character is whitespace.
                 *  Eat in any case.
                 */
                if (commentState == SingleLineCommentState.DEFAULT_STATE)
                {
                    if (currentChar == '/')
                    {
                        // Default --> One slash
                        commentState = SingleLineCommentState.ONE_SLASH_STATE;
                    }
                    
                    eat(currentChar);
                }
                
                /*
                 * If we're in the one slash state (don't know if next character is slash or not),
                 * if the current character is another slash, transition to the two slash state 
                 * and eat and begin a comment. otherwise, transition to the alone state and do NOT 
                 * eat because the scanner is one character ahead.
                 */
                else if (commentState == SingleLineCommentState.ONE_SLASH_STATE)
                {
                    if (currentChar == '/')
                    {
                        // One slash --> Two-slash
                        commentState = SingleLineCommentState.TWO_SLASH_STATE;
                        eat(currentChar);
                    }
                    else
                    {
                        // One slash --> Alone slash
                        commentState = SingleLineCommentState.ALONE_SLASH_STATE;
                    } // else
                } // else if (commentState == SingleLineCommentState.ONE_SLASH_STATE)
                
                /*
                 * If we're in the two slash state (inside a comment),
                 * if the next character is a new line or carriage return, return to default state.
                 * Eat in any case.
                 */
                else if (commentState == SingleLineCommentState.TWO_SLASH_STATE)
                {
                    if (currentChar == '\r' || currentChar == '\n')
                    {
                        // Two slash --> Default
                        commentState = SingleLineCommentState.DEFAULT_STATE;
                    }
                    
                    eat(currentChar);
                } // else if (commentState == SingleLineCommentState.TWO_SLASH_STATE)
            } // while loop for whitespace and comments
            
            /*
             * If we reached the end of the stream and no slash was read, return an "END" token 
             * If an alone slash was read OR the end of file was reached and a slash was read, 
             * store a slash.
             * If the next character is a digit, scan and store a number
             * If the next character is a letter, scan and store an identifier
             * If the next character is a potential double operator, 
             * scan and store a single or double operator as appropriate.
             * If the next character is a definite single operator, scan and store an operator
             * If the next character is a separator, scan and store a separator
             * Otherwise, throw exception for untokenizable type
             * The order of these checks should not matter much since they should be disjoint.
             */
            if ( ! hasNext() && commentState != SingleLineCommentState.ONE_SLASH_STATE)
            {
                lexeme = "EOF";
            }
            else if (commentState == SingleLineCommentState.ALONE_SLASH_STATE 
                    || (!hasNext() && commentState == SingleLineCommentState.ONE_SLASH_STATE))
            {
                lexeme = "/";
            }
            else if (Scanner.isDigit(currentChar))
            {
                lexeme = scanNumber();
            }
            else if (Scanner.isLetter(currentChar))
            {
                lexeme = scanIdentifier();
            }
            else if (Scanner.isPotentialDoubleOperator(currentChar))
            {
                lexeme = scanPotentialDoubleOperator();
            }
            else if (Scanner.isSingleOperator(currentChar))
            {
                lexeme = scanSingleOperator();
            }
            else if (Scanner.isSeparator(currentChar))
            {
                lexeme = scanSeparator();
            }
            else
            {
                String exceptionMessage = "Illegal scan - encountered untokenizable character '";
                exceptionMessage += (currentChar + "'");
                
                throw (new ScanErrorException(exceptionMessage));
            }
            
            return lexeme;
        }
        
        /*
         * If an exception is encountered, keep going and return an empty string
         */
        catch (ScanErrorException exception)
        {
            // System.exit(-1);
            System.out.println("Encountered exception during scanning: ");
            exception.printStackTrace();
            
            System.out.println("Returning empty token and continuing...");
            eat(currentChar);
            
            return "";
        } // catch     
    } // nextToken
} // Scanner
