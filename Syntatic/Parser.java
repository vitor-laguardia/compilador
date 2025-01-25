package Syntatic;
import Lexical.*;

public class Parser {

    private Lexer lexer;
    private Word currentToken;
    

    public Parser(Lexer lexer, boolean debug) throws Exception {
        this.lexer = lexer;
        advance();
    }       

    public Token getCurrentToken() {
        return currentToken;
    }

    private void advance() throws Exception {
       //proximo token
    }

    public SymbolTable getSymbolTable(){
        SymbolTable symbolTable = new SymbolTable();
        return symbolTable;
    }

}
