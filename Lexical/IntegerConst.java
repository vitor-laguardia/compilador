package Lexical;
public class IntegerConst extends Token {
  public final int value;

  public IntegerConst(int value) {
    super(Tag.INT_CONST);
    this.value = value;
  }

  public String toString() {
    return "Token: " + super.TAG + " | Value: " + value;
  }
}