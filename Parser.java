import java.io.IOException;

public class Parser {
  private Lexer lexer;
  private Token token;

  Parser(Lexer lexer) throws IOException {
    this.lexer = lexer;
    token = lexer.scan();
  }

  private void advance() throws IOException {
    token = lexer.scan();
  }

  private void eat(Tag t) throws IOException {
    if (token.TAG == t)
      advance();
    else
      System.out.println("Error: expected " + t + " at line " + Position.line);
  }

  private Tag getWhichRelopTag(Tag relop) {
    switch (relop) {
      case EQ:
        return Tag.EQ;
      case GREATER:
        return Tag.GREATER;
      case GREATER_EQ:
        return Tag.GREATER_EQ;
      case LESS:
        return Tag.LESS;
      case LESS_EQ:
        return Tag.LESS_EQ;
      case NOT_EQ:
        return Tag.NOT_EQ;
      default:
        return relop;
    }
  }

  private Tag getWhichAddopTag(Tag addop) {
    switch (addop) {
      case PLUS:
        return Tag.PLUS;
      case MINUS:
        return Tag.MINUS;
      case OR:
        return Tag.OR;
      default:
        return addop;
    }
  }

  private Tag getWitchMulopTag(Tag mulop) {
    switch (mulop) {
      case MULT:
        return Tag.MULT;
      case DIV:
        return Tag.DIV;
      case MOD:
        return Tag.MOD;
      case AND:
        return Tag.AND;
      default:
        return mulop;
    }
  }

  private Tag getWhichConstantTag(Tag constant) {
    switch (constant) {
      case INT_CONST:
        return Tag.INT_CONST;
      case FLOAT_CONST:
        return Tag.FLOAT_CONST;
      case STRING_CONST:
        return Tag.STRING_CONST;
      default:
        return constant;
    }
  }

  // Gramática

  // ⟨factor⟩ ::= identifier | constant | ( ⟨expression⟩ )
  private void factor() throws IOException {
    switch (token.TAG) {
      case IDENTIFIER:
        eat(Tag.IDENTIFIER);
        break;

      case OPEN_PAR:
        eat(Tag.OPEN_PAR);
        expression();
        eat(Tag.CLOSE_PAR);
        break;

      case STRING_CONST:
      case INT_CONST:
      case FLOAT_CONST:
        eat(getWhichConstantTag(token.TAG));
        break;

      default:
        System.out.println("Erro em factor");
        break;
    }
  }

  // ⟨factor-a⟩ ::= ⟨factor⟩ | ! ⟨factor⟩ | - ⟨factor⟩
  private void factorA() throws IOException {
    switch (token.TAG) {
      case IDENTIFIER:
      case OPEN_PAR:
      case STRING_CONST:
      case INT_CONST:
      case FLOAT_CONST:
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
        System.out.println("Erro em factor A");
        break;
    }
  }

  // ⟨term-tail⟩ ::= mulop ⟨factor-a⟩⟨term-tail⟩ | λ
  private void termTail() throws IOException {
    switch (token.TAG) {
      case SEMICOLON:
      case THEN:
      case END:
      case CLOSE_PAR:
      case EQ:
      case GREATER:
      case GREATER_EQ:
      case LESS:
      case LESS_EQ:
      case NOT_EQ:
      case PLUS:
      case MINUS:
      case OR:
        break;

      case MULT:
      case DIV:
      case MOD:
      case AND:
        eat(getWitchMulopTag(token.TAG));
        factorA();
        termTail();
        break;

      default:
        System.out.println("Erro em termTail");
        break;
    }
  }

  // ⟨term⟩ ::= ⟨factor-a⟩⟨term-tail⟩
  private void term() throws IOException {
    switch (token.TAG) {
      case IDENTIFIER:
      case OPEN_PAR:
      case NOT:
      case MINUS:
      case STRING_CONST:
      case INT_CONST:
      case FLOAT_CONST:
        factorA();
        termTail();
        break;

      default:
        System.out.println("Erro em term");
        break;
    }
  }

  // ⟨simple-expr-tail⟩ ::= addop ⟨term⟩ ⟨simple-expr-tail⟩ | λ
  private void simpleExprTail() throws IOException {
    switch (token.TAG) {
      case SEMICOLON:
      case THEN:
      case END:
      case CLOSE_PAR:
      case EQ:
      case GREATER:
      case GREATER_EQ:
      case LESS:
      case LESS_EQ:
      case NOT_EQ:
        break;

      case PLUS:
      case MINUS:
      case OR:
        eat(getWhichAddopTag(token.TAG));
        term();
        simpleExprTail();
        break;

      default:
        System.out.println("Erro em simpleExprTail");
        break;
    }
  }

