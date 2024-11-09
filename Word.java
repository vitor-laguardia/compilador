class Word extends Token {
  private String lexeme;

  Word( Tag tag, String lexeme) {
    super(tag);
    this.lexeme = lexeme;
  }

  public String getLexeme() {
    return this.lexeme;
  }

  public String toString() {
    return "" + lexeme;
  }
}