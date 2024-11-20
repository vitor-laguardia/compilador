/*enum Tag {
  // Palavras Reservadas
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
  EOF,
}
*/
 public class Tag {
  public final static int
    // Palavras Reservadas
    START = 256,
    EXIT= 257,
    INT = 258,
    FLOAT = 259,
    STRING =260,
    IF = 261,
    THEN = 262,
    END = 263,
    DO = 264,
    WHILE =265,
    SCAN = 266,
    PRINT = 267,
    EOF = 268,
  
  //Operadores e pontuação
    EQ = 269,
    GREATER = 270,
    GREATEREQ = 271,
    LESS = 272,
    LESSEQ= 273,
    DIFF = 274,
    ADD = 275,
    SUB = 276,
    OR = 277,
    MULT = 278,
    DIV = 279,
    DIVREST = 280,
    AND = 281,

  //Outros
    NUM=282;
}
