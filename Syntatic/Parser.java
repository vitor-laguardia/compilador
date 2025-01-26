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

    private void eat(Tag tag) throws Exception{
        while (currentToken != null && currentToken.TAG != Tag.EOF) {
            // logica de erro depois
            advance();
        }

    }



    //Construções
    //⟨begin⟩ ::= ⟨program⟩#
    public void begin() throws Exception{
        System.out.println("-------------------------------------------- PROGRAMA INICIADO");
        program();
        eat(Tag.EOF);
        System.out.println("-------------------------------------------- PROGRAMA FINALIZADO");
    }
    //⟨program⟩ ::= start [decl-list] ⟨stmt-list⟩ exit
    public void program() throws Exception{

    }
    //⟨decl-list⟩ ::= ⟨decl⟩ {decl}
    public void decllist() throws Exception{

    }
    //⟨decl⟩ ::= ⟨type⟩ ⟨ident-list⟩ ;
    public void decl() throws Exception{

    }
    //⟨ident-list⟩ ::= identifier {, identifier}
    public void identlist() throws Exception{

    }
    //⟨type⟩ ::= int | float | string
    public void type() throws Exception{

    }
    //⟨stmt-list⟩ ::= ⟨stmt⟩ {⟨stmt⟩}
    public void stmtlist() throws Exception{

    }
    //⟨stmt⟩ ::= ⟨assign-stmt⟩ ; | ⟨if-stmt⟩ | ⟨while-stmt⟩ | ⟨read-stmt⟩ ; | ⟨write-stmt⟩ ;
    public void stmt() throws Exception{

    }
    //⟨assign-stmt⟩ ::= identifier = ⟨simple-expr⟩
    public void assignstmt() throws Exception{

    }
    //⟨if-stmt⟩ ::= if ⟨condition⟩ then ⟨stmt-list⟩ ⟨if-stmt-tail⟩
    public void ifstmt() throws Exception{

    }
    //⟨if-stmt-tail⟩ ::= end | else ⟨stmt-list⟩ end
    public void ifstmttail() throws Exception{

    }
    //⟨condition⟩ ::= ⟨expression⟩
    public void condition() throws Exception{

    }
    //⟨while-stmt⟩ ::= do ⟨stmt-list⟩ ⟨stmt-sufix⟩
    public void whilestmt() throws Exception{

    }
    //⟨stmt-sufix⟩ ::= while ⟨condition⟩ end
    public void stmtsufix() throws Exception{

    }
    //⟨read-stmt⟩ ::= scan ( identifier )
    public void readstmt() throws Exception{

    }
    //⟨write-stmt⟩ ::= print ( ⟨writable⟩ )
    public void writestmt() throws Exception{

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
