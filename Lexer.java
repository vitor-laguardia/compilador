
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

public class Lexer {
  private char ch = ' '; 
  private FileReader file;
  private Hashtable words = new Hashtable();

  public Lexer(String fileName) throws FileNotFoundException {
    try{
      file = new FileReader (fileName);
    }
    catch(FileNotFoundException e){
      System.out.println("Arquivo não encontrado");
      throw e;
    }

    initializeReservedWordsInSymbolTable();
  }

  private void reserve(Word w) {
    words.put(w.getLexeme(), w); 
  }

  private void initializeReservedWordsInSymbolTable() {
    reserve(new Word (Tag.START, "start"));
    reserve(new Word (Tag.EXIT, "exit"));
    reserve(new Word (Tag.INT, "int"));
    reserve(new Word (Tag.FLOAT, "float"));
    reserve(new Word (Tag.STRING, "string"));
    reserve(new Word (Tag.IF, "if"));
    reserve(new Word (Tag.THEN, "then"));
    reserve(new Word (Tag.END, "end"));
    reserve(new Word (Tag.DO, "do"));
    reserve(new Word (Tag.WHILE, "while"));
    reserve(new Word (Tag.SCAN, "scan"));
    reserve(new Word (Tag.PRINT, "print"));
  }

  private void readch() throws IOException {
    int result = file.read();
    if (result == -1) {
        // Fim do arquivo
        ch = '¨';
    } else {
        ch = (char) result;
    }
  }

  private boolean readch(char c) throws IOException {
    readch();
    if (ch != c) return false;
    ch = ' ';
    return true;
  }

  public Token scan() throws IOException {
    //Desconsidera delimitadores na entrada
    for (;; readch()) {
      if (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\b') continue;
      else if (ch == '\n') Position.line ++; //conta linhas
      else if (ch == '¨') return new Token(Tag.EOF);
      else break;
    }
    //Identificadores
    if (Character.isLetter(ch)) {
      StringBuffer sb = new StringBuffer();

      do {
        sb.append(ch);
        readch();
      } while(Character.isLetterOrDigit(ch) || ch == '_') ;

      String s = sb.toString();
      Word w = (Word)words.get(s);
      if (w != null) return w; //palavra já existe na HashTable
      // w = new Word (s, Tag.ID);
      // words.put(s, w);
      return null;
    }

    ch = ' ';
    return null;
  }
}