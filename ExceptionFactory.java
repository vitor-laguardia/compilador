public class ExceptionFactory {
  public static ParserException createParserException(String message, int line) {
    return new ParserException(message, line);
  }

  public static LexicalException createLexicalException(String message, int line) {
    return new LexicalException(message, line);
  }

  public static SemanticException createSemanticException(String message, int line) {
    return new SemanticException(message, line);
  }
}