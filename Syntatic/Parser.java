package Syntatic;
import Lexical.*;


public class Parser {

    private Lexer lexer;
    private Token currentToken;
    

    public Parser(Lexer lexer) throws Exception {
        this.lexer = lexer;
        advance();
    }       

    public Token getCurrentToken() {
        return currentToken;
    }

    private void advance() throws Exception {
        
       this.currentToken = this.lexer.scan();
    }

    public SymbolTable getSymbolTable(){
        SymbolTable symbolTable = new SymbolTable();
        return symbolTable;
    }

    public void ErroSintatico(String message) throws Exception{
        Lexical.Error error = new Lexical.Error(message, Position.line);
        System.out.println(error);
        } 

    private void eat(Tag tag) throws Exception{
        while (currentToken != null && currentToken.TAG != Tag.EOF) {
            // logica de erro aqui depois
            System.out.println("Comeu " + tag);
            advance();
        }
        System.out.println("Comeu " + tag);

    }



    //Construções
    //⟨begin⟩ ::= ⟨program⟩#
    public void begin() throws Exception{
        System.out.println("------ PROGRAMA INICIADO");
        program();
        eat(Tag.EOF);
        System.out.println("------ PROGRAMA FINALIZADO");
    }
    //⟨program⟩ ::= start [decl-list] ⟨stmt-list⟩ exit
    public void program() throws Exception{
        //exemplo chamada de erro
        ErroSintatico("Errou chefe");

        eat(Tag.START);
        decllist();
        stmtlist();
        eat(Tag.EXIT);
    }
    //⟨decl-list⟩ ::= ⟨decl⟩ {decl}
    public void decllist() throws Exception{
        try{
            decl();
            decllist(); 
        }catch(Exception e){//-----------ver
            return;
        }
    }
    //⟨decl⟩ ::= ⟨type⟩ ⟨ident-list⟩ ;
    public void decl() throws Exception{
        try{
            type();
            identlist();
            eat(Tag.SEMICOLON);
        }catch(Exception e){
            ErroSintatico("Erro na decl");
            return;
        }
        

    }
    //⟨ident-list⟩ ::= identifier {, identifier}
    public void identlist() throws Exception{
        try{
            identifier();
            eat(Tag.COMMA);
            identlist();
        }catch(Exception e){
            return;    
        }
    }
    //⟨type⟩ ::= int | float | string
    //conferir logica
    public void type() throws Exception{
        switch (this.currentToken.TAG) {
            case INT:
                eat(Tag.INT);
            case FLOAT:
                eat(Tag.FLOAT);
            case STRING:
                eat(Tag.STRING);        
            default:
                ErroSintatico("Erro no Type");
        }
        return;
    }
    //⟨stmt-list⟩ ::= ⟨stmt⟩ {⟨stmt⟩}
    public void stmtlist() throws Exception{
        try{
            stmt();
            stmtlist();
        }catch(Exception e){
            return;
        }
    }
    //⟨stmt⟩ ::= ⟨assign-stmt⟩ ; | ⟨if-stmt⟩ | ⟨while-stmt⟩ | ⟨read-stmt⟩ ; | ⟨write-stmt⟩ ;
    public void stmt() throws Exception{
        switch (this.currentToken.TAG) {
            case ASSIGN:
                assignstmt();
                eat(Tag.SEMICOLON);            
            case IF:
                ifstmt();;
            case WHILE:
                whilestmt();;
            case SCAN:
                readstmt();
                eat(Tag.SEMICOLON);
            case PRINT:
                writestmt();    
                eat(Tag.SEMICOLON);
            default:
                ErroSintatico("Erro no Stmt");
        }
        return;

    }
    //⟨assign-stmt⟩ ::= identifier = ⟨simple-expr⟩
    public void assignstmt() throws Exception{
        try{
            identifier();
            eat(Tag.EQ);
            simpleexpr();
        }catch(Exception e){
            ErroSintatico("Erro no assign");
        }
    }
    //⟨if-stmt⟩ ::= if ⟨condition⟩ then ⟨stmt-list⟩ ⟨if-stmt-tail⟩
    public void ifstmt() throws Exception{
        try{
            eat(Tag.IF);
            condition();
            eat(Tag.THEN);
            stmtlist();
            ifstmttail();
        }catch(Exception e){
            ErroSintatico("Erro no ifstmt");
        }
    }
    //⟨if-stmt-tail⟩ ::= end | else ⟨stmt-list⟩ end
    public void ifstmttail() throws Exception{
        switch (this.currentToken.TAG) {
            case END:
                eat(Tag.END);
                break;
            case ELSE:
                eat(Tag.ELSE);
                stmtlist();
                eat(Tag.END);
                break;
            default:
                ErroSintatico("Erro no ifstmttail");
        }
        return;

    }
    //⟨condition⟩ ::= ⟨expression⟩
    public void condition() throws Exception{
        try{
            expression();
        }catch(Exception e){
            ErroSintatico("Erro em condition");
        }
    }
    //⟨while-stmt⟩ ::= do ⟨stmt-list⟩ ⟨stmt-sufix⟩
    public void whilestmt() throws Exception{
        try{
            eat(Tag.DO);
            stmtlist();
            stmtsufix();
        }catch(Exception e){
            ErroSintatico("Erro em while-stmt");
        }
    }
    //⟨stmt-sufix⟩ ::= while ⟨condition⟩ end
    public void stmtsufix() throws Exception{
        try{
            eat(Tag.WHILE);
            condition();
            eat(Tag.END);
        }catch(Exception e){
            ErroSintatico("Erro em stmt-sufix");
        }
    }
    //⟨read-stmt⟩ ::= scan ( identifier )
    public void readstmt() throws Exception{
        try{
            eat(Tag.SCAN);
            eat(Tag.OPEN_BRACKET);
            identifier();
            eat(Tag.CLOSE_BRACKET);
        }catch(Exception e){
            ErroSintatico("Erro em read-stmt");
        }
    }
    //⟨write-stmt⟩ ::= print ( ⟨writable⟩ )
    public void writestmt() throws Exception{
        try{
            eat(Tag.PRINT);
            eat(Tag.OPEN_BRACKET);
            writable();
            eat(Tag.CLOSE_BRACKET);
        }catch(Exception e){
            ErroSintatico("Erro em write-stmt");
        }
    }
    //⟨writable⟩ ::= ⟨simple-expr⟩ | literal
    public void writable() throws Exception{

    }
    //⟨expression⟩ ::= ⟨simple-expr⟩ ⟨expression-tail⟩
    public void expression() throws Exception{

    }
    //⟨expression-tail⟩ ::= relop ⟨simple-expr⟩ | λ
    public void expressiontail() throws Exception{

    }
    //⟨simple-expr⟩ ::= ⟨term⟩ ⟨simple-expr-tail⟩
    public void simpleexpr() throws Exception{

    }
    //⟨simple-expr-tail⟩ ::= addop ⟨term⟩ ⟨simple-expr-tail⟩ | λ
    public void simpleexprtail() throws Exception{

    }
    //⟨term⟩ ::= ⟨factor-a⟩⟨term-tail⟩
    public void term() throws Exception{

    }
    //⟨term-tail⟩ ::= mulop ⟨factor-a⟩⟨term-tail⟩ | λ
    public void termtail() throws Exception{

    }
    //⟨factor-a⟩ ::= ⟨factor⟩ | ! ⟨factor⟩ | - ⟨factor⟩
    public void factora() throws Exception{

    }
    //⟨factor⟩ ::= identifier | constant | ( ⟨expression⟩ )
    public void factor() throws Exception{

    }

    //relop           ::= "=="  |  ">"  |  ">="  |  "<"  |  "<="  | "!="
    public void relop() throws Exception{

    }
    //addop           ::= "+"  |  "-"  | "||"
    public void addop() throws Exception{

    }
    //mulop           ::=  "*"  | "/"   | "%" | "&&"
    public void mulop() throws Exception{

    }
    //constant        ::= integer_const | float_const | literal
    public void constant() throws Exception{

    }
    //integer_const   ::= digit+
    public void integer_const() throws Exception{

    }
    //float_const     ::= digit+ "." digit+
    public void float_const() throws Exception{

    }
    //literal         ::= "{" caractere* "}"
    public void literal() throws Exception{

    }
    //identifier      ::= (letter | _ )  (letter | digit )*
    public void identifier() throws Exception{
        
    }
    //letter          ::= [A-Za-z]
    public void letter () throws Exception{

    }
    //digit           ::= [0-9]
    public void digit() throws Exception{

    }
    //caractere       ::= um dos caracteres ASCII, exceto quebra de linha
    public void caractere() throws Exception{

    }


}