  // ⟨simple-expr⟩ ::= ⟨term⟩ ⟨simple-expr-tail⟩
  private void simpleExpr() throws IOException {
    switch (token.TAG) {
      case IDENTIFIER:
      case OPEN_PAR:
      case NOT:
      case MINUS:
      case STRING_CONST:
      case INT_CONST:
      case FLOAT_CONST:
        term();
        simpleExprTail();
        break;

      default:
        System.out.println("Erro em simpleExpr");
        break;
    }
  }

  // ⟨expression-tail⟩ ::= relop ⟨simple-expr⟩ | λ
  private void expressionTail() throws IOException {
    switch (token.TAG) {
      case THEN:
      case END:
      case CLOSE_PAR:
        break;

      case EQ:
      case GREATER:
      case GREATER_EQ:
      case LESS:
      case LESS_EQ:
      case NOT_EQ:
        eat(getWhichRelopTag(token.TAG));
        simpleExpr();
        break;

      default:
        System.out.println("Erro em expressionTail");
        break;
    }
  }

  // ⟨expression⟩ ::= ⟨simple-expr⟩ ⟨expression-tail⟩
  private void expression() throws IOException {
    switch (token.TAG) {
      case IDENTIFIER:
      case OPEN_PAR:
      case NOT:
      case MINUS:
      case STRING_CONST:
      case INT_CONST:
      case FLOAT_CONST:
        simpleExpr();
        expressionTail();
        break;

      default:
        System.out.println("Erro em expression");
        break;
    }
  }

  // ⟨writable⟩ ::= ⟨simple-expr⟩ | literal
  private void writable() throws IOException {
    switch (token.TAG) {
      case IDENTIFIER:
      case OPEN_PAR:
      case NOT:
      case MINUS:
      case INT_CONST:
      case FLOAT_CONST:
        simpleExpr();
        break;

      case STRING_CONST:
        eat(Tag.STRING_CONST);
        break;

      default:
        System.out.println("Erro no writable");
        break;
    }
  }

  // ⟨write-stmt⟩ ::= print ( ⟨writable⟩ )
  private void writeStmt() throws IOException {
    switch (token.TAG) {
      case PRINT:
        eat(Tag.PRINT);
        eat(Tag.OPEN_PAR);
        writable();
        eat(Tag.CLOSE_PAR);
        break;

      default:
        System.out.println("Erro writeStmt");
        break;
    }
  }

  // ⟨read-stmt⟩ ::= scan ( identifier )
  private void readStmt() throws IOException {
    switch (token.TAG) {
      case SCAN:
        eat(Tag.SCAN);
        eat(Tag.OPEN_PAR);
        eat(Tag.IDENTIFIER);
        eat(Tag.CLOSE_PAR);
        break;

      default:
        System.out.println("Erro em readtStmt");
        break;
    }
  }

  // ⟨stmt-sufix⟩ ::= while ⟨condition⟩ end
  private void stmtSufix() throws IOException {
    switch (token.TAG) {
      case WHILE:
        eat(Tag.WHILE);
        condition();
        eat(Tag.END);
        break;

      default:
        System.out.println("Erro em stmtSufix");
        break;
    }
  }

  // ⟨while-stmt⟩ ::= do ⟨stmt-list⟩ ⟨stmt-sufix⟩
  private void whileStmt() throws IOException {
    switch (token.TAG) {
      case DO:
        eat(Tag.DO);
        stmtList();
        stmtSufix();
        break;

      default:
        System.out.println("Erro em whileStmt");
        break;
    }
  }

  // ⟨condition⟩ ::= ⟨expression⟩
  private void condition() throws IOException {
    switch (token.TAG) {
      case IDENTIFIER:
      case OPEN_PAR:
      case NOT:
      case MINUS:
      case INT_CONST:
      case FLOAT_CONST:
        expression();
        break;

      default:
        System.out.println("Erro em condition");
        break;
    }
  }

  // ⟨if-stmt-tail⟩ ::= end | else ⟨stmt-list⟩ end
  private void ifStmtTail() throws IOException {
    switch (token.TAG) {
      case END:
        eat(Tag.END);
        break;

      case ELSE:
        eat(Tag.ELSE);
        stmtList();
        eat(Tag.END);
        break;

      default:
        System.out.println("Erro em ifStmtTail");
        break;
    }
  }

