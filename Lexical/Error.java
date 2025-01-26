package Lexical;
public class Error extends Token {
  String message;
  int line;

  Error(String message, int line) {
    super(Tag.ERROR);
    this.message = message;
    this.line = line;
  }

  public String toString() {
    return "" + "Error: " + message + " at line " + line;
  }
}
