package Lexical;
public enum Tag {
  // Palavras Reservadas
  PROGRAM,
  START,
  EXIT,
  INT,
  FLOAT,
  STRING,
  IF,
  THEN,
  END,
  DO,
  WHILE,
  SCAN,
  PRINT,
  ELSE,

  // Identificador
  IDENTIFIER,

  // Operadores
  EQ,
  GREATER,
  GREATER_EQ,
  LESS,
  LESS_EQ,
  NOT_EQ,
  PLUS,
  MINUS,
  OR,
  NOT,
  MULT,
  DIV,
  MOD,
  AND,

  // Literal
  INT_CONST,
  FLOAT_CONST,
  STRING_CONST,

  // Outros
  OPEN_BRACKET,
  CLOSE_BRACKET,
  ASSIGN,
  SEMICOLON,
  COMMA,
  OPEN_PAR,
  CLOSE_PAR,
  CHARACTER,
  ERROR,
  EOF,
}
