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
      Token t = null;

      do {
        t = lexer.scan();
        //removendo print debug lexico
        //System.out.println(t.toString());
        if (t.TAG == Tag.ERROR){
          erro = true;
        }
      } while (t.TAG != Tag.EOF);
      System.out.println();
      System.out.println("---------- Symbol Table -----------");
      SymbolTable.printTable();

      try {
        //initiate syntatic
        Parser parser = new Parser(lexer);
  
        do{
          //parser.begin();
        } while(parser.getCurrentToken().TAG!=Tag.EOF); //le o proximo token ate chegar na Tag de EOF
          System.out.println("Analise sintática concluída.");
  
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
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
