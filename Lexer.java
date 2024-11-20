
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
    reserve(new Word ("start", Tag.START));
    reserve(new Word ("exit", Tag.EXIT));
    reserve(new Word ("int", Tag.INT));
    reserve(new Word ("float", Tag.FLOAT));
    reserve(new Word ("string", Tag.STRING));
    reserve(new Word ("if", Tag.IF));
    reserve(new Word ("then", Tag.THEN));
    reserve(new Word ("end", Tag.END));
    reserve(new Word ("do", Tag.DO));
    reserve(new Word ("while", Tag.WHILE));
    reserve(new Word ("scan", Tag.SCAN));
    reserve(new Word ("print", Tag.PRINT));
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