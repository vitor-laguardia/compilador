package env;

public class StringConst extends Token {
  String content;

  public StringConst(String content) {
    super(Tag.STRING_CONST);
    this.content = content;
  }

  public String toString() {
    return "" + content;
  }

}
