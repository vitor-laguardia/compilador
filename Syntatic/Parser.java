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
            switch (this.currentToken.TAG) {
                case IDENTIFIER:
                case IF:
                case DO:
                case SCAN:
                case PRINT:
                    stmtlist();;
                    break;
                default:
                    break;
            }
        }catch(Exception e){
            return;
        }
    }
    //⟨stmt⟩ ::= ⟨assign-stmt⟩ ; | ⟨if-stmt⟩ | ⟨while-stmt⟩ | ⟨read-stmt⟩ ; | ⟨write-stmt⟩ ;
    public void stmt() throws Exception{
        switch (this.currentToken.TAG) {
            case IDENTIFIER:
                assignstmt();
                eat(Tag.SEMICOLON);            
            case IF:
                ifstmt();;
            case DO:
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
            eat(Tag.ASSIGN);
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
    //simpleexpr e literal tem como first TAG STRING_CONST
    
    public void writable() throws Exception{
        switch (this.currentToken.TAG) {
            case IDENTIFIER:
            //case STRING_CONST:
            case INT_CONST:
            case FLOAT_CONST:
            case OPEN_BRACKET:
            case NOT:
            case MINUS:
                simpleexpr();
                break;    
            //STRING_CONST é o literal    
            case STRING_CONST:
                literal();
                break;
            default:
                ErroSintatico("Erro no writable");
        }
        return;


    }
    //⟨expression⟩ ::= ⟨simple-expr⟩ ⟨expression-tail⟩
    public void expression() throws Exception{
        try{
            simpleexpr();
            expressiontail();
        }catch(Exception e){
            ErroSintatico("Erro em expression");
        }
    }
    //⟨expression-tail⟩ ::= relop ⟨simple-expr⟩ | λ
    //lambda = follow? ou return vazio?
    public void expressiontail() throws Exception{
        switch (this.currentToken.TAG) {
            case EQ:
            case GREATER:
            case GREATER_EQ:
            case LESS:
            case LESS_EQ:
            case NOT_EQ:
                relop();
                simpleexpr();
                break;
            default:
                break;
        }

    }
    //⟨simple-expr⟩ ::= ⟨term⟩ ⟨simple-expr-tail⟩
    public void simpleexpr() throws Exception{
        try{
            term();
            simpleexprtail();
        }catch(Exception e){
            ErroSintatico("Erro em simple-expr");
        }

    }
    //⟨simple-expr-tail⟩ ::= addop ⟨term⟩ ⟨simple-expr-tail⟩ | λ
    //"+"  |  "-"  | "||"
    public void simpleexprtail() throws Exception{
        switch (this.currentToken.TAG) {
            case PLUS:
            case MINUS:
            case OR:
                addop();
                term();
                simpleexprtail();
                break;
            default:
                break;
        }
    }
    //⟨term⟩ ::= ⟨factor-a⟩⟨term-tail⟩
    public void term() throws Exception{
        try{
            factora();
            termtail();
        }catch(Exception e){
            ErroSintatico("Erro em term");
        }
    }
    //⟨term-tail⟩ ::= mulop ⟨factor-a⟩⟨term-tail⟩ | λ
    public void termtail() throws Exception{
        switch (this.currentToken.TAG) {
            case MULT:
            case DIV:
            case MOD:
            case AND:
                mulop();
                term();
                simpleexprtail();
                break;
            default:
                break;
        }

    }
    //⟨factor-a⟩ ::= ⟨factor⟩ | ! ⟨factor⟩ | - ⟨factor⟩
    public void factora() throws Exception{
        switch (this.currentToken.TAG) {
            case IDENTIFIER:
            case INT_CONST:
            case FLOAT_CONST:
            case STRING_CONST:
            case OPEN_PAR:
                factor();
                break;
            case NOT:
                eat(Tag.NOT);
                factor();
                break;
            case MINUS:
                eat(Tag.MINUS);
                factor();
                break;
            default:
                ErroSintatico("Erro no factor a");;
        }
    }
    //⟨factor⟩ ::= identifier | constant | ( ⟨expression⟩ )
    public void factor() throws Exception{
        switch (this.currentToken.TAG) {
            case IDENTIFIER:
                identifier();
                break;
            case INT_CONST:
            case FLOAT_CONST:
            case STRING_CONST:
                constant();
                break;
            case OPEN_PAR:
                factor();
                expression();
                break;
            default:
                ErroSintatico("Erro no factor");;
        }
    }

    //relop           ::= "=="  |  ">"  |  ">="  |  "<"  |  "<="  | "!="
    public void relop() throws Exception{
        switch (this.currentToken.TAG) {
            case EQ:
                eat(Tag.EQ);
                break;
            case GREATER:
                eat(Tag.GREATER);
                break;
            case GREATER_EQ:
                eat(Tag.GREATER_EQ);
                break;
            case LESS:
                eat(Tag.LESS);
                break;
            case LESS_EQ:
                eat(Tag.LESS_EQ);
                break;
            case NOT_EQ:
                eat(Tag.NOT_EQ);
                break;
            default:
                ErroSintatico("Erro em Relop");
        }
    }
    //addop           ::= "+"  |  "-"  | "||"
    public void addop() throws Exception{
        switch (this.currentToken.TAG) {
            case PLUS:
                eat(Tag.PLUS);
                break;
            case MINUS:
                eat(Tag.MINUS);
                break;
            case OR:
                eat(Tag.OR);
                break;
            default:
                ErroSintatico("Erro em addop");
        }
    }
    //mulop           ::=  "*"  | "/"   | "%" | "&&"
    public void mulop() throws Exception{
        switch (this.currentToken.TAG) {
            case MULT:
                eat(Tag.MULT);
                break;
            case DIV:
                eat(Tag.DIV);
                break;
            case MOD:
                eat(Tag.MOD);
                break;
            case AND:
                eat(Tag.AND);
                break;
            default:
                ErroSintatico("Erro em addop");
        }
    }
    //constant        ::= integer_const | float_const | literal
    public void constant() throws Exception{
        switch (this.currentToken.TAG) {
            case INT_CONST:
                eat(Tag.INT_CONST);
                break;
            case FLOAT_CONST:
                eat(Tag.FLOAT_CONST);
                break;
            case STRING_CONST:
                literal();;
                break;
            default:
                ErroSintatico("Erro em constant");
        }
    }
    //literal         ::= "{" caractere* "}"
    public void literal() throws Exception{
        try{
            eat(Tag.STRING_CONST);
        }catch(Exception e){
            ErroSintatico("Erro em literal");
        }
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

    //integer_const   ::= digit+
    public void integer_const() throws Exception{

    }
    //float_const     ::= digit+ "." digit+
    public void float_const() throws Exception{

    }

}
