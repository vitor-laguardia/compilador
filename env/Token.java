package env;

public class Token {
  public final Tag TAG;

  public Token(Tag tag) {
    TAG = tag;
  }

  public String toString() {
    return "" + TAG;
  }
}
