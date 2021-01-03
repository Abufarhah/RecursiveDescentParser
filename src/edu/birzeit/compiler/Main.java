package edu.birzeit.compiler;

import edu.birzeit.compiler.util.ParserUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    private static Scanner scanner;
    private static int index=0;
    private static String token;
    private static String line;
    public static void main(String[] args){
        initFile();
        readLine();
        program();
    }

    private static void initFile(){
        try {
            File file=new File("SampleProgram");
            scanner=new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void readLine(){
        if(scanner.hasNext()){
            line=scanner.nextLine();
        }
    }

    private static String getToken(){
//        String temp="";
//        while (!ParserUtil.isReserved(temp))
            return null;
//        TODO: Complete this method
    }

    private static void program(){
        getToken();
        body();
        if(token.equals("$")){
            System.out.println("Parsed Successfully!");
        }else{
            System.out.println("Error: Missing $");
        }
    }

    private static void body(){
//        libDecl(); TODO: implement this method

    }

}
