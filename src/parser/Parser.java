package parser;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import ast.ArithmeticOperator;
import ast.Assignment;
import ast.Block;
import ast.Expression;
import ast.ForToDo;
import ast.IfThen;
import ast.Literal;
import ast.ProcedureCall;
import ast.ProcedureDeclaration;
import ast.Program;
import ast.RelativeOperator;
import ast.SemanticErrorException;
import ast.Statement;
import ast.Variable;
import ast.WhileDo;
import ast.Writeln;
import environment.DeclarationEnvironment;
import scanner.ScanErrorException;
import scanner.Scanner;

/**
 * Parser is a simple Pascal parser for Compilers and Interpreters 2022.
 * A parser can verify the grammar of and build an executable AST for statement blocks with
 * The parser can then compile the AST to MIPS assembly.
 * 
 * 1. Integer arithmetic with +,-,*,/, mod, and ()
 * 2. If-then conditionals and while and for loops
 * 3. Integer expression binary comparison
 * 4. Print statements
 * 5. Integer and boolean variable storage
 * 6. Procedures with integer return values
 * 
 * 
 * @author Jack Hsieh
 * with assistance from Anu Datar, Krish Maniar, and Clarice Wang.
 * @version 2022/05/29
 */
public class Parser
{
    private Scanner scanner;                    // the token stream
    private String currentToken;                // the lookahead
    
    private DeclarationEnvironment declarationEnvironment; // variable and procedure declarations

    /**
     * Constructs a parser given a token stream
     * 
     * @precondition The inputed scanner is valid.
     * 
     * @postcondition If the scanner retrieves the next token successfully,
     *                then the internal scanner, lookahead, and declaration environment are set.
     *                Otherwise, a ScanErrorException is thrown.
     *                
     * @param inputScanner  the input token stream
     * @exception ScanErrorException if the scanner fails to scan the first token
     */
    public Parser(Scanner inputScanner) throws ScanErrorException
    {
        scanner = inputScanner;
        currentToken = scanner.nextToken();
        declarationEnvironment = new DeclarationEnvironment();
    }
    
    /**
     * Returns whether the parser has a next token
     * 
     * @precondition The internal scanner is initialized appropriately.
     * 
     * @return whether there are more characters to read in the file
     */
    public boolean hasNext()
    {
        return scanner.hasNext();
    }
    
    /**
     * Eats the next token while checking it against expectation
     * 
     * @precondition The lookahead and internal scanner are initialized appropriately.
     * 
     * @postcondition If the expected token matches the current token,
     *                the lookahead and internal scanner are advanced to the next token 
     *                while throwing a ScanErrorException if scanning fails.
     *                If the expected token does not match the current token, a
     *                labeled SyntaxErrorException is thrown.
     * 
     * @param expectedToken the expected token
     * @exception ScanErrorException    if scanning fails
     * @exception SyntaxErrorException  if the lookahead does not match the expected token
     */
    private void eat(String expectedToken) throws ScanErrorException, SyntaxErrorException
    {
        if (expectedToken.equals(currentToken))
        {
            // System.out.print("currentToken: " + currentToken + "->");
            currentToken = scanner.nextToken();
            // System.out.println(currentToken);
        }
        
        else
        {
            String exceptionMessage = "Error! Expected token ";
            exceptionMessage += expectedToken;
            exceptionMessage += " but found token ";
            exceptionMessage += currentToken;
            exceptionMessage += " instead.";  
            
            throw new SyntaxErrorException(exceptionMessage);
        } // else
    } // private void eat
    
    /**
     * Parses and returns a Pascal Program.
     * 
     * @postcondition If the stream defines Pascal variable declarations
     *                followed by procedure declarations followed by a 
     *                Pascal statement, then the variables, procedures, and
     *                program body are parsed and the scanner is advanced accordingly.
     * 
     * @return the parsed program as a semantic object
     * @throws ScanErrorException     if scanning fails
     * @throws SyntaxErrorException   if the token stream fails to match the program grammar
     * @throws SemanticErrorException if an uninitialized variable or procedure is parsed
     * @throws IOException            if marking the stream fails
     */
    public Program parseProgram() throws ScanErrorException, SyntaxErrorException,
        SemanticErrorException, IOException
    {
        parseVariableDeclarations();
        System.out.println("====PARSED VARIABLES!====");
        
        
        parseProcedureDeclarations();
        System.out.println("====PARSED PROCEDURES!====");
        
        System.out.println(declarationEnvironment);
        
        return new Program(declarationEnvironment, parseStatement());
    }

