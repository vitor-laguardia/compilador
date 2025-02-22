public class FloatConst extends Token {
  public final float value;
  public final Type type = Type.FLOAT;

  public FloatConst(float value) {
    super(Tag.FLOAT_CONST);
    this.value = value;
  }

  public String toString() {
    return "Token: " + super.TAG + " | Value: " + value;
  }
}
