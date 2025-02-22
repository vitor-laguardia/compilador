public class StringConst extends Token {
  String content;
  public final Type type = Type.STRING;

  StringConst(String content) {
    super(Tag.STRING_CONST);
    this.content = content;
  }

  public String toString() {
    return "Token: " + super.TAG + " | Content: " + content;
  }

}