    /**
     * Parses and stores a series of variable declarations.
     * 
     * @postcondition If the stream defines a series of Pascal declarations of the form
     *                  VAR
     *                  [identifier], [identifier], ..., [identifier] : [type];,
     *                with at least one identifier,
     *                then the variables defined by the provided identifiers 
     *                and their corresponding types are created and stored 
     *                in the parser's global declaration environment.
     * 
     * @throws ScanErrorException     if scanning fails
     * @throws SyntaxErrorException   if the token stream fails to match the statement grammar
     * @throws SemanticErrorException if an undeclared variable or procedure or 
     *                                a type error is parsed
     */
    private void parseVariableDeclarations() throws ScanErrorException, SyntaxErrorException, 
        SemanticErrorException
    {
        List<String> identifiers;
        Class<?> type;
        
        while (currentToken.equals("VAR"))
        {
            // Consume the VAR
            this.eat("VAR");
            
            // While there's more [identifiers] : [type]; pairs
            while (Scanner.isIdentifier(currentToken))
            {
                identifiers = new LinkedList<String>();
                
                // Consume one or more (comma-separated identifiers)
                while (Scanner.isIdentifier(currentToken))
                {
                    identifiers.add(currentToken);
                    this.eat(currentToken);
                    
                    // Do not consume comma if the last identifier
                    if (!currentToken.equals(":"))
                    {
                        this.eat(",");
                    } // if
                } // while
                
                // Consume the colon
                this.eat(":");
                
                // Consume the type
                type = parseType();
            
                // Consume the semicolon
                this.eat(";");
            
                // Adds the variables of the same type to the declaration environment
                for (String identifier : identifiers)
                {
                    declarationEnvironment.declareVariable(identifier, type); 
                }
            }  // while
        } // while
    } // private void parseVariableDeclarations

    /**
     * Parses and stores a series of procedure declarations.
     * 
     * 
     * @precondition All procedures invoked in the body are declared previously.
     * @postcondition If the stream defines a series of Pascal declarations of the form
     *                PROCEDURE [identifier]([variables]); [statement]
     *                then the procedures defined by the
     *                provided identifiers, parameters, and bodies 
     *                are created and stored in the parser's global declaration environment.
     * 
     * @throws ScanErrorException     if scanning fails
     * @throws SyntaxErrorException   if the token stream fails to match the statement grammar
     * @throws SemanticErrorException if an undeclared variable or procedure or 
     *                                a type error is parsed
     * @throws IOException            if returning to a mark in the token stream fails
     */
    private void parseProcedureDeclarations() throws ScanErrorException, SyntaxErrorException, 
        SemanticErrorException, IOException
    {
        // Mark the scanner at the current position (post-variables) and save the token
        String savedToken = currentToken;
        scanner.markPosition();
        
        // Parse the headers
        this.parseProcedureHeaders();
        
        // Return to position, restore the token, and mark again
        scanner.returnToMark();
        currentToken = savedToken;
        scanner.markPosition();
                
        // Fill the headers
        this.fillProcedures();
        
        // Return to position, restore the token, and mark again
        currentToken = savedToken;
        scanner.returnToMark();
    } // public void parseProcedureDeclarations
    
    /**
     * Parses all procedure headers.
     * 
     * @precondition The lookahead and internal scanner are initialized appropriately
     *               and all variable declarations have been parsed.
     *               The scanner is marked to directly after variable declarations.
     * @postcondition The parser attempts to parse and add all headers 
     *                to the declaration environment.
     *                The scanner's position is set to the end of the file.
     *                Marks are preserved.
     *                If the procedure identifier already exists within the environment, 
     *                a semantic error is thrown.
     *                If the parser cannot parse the stream as a header, 
     *                a syntax exception is thrown.
     *                If the scanner cannot tokenize the input, a scan exception is thrown.
     *                
     * @throws ScanErrorException if scanning fails   
     * @throws SyntaxErrorException if the token stream fails to match the header grammar
     * @throws SemanticErrorException if the identifier already exists as a procedure
     */
    private void parseProcedureHeaders() throws ScanErrorException, SyntaxErrorException, 
        SemanticErrorException
    { 
        // Run through the entire rest of the program but only scanning and adding headers
        while (scanner.hasNext())
        {       
            // Scan a procedure header if the current token is procedure
            if (currentToken.equals("PROCEDURE"))
            {
                parseProcedureHeader();
            }
            
            // Otherwise, eat (technically unnecessary else)
            else
            {
                this.eat(currentToken);  
            } // else
        } // while
    } // private void parseProcedureHeaders()
    
