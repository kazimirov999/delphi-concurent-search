package com.projects;

import picocli.CommandLine;

public class Main {

    public static void main(String[] args) {
        new CommandLine(new StartFinder()).execute("-s", "-p");
    }

}
