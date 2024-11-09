public class Token {
  public final Tag TAG;

  Token(Tag tag) {
    TAG = tag;
  }

  public String toString() {
    return "" + TAG;
  }
}