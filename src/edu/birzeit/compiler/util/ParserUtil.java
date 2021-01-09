package edu.birzeit.compiler.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ParserUtil {
    private static Set<String> reservedKeyWords = new HashSet(Arrays.asList(
            "main()", "include", "const",
            "var", "int", "float",
            "user-defined-name", "float-number", "int-number",
            "input", "output", "if",
            "endif", "else", "while"));

    private static Set<String> characterSet = new HashSet(Arrays.asList(
            "$", ";", "+",
            "-", "*", "/",
            "%", "<<", ">>",
            "=", "output", "if",
            "endif", "else", "while",
            "#","<",">",
            ",","{","}"));

    public static boolean isReserved(String s) {
        return reservedKeyWords.contains(s)||characterSet.contains(s);
    }
}