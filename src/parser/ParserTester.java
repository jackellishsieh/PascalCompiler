package parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import ast.Program;
import emitter.Emitter;
import scanner.Scanner;

/**
 * ParserTester tests a Parser on a Pascal file, prints the
 * AST, and writes compiled code to a MIPS file.
 * 
 * @author Jack Hsieh
 * with assistance from Anu Datar, Krish Maniar, and Clarice Wang.
 * @version 2022/03/09
 * 
 */
public class ParserTester 
{
    private static String defaultInputFilename = "parser/parserTest13.txt";  // default input
    private static String defaultOutputFilename = "parser/outputTest13.asm"; // default output
    
    /**
     * Tests the parser class on the provided test file by parsing a Pascal statement,
     * printing the generated AST, and writing the compiled MIPS code to the provided output file.
     * Abort and reports any encountered lexical, syntactic, or semantic errors.
     * Uses tester default test filename if no alternative filename is provided.
     * 
     * @postcondition If the provided filename is unrecognized or unreadable,
     *                the failure is printed to the console and execution ends.
     *                Otherwise, a parser parses and executes Pascal statements in the file 
     *                until the statements are depleted.
     *                If scanning or parsing fails at any point, 
     *                then the failure is printed to the console and execution ends.
     * 
     * @param args the command line arguments. First element is used as filename if provided.
     */
    public static void main(String[] args)
    {           
        /*
         * Sets inputFilename to default if none is provided
         * Otherwise, use first argument of main
         */
        String inputFilename;
        
        if (args.length == 0)
        {
            inputFilename = ParserTester.defaultInputFilename;
        }
        else
        {
            inputFilename = args[0];
        }
        
        File inputFile = new File(inputFilename);           // the file with Pascal code to input
        InputStream inputStream = null;                     // the input stream
        
        /*
         * Attempt to open the file. Report error and terminate execution if necessary
         */
        try
        {
            inputStream = new FileInputStream(inputFile);
        }
        catch (FileNotFoundException exception)
        {
            System.out.println("Exception reported: file \"" + inputFilename + "\" not found.");
            exception.printStackTrace();
        }
        
        /*
         * Sets outputFilename to default if none is provided
         * Otherwise, use first argument of main
         */
        String outputFilename;
        
        if (args.length == 0)
        {
            outputFilename = ParserTester.defaultOutputFilename;
        }
        else
        {
            outputFilename = args[1];
        }
        
        /*
         * If the input file is readable, create a parser and parse the input stream
         */
        if (inputStream != null)
        {
            // Token stream
            Scanner scanner = new Scanner(inputStream);
	    
            /*
             * Try to parse the input stream as a statement.
             * Any reading error will cause parsing to terminate.
             */
    	    try
    	    {
    	        
                Parser parser = new Parser(scanner);

    	        System.out.println("Beginning input stream parsing");
    	       
    	        // Parse program
    	        Program program = parser.parseProgram();
    	                        
                // Print program
    	        System.out.println(program);
    	        
                System.out.println("Ending input stream parsing");
                
                // Create a new emitter and emit
                Emitter emitter = new Emitter(outputFilename);
                program.compile(emitter);
                emitter.close();
                
                System.out.println("Emitted code.");
            }
    	    
    	    catch (Exception exception)
    	    {
    	        System.out.println("Exception caught during parsing:");
    	        exception.printStackTrace();
    	    }
    	    
    	    /*
    	     * Close the input stream no matter what
    	     */
    	    finally
    	    {
    	        try
    	        {
    	            inputStream.close();
    	        }
    	        catch (IOException exception)
    	        {
    	            System.out.println("Exception caught during closure of input stream:");
    	            exception.printStackTrace();
    	        } // catch
    	    } // finally
        } // if inputStream is readable
    } // main function
} // public class ParserTester
