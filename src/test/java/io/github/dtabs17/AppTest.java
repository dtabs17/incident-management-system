
package io.github.dtabs17;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class AppTest {

    @Test
    void applicationStartsWithoutErrors() {
        assertDoesNotThrow(() -> App.main(new String[0]));
    }
}
