# PascalCompiler
 A compiler for Pascal to MIPS Assembly written in Java from scratch.
 
## Overview
A Pascal compiler with support for
* Integer and boolean arithmetic and comparison
* Variables
* Control flow constructs
* Console output
* Functions/subroutines with scope

An equivalent MIPS Assembly file is outputed. This project was created for ATCS: Compilers and Interpreters at The Harker School, taught by Ms. Datar. 

## Usage
### Dependencies
Standard Java libraries.

### Running
1. Run _ParserTester.java_ from the terminal with two arguments, where
   1. The first argument is the desired Pascal input filename (.txt)
   2. The second argument is the desired Assembly output filename (.asm)
2. Upon execution, the program will write the compiled Assembly code to the specified output file.
4. The program will also print the AST and declaration environment to the console.

## Acknowledgments
Thank you to Anu Datar for providing the original code for _Emitter.java_ and _ScanErrorException.java_.
