// import java.io.IOException;

// public class Parser {
// private static Lexer lexer;
// private static Token token;

// Parser(Lexer lexer) throws IOException {
// this.lexer = lexer;
// token = lexer.scan();
// }

// private static void advance() throws IOException {
// token = lexer.scan();
// }

// private static void eat(Tag t) throws IOException {
// if (token.TAG == t)
// advance();
// else
// throw ExceptionFactory.createParserException(
// "Unexpected symbol " + "'" + token.toString() + "'" + "," + " expected " +
// "'" + t + "'", Position.line);

// }

// private Tag getWhichRelopTag(Tag relop) {
// switch (relop) {
// case EQ:
// return Tag.EQ;
// case GREATER:
// return Tag.GREATER;
// case GREATER_EQ:
// return Tag.GREATER_EQ;
// case LESS:
// return Tag.LESS;
// case LESS_EQ:
// return Tag.LESS_EQ;
// case NOT_EQ:
// return Tag.NOT_EQ;
// default:
// return relop;
// }
// }

// private Tag getWhichAddopTag(Tag addop) {
// switch (addop) {
// case PLUS:
// return Tag.PLUS;
// case MINUS:
// return Tag.MINUS;
// case OR:
// return Tag.OR;
// default:
// return addop;
// }
// }

// private Tag getWitchMulopTag(Tag mulop) {
// switch (mulop) {
// case MULT:
// return Tag.MULT;
// case DIV:
// return Tag.DIV;
// case MOD:
// return Tag.MOD;
// case AND:
// return Tag.AND;
// default:
// return mulop;
// }
// }

// private Tag getWhichConstantTag(Tag constant) {
// switch (constant) {
// case INT_CONST:
// return Tag.INT_CONST;
// case FLOAT_CONST:
// return Tag.FLOAT_CONST;
// case STRING_CONST:
// return Tag.STRING_CONST;
// default:
// return constant;
// }
// }

// // Gramática

// // ⟨factor⟩ ::= identifier | constant | ( ⟨expression⟩ )
// private class Factor extends Semantic {
// public void exec() throws IOException {
// switch (token.TAG) {
// case IDENTIFIER:
// eat(Tag.IDENTIFIER);
// break;

// case OPEN_PAR:
// eat(Tag.OPEN_PAR);
// expression();
// eat(Tag.CLOSE_PAR);
// break;

// case STRING_CONST:
// case INT_CONST:
// case FLOAT_CONST:
// eat(getWhichConstantTag(token.TAG));
// break;

// default:
// throw ExceptionFactory.createParserException("Unexpected symbol " + "'" +
// token.toString() + "'",
// Position.line);
// }
// }
// }

// // ⟨factor-a⟩ ::= ⟨factor⟩ | ! ⟨factor⟩ | - ⟨factor⟩
// private class factorA extends Semantic {
// public void exec() {
// switch (token.TAG) {
// case IDENTIFIER:
// case OPEN_PAR:
// case STRING_CONST:
// case INT_CONST:
// case FLOAT_CONST:
// factor();
// break;

// case NOT:
// eat(Tag.NOT);
// factor();
// break;

// case MINUS:
// eat(Tag.MINUS);
// factor();
// break;

// default:
// throw ExceptionFactory.createParserException("Unexpected symbol " + "'" +
// token.toString() + "'",
// Position.line);
// }
// }
// }

// // ⟨term-tail⟩ ::= mulop ⟨factor-a⟩⟨term-tail⟩ | λ
// private void termTail() throws IOException {
// switch (token.TAG) {
// case SEMICOLON:
// case THEN:
// case END:
// case CLOSE_PAR:
// case EQ:
// case GREATER:
// case GREATER_EQ:
// case LESS:
// case LESS_EQ:
// case NOT_EQ:
// case PLUS:
// case MINUS:
// case OR:
// break;

// case MULT:
// case DIV:
// case MOD:
// case AND:
// eat(getWitchMulopTag(token.TAG));
// factorA();
// termTail();
// break;

// default:
// throw ExceptionFactory.createParserException("Unexpected symbol " + "'" +
// token.toString() + "'",
// Position.line);
// }
// }

