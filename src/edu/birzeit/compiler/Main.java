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
            System.out.println("at line: "+lineIndex);
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
            line = scanner.nextLine();
            ++lineIndex;
            tokenIndex = 0;
        }
    }

    private static void getToken() {
        StringBuilder temp = new StringBuilder();
        if (tokenIndex < line.length()) {
            temp.append(getNext());
            while (tokenIndex == 0 || (!ParserUtil.isReserved(next + "") && !ParserUtil.isReserved(temp.toString()) && !ParserUtil.isReserved(next() + "")&&tokenIndex < line.length())) {
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
        int index = tokenIndex;
        char n = line.charAt(index++);
        while (n == ' ') {
            n = line.charAt(index++);
        }
        return n;
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
        if (token.equals("main()")) {
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
        while (token.equals("const")){
            dataType();
            name();
            getToken();
            if(!token.equals("=")){
                throw new RuntimeException("constant declaration error, missing =");
            }
            value();
            getToken();
            if(!token.equals(";")){
                throw new RuntimeException("constant declaration error, missing ;");
            }
            getToken();
        }
    }

    private static void varDecl(){
        while (token.equals("var")){
            dataType();
            nameList();
            if(!token.equals(";")){
                throw new RuntimeException("variable declaration error, missing ;");
            }
            getToken();
        }
    }

    private static void dataType(){
        getToken();
        if(!token.equals("int")&&!token.equals("float")){
            throw new RuntimeException("data type error, "+token+" not a data type");
        }
    }

    private static void value(){
        getToken();
        try {
            Double.parseDouble(token);
        }catch (NumberFormatException e){
            throw new RuntimeException("value error, "+token+" not a number");
        }
    }

    private static void nameList(){
        name();
        getToken();
        while (token.equals(",")){
            name();
            getToken();
        }
    }

    private static void block(){
        if(!token.equals("{")){
            System.out.println("block error, missing {");
        }
        stmtList();
        if(!token.equals("}")){
            System.out.println("block error, missing }");
        }
    }

    private static void stmtList(){
        statement();
        getToken();
        while (token.equals(";")){
            statement();
            getToken();
        }
    }

    private static void statement(){
        getToken();
        
    }

}
