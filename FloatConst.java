public class FloatConst extends Token {
  public final float value;

  public FloatConst(float value) {
    super(Tag.FLOAT_CONST);
    this.value = value;
  }

  public String toString() {
    return "Token: " + super.TAG + " | Value: " + value;
  }
}
