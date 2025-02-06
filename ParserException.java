public class ParserException extends RuntimeException {
  private final int line;

  public ParserException(String message, int line) {
    super("Syntax Error at line " + line + ": " + message);
    this.line = line;
  }

  public int getLine() {
    return line;
  }
}