package edu.birzeit.compiler;

import edu.birzeit.compiler.util.ParserUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    private static Scanner scanner;
    private static String token;
    private static String line;
    private static char next;
    private static int tokenIndex = 0;
    private static int lineIndex = 0;

    /**
     * main method
     * initialize the file and read the first line the call program method
     *
     * @param args args
     */
    public static void main(String[] args) {
        try {
            initFile();
            readLine();
        } catch (Exception e) {
            System.out.println("File error");
            return;
        }
        try {
            program();
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            System.out.println("at line: " + lineIndex);
        }
    }

    /**
     * this method used to initialize the file
     */
    private static void initFile() {
        try {
            File file = new File("SampleProgram");
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * this method used to read line by line
     */
    private static void readLine() {
        if (scanner.hasNext()) {
            line = scanner.nextLine().trim();
            ++lineIndex;
            tokenIndex = 0;
        } else {
            throw new RuntimeException("early eof, missing $");
        }
    }

    /**
     * a generic method used to get token
     */
    private static void getToken() {
        StringBuilder temp = new StringBuilder();
        if (tokenIndex < line.length()) {
            temp.append(getNext());
            while (tokenIndex == 0 || (!ParserUtil.isReserved(next + "") && tokenIndex < line.length() && !ParserUtil.isReserved(temp.toString()) && !ParserUtil.isReserved(next() + ""))) {
                temp.append(getNext());
            }
            token = temp.toString();
        } else {
            readLine();
            getToken();
        }
    }

    /**
     * this method used to read the next character
     *
     * @return char the next character in the file
     */
    private static char getNext() {
        next = line.charAt(tokenIndex++);
        while (next == ' ') {
            next = line.charAt(tokenIndex++);
        }
        return next;
    }

    /**
     * this method used to read the next character without increment the index
     *
     * @return char
     */
    private static char next() {
        if (tokenIndex < line.length()) {
            int index = tokenIndex;
            char n = line.charAt(index++);
            while (n == ' ') {
                n = line.charAt(index++);
            }
            return n;
        } else {
            return ' ';
        }
    }

    /**
     * non-terminal
     * program     body   $
     */
    private static void program() {
        body();
        getToken();
        if (token.equals("$")) {
            System.out.println("Parsed Successfully!");
        } else {
            throw new RuntimeException("Error: Missing $");
        }
    }

    /**
     * non-terminal
     * body    lib-decl       main ()       declarations      block
     */
    private static void body() {
        libDecl();
        String t = token;
        try {
            getToken();
            t += token;
            getToken();
            t += token;
        } catch (Exception e) {
            throw new RuntimeException("main function error, missing main");
        }
        if (t.equals("main()")) {
            declarations();
            block();
        } else {
            throw new RuntimeException("main function error, missing main");
        }

    }

    /**
     * non-terminal
     * lib-decl    (  # include < name >   ;   )*
     */
    private static void libDecl() {
        getToken();
        while (token.equals("#")) {
            getToken();
            if (!token.equals("include")) {
                throw new RuntimeException("library declaration error, include not present");
            }
            getToken();
            if (!token.equals("<")) {
                throw new RuntimeException("library declaration error, missing <");
            }
            name();
            getToken();
            if (!token.equals(">")) {
                throw new RuntimeException("library declaration error, missing >");
            }
            getToken();
            if (!token.equals(";")) {
                throw new RuntimeException("library declaration error, missing ;");
            }
            getToken();
        }
    }

    /**
     * non-terminal
     * name       “user-defined-name”
     */
    private static void name() {
        getToken();
        if (ParserUtil.isReserved(token)) {
            throw new RuntimeException("user defined name error, reserved keyword");
        }
    }

    /**
     * non-terminal
     * declarations    const-decl       var-decl
     */
    private static void declarations() {
        getToken();
        constDecl();
        varDecl();
    }

    /**
     * non-terminal
     * const-decl   (  const   data-type   name   =    value   ;   )*
     */
    private static void constDecl() {
        while (token.equals("const")) {
            dataType();
            name();
            getToken();
            if (!token.equals("=")) {
                throw new RuntimeException("constant declaration error, missing =");
            }
            value();
            getToken();
            if (!token.equals(";")) {
                throw new RuntimeException("constant declaration error, missing ;");
            }
            getToken();
        }
    }

    /**
     * non-terminal
     * var-decl     (  var    data-type    name-list   ;   )*
     */
    private static void varDecl() {
        while (token.equals("var")) {
            dataType();
            nameList();
            if (!token.equals(";")) {
                throw new RuntimeException("variable declaration error, missing ;");
            }
            getToken();
        }
    }

    /**
     * non-terminal
     * data-type     int     |       float
     */
    private static void dataType() {
        getToken();
        if (!token.equals("int") && !token.equals("float")) {
            throw new RuntimeException("data type error, " + token + " not a data type");
        }
    }

    /**
     * non-terminal
     * value   “float-number”   |        “int-number”
     */
    private static void value() {
        getToken();
        try {
            Double.parseDouble(token);
        } catch (NumberFormatException e) {
            throw new RuntimeException("value error, " + token + " not a number");
        }
    }

    /**
     * non-terminal
     * name-list     name   (  ,   name  )*
     */
    private static void nameList() {
        name();
        getToken();
        while (token.equals(",")) {
            name();
            getToken();
        }
    }

    /**
     * non-terminal
     * block    {    stmt-list    }
     */
    private static void block() {
        if (!token.equals("{")) {
            throw new RuntimeException("block error, missing {");
        }
        stmtList();
        if (!token.equals("}")) {
            throw new RuntimeException("block error, missing }");
        }
    }

    /**
     * non-terminal
     * stmt-list      statement   (  ;     statement   )*
     */
    private static void stmtList() {
        getToken();
        statement();
        while (token.equals(";")) {
            getToken();
            statement();
        }
    }

    /**
     * non-terminal
     * statement  ass-stmt     |     inout-stmt    |      if-stmt     |    while-stmt   |
     * block  |   @
     */
    private static void statement() {
        if (token.equals("input") || token.equals("output")) {
            inOutStmt();
            getToken();
        } else if (token.equals("if")) {
            ifStmt();
            getToken();
        } else if (token.equals("while")) {
            whileStmt();
            getToken();
        } else if (token.equals("{")) {
            block();
            getToken();
        } else if (!ParserUtil.isReserved(token)) {
            assStmt();
            getToken();
        } else if (!token.equals("}") && !token.equals(";")) {
            throw new RuntimeException("statement error");
        }
    }

    /**
     * non-terminal
     * ass-stmt  name     =      exp
     */
    private static void assStmt() {
        if (ParserUtil.isReserved(token)) {
            throw new RuntimeException("user defined name error, reserved keyword");
        }
        getToken();
        if (!token.equals("=")) {
            throw new RuntimeException("assign statement error, missing =");
        }
        exp();
    }

    /**
     * non-terminal
     * inout-stmt input    >>    name         |    output     <<    name-value
     */
    private static void inOutStmt() {
        if (token.equals("input")) {
            getToken();
            String t = token;
            getToken();
            t += token;
            if (t.equals(">>")) {
                name();
            } else {
                throw new RuntimeException("input statement error, missing >>");
            }
        } else {
            getToken();
            String t = token;
            getToken();
            t += token;
            if (t.equals("<<")) {
                nameValue();
            } else {
                throw new RuntimeException("output statement error, missing <<");
            }
        }
    }

    /**
     * non-terminal
     * if-stmt  if   (   bool-exp  )  statement     else-part     endif
     */
    private static void ifStmt() {
        getToken();
        if (!token.equals("(")) {
            throw new RuntimeException("if statement error, missing (");
        }
        boolExp();
        getToken();
        if (!token.equals(")")) {
            throw new RuntimeException("if statement error, missing )");
        }
        getToken();
        statement();
        elsePart();
        if (!token.equals("endif")) {
            throw new RuntimeException("if statement error, missing endif");
        }
    }

    /**
     * non-terminal
     * while-stmt  while   (   bool-exp    )   {    stmt-list    }
     */
    private static void whileStmt() {
        getToken();
        if (!token.equals("(")) {
            throw new RuntimeException("while statement error, missing (");
        }
        boolExp();
        getToken();
        if (!token.equals(")")) {
            throw new RuntimeException("while statement error, missing )");
        }
        getToken();
        if (!token.equals("{")) {
            throw new RuntimeException("while statement error, missing {");
        }
        stmtList();
        if (!token.equals("}")) {
            throw new RuntimeException("while statement error, missing }");
        }
    }

    /**
     * non-terminal
     * name-value   name    |      value
     */
    private static void nameValue() {
        if (Character.isDigit(next())) {
            value();
        } else {
            name();
        }
    }

    /**
     * non-terminal
     * bool-exp  name-value       relational-oper        name-vaue
     */
    private static void boolExp() {
        nameValue();
        relationalOpr();
        nameValue();
    }

    /**
     * non-terminal
     * relational-oper   ==      |       !=         |     <     |       <=     |
     * >     |     >=
     */
    private static void relationalOpr() {
        getToken();
        String t = token;
        if (token.equals("=") || ((token.equals("<") || token.equals(">") || token.equals("!")) && (next() + "").equals("="))) {
            getToken();
            t += next;
        }
        if (!(t.equals("==") || t.equals("!=") || t.equals("<") || t.equals("<=") || t.equals(">") || t.equals(">="))) {
            throw new RuntimeException("relational operation error");
        }
    }

    /**
     * non-terminal
     * else-part   else     statement   |   @
     */
    private static void elsePart() {
        if (token.equals("else")) {
            getToken();
            statement();
        }
    }

    /**
     * non-terminal
     * exp  term      (  add-oper   term  )*
     */
    private static void exp() {
        term();
        while ((next() + "").equals("+") || (next() + "").equals("-")) {
            addOper();
            term();
        }
    }

    /**
     * non-terminal
     * term  factor   (  mul-oper    factor   )*
     */
    private static void term() {
        factor();
        while (!(next() + "").equals("+") && !(next() + "").equals("-") && !(next() + "").equals(";") && !(next() + "").equals(" ")) {
            mulOper();
            factor();
        }
    }

    /**
     * non-terminal
     * factor   (     exp     )     |     name     |     value
     */
    private static void factor() {
        if ((next() + "").equals("(")) {
            getToken();
            if (!token.equals("(")) {
                throw new RuntimeException("factor error, missing (");
            }
            exp();
            getToken();
            if (!token.equals(")")) {
                throw new RuntimeException("factor error, missing )");
            }
        } else {
            nameValue();
        }
    }

    /**
     * non-terminal
     * mul-sign  *    |    /   |   %
     */
    private static void mulOper() {
        getToken();
        if (!token.equals("*") && !token.equals("/") && !token.equals("%")) {
            throw new RuntimeException("multiplication operation error");
        }
    }

    /**
     * non-terminal
     * add-sign   +    |   -
     */
    private static void addOper() {
        getToken();
        if (!token.equals("+") && !token.equals("-")) {
            throw new RuntimeException("addition operation error");
        }
    }
}