// // ⟨term⟩ ::= ⟨factor-a⟩⟨term-tail⟩
// private void term() throws IOException {
// switch (token.TAG) {
// case IDENTIFIER:
// case OPEN_PAR:
// case NOT:
// case MINUS:
// case STRING_CONST:
// case INT_CONST:
// case FLOAT_CONST:
// factorA();
// termTail();
// break;

// default:
// throw ExceptionFactory.createParserException("Unexpected symbol " + "'" +
// token.toString() + "'",
// Position.line);
// }
// }

// // ⟨simple-expr-tail⟩ ::= addop ⟨term⟩ ⟨simple-expr-tail⟩ | λ
// private void simpleExprTail() throws IOException {
// switch (token.TAG) {
// case SEMICOLON:
// case THEN:
// case END:
// case CLOSE_PAR:
// case EQ:
// case GREATER:
// case GREATER_EQ:
// case LESS:
// case LESS_EQ:
// case NOT_EQ:
// break;

// case PLUS:
// case MINUS:
// case OR:
// eat(getWhichAddopTag(token.TAG));
// term();
// simpleExprTail();
// break;

// default:
// throw ExceptionFactory.createParserException("Unexpected symbol " + "'" +
// token.toString() + "'",
// Position.line);
// }
// }

// // ⟨simple-expr⟩ ::= ⟨term⟩ ⟨simple-expr-tail⟩
// private void simpleExpr() throws IOException {
// switch (token.TAG) {
// case IDENTIFIER:
// case OPEN_PAR:
// case NOT:
// case MINUS:
// case STRING_CONST:
// case INT_CONST:
// case FLOAT_CONST:
// term();
// simpleExprTail();
// break;

// default:
// throw ExceptionFactory.createParserException("Unexpected symbol " + "'" +
// token.toString() + "'",
// Position.line);
// }
// }

// // ⟨expression-tail⟩ ::= relop ⟨simple-expr⟩ | λ
// private void expressionTail() throws IOException {
// switch (token.TAG) {
// case THEN:
// case END:
// case CLOSE_PAR:
// break;

// case EQ:
// case GREATER:
// case GREATER_EQ:
// case LESS:
// case LESS_EQ:
// case NOT_EQ:
// eat(getWhichRelopTag(token.TAG));
// simpleExpr();
// break;

// default:
// throw ExceptionFactory.createParserException("Unexpected symbol " + "'" +
// token.toString() + "'",
// Position.line);
// }
// }

// // ⟨expression⟩ ::= ⟨simple-expr⟩ ⟨expression-tail⟩
// private void expression() throws IOException {
// switch (token.TAG) {
// case IDENTIFIER:
// case OPEN_PAR:
// case NOT:
// case MINUS:
// case STRING_CONST:
// case INT_CONST:
// case FLOAT_CONST:
// simpleExpr();
// expressionTail();
// break;

// default:
// throw ExceptionFactory.createParserException("Unexpected symbol " + "'" +
// token.toString() + "'",
// Position.line);
// }
// }

// // ⟨writable⟩ ::= ⟨simple-expr⟩ | literal
// private void writable() throws IOException {
// switch (token.TAG) {
// case IDENTIFIER:
// case OPEN_PAR:
// case NOT:
// case MINUS:
// case INT_CONST:
// case FLOAT_CONST:
// simpleExpr();
// break;

// case STRING_CONST:
// eat(Tag.STRING_CONST);
// break;

// default:
// throw ExceptionFactory.createParserException("Unexpected symbol " + "'" +
// token.toString() + "'",
// Position.line);
// }
// }

// // ⟨write-stmt⟩ ::= print ( ⟨writable⟩ )
// private void writeStmt() throws IOException {
// switch (token.TAG) {
// case PRINT:
// eat(Tag.PRINT);
// eat(Tag.OPEN_PAR);
// writable();
// eat(Tag.CLOSE_PAR);
// break;

// default:
// throw ExceptionFactory.createParserException("Unexpected symbol " + "'" +
// token.toString() + "'",
// Position.line);
// }
// }

// // ⟨read-stmt⟩ ::= scan ( identifier )
// private void readStmt() throws IOException {
// switch (token.TAG) {
// case SCAN:
// eat(Tag.SCAN);
// eat(Tag.OPEN_PAR);
// eat(Tag.IDENTIFIER);
// eat(Tag.CLOSE_PAR);
// break;

// default:
// throw ExceptionFactory.createParserException("Unexpected symbol " + "'" +
// token.toString() + "'",
// Position.line);
// }
// }

