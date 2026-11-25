package io.github.dtabs17;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

class AppTest {

    @Test
    void applicationStartsAndExitsWithoutErrors() {
        var input = new Scanner("0\n");
        var output = new PrintStream(OutputStream.nullOutputStream());

        assertDoesNotThrow(() -> App.run(input, output));
    }
}
