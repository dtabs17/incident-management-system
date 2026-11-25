package io.github.dtabs17;

import java.io.PrintStream;
import java.util.Scanner;

import io.github.dtabs17.incident.cli.IncidentCli;
import io.github.dtabs17.incident.service.IncidentManager;

public class App {

    public static void main(String[] args) {
        run(new Scanner(System.in), System.out);
    }

    static void run(Scanner input, PrintStream output) {
        var manager = new IncidentManager();
        var cli = new IncidentCli(manager, input, output);
        cli.run();
    }
}
