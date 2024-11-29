import java.util.Hashtable;
import java.util.Map;

public class SymbolTable {
  private static final Hashtable<String, Word> table = new Hashtable<String, Word>();

  public static void initializeTable() {
    addWord(new Word(Tag.START, "start"));
    addWord(new Word(Tag.EXIT, "exit"));
    addWord(new Word(Tag.INT, "int"));
    addWord(new Word(Tag.FLOAT, "float"));
    addWord(new Word(Tag.STRING, "string"));
    addWord(new Word(Tag.IF, "if"));
    addWord(new Word(Tag.THEN, "then"));
    addWord(new Word(Tag.END, "end"));
    addWord(new Word(Tag.DO, "do"));
    addWord(new Word(Tag.WHILE, "while"));
    addWord(new Word(Tag.SCAN, "scan"));
    addWord(new Word(Tag.PRINT, "print"));
  }

  public static void addWord(Word w) {
    table.put(w.getLexeme(), w);
  }

  public static Word getWord(String name) {
    return table.get(name);
  }

  public static boolean containsWord(String name) {
    return table.containsKey(name);
  }

  public static void printTable() {
    for (Map.Entry<String, Word> entry : table.entrySet()) {
      System.out.println("KEY: " + entry.getKey() + " | VALUE: (" +
          entry.getValue().toString() + ")");
    }
  }
}
