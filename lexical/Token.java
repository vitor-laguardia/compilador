public class Token {
  public final int TAG;

  Token(int tag) {
    TAG = tag;
  }

  public String toString() {
    return "" + TAG;
  }
}