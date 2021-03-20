package vpreprocessor;

import org.junit.Test;
import vpreprocessor.token.EOFToken;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestPreprocessorToken {
    @Test
    public void eof() {
        EOFToken eof1 = new EOFToken();
        EOFToken eof2 = new EOFToken();
        assertEquals(eof1, eof1);
        assertNotEquals(eof1, null);
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(eof1, 1);
        assertEquals(eof2, eof1);
        assertEquals(0, eof1.hashCode());
        assertEquals(0, eof2.hashCode());
    }
}