  // ⟨if-stmt⟩ ::= if ⟨condition⟩ then ⟨stmt-list⟩ ⟨if-stmt-tail⟩
  private void ifStmt() throws IOException {
    switch (token.TAG) {
      case IF:
        eat(Tag.IF);
        condition();
        eat(Tag.THEN);
        stmtList();
        ifStmtTail();
        break;

      default:
        System.out.println("Erro em ifStmt");
        break;
    }
  }

  // ⟨assign-stmt⟩ ::= identifier = ⟨simple-expr⟩
  private void assignStmt() throws IOException {
    switch (token.TAG) {
      case IDENTIFIER:
        eat(Tag.IDENTIFIER);
        eat(Tag.ASSIGN);
        // System.err.println("vai entrar em simpleExpr");
        simpleExpr();
        break;

      default:
        System.out.println("Erro em assignStmt");
        break;
    }
  }

  // ⟨stmt⟩ ::= ⟨assign-stmt⟩ ; | ⟨if-stmt⟩ | ⟨while-stmt⟩ | ⟨read-stmt⟩ ; |
  // ⟨write-stmt⟩ ;
  private void stmt() throws IOException {
    switch (token.TAG) {
      case IDENTIFIER:
        assignStmt();
        eat(Tag.SEMICOLON);
        break;

      case IF:
        ifStmt();
        break;

      case DO:
        whileStmt();
        break;

      case SCAN:
        readStmt();
        eat(Tag.SEMICOLON);
        break;

      case PRINT:
        writeStmt();
        eat(Tag.SEMICOLON);
        break;

      default:
        System.out.println("Erro em stmt");
        break;
    }
  }

  // ⟨stmt-list⟩ ::= ⟨stmt⟩ {⟨stmt⟩}
  private void stmtList() throws IOException {
    switch (token.TAG) {
      case IDENTIFIER:
      case IF:
      case DO:
      case SCAN:
      case PRINT:
        stmt();
        while (token.TAG == Tag.IDENTIFIER || token.TAG == Tag.IF || token.TAG == Tag.DO || token.TAG == Tag.SCAN
            || token.TAG == Tag.PRINT)
          stmtList(); // recursão quantas vezes for necessário
        break;

      default:
        System.out.println("Erro em stmtList");
        break;
    }
  }

  // ⟨type⟩ ::= int | float | string
  private void type() throws IOException {
    switch (token.TAG) {
      case INT:
        eat(Tag.INT);
        break;

      case FLOAT:
        eat(Tag.FLOAT);
        break;

      case STRING:
        eat(Tag.STRING);
        break;

      default:
        System.out.println("Erro em type");
        break;
    }
  }

  // ⟨ident-list⟩ ::= identifier {, identifier}
  private void identList() throws IOException {
    switch (token.TAG) {
      case IDENTIFIER:
        eat(Tag.IDENTIFIER);
        if (token.TAG == Tag.COMMA) {
          eat(Tag.COMMA);
          identList();
        }
        break;

      default:
        System.out.println("Erro em identList");
        break;
    }
  }

  // ⟨decl⟩ ::= ⟨type⟩ ⟨ident-list⟩ ;
  private void decl() throws IOException {
    switch (token.TAG) {
      case INT:
      case FLOAT:
      case STRING:
        type();
        identList();
        eat(Tag.SEMICOLON);
        break;

      default:
        System.out.println("Erro em decl");
        break;
    }
  }

  // ⟨decl-list⟩ ::= ⟨decl⟩ {decl}
  private void declList() throws IOException {
    switch (token.TAG) {
      case INT:
      case FLOAT:
      case STRING:
        decl();
        if (token.TAG == Tag.INT || token.TAG == Tag.FLOAT || token.TAG == Tag.STRING)
          decl();
        break;

      default:
        System.out.println("Erro em declList");
        break;
    }
  }

  // ⟨program⟩ ::= start [decl-list] ⟨stmt-list⟩ exit
  private void program() throws IOException {
    switch (token.TAG) {
      case START:
        eat(Tag.START);
        if (token.TAG == Tag.INT || token.TAG == Tag.FLOAT || token.TAG == Tag.STRING)
          declList();
        stmtList();
        eat(Tag.EXIT);
        break;

      default:
        System.out.println("Erro em program");
        break;
    }
  }

  // ⟨begin⟩ ::= ⟨program⟩#
  public void begin() throws IOException {
    switch (token.TAG) {
      case START:
        program();
        eat(Tag.EOF);
        break;

      default:
        System.out.println("Erro em begin");
        break;
    }
  }

}
