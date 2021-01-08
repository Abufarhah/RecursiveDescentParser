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
        if (tokenIndex < line.length() - 1) {
            temp.append(getNext());
            while ((!ParserUtil.isReserved(next + "") || tokenIndex == 0) && !ParserUtil.isReserved(temp.toString())) {
                temp.append(getNext());
            }
            token = temp.toString();
        } else {
            token = "";
        }
    }

    private static char getNext() {
        next = line.charAt(tokenIndex++);
        while (next == ' ') {
            next = line.charAt(tokenIndex++);
        }
        return next;
    }

    private static void program() {
        body();
        getToken();
        if (token.equals("$")) {
            System.out.println("Parsed Successfully!");
        } else {
            System.out.println("Error: Missing $");
        }
    }

    private static void body() {
        libDecl();

    }

    private static void libDecl() {
        getToken();
        while (!token.equals("main()")) {
            if (!token.equals("#")) {
                throw new RuntimeException("library declaration error, missing # at line: " + lineIndex);
            }
            getToken();
            if (!token.equals("include")) {
                throw new RuntimeException("library declaration error, include not present at line: "
                        + lineIndex);
            }
            getToken();
            if (!token.equals("<")) {
                throw new RuntimeException("library declaration error, missing < at line: " + lineIndex);
            }
//                name();
            getToken();
            if (!token.equals(">")) {
                throw new RuntimeException("library declaration error, missing > at line: " + lineIndex);
            }
            getToken();
            if (!token.equals(";")) {
                throw new RuntimeException("library declaration error, missing ; at line: " + lineIndex);
            }
            getToken();
        }
//            declarations();
//            block();
    }

}
