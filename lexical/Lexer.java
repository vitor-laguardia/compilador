import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

public class Lexer {
  private char ch = ' '; 
  private FileReader file;
  private Hashtable words = new Hashtable();

  private void reserve(Word w) {
    words.put(w.getLexeme(), w); 
  }

  public Lexer(String fileName) throws FileNotFoundException {
    try{
      file = new FileReader (fileName);
    }
    catch(FileNotFoundException e){
      System.out.println("Arquivo não encontrado");
      throw e;
    }

    //Insere palavras reservadas na HashTable
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
    ch = (char) file.read();
  }

  private boolean readch(char c) throws IOException {
    readch();
    if (ch != c) return false;
    ch = ' ';
    return true;
  }

  public Token scan() throws IOException {
    return new Token(null);
  }
}