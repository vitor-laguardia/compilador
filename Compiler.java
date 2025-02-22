import java.io.IOException;

public class Compiler {
  public static void main(String[] args) {
    if (args.length == 0) {
      System.out.println("Por favor, forneça o caminho do arquivo como argumento.");
      return;
    }

    try {
      Lexer lexer = new Lexer(args[0]);
      Semantic semantic = new Semantic();
      Parser parser = new Parser(lexer, semantic);

      parser.begin();
    } catch (IOException e) {
      System.err.println(e.getMessage());
    } catch (LexicalException e) {
      System.err.println(e.getMessage());
    } catch (ParserException e) {
      System.err.println(e.getMessage());
    } catch (SemanticException e) {
      System.err.println(e.getMessage());
    }
  }
}
