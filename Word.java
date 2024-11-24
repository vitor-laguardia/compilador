class Word extends Token {
  private String lexeme = "";

  public static final Word eq = new Word("==", Tag.EQ);

  public Word(String s, int tag) {
    super(tag);
    lexeme = s;
  }

  public String getLexeme() {
    return this.lexeme;
  }

  public String toString() {
    return "" + lexeme;
  }

}