package Lexical;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Lexer {
  private char ch = ' ';
  private FileReader file;

  public Lexer(
      String fileName) throws FileNotFoundException {
    try {
      file = new FileReader(fileName);
    } catch (FileNotFoundException e) {
      System.out.println("Arquivo não encontrado");
      throw e;
    }

    SymbolTable.initializeTable();
  }

  private void readch() throws IOException {
    int value = file.read();
    if (value != -1)
      ch = (char) value;
    else
      ch = '#';
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
    if (Character.isLetter(ch) || ch == '_') {
      StringBuffer sb = new StringBuffer();

      do {
        sb.append(ch);
        readch();
      } while (Character.isLetterOrDigit(ch) || ch == '_');
      String s = sb.toString();
      Word w = (Word) SymbolTable.getWord(s);
      if (w != null)
        return w; // palavra já existe na HashTable

      w = new Word(Tag.IDENTIFIER, s);
      SymbolTable.addWord(w);
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
        return new Error("Invalid float number", Position.line);
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
          return new Error("Invalid character '|'", Position.line);
      case '*':
        ch = ' ';
        return new Token(Tag.MULT);
      case '/':
        // comment
        if (readch('/')) {
          while (ch != '\n' && ch != '#') {
            readch();
          }
          return scan();
        } else if (ch == '*') {
          readch();
          while (true) {
            if (ch == '*') {
              if (readch('/'))
                break;
            }
            if (ch == '\n')
              Position.line++;
            if (ch == '#')
              return new Error("Comment not closed", Position.line);
            else
              readch();
          }
          return scan();
        }
        return new Token(Tag.DIV);
      case '%':
        ch = ' ';
        return new Token(Tag.MOD);
      case '&':
        if (readch('&'))
          return new Token(Tag.AND);
        else
          return new Error("Invalid character '&'", Position.line);
    }

    // String const
    if (ch == '{') {
      StringBuffer sb = new StringBuffer();
      readch();
      do {
        sb.append(ch);
        readch();
      } while (ch != '}' && ch != '\n');

      String s = sb.toString();

      if (ch == '}') {
        ch = ' ';
        return new StringConst(s);
      }

      return new Error("Unexpected line break", Position.line);
    }

    // Outros caracteres
    switch (ch) {
      case ';':
        ch = ' ';
        return new Token(Tag.SEMICOLON);
      case ',':
        ch = ' ';
        return new Token(Tag.COMMA);
      case '(':
        ch = ' ';
        return new Token(Tag.OPEN_PAR);
      case ')':
        ch = ' ';
        return new Token(Tag.CLOSE_PAR);
    }

    if (ch == '#') {
      return new Token(Tag.EOF);
    }

    Token t = new Error("Unexpected token: '" + ch + "'", Position.line);
    ch = ' ';
    return t;
  }

}