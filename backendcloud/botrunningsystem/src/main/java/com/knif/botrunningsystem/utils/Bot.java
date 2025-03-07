package com.knif.botrunningsystem.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Bot implements java.util.function.Supplier<String> {
    String nextMove(String input) {
        return "";
    }

    @Override
    public String get() {
        File file = new File("input.txt");
        try {
            Scanner sc = new Scanner(file);
            return nextMove(sc.next());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
}
