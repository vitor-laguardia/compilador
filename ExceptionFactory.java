public class ExceptionFactory {
  public static ParserException createParserException(String message, int line) {
    return new ParserException(message, line);
  }

  public static LexicalException createLexicalException(String message, int line) {
    return new LexicalException(message, line);
  }
}