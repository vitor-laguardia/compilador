/*public class Token {
  public final Tag TAG;

  Token(Tag tag) {
    TAG = tag;
  }

  public String toString() {
    return "" + TAG;
  }
}
*/
public class Token {
  public final int  tag;

  public Token(int t) {
    tag = t;
  }

  public String toString() {
    return "" + tag;
  }
}


