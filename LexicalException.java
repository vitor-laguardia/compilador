public class LexicalException extends RuntimeException {
  private final int line;

  public LexicalException(String message, int line) {
    super("Lexical Error at line " + line + ": " + message);
    this.line = line;
  }

  public int getLine() {
    return line;
  }
}