    /**
     * Parses a bodiless procedure header.
     * 
     * @precondition The lookahead and internal scanner are initialized appropriately
     *               and the current token is "PROCEDURE"
     * @postcondition The parser attempts to parse and add the header 
     *                to the declaration environment.
     *                The scanner position is advanced accordingly.
     *                If the procedure identifier already exists within the environment, 
     *                a semantic error is thrown.
     *                If the parser cannot parse the stream as a header, 
     *                a syntax exception is thrown.
     *                If the scanner cannot tokenize the input, a scan exception is thrown.
     *                
     * @throws ScanErrorException if scanning fails   
     * @throws SyntaxErrorException if the token stream fails to match the header grammar
     * @throws SemanticErrorException if the identifier already exists as a procedure
     */
    private void parseProcedureHeader() throws ScanErrorException, SyntaxErrorException, 
        SemanticErrorException
    {
        // Consume the PROCEDURE
        this.eat("PROCEDURE");
     
        // Consume the identifier
        String identifier = currentToken;
        this.eat(identifier);
                        
        // Consume the left parenthesis
        this.eat("(");

        // Consume all parameters
        List<Variable> parameters = new LinkedList<Variable>();

        while (Scanner.isIdentifier(currentToken))
        {
            parameters.add(parseVariable());

            // Don't try to eat a comma if a right parenthesis follows
            if (!currentToken.equals(")"))
            {
                this.eat(",");
            } // if
        } // while

        // Consume the right parenthesis and semicolon
        this.eat(")");
        this.eat(";");

        // Adds the bodiless header to the declaration environment
        declarationEnvironment.declareHeader(identifier, parameters); 
    }
    
    /**
     * Fills all procedures with the body
     * 
     * @precondition The lookahead and internal scanner are initialized appropriately
     *               and all variable declarations are parsed.
     *               The scanner is marked to the position before procedures.
     * @postcondition The parser attempts to parse and add the body to the header
     *                in the declaration environment.
     *                The scanner position is set to the end of the file.
     *                Marks are preserved.
     *                If the procedure identifier does not exist within the environment, 
     *                a semantic error is thrown.
     *                If the parser cannot parse the stream as a header, 
     *                a syntax exception is thrown.
     *                If the scanner cannot tokenize the input, a scan exception is thrown.
     *                
     * @throws ScanErrorException if scanning fails   
     * @throws SyntaxErrorException if the token stream fails to match the header grammar
     * @throws SemanticErrorException if the identifier already exists as a procedure
     */
    private void fillProcedures() throws ScanErrorException, SyntaxErrorException, 
        SemanticErrorException
    {
        // Scan through the entire rest of the program but only scanning and adding headers
        while (scanner.hasNext())
        {               
            // Scan a procedure header if the current token is procedure
            if (currentToken.equals("PROCEDURE"))
            {
                fillProcedure();
            }
            
            // Otherwise, progress to the next token
            else
            {
                this.eat(currentToken);
            } // else
        } // while
    } // private void fillProcedures()
        
    /**
     * Fills a bodiless procedure with the non-parameter local variables body.
     * 
     * @precondition The lookahead and internal scanner are initialized appropriately
     *               and the current token is "PROCEDURE"
     * @postcondition The parser attempts to parse and fill the headers
     *                in the declaration environment.
     *                The scanner position is advanced accordingly.
     *                If the procedure identifier does not exist within the environment, 
     *                a semantic error is thrown.
     *                If the parser cannot parse the stream as a header, 
     *                a syntax exception is thrown.
     *                If the scanner cannot tokenize the input, a scan exception is thrown.
     *                
     * @throws ScanErrorException if scanning fails   
     * @throws SyntaxErrorException if the token stream fails to match the header grammar
     * @throws SemanticErrorException if the identifier already exists as a procedure
     */
    private void fillProcedure() throws ScanErrorException, SyntaxErrorException, 
        SemanticErrorException
    {
        // Consume the PROCEDURE
        this.eat("PROCEDURE");
        
        String identifier = currentToken;
        this.eat(identifier);
        
        // Retrieve the header
        ProcedureDeclaration header = declarationEnvironment.getProcedure(identifier);
        
        // Verify that the procedure is a header
        if (!header.isHeader())
        {
            String message = "Error: attempted to fill non-bodiless header";
            throw new SemanticErrorException(message);
        }
        
        // Consume the left parenthesis
        this.eat("(");

        // Consume and ignore all parameters
        while (Scanner.isIdentifier(currentToken))
        {
            parseVariable();

            // Don't try to eat a comma if a right parenthesis follows
            if (!currentToken.equals(")"))
            {
                this.eat(",");
            } // if
        } // while

        // Consume the right parenthesis and semicolon
        this.eat(")");
        this.eat(";");
        
        // Consume all non-parameter local variables
        List<Variable> nonParameters = new LinkedList<Variable>();

        if (currentToken.equals("LOCAL"))
        {
            this.eat("LOCAL");
            
            while (Scanner.isIdentifier(currentToken))
            {
                nonParameters.add(parseVariable());

                // Don't try to eat a comma if a semicolon parenthesis follows
                if (!currentToken.equals(";"))
                {
                    this.eat(",");
                } // if
            } // while
            
            // Consume semicolon
            this.eat(";");
        }
         
        // Consume the body
        Statement body = parseStatement();
        
        // Fill the procedure
        header.fill(nonParameters, body);
    }
    
