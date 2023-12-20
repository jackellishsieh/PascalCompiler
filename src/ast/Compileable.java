package ast;

import emitter.Emitter;

/**
 * Interface for compileable AST objects
 * 
 * @author Jack Hsieh
 * @version 2022/05/09
 */
public interface Compileable
{
    public abstract void compile(Emitter emitter);
}