// // ⟨stmt-sufix⟩ ::= while ⟨condition⟩ end
// private void stmtSufix() throws IOException {
// switch (token.TAG) {
// case WHILE:
// eat(Tag.WHILE);
// condition();
// eat(Tag.END);
// break;

// default:
// throw ExceptionFactory.createParserException("Unexpected symbol " + "'" +
// token.toString() + "'",
// Position.line);
// }
// }

// // ⟨while-stmt⟩ ::= do ⟨stmt-list⟩ ⟨stmt-sufix⟩
// private void whileStmt() throws IOException {
// switch (token.TAG) {
// case DO:
// eat(Tag.DO);
// stmtList();
// stmtSufix();
// break;

// default:
// throw ExceptionFactory.createParserException("Unexpected symbol " + "'" +
// token.toString() + "'",
// Position.line);
// }
// }

// // ⟨condition⟩ ::= ⟨expression⟩
// private void condition() throws IOException {
// switch (token.TAG) {
// case IDENTIFIER:
// case OPEN_PAR:
// case NOT:
// case MINUS:
// case INT_CONST:
// case FLOAT_CONST:
// expression();
// break;

// default:
// throw ExceptionFactory.createParserException("Unexpected symbol " + "'" +
// token.toString() + "'",
// Position.line);
// }
// }

// // ⟨if-stmt-tail⟩ ::= end | else ⟨stmt-list⟩ end
// private void ifStmtTail() throws IOException {
// switch (token.TAG) {
// case END:
// eat(Tag.END);
// break;

// case ELSE:
// eat(Tag.ELSE);
// stmtList();
// eat(Tag.END);
// break;

// default:
// throw ExceptionFactory.createParserException("Unexpected symbol " + "'" +
// token.toString() + "'",
// Position.line);
// }
// }

// // ⟨if-stmt⟩ ::= if ⟨condition⟩ then ⟨stmt-list⟩ ⟨if-stmt-tail⟩
// private void ifStmt() throws IOException {
// switch (token.TAG) {
// case IF:
// eat(Tag.IF);
// condition();
// eat(Tag.THEN);
// stmtList();
// ifStmtTail();
// break;

// default:
// throw ExceptionFactory.createParserException("Unexpected symbol " + "'" +
// token.toString() + "'",
// Position.line);
// }
// }

// // ⟨assign-stmt⟩ ::= identifier = ⟨simple-expr⟩
// private void assignStmt() throws IOException {
// switch (token.TAG) {
// case IDENTIFIER:
// eat(Tag.IDENTIFIER);
// eat(Tag.ASSIGN);
// simpleExpr();
// break;

// default:
// throw ExceptionFactory.createParserException("Unexpected symbol " + "'" +
// token.toString() + "'",
// Position.line);
// }
// }

// // ⟨stmt⟩ ::= ⟨assign-stmt⟩ ; | ⟨if-stmt⟩ | ⟨while-stmt⟩ | ⟨read-stmt⟩ ; |
// // ⟨write-stmt⟩ ;
// private void stmt() throws IOException {
// switch (token.TAG) {
// case IDENTIFIER:
// assignStmt();
// eat(Tag.SEMICOLON);
// break;

// case IF:
// ifStmt();
// break;

// case DO:
// whileStmt();
// break;

// case SCAN:
// readStmt();
// eat(Tag.SEMICOLON);
// break;

// case PRINT:
// writeStmt();
// eat(Tag.SEMICOLON);
// break;

// default:
// throw ExceptionFactory.createParserException("Unexpected symbol " + "'" +
// token.toString() + "'",
// Position.line);
// }
// }

// // ⟨stmt-list⟩ ::= ⟨stmt⟩ {⟨stmt⟩}
// private void stmtList() throws IOException {
// switch (token.TAG) {
// case IDENTIFIER:
// case IF:
// case DO:
// case SCAN:
// case PRINT:
// stmt();
// while (token.TAG == Tag.IDENTIFIER || token.TAG == Tag.IF || token.TAG ==
// Tag.DO || token.TAG == Tag.SCAN
// || token.TAG == Tag.PRINT)
// stmtList(); // recursão quantas vezes for necessário
// break;

// default:
// throw ExceptionFactory.createParserException("Unexpected symbol " + "'" +
// token.toString() + "'",
// Position.line);
// }
// }

