public enum Tag {
  // Palavras Reservadas
  START("start"),
  EXIT("exit"),
  INT("int"),
  FLOAT("float"),
  STRING("string"),
  IF("if"),
  ELSE("else"),
  THEN("then"),
  END("end"),
  DO("do"),
  WHILE("while"),
  SCAN("scan"),
  PRINT("print"),

  // Identificador
  IDENTIFIER("identifier"),

  // Operadores
  EQ("=="),
  GREATER(">"),
  GREATER_EQ(">="),
  LESS("<"),
  LESS_EQ("<="),
  NOT_EQ("!="),
  PLUS("+"),
  MINUS("-"),
  OR("||"),
  NOT("!"),
  MULT("*"),
  DIV("/"),
  MOD("%"),
  AND("&&"),

  // Literal
  INT_CONST("int_const"),
  FLOAT_CONST("float_const"),
  STRING_CONST("string_const"),

  // Outros
  OPEN_BRACKET("{"),
  CLOSE_BRACKET("}"),
  ASSIGN("="),
  SEMICOLON(";"),
  COMMA(","),
  OPEN_PAR("("),
  CLOSE_PAR(")"),
  CHARACTER("char"),
  ERROR("error"),
  EOF("eof");

  private final String representation;

  // Construtor do enum
  Tag(String representation) {
    this.representation = representation;
  }

  public String toString() {
    return representation;
  }
}
