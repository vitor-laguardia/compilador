package env;

public class Word extends Token {
  private String lexeme = "";

  public Word(Tag tag, String lexeme) {
    super(tag);
    this.lexeme = lexeme;
  }

  public String getLexeme() {
    return lexeme;
  }

  public String toString() {
    return "" + lexeme;
  }

}