// // ⟨type⟩ ::= int | float | string
// private void type() throws IOException {
// switch (token.TAG) {
// case INT:
// eat(Tag.INT);
// break;

// case FLOAT:
// eat(Tag.FLOAT);
// break;

// case STRING:
// eat(Tag.STRING);
// break;

// default:
// throw ExceptionFactory.createParserException("Unexpected symbol " + "'" +
// token.toString() + "'",
// Position.line);
// }
// }

// // ⟨ident-list⟩ ::= identifier {, identifier}
// private void identList() throws IOException {
// switch (token.TAG) {
// case IDENTIFIER:
// eat(Tag.IDENTIFIER);
// if (token.TAG == Tag.COMMA) {
// eat(Tag.COMMA);
// identList();
// }
// break;

// default:
// throw ExceptionFactory.createParserException("Unexpected symbol " + "'" +
// token.toString() + "'",
// Position.line);
// }
// }

// // ⟨decl⟩ ::= ⟨type⟩ ⟨ident-list⟩ ;
// private void decl() throws IOException {

// static String teste = ""switch(token.TAG)
// {
// case INT:
// case FLOAT:
// case STRING:
// type();
// identList();
// eat(Tag.SEMICOLON);
// break;

// default:
// throw ExceptionFactory.createParserException("Unexpected symbol " + "'" +
// token.toString() + "'",
// Position.line);
// }
// }

// // ⟨decl-list⟩ ::= ⟨decl⟩ {decl}
// private void declList() throws IOException {
// switch (token.TAG) {
// case INT:
// case FLOAT:
// case STRING:
// decl();
// while (token.TAG == Tag.INT || token.TAG == Tag.FLOAT || token.TAG ==
// Tag.STRING)
// decl();
// break;

// default:
// throw ExceptionFactory.createParserException("Unexpected symbol " + "'" +
// token.toString() + "'",
// Position.line);
// }
// }

// // ⟨program⟩ ::= start [decl-list] ⟨stmt-list⟩ exit
// private void program() throws IOException {
// String teste = "ola";

// switch (token.TAG) {
// case START:
// eat(Tag.START);
// if (token.TAG == Tag.INT || token.TAG == Tag.FLOAT || token.TAG ==
// Tag.STRING)
// declList();
// stmtList();
// eat(Tag.EXIT);
// break;

// default:
// throw ExceptionFactory.createParserException("Unexpected symbol " + "'" +
// token.toString() + "'",
// Position.line);
// }
// }

// // ⟨begin⟩ ::= ⟨program⟩#
// public void begin() throws IOException {
// switch (token.TAG) {
// case START:
// program();
// eat(Tag.EOF);
// break;

// default:
// throw ExceptionFactory.createParserException("Unexpected symbol " + "'" +
// token.toString() + "'",
// Position.line);
// }
// }

