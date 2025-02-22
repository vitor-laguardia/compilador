import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Parser {
  private Lexer lexer;
  private Token token;
  private Semantic semantic;
  private Map<String, Type> identifiersTable = new HashMap<>(); // Tabela para armazenar tipos das variáveis declaradas

  Parser(Lexer lexer, Semantic semantic) throws IOException {
    this.lexer = lexer;
    this.semantic = semantic;
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

  // Interface para as classes de procedimentos
  private abstract class ParserProcedure {
    protected Type type; // Atributo para armazenar o tipo semântico do procedimento

    // Método abstrato para execução sintática
    public abstract void exec() throws IOException;

    // Método para obter o tipo semântico
    public Type getType() {
      return type;
    }
  }

  // ⟨factor⟩ ::= identifier | constant | ( ⟨expression⟩ )
  private class Factor extends ParserProcedure {
    public void exec() throws IOException {
      switch (token.TAG) {
        case IDENTIFIER:
          String id = token.toString();
          eat(Tag.IDENTIFIER);

          // Verificar se a variável foi declarada
          if (identifiersTable.containsKey(id)) {
            type = identifiersTable.get(id);
          } else {
            throw ExceptionFactory.createSemanticException("Undefined variable: " + id, Position.line);
          }
          break;

        case OPEN_PAR:
          eat(Tag.OPEN_PAR);
          Expression expr = new Expression();
          expr.exec();
          type = expr.getType();
          eat(Tag.CLOSE_PAR);
          break;

        case STRING_CONST:
          eat(Tag.STRING_CONST);
          type = Type.STRING;
          break;

        case INT_CONST:
          eat(Tag.INT_CONST);
          type = Type.INTEGER;
          break;

        case FLOAT_CONST:
          eat(Tag.FLOAT_CONST);
          type = Type.FLOAT;
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨factor-a⟩ ::= ⟨factor⟩ | ! ⟨factor⟩ | - ⟨factor⟩
  private class FactorA extends ParserProcedure {
    public void exec() throws IOException {
      switch (token.TAG) {
        case IDENTIFIER:
        case OPEN_PAR:
        case STRING_CONST:
        case INT_CONST:
        case FLOAT_CONST:
          Factor factor = new Factor();
          factor.exec();
          type = factor.getType();
          break;

        case NOT:
          eat(Tag.NOT);
          Factor notFactor = new Factor();
          notFactor.exec();
          // Verifica se o operando é numérico para NOT
          if (semantic.isNumeric(notFactor.getType())) {
            type = Type.INTEGER;
          } else {
            throw ExceptionFactory.createSemanticException("Operation '!' requires numeric operand ", Position.line);
          }
          break;

        case MINUS:
          eat(Tag.MINUS);
          Factor minusFactor = new Factor();
          minusFactor.exec();
          // Verifica se o operando é numérico para negação
          if (semantic.isNumeric(minusFactor.getType())) {
            type = minusFactor.getType();
          } else {
            throw ExceptionFactory.createSemanticException("Operation '-' requires numeric operand", Position.line);
          }
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨term-tail⟩ ::= mulop ⟨factor-a⟩⟨term-tail⟩ | λ
  private class TermTail extends ParserProcedure {
    public void exec(Type leftType) throws IOException {
      type = leftType; // Inicializa com o tipo do termo à esquerda

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
          break; // λ - não faz nada

        case MULT:
        case DIV:
        case MOD:
        case AND:
          Tag operator = token.TAG;
          eat(getWhichMulopTag(token.TAG));

          FactorA factorA = new FactorA();
          factorA.exec();

          // Verifica compatibilidade de tipos para o operador
          Type rightType = factorA.getType();
          Type resultType = semantic.resultType(type, rightType, operator);

          if (resultType == null) {
            throw ExceptionFactory.createSemanticException("Incompatible types for operator '" + operator + "'",
                Position.line);
          }

          // Verificação específica para MOD
          if (operator == Tag.MOD && (type != Type.INTEGER || rightType != Type.INTEGER)) {
            throw ExceptionFactory.createSemanticException("Operator '%' requires integer operands",
                Position.line);
          }

          type = resultType; // Atualiza o tipo do resultado

          // Continua analisando o tail com o novo tipo resultante
          TermTail tail = new TermTail();
          tail.exec(type);
          type = tail.getType();
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }

    // Sobrecarga para compatibilidade com interface
    public void exec() throws IOException {
      exec(null);
    }
  }

  // ⟨term⟩ ::= ⟨factor-a⟩⟨term-tail⟩
  private class Term extends ParserProcedure {
    public void exec() throws IOException {
      switch (token.TAG) {
        case IDENTIFIER:
        case OPEN_PAR:
        case NOT:
        case MINUS:
        case STRING_CONST:
        case INT_CONST:
        case FLOAT_CONST:
          FactorA factorA = new FactorA();
          factorA.exec();
          Type factorType = factorA.getType();

          TermTail termTail = new TermTail();
          termTail.exec(factorType);
          type = termTail.getType();
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨simple-expr-tail⟩ ::= addop ⟨term⟩ ⟨simple-expr-tail⟩ | λ
  private class SimpleExprTail extends ParserProcedure {
    public void exec(Type leftType) throws IOException {
      type = leftType; // Inicializa com o tipo da expressão à esquerda

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
          break; // λ - não faz nada

        case PLUS:
        case MINUS:
        case OR:
          Tag operator = token.TAG;
          eat(getWhichAddopTag(token.TAG));

          Term term = new Term();
          term.exec();
          Type rightType = term.getType();

          // Verifica compatibilidade de tipos para o operador
          Type resultType = semantic.resultType(type, rightType, operator);

          if (resultType == null) {
            if (operator == Tag.PLUS) {
              throw ExceptionFactory.createSemanticException(
                  "Operator '+' requires both operands to be numeric, or both to be strings",
                  Position.line);
            } else {
              throw ExceptionFactory.createSemanticException("Incompatible types for operator '" + operator + "'",
                  Position.line);
            }
          }

          type = resultType; // Atualiza o tipo do resultado

          // Continua analisando o tail com o novo tipo resultante
          SimpleExprTail tail = new SimpleExprTail();
          tail.exec(type);
          type = tail.getType();
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }

    // Sobrecarga para compatibilidade com interface
    public void exec() throws IOException {
      exec(null);
    }
  }

  // ⟨simple-expr⟩ ::= ⟨term⟩ ⟨simple-expr-tail⟩
  private class SimpleExpr extends ParserProcedure {
    public void exec() throws IOException {
      switch (token.TAG) {
        case IDENTIFIER:
        case OPEN_PAR:
        case NOT:
        case MINUS:
        case STRING_CONST:
        case INT_CONST:
        case FLOAT_CONST:
          Term term = new Term();
          term.exec();
          Type termType = term.getType();

          SimpleExprTail simpleExprTail = new SimpleExprTail();
          simpleExprTail.exec(termType);
          type = simpleExprTail.getType();
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨expression-tail⟩ ::= relop ⟨simple-expr⟩ | λ
  private class ExpressionTail extends ParserProcedure {
    public void exec(Type leftType) throws IOException {
      type = leftType; // Inicializa com o tipo da expressão à esquerda

      switch (token.TAG) {
        case THEN:
        case END:
        case CLOSE_PAR:
        case SEMICOLON:
          break; // λ - não faz nada

        case EQ:
        case GREATER:
        case GREATER_EQ:
        case LESS:
        case LESS_EQ:
        case NOT_EQ:
          Tag operator = token.TAG;
          eat(getWhichRelopTag(token.TAG));

          SimpleExpr simpleExpr = new SimpleExpr();
          simpleExpr.exec();
          Type rightType = simpleExpr.getType();

          // Verifica compatibilidade de tipos para o operador relacional
          if (operator == Tag.EQ || operator == Tag.NOT_EQ) {
            // Igualdade e desigualdade: os tipos devem ser compatíveis
            if (leftType != rightType && !(semantic.isNumeric(leftType) && semantic.isNumeric(rightType))) {
              throw ExceptionFactory.createSemanticException("Incompatible types for comparison operator",
                  Position.line);
            }
          } else {
            // Outras comparações: ambos devem ser numéricos
            if (!semantic.isNumeric(leftType) || !semantic.isNumeric(rightType)) {
              throw ExceptionFactory.createSemanticException("Comparison operators require numeric operands",
                  Position.line);
            }
          }

          // O resultado de uma expressão relacional é sempre booleano (representado como
          // INTEGER)
          type = Type.INTEGER;
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }

    // Sobrecarga para compatibilidade com interface
    public void exec() throws IOException {
      exec(null);
    }
  }

  // ⟨expression⟩ ::= ⟨simple-expr⟩ ⟨expression-tail⟩
  private class Expression extends ParserProcedure {
    public void exec() throws IOException {
      switch (token.TAG) {
        case IDENTIFIER:
        case OPEN_PAR:
        case NOT:
        case MINUS:
        case STRING_CONST:
        case INT_CONST:
        case FLOAT_CONST:
          SimpleExpr simpleExpr = new SimpleExpr();
          simpleExpr.exec();
          Type exprType = simpleExpr.getType();

          ExpressionTail expressionTail = new ExpressionTail();
          expressionTail.exec(exprType);
          type = expressionTail.getType();
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨writable⟩ ::= ⟨simple-expr⟩ | literal
  private class Writable extends ParserProcedure {
    public void exec() throws IOException {
      switch (token.TAG) {
        case IDENTIFIER:
        case OPEN_PAR:
        case NOT:
        case MINUS:
        case INT_CONST:
        case FLOAT_CONST:
          SimpleExpr simpleExpr = new SimpleExpr();
          simpleExpr.exec();
          type = simpleExpr.getType();
          break;

        case STRING_CONST:
          eat(Tag.STRING_CONST);
          type = Type.STRING;
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨write-stmt⟩ ::= print ( ⟨writable⟩ )
  private class WriteStmt extends ParserProcedure {
    public void exec() throws IOException {
      switch (token.TAG) {
        case PRINT:
          eat(Tag.PRINT);
          eat(Tag.OPEN_PAR);
          Writable writable = new Writable();
          writable.exec();
          eat(Tag.CLOSE_PAR);
          type = Type.VOID; // Comando print não tem tipo de retorno
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨read-stmt⟩ ::= scan ( identifier )
  private class ReadStmt extends ParserProcedure {
    public void exec() throws IOException {
      switch (token.TAG) {
        case SCAN:
          eat(Tag.SCAN);
          eat(Tag.OPEN_PAR);
          String id = token.toString();
          eat(Tag.IDENTIFIER);

          // Verificar se a variável foi declarada
          if (!identifiersTable.containsKey(id)) {
            throw ExceptionFactory.createSemanticException("Undefined variable in scan: " + id,
                Position.line);
          }

          eat(Tag.CLOSE_PAR);
          type = Type.VOID; // Comando scan não tem tipo de retorno
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨stmt-sufix⟩ ::= while ⟨condition⟩ end
  private class StmtSufix extends ParserProcedure {
    public void exec() throws IOException {
      switch (token.TAG) {
        case WHILE:
          eat(Tag.WHILE);
          Condition condition = new Condition();
          condition.exec();
          eat(Tag.END);
          type = Type.VOID;
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨while-stmt⟩ ::= do ⟨stmt-list⟩ ⟨stmt-sufix⟩
  private class WhileStmt extends ParserProcedure {
    public void exec() throws IOException {
      switch (token.TAG) {
        case DO:
          eat(Tag.DO);
          StmtList stmtList = new StmtList();
          stmtList.exec();
          StmtSufix stmtSufix = new StmtSufix();
          stmtSufix.exec();
          type = Type.VOID;
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨condition⟩ ::= ⟨expression⟩
  private class Condition extends ParserProcedure {
    public void exec() throws IOException {
      switch (token.TAG) {
        case IDENTIFIER:
        case OPEN_PAR:
        case NOT:
        case MINUS:
        case INT_CONST:
        case FLOAT_CONST:
          Expression expression = new Expression();
          expression.exec();
          type = expression.getType();

          // Verifica se o resultado da expressão pode ser usado como condição
          if (type != null && !semantic.isNumeric(type)) {
            throw ExceptionFactory.createSemanticException("Condition expression must result in a numeric type",
                Position.line);
          }
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨if-stmt-tail⟩ ::= end | else ⟨stmt-list⟩ end
  private class IfStmtTail extends ParserProcedure {
    public void exec() throws IOException {
      switch (token.TAG) {
        case END:
          eat(Tag.END);
          type = Type.VOID;
          break;

        case ELSE:
          eat(Tag.ELSE);
          StmtList stmtList = new StmtList();
          stmtList.exec();
          eat(Tag.END);
          type = Type.VOID;
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨if-stmt⟩ ::= if ⟨condition⟩ then ⟨stmt-list⟩ ⟨if-stmt-tail⟩
  private class IfStmt extends ParserProcedure {
    public void exec() throws IOException {
      switch (token.TAG) {
        case IF:
          eat(Tag.IF);
          Condition condition = new Condition();
          condition.exec();
          eat(Tag.THEN);
          StmtList stmtList = new StmtList();
          stmtList.exec();
          IfStmtTail ifStmtTail = new IfStmtTail();
          ifStmtTail.exec();
          type = Type.VOID;
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨assign-stmt⟩ ::= identifier = ⟨simple-expr⟩
  private class AssignStmt extends ParserProcedure {
    public void exec() throws IOException {
      switch (token.TAG) {
        case IDENTIFIER:
          String id = token.toString();
          eat(Tag.IDENTIFIER);

          // Verificar se a variável foi declarada
          if (!identifiersTable.containsKey(id)) {
            throw ExceptionFactory.createSemanticException("Undefined variable in assignment: " + id, Position.line);
          }

          Type varType = identifiersTable.get(id);
          eat(Tag.ASSIGN);

          SimpleExpr expr = new SimpleExpr();
          expr.exec();
          Type exprType = expr.getType();

          // Verificar compatibilidade de tipos na atribuição
          if (exprType != null && varType != exprType) {
            // Regra especial: int pode receber float se for uma constante inteira
            if (!(varType == Type.INTEGER && exprType == Type.FLOAT && token.TAG == Tag.INT_CONST)) {
              throw ExceptionFactory.createSemanticException("Type mismatch in assignment. Variable '" + id +
                  "' is " + varType + " but expression is " + exprType,
                  Position.line);
            }
          }

          type = Type.VOID; // Comando de atribuição não tem tipo de retorno
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨stmt⟩ ::= ⟨assign-stmt⟩ ; | ⟨if-stmt⟩ | ⟨while-stmt⟩ | ⟨read-stmt⟩ ; |
  // ⟨write-stmt⟩ ;
  private class Stmt extends ParserProcedure {
    public void exec() throws IOException {
      switch (token.TAG) {
        case IDENTIFIER:
          AssignStmt assignStmt = new AssignStmt();
          assignStmt.exec();
          eat(Tag.SEMICOLON);
          type = Type.VOID;
          break;

        case IF:
          IfStmt ifStmt = new IfStmt();
          ifStmt.exec();
          type = Type.VOID;
          break;

        case DO:
          WhileStmt whileStmt = new WhileStmt();
          whileStmt.exec();
          type = Type.VOID;
          break;

        case SCAN:
          ReadStmt readStmt = new ReadStmt();
          readStmt.exec();
          eat(Tag.SEMICOLON);
          type = Type.VOID;
          break;

        case PRINT:
          WriteStmt writeStmt = new WriteStmt();
          writeStmt.exec();
          eat(Tag.SEMICOLON);
          type = Type.VOID;
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨stmt-list⟩ ::= ⟨stmt⟩ {⟨stmt⟩}
  private class StmtList extends ParserProcedure {
    public void exec() throws IOException {
      switch (token.TAG) {
        case IDENTIFIER:
        case IF:
        case DO:
        case SCAN:
        case PRINT:
          Stmt stmt = new Stmt();
          stmt.exec();
          while (token.TAG == Tag.IDENTIFIER || token.TAG == Tag.IF || token.TAG == Tag.DO || token.TAG == Tag.SCAN
              || token.TAG == Tag.PRINT) {
            Stmt nextStmt = new Stmt();
            nextStmt.exec();
          }
          type = Type.VOID;
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨type⟩ ::= int | float | string
  private class TypeDecl extends ParserProcedure {
    public void exec() throws IOException {
      switch (token.TAG) {
        case INT:
          eat(Tag.INT);
          type = Type.INTEGER;
          break;

        case FLOAT:
          eat(Tag.FLOAT);
          type = Type.FLOAT;
          break;

        case STRING:
          eat(Tag.STRING);
          type = Type.STRING;
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨ident-list⟩ ::= identifier {, identifier}
  private class IdentList extends ParserProcedure {
    public void exec(Type declType) throws IOException {
      switch (token.TAG) {
        case IDENTIFIER:
          String id = token.toString();
          eat(Tag.IDENTIFIER);

          // Verificar se a variável já foi declarada
          if (identifiersTable.containsKey(id)) {
            throw ExceptionFactory.createSemanticException("Variable already declared: "
                + id,
                Position.line);
          } else {
            // Adicionar variável à tabela de símbolos com seu tipo
            identifiersTable.put(id, declType);
          }

          if (token.TAG == Tag.COMMA) {
            eat(Tag.COMMA);
            IdentList nextId = new IdentList();
            nextId.exec(declType);
          }
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }

    // Sobrecarga para compatibilidade com interface
    public void exec() throws IOException {
      exec(null);
    }
  }

  // ⟨decl⟩ ::= ⟨type⟩ ⟨ident-list⟩ ;
  private class Decl extends ParserProcedure {
    public void exec() throws IOException {
      switch (token.TAG) {
        case INT:
        case FLOAT:
        case STRING:
          TypeDecl typeDecl = new TypeDecl();
          typeDecl.exec();
          Type declType = typeDecl.getType();

          IdentList identList = new IdentList();
          identList.exec(declType);

          eat(Tag.SEMICOLON);
          type = Type.VOID;
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨decl-list⟩ ::= ⟨decl⟩ {decl}
  private class DeclList extends ParserProcedure {
    public void exec() throws IOException {
      switch (token.TAG) {
        case INT:
        case FLOAT:
        case STRING:
          new Decl().exec();
          while (token.TAG == Tag.INT || token.TAG == Tag.FLOAT || token.TAG == Tag.STRING)
            new Decl().exec();

          type = Type.VOID;
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨program⟩ ::= start [decl-list] ⟨stmt-list⟩ exit
  private class Program extends ParserProcedure {
    public void exec() throws IOException {
      switch (token.TAG) {
        case START:
          eat(Tag.START);
          if (token.TAG == Tag.INT || token.TAG == Tag.FLOAT || token.TAG == Tag.STRING)
            new DeclList().exec();
          new StmtList().exec();
          eat(Tag.EXIT);

          type = Type.VOID;
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }
  }

  // ⟨begin⟩ ::= ⟨program⟩#
  private class Begin extends ParserProcedure {
    public void exec() throws IOException {
      switch (token.TAG) {
        case START:
          new Program().exec();
          eat(Tag.EOF);

          type = Type.VOID;
          break;

        default:
          throw ExceptionFactory.createParserException("Unexpected symbol " + "'" + token.toString() + "'",
              Position.line);
      }
    }

  }

  public void begin() throws IOException {
    new Begin().exec();
  }
}