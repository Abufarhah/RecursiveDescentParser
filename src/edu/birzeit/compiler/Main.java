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

    public static void main(String[] args) {
        initFile();
        readLine();
        try {
            program();
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            System.out.println("at line: " + (lineIndex - 1));
        }
    }

    private static void initFile() {
        try {
            File file = new File("SampleProgram");
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void readLine() {
        if (scanner.hasNext()) {
            line = scanner.nextLine().trim();
            ++lineIndex;
            tokenIndex = 0;
        }
    }

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

    private static char getNext() {
        next = line.charAt(tokenIndex++);
        while (next == ' ') {
            next = line.charAt(tokenIndex++);
        }
        return next;
    }

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

    private static void program() {
        body();
        getToken();
        if (token.equals("$")) {
            System.out.println("Parsed Successfully!");
        } else {
            throw new RuntimeException("Error: Missing $");
        }
    }

    private static void body() {
        libDecl();
        String t = token;
        getToken();
        t += token;
        getToken();
        t += token;
        if (t.equals("main()")) {
            declarations();
            block();
        } else {
            throw new RuntimeException("main function error, missing main");
        }

    }

    private static void libDecl() {
        getToken();
        while (token.equals("#")) {
            getToken();
            if (!token.equals("include")) {
                throw new RuntimeException("library declaration error, include not present at line: "
                        + lineIndex);
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

    private static void name() {
        getToken();
        if (ParserUtil.isReserved(token)) {
            throw new RuntimeException("user defined name error, reserved keyword");
        }
    }

    private static void declarations() {
        getToken();
        constDecl();
        varDecl();
    }

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

    private static void dataType() {
        getToken();
        if (!token.equals("int") && !token.equals("float")) {
            throw new RuntimeException("data type error, " + token + " not a data type");
        }
    }

    private static void value() {
        getToken();
        try {
            Double.parseDouble(token);
        } catch (NumberFormatException e) {
            throw new RuntimeException("value error, " + token + " not a number");
        }
    }

    private static void nameList() {
        name();
        getToken();
        while (token.equals(",")) {
            name();
            getToken();
        }
    }

    private static void block() {
        if (!token.equals("{")) {
            throw new RuntimeException("block error, missing {");
        }
        stmtList();
        if (!token.equals("}")) {
            throw new RuntimeException("block error, missing }");
        }
    }

    private static void stmtList() {
        while (!token.equals("}")) {
            getToken();
            if (token.equals("}")) {
                return;
            }
            statement();
            if (token.equals("}")) {
                return;
            }
            if (!token.equals(";")) {
                throw new RuntimeException("statement list error, missing ;");
            }
        }
    }

    private static void statement() {
        if (token.equals("input") || token.equals("output")) {
            inOutStmt();
        } else if (token.equals("if")) {
            ifStmt();
        } else if (token.equals("while")) {
            whileStmt();
        } else if (token.equals("{")) {
            block();
        } else if (!ParserUtil.isReserved(token)) {
            assStmt();
        } else if (!token.equals("}") && !token.equals(";")) {
            throw new RuntimeException("statement error");
        }
    }

    private static void assStmt() {
        if (ParserUtil.isReserved(token)) {
            throw new RuntimeException("user defined name error, reserved keyword");
        }
        getToken();
        if (!token.equals("=")) {
            throw new RuntimeException("assign statement error, missing =");
        }
        exp();
        getToken();
    }

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
        getToken();
    }

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
        getToken();
    }

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
        if(!token.equals("{")){
            throw new RuntimeException("while statement error, missing {");
        }
        stmtList();
        if(!token.equals("}")){
            throw new RuntimeException("while statement error, missing }");
        }
        getToken();
    }

    private static void nameValue() {
        if (Character.isDigit(next())) {
            value();
        } else {
            name();
        }
    }

    private static void boolExp() {
        nameValue();
        relationalOpr();
        nameValue();
    }

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

    private static void elsePart() {
        getToken();
        if (token.equals("else")) {
            getToken();
            statement();
            getToken();
        }
    }

    private static void exp() {
        term();
        while ((next() + "").equals("+") || (next() + "").equals("-")) {
            addOper();
            term();
        }
    }

    private static void term() {
        factor();
        while (!(next() + "").equals("+") && !(next() + "").equals("-")&& !(next() + "").equals(";") && !(next() + "").equals(" ")) {
            mulOper();
            factor();
        }
    }

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

    private static void mulOper() {
        getToken();
        if (!token.equals("*") && !token.equals("/") && !token.equals("%")) {
            throw new RuntimeException("multiplication operation error");
        }
    }

    private static void addOper() {
        getToken();
        if (!token.equals("+") && !token.equals("-")) {
            throw new RuntimeException("addition operation error");
        }
    }
}