    /**
     * Skips a procedure declaration.
     * 
     * @precondition The lookahead and internal scanner are initialized appropriately
     *               and the current token is "PROCEDURE"
     * @postcondition The parser parses but ignores the procedure.
     *                The scanner position is advanced accordingly.
     *                If the procedure identifier does not exist within the environment, 
     *                a semantic error is thrown.
     *                If the parser cannot parse the stream as a header, 
     *                a syntax exception is thrown.
     *                If the scanner cannot tokenize the input, a scan exception is thrown.
     *                
     * @throws ScanErrorException if scanning fails   
     * @throws SyntaxErrorException if the token stream fails to match the header grammar
     * @throws SemanticErrorException if the identifier already exists as a procedure
     */
    private void skipProcedureDeclaration() throws ScanErrorException, 
        SyntaxErrorException, SemanticErrorException
    {
        // Consume the PROCEDURE
        this.eat("PROCEDURE");
    
        // Consume the identifier
        this.eat(currentToken);
        
        // Consume the left parenthesis
        this.eat("(");
    
        // Consume and ignore all parameters
        while (Scanner.isIdentifier(currentToken))
        {
            parseVariable();
    
            // Don't try to eat a comma if a right parenthesis follows
            if (!currentToken.equals(")"))
            {
                this.eat(",");
            } // if
        } // while
        
        // Consume the right parenthesis and semicolon
        this.eat(")");
        this.eat(";");
        
        // Consume and ignore all non-parameter local variables
        if (currentToken.equals("LOCAL"))
        {
            this.eat("LOCAL");
            
            while (Scanner.isIdentifier(currentToken))
            {
                parseVariable();
        
                // Don't try to eat a comma if a right parenthesis follows
                if (!currentToken.equals(";"))
                {
                    this.eat(",");
                } // if
            } // while
            
            // Consume the semicolon
            this.eat(";");
        }
        
        // Consume and ignore the body
        parseStatement();
    }

    /**
     * Parses and returns a Pascal statement.
     * Statements are defined as nested arithmetic, print statements, variable assignments,
     * blocks, if-then statements, and while and for loops.
     * 
     * @precondition The lookahead and internal scanner are initialized appropriately.
     * @postcondition The parser attempts to return a valid Pascal statement as a semantic object.
     *                If the parser cannot parse the stream as a statement,
     *                a syntax exception is thrown.
     *                If the scanner cannot tokenize the input, a scan exception is thrown.
     * 
     * @return the parsed statement as a semantic object
     * @throws ScanErrorException     if scanning fails
     * @throws SyntaxErrorException   if the token stream fails to match the statement grammar
     * @throws SemanticErrorException if an undeclared variable or procedure or 
     *                                a type error is parsed
     */
    private Statement parseStatement() throws ScanErrorException, SyntaxErrorException, 
        SemanticErrorException
    {           
        Statement statement = null;
                
        // PROCEDURE (skip)
        while (currentToken.equals("PROCEDURE"))
        {
            skipProcedureDeclaration();
        }
        
        // WRITELN(expression)
        if (currentToken.equals("WRITELN")) 
        {
            statement = parseWriteln();
        }
    
        // Block BEGIN... END
        else if (currentToken.equals("BEGIN"))
        {   
            statement = parseBlock();
        }

        // IF... THEN... (ELSE...)
        else if (currentToken.matches("IF"))
        {
            statement = parseIfThen();
        }
        
        // WHILE... DO...
        else if (currentToken.matches("WHILE"))
        {
            statement = parseWhileDo();
        }
        
        // FOR... TO... DO...
        else if (currentToken.matches("FOR"))
        {
            statement = parseForToDo();
        } 
        
        /*
         * identifier := expression
         * Identifier regex is mutually exclusive with keywords
         */
        else if (Scanner.isIdentifier(currentToken)) 
        {            
            statement = parseAssignment();
        }
        
        else
        {
            String message = "Error: Invalid statement beginning with " + currentToken;
            throw new SyntaxErrorException(message);
        }
        
        return statement;
    } // public Statement parseStatement()
    
    /**
     * Parses and returns a Writeln statement.
     * 
     * @precondition The statement is set to parse a valid Writeln statement. 
     * @postcondition The internal parser is advanced to after the Writeln statement.
     * 
     * @return a Writeln statement with the embedded expression
     * @throws ScanErrorException     if scanning fails
     * @throws SyntaxErrorException   if the token stream fails to match the Writeln grammar
     * @throws SemanticErrorException if an undeclared variable or procedure or 
     *                                a type error is parsed
     */
    private Writeln parseWriteln() throws ScanErrorException, SyntaxErrorException, 
        SemanticErrorException
    {
        Writeln writeln = null;
        
        // Consume the WRITELN(
        this.eat("WRITELN");
        this.eat("(");
        
        // Consume the expression
        writeln = new Writeln(parseIntegerExpression());
        
        // Consume the );
        this.eat(")");
        this.eat(";");
        
        // Return the writeln
        return writeln;
    }
    
