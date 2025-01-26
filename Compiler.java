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

    Lexer lexer;
    Parser parser;

    try {
      lexer = new Lexer(args[0]);
      try{
          parser = new Parser(lexer);
          do {
            parser.begin();
          } while (parser.getCurrentToken().TAG != Tag.EOF);
        }catch(Exception e){
          e.printStackTrace();
        }
        
      //System.out.println();
      //System.out.println("---------- Symbol Table -----------");
      //SymbolTable.printTable();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    System.out.println();
    System.out.println("---------- Compilation Result -----------");
    if(erro == true){
      System.out.println(" Compilation ERROR ");
      System.out.println("-----------------------------------------");
    }else{
      System.out.println(" Compilation SUCCESS! ");
      System.out.println("-----------------------------------------");
    }

  }
}
