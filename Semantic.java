public class Semantic {
  public Type resultType(Type t1, Type t2, Tag operator) {
    if (t1 == null || t2 == null)
      return null;

    switch (operator) {
      case PLUS:
        // Regra para operador "+"
        if (t1 == Type.STRING && t2 == Type.STRING) {
          return Type.STRING; // Concatenação de strings
        } else if (isNumeric(t1) && isNumeric(t2)) {
          // Se algum operando for FLOAT, o resultado é FLOAT
          return (t1 == Type.FLOAT || t2 == Type.FLOAT) ? Type.FLOAT : Type.INTEGER;
        }
        return null; // Incompatível

      case MINUS:
      case MULT:
        // Operações numéricas
        if (isNumeric(t1) && isNumeric(t2)) {
          return (t1 == Type.FLOAT || t2 == Type.FLOAT) ? Type.FLOAT : Type.INTEGER;
        }
        return null; // Incompatível

      case DIV:
        // Divisão
        if (isNumeric(t1) && isNumeric(t2)) {
          // Se ambos forem inteiros, o resultado é inteiro
          if (t1 == Type.INTEGER && t2 == Type.INTEGER) {
            return Type.INTEGER;
          }
          // Caso contrário, o resultado é float
          return Type.FLOAT;
        }
        return null; // Incompatível

      case MOD:
        // Operador "%": ambos devem ser inteiros
        if (t1 == Type.INTEGER && t2 == Type.INTEGER) {
          return Type.INTEGER;
        }
        return null; // Incompatível

      case AND:
      case OR:
        // Operadores lógicos: ambos devem ser numéricos
        if (isNumeric(t1) && isNumeric(t2)) {
          return Type.INTEGER; // Resultado booleano representado como inteiro
        }
        return null; // Incompatível

      case EQ:
      case NOT_EQ:
        // Igualdade e desigualdade: os tipos devem ser compatíveis
        if (t1 == t2 || (isNumeric(t1) && isNumeric(t2))) {
          return Type.INTEGER; // Resultado booleano representado como inteiro
        }
        return null; // Incompatível

      case LESS:
      case LESS_EQ:
      case GREATER:
      case GREATER_EQ:
        // Comparações: ambos devem ser numéricos
        if (isNumeric(t1) && isNumeric(t2)) {
          return Type.INTEGER; // Resultado booleano representado como inteiro
        }
        return null; // Incompatível

      default:
        return null; // Operador não reconhecido
    }
  }

  public boolean isNumeric(Type type) {
    return type == Type.INTEGER || type == Type.FLOAT;
  }
}