    /**
     * Parses and returns a block statement.
     * 
     * @precondition The statement is set to parse a valid block statement. 
     * @postcondition The internal parser is advanced to after the block statement.
     * 
     * @return a Block statement with an ordered list of embedded statements
     * @throws ScanErrorException     if scanning fails
     * @throws SyntaxErrorException   if the token stream fails to match the block grammar
     * @throws SemanticErrorException if an undeclared variable or procedure or 
     *                                a type error is parsed
     */
    private Block parseBlock() throws ScanErrorException, SyntaxErrorException, 
        SemanticErrorException
    {
        // Consume the BEGIN
        this.eat("BEGIN");

        // Consume the statements in order
        List<Statement> statements = new LinkedList<Statement>();

        while (!currentToken.equals("END"))
        {
            statements.add(parseStatement());
        }
                    
        // Consume the END;
        this.eat("END");
        this.eat(";");
       
        // Return the block
        return new Block(statements);
    }
    
    /**
     * Parses and returns an if-then statement.
     * 
     * @precondition The statement is set to parse a valid if-then statement. 
     * @postcondition The internal parser is advanced to after the if-then statement.
     * 
     * @return an IfThen statement with an ordered list of embedded statements
     * @throws ScanErrorException     if scanning fails
     * @throws SyntaxErrorException   if the token stream fails to match the if-then grammar
     * @throws SemanticErrorException if an undeclared variable or procedure or 
     *                                a type error is parsed
     */
    private IfThen parseIfThen() throws ScanErrorException, SyntaxErrorException, 
        SemanticErrorException
    {        
        // Consume the IF
        this.eat("IF");
        
        // Consume the condition
        Expression condition = parseBooleanExpression();
        
        // Consume the THEN
        this.eat("THEN");
        
        // Consume the statement
        Statement thenStatement = parseStatement();
        
        // Return the if-then statement
        return new IfThen(condition, thenStatement);
    }
    
    /**
     * Parses and returns a while loop statement.
     * 
     * @precondition The statement is set to parse a valid while loop statement. 
     * @postcondition The internal parser is advanced to after the while loop statement.
     * 
     * @return a WhileDo statement with the embedded condition and loop statement
     * @throws ScanErrorException     if scanning fails
     * @throws SyntaxErrorException   if the token stream fails to match the while loop grammar
     * @throws SemanticErrorException if an undeclared variable or procedure or 
     *                                a type error is parsed
     */
    private WhileDo parseWhileDo() throws ScanErrorException, SyntaxErrorException, 
        SemanticErrorException
    {        
        // Consume the WHILE
        this.eat("WHILE");
        
        // Consume the condition
        Expression condition = parseBooleanExpression();
        
        // Consume the DO
        this.eat("DO");
        
        // Consume the statement
        Statement doStatement = parseStatement();
        
        // Return the while loop
        return new WhileDo(condition, doStatement);
    }
    
    /**
     * Parses and returns a for loop statement.
     * 
     * @precondition The statement is set to parse a valid for loop statement. 
     * @postcondition The internal parser is advanced to after the for loop statement.
     * 
     * @return a ForToDo statement with the embedded index, lower bound, upper bound, 
     *         and loop statement
     * @throws ScanErrorException     if scanning fails
     * @throws SyntaxErrorException   if the token stream fails to match the for loop grammar
     * @throws SemanticErrorException if an undeclared variable or procedure or 
     *                                a type error is parsed
     */
    private ForToDo parseForToDo() throws ScanErrorException, SyntaxErrorException, 
        SemanticErrorException
    {        
        // Consume the FOR
        this.eat("FOR");
        
        // Consume the index
        Variable index = parseVariable();
        
        // Consume the =
        this.eat(":=");
        
        // Consume the lower bound
        Expression lowerBound = parseIntegerExpression();
        
        // Consume the TO
        this.eat("TO");
        
        // Consume the upper bound
        Expression upperBound = parseIntegerExpression();
        
        // Consume the DO
        this.eat("DO");
        
        // Consume the statement
        Statement doStatement = parseStatement();
        
        // Return the for loop
        return new ForToDo(index, lowerBound, upperBound, doStatement);
    }
        
