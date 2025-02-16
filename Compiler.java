import Lexical.*;
import Syntatic.*;

import java.io.IOException;

public class Compiler {
  static boolean erro = false;

  public static void main(String[] args) {
    if (args.length == 0) {
      System.out.println("Por favor, forneça o caminho do arquivo como argumento.");
      return;
    }

    try {
      Lexer lexer = new Lexer(args[0]);
      Parser parser = new Parser(lexer);
      parser.begin();
    } catch (IOException e) {
      System.err.println(e.getMessage());
    } catch (LexicalException e) {
      System.err.println(e.getMessage());
    } catch (ParserException e) {
      System.err.println(e.getMessage());
    }
  }
}
