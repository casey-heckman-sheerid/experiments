import java.io.IOException;

import org.junit.Test;

public class PolymorphismTest {

    @Test
    public void exceptionHandling() throws Exception {
        try {
            throwSubclassOfException();
        } catch (IOException e) {
            // We can explicitly catch and handle sub-types of declared exceptions without handling the parent type
        }
    }

    /**
     * Throws IOException, although the caller cannot tell that from the signature.
     *
     * @throws Exception
     */
    private void throwSubclassOfException() throws Exception {
        throw new IOException();
    }
}