    /**
     * Parses and returns an assignment statement without a semicolon
     * (i.e. identifier := value;)
     * 
     * @precondition The statement is set to parse a valid for assignment statement. 
     * @postcondition The internal parser is advanced to after the assignment loop statement.
     * 
     * @return an assignment statement with the embedded variable and value
     * @throws ScanErrorException     if scanning fails
     * @throws SyntaxErrorException   if the token stream fails to match the assignment grammar
     * @throws SemanticErrorException if an undeclared variable or procedure or 
     *                                a type error is parsed
     */
    private Assignment parseAssignment() throws ScanErrorException, 
        SyntaxErrorException, SemanticErrorException
    {
        // Consume the variable
        Variable variable = parseVariable();
        
        // Consume the :=
        this.eat(":=");
        
        // Consume the expression
        Expression expression = null;
        
        Class<?> type = variable.getEvaluationType();
        
        if (type.equals(Integer.class))
        {
            expression = parseIntegerExpression();            
        }
        else if (type.equals(Boolean.class))
        {
            expression = parseBooleanExpression();
        }
        
        // Consume the semicolon
        this.eat(";");
        
        // Return the assignment
        return new Assignment(variable, expression);
    }
      
    /**
     * Parses and returns a boolean expression.
     * Currently, boolean expressions are synonymous with boolean factors.
     * 
     * @precondition The statement is set to parse a valid boolean expression. 
     * @postcondition The internal parser is advanced to after the boolean expression.
     * 
     * @return a boolean expression object
     * @throws ScanErrorException     if scanning fails
     * @throws SyntaxErrorException   if the token stream fails to match the 
     *                                boolean expression grammar
     * @throws SemanticErrorException if an undeclared variable or procedure or 
     *                                a type error is parsed
     */
    private Expression parseBooleanExpression() throws ScanErrorException, 
        SyntaxErrorException, SemanticErrorException
    {
        return parseBooleanFactor();
    }
    
    /**
     * Parses and returns a boolean factor.
     * Boolean factors currently consist of relative operator comparisons 
     * between integer expressions, boolean variables, and boolean literals.
     *  
     * @precondition The statement is set to parse a valid boolean factor. 
     * @postcondition The internal parser is advanced to after the boolean expression.
     * 
     * @return a boolean factor object
     * @throws ScanErrorException     if scanning fails
     * @throws SyntaxErrorException   if the token stream fails to match the boolean factor grammar
     * @throws SemanticErrorException if an undeclared variable or procedure or 
     *                                a type error is parsed
     */
    private Expression parseBooleanFactor() throws ScanErrorException, SyntaxErrorException, 
        SemanticErrorException
    {
        Expression booleanFactor = null;
        
        // If a boolean variable (integer variables may still be part of an expression)
        if (Scanner.isIdentifier(currentToken) 
                && declarationEnvironment.containsVariable(currentToken)
                && declarationEnvironment.getVariable(currentToken)
                    .getEvaluationType().equals(Boolean.class))
        {
            booleanFactor = parseVariable();
        }
            
        // If the literal true
        else if (currentToken.equals("true"))
        {
            this.eat("true");
            booleanFactor = new Literal(true);
        }
        
        // If the literal false
        else if (currentToken.equals("false"))
        {
            this.eat("false");
            booleanFactor = new Literal(false);
        }
        
        // Otherwise, it should be a relative operator
        else
        {
            Expression operand1 = parseIntegerExpression();
            
            // If relative operator
            if (Scanner.isRelativeOperator(currentToken))
            {
                // Consume the operator
                String relativeOperator = currentToken;
                this.eat(relativeOperator);
                
                // Consume the second operand
                Expression operand2 = parseIntegerExpression();
                booleanFactor = new RelativeOperator(relativeOperator, operand1, operand2);
            }
            
            // If not relative operator
            else
            {
                String message = "Error! Expected relative operator but found ";
                message += currentToken + " instead!";
                throw new SyntaxErrorException(message);
            }
        }
        
        return booleanFactor;
    }
    
    /**
     * Parses and returns an arithmetic integer expression
     * involving addition, subtraction, multiplication, division, and modulo operators,
     * integer variables, number literals, and integer procedure calls.
     * 
     * @precondition The lookahead and internal scanner are initialized appropriately.
     * 
     * @postcondition If the internal scanner scans a valid expression 
     *                as defined by the aforementioned production rules, 
     *                arithmetic is evaluated, the value of the expression is returned,
     *                and the scanner and lookahead are advanced to directly after the expression.
     *                If the parser cannot parse the stream as an expression,
     *                a syntax exception is thrown.
     *                If the scanner cannot tokenize the input, a scan exception is thrown.
     * 
     * @return an integer expression object
     * @throws ScanErrorException     if scanning fails
     * @throws SyntaxErrorException   if the token stream fails to match
     *                                the integer expression grammar
     * @throws SemanticErrorException if an undeclared variable or procedure or 
     *                                a type error is parsed
     */
    private Expression parseIntegerExpression() throws ScanErrorException, 
        SyntaxErrorException, SemanticErrorException
    {
        Expression expression;          // the numerical value of the term
    
        // First term (or expression)
        expression = parseIntegerTerm();
    
        // Whether whileExpression can continue to be expanded
        boolean keepMatching = true;
    
        /*
         * Loop to add terms, addition, and subtraction until depleted according to
         *  whileExpression -> + term whileExpression | - term whileExpression | epsilon
         */
        while (keepMatching)
        {
            // + term whileExpression
            if (currentToken.equals("+"))
            {
                this.eat("+");                
                expression = new ArithmeticOperator("+", expression, parseIntegerTerm());
            }
    
            // - term whileExpression
            else if (currentToken.equals("-"))
            {
                this.eat("-");
                expression = new ArithmeticOperator("-", expression, parseIntegerTerm());
            }
    
            // epsilon
            else
            {
                keepMatching = false;
            }
        }
    
        return expression;
    }

