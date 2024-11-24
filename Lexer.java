
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

public class Lexer {
  private char ch = ' ';
  private FileReader file;
  private Hashtable<String, Word> words = new Hashtable<String, Word>();

  public Lexer(
      String fileName) throws FileNotFoundException {
    try {
      file = new FileReader(fileName);
    } catch (FileNotFoundException e) {
      System.out.println("Arquivo não encontrado");
      throw e;
    }

    initializeReservedWordsInSymbolTable();
  }

  private void reserve(Word w) {
    words.put(w.getLexeme(), w);
  }

  private void initializeReservedWordsInSymbolTable() {
    reserve(new Word(Tag.START, "start"));
    reserve(new Word(Tag.EXIT, "exit"));
    reserve(new Word(Tag.INT, "int"));
    reserve(new Word(Tag.FLOAT, "float"));
    reserve(new Word(Tag.STRING, "string"));
    reserve(new Word(Tag.IF, "if"));
    reserve(new Word(Tag.THEN, "then"));
    reserve(new Word(Tag.END, "end"));
    reserve(new Word(Tag.DO, "do"));
    reserve(new Word(Tag.WHILE, "while"));
    reserve(new Word(Tag.SCAN, "scan"));
    reserve(new Word(Tag.PRINT, "print"));
  }

  private void readch() throws IOException {
    ch = (char) file.read();
  }

  private boolean readch(char c) throws IOException {
    readch();
    if (ch != c)
      return false;
    ch = ' ';
    return true;
  }

  public Token scan() throws IOException {
    // Desconsidera delimitadores na entrada
    for (;; readch()) {
      if (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\b')
        continue;
      else if (ch == '\n')
        Position.line++; // conta linhas
      else
        break;
    }

    // Identificadores
    if (Character.isLetter(ch)) {
      StringBuffer sb = new StringBuffer();

      do {
        sb.append(ch);
        readch();
      } while (Character.isLetterOrDigit(ch) || ch == '_');
      String s = sb.toString();
      Word w = (Word) words.get(s);
      if (w != null)
        return w; // palavra já existe na HashTable

      w = new Word(Tag.IDENTIFIER, s);
      words.put(s, w);
      return w;
    }

    // Números
    if (Character.isDigit(ch)) {
      int intValue = 0;

      do {
        intValue = 10 * intValue + Character.digit(ch, 10);
        readch();
      } while (Character.isDigit(ch));

      if (ch != '.') {
        return new IntegerConst(intValue);
      }

      readch();
      if (!Character.isDigit(ch)) {
        return new Word(Tag.ERROR, "Invalid float number");
      }

      float floatValue = intValue;
      float divider = 10;

      for (;;) {
        floatValue = floatValue + Character.digit(ch, 10) / divider;
        divider = divider * 10;

        readch();
        if (!Character.isDigit(ch))
          break;
      }

      return new FloatConst(floatValue);
    }

    // Operadores
    switch (ch) {
      case '=':
        if (readch('='))
          return new Token(Tag.EQ);
        return new Token(Tag.ASSIGN);
      case '>':
        if (readch('='))
          return new Token(Tag.GREATER_EQ);
        return new Token(Tag.GREATER);
      case '<':
        if (readch('='))
          return new Token(Tag.LESS_EQ);
        return new Token(Tag.LESS);
      case '!':
        if (readch('='))
          return new Token(Tag.NOT_EQ);
        return new Token(Tag.NOT);
      case '+':
        ch = ' ';
        return new Token(Tag.PLUS);
      case '-':
        ch = ' ';
        return new Token(Tag.MINUS);
      case '|':
        if (readch('|'))
          return new Token(Tag.OR);
        else
          return new Word(Tag.ERROR, "Invalid character '|'");
      case '*':
        ch = ' ';
        return new Token(Tag.MULT);
      case '/':
        ch = ' ';
        return new Token(Tag.DIV);
      case '%':
        ch = ' ';
        return new Token(Tag.MOD);
      case '&':
        if (readch('&'))
          return new Token(Tag.AND);
        else
          return new Word(Tag.ERROR, "Invalid character '&'");
    }

    ch = ' ';
    return null;
  }

}