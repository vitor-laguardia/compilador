class Word extends Token {
  private String lexeme;

  Word(int tag, String lexeme) {
    super(tag);
    this.lexeme = lexeme;
  }

  public String toString() {
    return "" + lexeme;
  }
}