    /**
     * Parses and returns an arithmetic integer term involving multiplication, division, and modulus
     * 
     * The raw production rules are
     *  term -> term * factor | term / factor | term mod factor | factor
     *  
     * The converted production rules are
     *  term -> factor whileTerm
     *  whileTerm -> * factor whileTerm | / factor whileTerm | mod factor whileTerm | epsilon
     * 
     * @precondition The lookahead and internal scanner are initialized appropriately.
     * 
     * @postcondition If the internal scanner scans a valid term 
     *                as defined by the aforementioned production rules, 
     *                arithmetic is evaluated, the value of the term is returned,
     *                and the scanner and lookahead are advanced to directly after the term.
     *                If the parser cannot parse the stream as a term,
     *                a syntax exception is thrown.
     *                If the scanner cannot tokenize the input, a scan exception is thrown.
     * 
     * @return the parsed integer term as an arithmetic integer expression
     * @throws ScanErrorException     if scanning fails
     * @throws SyntaxErrorException   if the token stream fails to match
     *                                the integer term grammar
     * @throws SemanticErrorException if an undeclared variable or procedure or 
     *                                a type error is parsed
     */
    private Expression parseIntegerTerm() throws ScanErrorException, SyntaxErrorException, 
        SemanticErrorException
    {    
        // factor
        Expression term = parseIntegerFactor();
    
        // Whether whileTerm can continue to be expanded
        boolean keepMatching = true;
    
        /*
         * Loop to add factors, multiplication, division, and mod until depleted according to
         *  whileTerm -> * factor whileTerm | / factor whileTerm | mod factor whileTerm | epsilon
         */
        while (keepMatching)
        {
            // * factor whileterm
            if (currentToken.equals("*"))
            {
                this.eat("*");
                term = new ArithmeticOperator("*", term, parseIntegerFactor());
            }
    
            // / factor whileterm
            else if (currentToken.equals("/"))
            {
                this.eat("/");
                term = new ArithmeticOperator("/", term, parseIntegerFactor());
            }
            
            // mod factor whileterm
            else if (currentToken.equals("mod"))
            {
                this.eat("mod");
                term = new ArithmeticOperator("mod", term, parseIntegerFactor());
            }
            
            // epsilon
            else
            {
                keepMatching = false;
            }
        }
    
        return term;
    }

    /**
     * Parses and returns a numerical factor involving integer literals, unary negation, 
     * parentheses, variables, procedure calls, and expressions.
     * 
     * The production rules are
     *  factor -> (expression)|-factor|number|identifier
     * 
     * @precondition The lookahead and internal scanner are initialized appropriately.
     * 
     * @postcondition If the internal scanner scans a valid factor 
     *                as defined by the aforementioned production rules, 
     *                arithmetic is evaluated, the value of the factor is returned,
     *                and the scanner and lookahead are advanced to directly after the factor.
     *                If the parser cannot parse the stream as a factor,
     *                a syntax exception is thrown.
     *                If the scanner cannot tokenize the input, a scan exception is thrown.
     *                
     * @return the parsed integer factor as an arithmetic integer expression
     * @throws ScanErrorException     if scanning fails
     * @throws SyntaxErrorException   if the token stream fails to match
     *                                the integer factor grammar
     * @throws SemanticErrorException if an undeclared variable or procedure or 
     *                                a type error is parsed
     */
    private Expression parseIntegerFactor() throws ScanErrorException, SyntaxErrorException, 
        SemanticErrorException
    {
        Expression factor = null;
    
        // (expression)
        if (currentToken.equals("("))
        {
            this.eat("(");
            factor = parseIntegerExpression();
            this.eat(")");
        }
    
        // -factor
        else if (currentToken.equals("-"))
        {
            this.eat("-");
            factor = new ArithmeticOperator("-", new Literal(0), parseIntegerFactor());
        }
    
        // number
        else if (Scanner.isNumber(currentToken))
        {
            factor = parseNumber();
        }
    
        // identifier or identifier()
        else if (Scanner.isIdentifier(currentToken))
        {
            // Consume the identifier
            String identifier = currentToken;
            this.eat(identifier);
            
            // If the form identifier( matches, consume a procedure
            if (currentToken.equals("("))
            {
                factor = parseProcedureCallHelper(identifier);
            }
            
            // Otherwise, consume an identifier
            else
            {
                factor = parseVariableHelper(identifier);
            }
        }
    
        // if parsing fails
        else
        {
            String exceptionMessage = "Error! Expected factor but found ";
            exceptionMessage += currentToken + " instead!";
            throw new SyntaxErrorException(exceptionMessage);
        }
    
        return factor;
    }