// }

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
      throw ExceptionFactory.createParserException(
          "Unexpected symbol " + "'" + token.toString() + "'" + "," + " expected " + "'" + t + "'", Position.line);
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

  private Tag getWhichMulopTag(Tag mulop) {
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

  // Interface para as classes de procedimentos
  private abstract class Semantic {
    private Type type; // Atributo para armazenar o tipo semântico

    // Método abstrato para execução sintática
    public abstract void exec() throws IOException;

    // Método para análise semântica
    public Type getTipo() {
      return type;
    }
  }

  // ⟨factor⟩ ::= identifier | constant | ( ⟨expression⟩ )
  private class Factor extends Semantic {
    public void exec() throws IOException {
      switch (token.TAG) {
        case IDENTIFIER:
          eat(Tag.IDENTIFIER);
          break;

        case OPEN_PAR:
          eat(Tag.OPEN_PAR);
          new Expression().exec();
          eat(Tag.CLOSE_PAR);
          break;

        case STRING_CONST:
        case INT_CONST:
        case FLOAT_CONST:
          eat(getWhichConstantTag(token.TAG));
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨factor-a⟩ ::= ⟨factor⟩ | ! ⟨factor⟩ | - ⟨factor⟩
  private class FactorA extends Semantic {
    public void exec() throws IOException {
      switch (token.TAG) {
        case IDENTIFIER:
        case OPEN_PAR:
        case STRING_CONST:
        case INT_CONST:
        case FLOAT_CONST:
          new Factor().exec();
          break;

        case NOT:
          eat(Tag.NOT);
          new Factor().exec();
          break;

        case MINUS:
          eat(Tag.MINUS);
          new Factor().exec();
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨term-tail⟩ ::= mulop ⟨factor-a⟩⟨term-tail⟩ | λ
  private class TermTail extends Semantic {
    public void exec() throws IOException {
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
          eat(getWhichMulopTag(token.TAG));
          new FactorA().exec();
          new TermTail().exec();
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨term⟩ ::= ⟨factor-a⟩⟨term-tail⟩
  private class Term extends Semantic {
    public void exec() throws IOException {
      switch (token.TAG) {
        case IDENTIFIER:
        case OPEN_PAR:
        case NOT:
        case MINUS:
        case STRING_CONST:
        case INT_CONST:
        case FLOAT_CONST:
          new FactorA().exec();
          new TermTail().exec();
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨simple-expr-tail⟩ ::= addop ⟨term⟩ ⟨simple-expr-tail⟩ | λ
  private class SimpleExprTail extends Semantic {
    public void exec() throws IOException {
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
          new Term().exec();
          new SimpleExprTail().exec();
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨simple-expr⟩ ::= ⟨term⟩ ⟨simple-expr-tail⟩
  private class SimpleExpr extends Semantic {
    public void exec() throws IOException {
      switch (token.TAG) {
        case IDENTIFIER:
        case OPEN_PAR:
        case NOT:
        case MINUS:
        case STRING_CONST:
        case INT_CONST:
        case FLOAT_CONST:
          new Term().exec();
          new SimpleExprTail().exec();
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨expression-tail⟩ ::= relop ⟨simple-expr⟩ | λ
  private class ExpressionTail extends Semantic {
    public void exec() throws IOException {
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
          new SimpleExpr().exec();
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨expression⟩ ::= ⟨simple-expr⟩ ⟨expression-tail⟩
  private class Expression extends Semantic {
    public void exec() throws IOException {
      switch (token.TAG) {
        case IDENTIFIER:
        case OPEN_PAR:
        case NOT:
        case MINUS:
        case STRING_CONST:
        case INT_CONST:
        case FLOAT_CONST:
          new SimpleExpr().exec();
          new ExpressionTail().exec();
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨writable⟩ ::= ⟨simple-expr⟩ | literal
  private class Writable extends Semantic {
    public void exec() throws IOException {
      switch (token.TAG) {
        case IDENTIFIER:
        case OPEN_PAR:
        case NOT:
        case MINUS:
        case INT_CONST:
        case FLOAT_CONST:
          new SimpleExpr().exec();
          break;

        case STRING_CONST:
          eat(Tag.STRING_CONST);
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨write-stmt⟩ ::= print ( ⟨writable⟩ )
  private class WriteStmt extends Semantic {
    public void exec() throws IOException {
      switch (token.TAG) {
        case PRINT:
          eat(Tag.PRINT);
          eat(Tag.OPEN_PAR);
          new Writable().exec();
          eat(Tag.CLOSE_PAR);
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨read-stmt⟩ ::= scan ( identifier )
  private class ReadStmt extends Semantic {
    public void exec() throws IOException {
      switch (token.TAG) {
        case SCAN:
          eat(Tag.SCAN);
          eat(Tag.OPEN_PAR);
          eat(Tag.IDENTIFIER);
          eat(Tag.CLOSE_PAR);
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨stmt-sufix⟩ ::= while ⟨condition⟩ end
  private class StmtSufix extends Semantic {
    public void exec() throws IOException {
      switch (token.TAG) {
        case WHILE:
          eat(Tag.WHILE);
          new Condition().exec();
          eat(Tag.END);
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨while-stmt⟩ ::= do ⟨stmt-list⟩ ⟨stmt-sufix⟩
  private class WhileStmt extends Semantic {
    public void exec() throws IOException {
      switch (token.TAG) {
        case DO:
          eat(Tag.DO);
          new StmtList().exec();
          new StmtSufix().exec();
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨condition⟩ ::= ⟨expression⟩
  private class Condition extends Semantic {
    public void exec() throws IOException {
      switch (token.TAG) {
        case IDENTIFIER:
        case OPEN_PAR:
        case NOT:
        case MINUS:
        case INT_CONST:
        case FLOAT_CONST:
          new Expression().exec();
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨if-stmt-tail⟩ ::= end | else ⟨stmt-list⟩ end
  private class IfStmtTail extends Semantic {
    public void exec() throws IOException {
      switch (token.TAG) {
        case END:
          eat(Tag.END);
          break;

        case ELSE:
          eat(Tag.ELSE);
          new StmtList().exec();
          eat(Tag.END);
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨if-stmt⟩ ::= if ⟨condition⟩ then ⟨stmt-list⟩ ⟨if-stmt-tail⟩
  private class IfStmt extends Semantic {
    public void exec() throws IOException {
      switch (token.TAG) {
        case IF:
          eat(Tag.IF);
          new Condition().exec();
          eat(Tag.THEN);
          new StmtList().exec();
          new IfStmtTail().exec();
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨assign-stmt⟩ ::= identifier = ⟨simple-expr⟩
  private class AssignStmt extends Semantic {
    public void exec() throws IOException {
      switch (token.TAG) {
        case IDENTIFIER:
          eat(Tag.IDENTIFIER);
          eat(Tag.ASSIGN);
          new SimpleExpr().exec();
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨stmt⟩ ::= ⟨assign-stmt⟩ ; | ⟨if-stmt⟩ | ⟨while-stmt⟩ | ⟨read-stmt⟩ ; |
  // ⟨write-stmt⟩ ;
  private class Stmt extends Semantic {
    public void exec() throws IOException {
      switch (token.TAG) {
        case IDENTIFIER:
          new AssignStmt().exec();
          eat(Tag.SEMICOLON);
          break;

        case IF:
          new IfStmt().exec();
          break;

        case DO:
          new WhileStmt().exec();
          break;

        case SCAN:
          new ReadStmt().exec();
          eat(Tag.SEMICOLON);
          break;

        case PRINT:
          new WriteStmt().exec();
          eat(Tag.SEMICOLON);
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨stmt-list⟩ ::= ⟨stmt⟩ {⟨stmt⟩}
  private class StmtList extends Semantic {
    public void exec() throws IOException {
      switch (token.TAG) {
        case IDENTIFIER:
        case IF:
        case DO:
        case SCAN:
        case PRINT:
          new Stmt().exec();
          while (token.TAG == Tag.IDENTIFIER || token.TAG == Tag.IF || token.TAG == Tag.DO || token.TAG == Tag.SCAN
              || token.TAG == Tag.PRINT)
            new StmtList().exec(); // recursão quantas vezes for necessário
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨type⟩ ::= int | float | string
  private class Type extends Semantic {
    public void exec() throws IOException {
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
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨ident-list⟩ ::= identifier {, identifier}
  private class IdentList extends Semantic {
    public void exec() throws IOException {
      switch (token.TAG) {
        case IDENTIFIER:
          eat(Tag.IDENTIFIER);
          if (token.TAG == Tag.COMMA) {
            eat(Tag.COMMA);
            new IdentList().exec();
          }
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨decl⟩ ::= ⟨type⟩ ⟨ident-list⟩ ;
  private class Decl extends Semantic {
    public void exec() throws IOException {
      switch (token.TAG) {
        case INT:
        case FLOAT:
        case STRING:
          new Type().exec();
          new IdentList().exec();
          eat(Tag.SEMICOLON);
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨decl-list⟩ ::= ⟨decl⟩ {decl}
  private class DeclList extends Semantic {
    public void exec() throws IOException {
      switch (token.TAG) {
        case INT:
        case FLOAT:
        case STRING:
          new Decl().exec();
          while (token.TAG == Tag.INT || token.TAG == Tag.FLOAT || token.TAG == Tag.STRING)
            new Decl().exec();
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨program⟩ ::= start [decl-list] ⟨stmt-list⟩ exit
  private class Program extends Semantic {
    public void exec() throws IOException {
      switch (token.TAG) {
        case START:
          eat(Tag.START);
          if (token.TAG == Tag.INT || token.TAG == Tag.FLOAT || token.TAG == Tag.STRING)
            new DeclList().exec();
          new StmtList().exec();
          eat(Tag.EXIT);
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨begin⟩ ::= ⟨program⟩#
  public void begin() throws IOException {
    switch (token.TAG) {
      case START:
        new Program().exec();
        eat(Tag.EOF);
        break;

      default:
        throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
            Position.line);
    }
  }
}