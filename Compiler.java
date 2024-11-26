import java.io.IOException;
import env.Tag;
import env.Token;
import lexic.Lexer;

public class Compiler {
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
        System.out.println(t.toString() + " " + t.TAG);
      } while (t.TAG != Tag.EOF);

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