    /**
     * Parses and returns a variable.
     * 
     * @precondition The lookahead and internal scanner are initialized appropriately.
     * 
     * @postcondition: The lookahead and internal scanner are advanced one token.
     * 
     * @return the parsed variable
     * @throws ScanErrorException     if scanning fails
     * @throws SemanticErrorException if an undeclared variable or procedure or 
     *                                a type error is parsed
     */
    private Variable parseVariable() throws ScanErrorException, SyntaxErrorException, 
        SemanticErrorException
    {
        // Store and consume the identifier
        String identifier = currentToken;
        this.eat(currentToken);
         
        return parseVariableHelper(identifier);
    }
    
    /**
     * Converts an identifier string into a variable from the declaration environment.
     * Does not consume a token.
     * 
     * @param identifier    the identifier of the desired variable
     * @return the parsed variable
     * @throws SemanticErrorException if an undeclared variable is provided
     */
    private Variable parseVariableHelper(String identifier) throws SemanticErrorException
    {                                    
        return declarationEnvironment.getVariable(identifier);
    }
    
    /**
     * Converts an identifier string into a procedure call from the declaration environment.
     * Does consume following parentheses and arguments.
     * 
     * @postcondition If the internal scanner scans a valid procedure call 
     *                the procedure call is returned,
     *                and the scanner and lookahead are advanced to directly after the call. 
     * @param identifier    the identifier of the desired procedure
     * @return the parsed procedure call
     * @throws ScanErrorException     if scanning fails
     * @throws SyntaxErrorException   if the token stream fails to match
     *                                the procedure call grammar
     * @throws SemanticErrorException if an undeclared variable or procedure or 
     *                                a type error is parsed
     */
    private ProcedureCall parseProcedureCallHelper(String identifier) throws ScanErrorException, 
        SyntaxErrorException, SemanticErrorException
    {        
        // Procedure definition
        ProcedureDeclaration procedure = declarationEnvironment.getProcedure(identifier);
        
                
        // Eat the left parenthesis
        this.eat("(");
        
        // Eat the arguments and commas
        List<Expression> arguments = new LinkedList<Expression>();
        
        while (!currentToken.equals(")"))
        {
            arguments.add(parseIntegerExpression());
            
            if (!currentToken.equals(")"))
            {
                this.eat(",");
            }
            
        }
        
        // Eat the right parenthesis
        this.eat(")");
        
        return new ProcedureCall(procedure, arguments);
    }

    /**
     * Parses and returns a number literal
     * 
     * @precondition The lookahead and internal scanner are initialized appropriately.
     * 
     * @postcondition: the lookahead and internal scanner are advanced one token.
     * 
     * @return the parsed number as a literal AST node
     * @exception ScanErrorException   if scanning fails
     * @exception SyntaxErrorException if parsing the identifier otherwise fails
     */
    private Literal parseNumber() throws ScanErrorException, SyntaxErrorException
    {
        int num = Integer.parseInt(currentToken);
        
        this.eat(currentToken);
        
        return new Literal(num);
    } 

    /**
     * Parses a data type name.
     * 
     * @postcondition The parser attempts to return a valid Pascal type.
     *                If the parser cannot parse the type,
     *                a syntax exception is thrown.
     *                If the scanner cannot tokenize the input, a scan exception is thrown.
     *                
     * @return the class object matching the string
     * @throws ScanErrorException     if scanning fails
     * @throws SyntaxErrorException   if the token stream fails to match a type name
     *                                (i.e. "integer" or "boolean")
     */
    private Class<?> parseType() throws SyntaxErrorException, ScanErrorException
    {
        // Consume the type string
        String typeString = currentToken;
        this.eat(typeString);
        
        // Choose the appropriate type
        Class<?> type = null;
        
        switch (typeString)
        {
            case ("integer"):
                type = Integer.class;
                break;
            
            case ("boolean"):
                type = Boolean.class;
                break;
            
            default:
                String message = "Error: " + typeString;
                message += " is not a valid type";
                throw new SyntaxErrorException(message);
        }
        
        return type;
    } // private Class<?> parseType
} // public class Parser