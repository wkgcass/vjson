package vjson;

import org.junit.Test;
import vjson.cs.LineCol;
import vjson.pl.ExprTokenizer;
import vjson.pl.token.Token;
import vjson.pl.token.TokenType;
import vjson.simple.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class TestExprTokenizer {
    private List<Token> token(String str) {
        List<Token> res = new ArrayList<>();
        ExprTokenizer tokenizer = new ExprTokenizer(str, LineCol.Companion.getEMPTY());
        int n = 0;
        while (true) {
            Token token = tokenizer.peek(++n);
            if (token == null) {
                break;
            }
            res.add(token);
        }
        for (int i = 0; i < n - 1; ++i) {
            Token token = tokenizer.next(1);
            assertSame(res.get(i), token);
        }
        assertNull(tokenizer.next(1));

        // then test whether it can run blank suffix
        String str2 = str + " ";
        ExprTokenizer tokenizer2 = new ExprTokenizer(str2, LineCol.Companion.getEMPTY());
        List<Token> res2 = new ArrayList<>();
        while (true) {
            Token token = tokenizer2.next(1);
            if (token == null) {
                break;
            }
            res2.add(token);
        }
        assertEquals(res2, res);

        return res;
    }

    @Test
    public void varname() {
        assertEquals(Collections.singletonList(new Token(TokenType.VAR_NAME, "a", LineCol.Companion.getEMPTY())), token("a"));
        assertEquals(Collections.singletonList(new Token(TokenType.VAR_NAME, "A", LineCol.Companion.getEMPTY())), token("A"));
        assertEquals(Collections.singletonList(new Token(TokenType.VAR_NAME, "abc", LineCol.Companion.getEMPTY())), token("abc"));
        assertEquals(Collections.singletonList(new Token(TokenType.VAR_NAME, "a1", LineCol.Companion.getEMPTY())), token("a1"));
        assertEquals(Collections.singletonList(new Token(TokenType.VAR_NAME, "$", LineCol.Companion.getEMPTY())), token("$"));
        assertEquals(Collections.singletonList(new Token(TokenType.VAR_NAME, "_", LineCol.Companion.getEMPTY())), token("_"));
        assertEquals(Collections.singletonList(new Token(TokenType.VAR_NAME, "hUDH78S9", LineCol.Companion.getEMPTY())), token("hUDH78S9"));
    }

    @Test
    public void aNull() {
        assertEquals(Collections.singletonList(new Token(TokenType.KEY_NULL, "null", LineCol.Companion.getEMPTY())), token("null"));
    }

    @Test
    public void parentheses() {
        assertEquals(Arrays.asList(
            new Token(TokenType.LEFT_PAR, "(", LineCol.Companion.getEMPTY()),
            new Token(TokenType.RIGHT_PAR, ")", LineCol.Companion.getEMPTY()),
            new Token(TokenType.LEFT_BRACKET, "[", LineCol.Companion.getEMPTY()),
            new Token(TokenType.RIGHT_BRACKET, "]", LineCol.Companion.getEMPTY())
        ), token("()[]"));
    }

    @Test
    public void operators() {
        assertEquals(Arrays.asList(
            new Token(TokenType.PLUS, "+", LineCol.Companion.getEMPTY()),
            new Token(TokenType.MINUS, "-", LineCol.Companion.getEMPTY()),
            new Token(TokenType.MULTIPLY, "*", LineCol.Companion.getEMPTY()),
            new Token(TokenType.DIVIDE, "/", LineCol.Companion.getEMPTY()),
            new Token(TokenType.PLUS_ASSIGN, "+=", LineCol.Companion.getEMPTY()),
            new Token(TokenType.MINUS_ASSIGN, "-=", LineCol.Companion.getEMPTY()),
            new Token(TokenType.MULTIPLY_ASSIGN, "*=", LineCol.Companion.getEMPTY()),
            new Token(TokenType.DIVIDE_ASSIGN, "/=", LineCol.Companion.getEMPTY()),
            new Token(TokenType.CMP_GT, ">", LineCol.Companion.getEMPTY()),
            new Token(TokenType.CMP_LT, "<", LineCol.Companion.getEMPTY()),
            new Token(TokenType.CMP_GE, ">=", LineCol.Companion.getEMPTY()),
            new Token(TokenType.CMP_LE, "<=", LineCol.Companion.getEMPTY()),
            new Token(TokenType.CMP_NE, "!=", LineCol.Companion.getEMPTY()),
            new Token(TokenType.LOGIC_NOT, "!", LineCol.Companion.getEMPTY()),
            new Token(TokenType.LOGIC_AND, "&&", LineCol.Companion.getEMPTY()),
            new Token(TokenType.LOGIC_OR, "||", LineCol.Companion.getEMPTY())
        ), token("+-*/+=-=*=/= ><>=<=!=!&&||"));
    }

    @Test
    public void numbers() {
        assertEquals(Arrays.asList(
            new Token(TokenType.INTEGER, "1", LineCol.Companion.getEMPTY(), new SimpleInteger(1)),
            new Token(TokenType.MINUS, "-", LineCol.Companion.getEMPTY()),
            new Token(TokenType.INTEGER, "1", LineCol.Companion.getEMPTY(), new SimpleInteger(1)),
            new Token(TokenType.INTEGER, "123", LineCol.Companion.getEMPTY(), new SimpleInteger(123)),
            new Token(TokenType.FLOAT, "1.2", LineCol.Companion.getEMPTY(), new SimpleDouble(1.2)),
            new Token(TokenType.MINUS, "-", LineCol.Companion.getEMPTY()),
            new Token(TokenType.FLOAT, "1.2", LineCol.Companion.getEMPTY(), new SimpleDouble(1.2)),
            new Token(TokenType.FLOAT, "0.123", LineCol.Companion.getEMPTY(), new SimpleDouble(0.123)),
            new Token(TokenType.FLOAT, "5e3", LineCol.Companion.getEMPTY(), new SimpleExp(5, 3)),
            new Token(TokenType.INTEGER, "1000", LineCol.Companion.getEMPTY(), new SimpleInteger(1000)),
            new Token(TokenType.INTEGER, "10000000000000", LineCol.Companion.getEMPTY(), new SimpleLong(10000000000000L))
        ), token("1 -1 123 1.2 -1.2 0.123 5e3 1000 10000000000000"));
    }

    @Test
    public void dot() {
        assertEquals(Arrays.asList(
            new Token(TokenType.VAR_NAME, "a", LineCol.Companion.getEMPTY()),
            new Token(TokenType.DOT, ".", LineCol.Companion.getEMPTY()),
            new Token(TokenType.VAR_NAME, "b", LineCol.Companion.getEMPTY()),
            new Token(TokenType.FLOAT, "1.2", LineCol.Companion.getEMPTY(), new SimpleDouble(1.2)),
            new Token(TokenType.VAR_NAME, "c", LineCol.Companion.getEMPTY()),
            new Token(TokenType.DOT, ".", LineCol.Companion.getEMPTY()),
            new Token(TokenType.VAR_NAME, "d", LineCol.Companion.getEMPTY())
        ), token("a.b 1.2 c.d"));
    }

    @Test
    public void string() throws Exception {
        assertEquals(Arrays.asList(
            new Token(TokenType.STRING, "'a'", LineCol.Companion.getEMPTY(), new SimpleString("a")),
            new Token(TokenType.STRING, "'a\\'b'", LineCol.Companion.getEMPTY(), new SimpleString("a'b")),
            new Token(TokenType.STRING, "''", LineCol.Companion.getEMPTY(), new SimpleString("")),
            new Token(TokenType.STRING, "'\\\\'", LineCol.Companion.getEMPTY(), new SimpleString("\\")),
            new Token(TokenType.STRING, "'\\b'", LineCol.Companion.getEMPTY(), new SimpleString("\b"))
        ), token("'a' 'a\\'b' '' '\\\\' '\\b'"));
    }

    @Test
    public void intAndDot() {
        assertEquals(Arrays.asList(
            new Token(TokenType.INTEGER, "1", LineCol.Companion.getEMPTY(), new SimpleInteger(1)),
            new Token(TokenType.DOT, ".", LineCol.Companion.getEMPTY()),
            new Token(TokenType.VAR_NAME, "toString", LineCol.Companion.getEMPTY()),
            new Token(TokenType.INTEGER, "2", LineCol.Companion.getEMPTY(), new SimpleInteger(2)),
            new Token(TokenType.DOT, ".", LineCol.Companion.getEMPTY()),
            new Token(TokenType.VAR_NAME, "toFloat", LineCol.Companion.getEMPTY()),
            new Token(TokenType.INTEGER, "10000000000", LineCol.Companion.getEMPTY(), new SimpleLong(10000000000L)),
            new Token(TokenType.DOT, ".", LineCol.Companion.getEMPTY()),
            new Token(TokenType.VAR_NAME, "toInt", LineCol.Companion.getEMPTY())
        ), token("1.toString  2.toFloat 10000000000.toInt"));
    }

    @Test
    public void all() {
        assertEquals(Arrays.asList(
            new Token(TokenType.INTEGER, "1", LineCol.Companion.getEMPTY(), new SimpleInteger(1)),
            new Token(TokenType.PLUS, "+", LineCol.Companion.getEMPTY()),
            new Token(TokenType.INTEGER, "2", LineCol.Companion.getEMPTY(), new SimpleInteger(2)),
            new Token(TokenType.MULTIPLY, "*", LineCol.Companion.getEMPTY()),
            new Token(TokenType.INTEGER, "3", LineCol.Companion.getEMPTY(), new SimpleInteger(3)),
            new Token(TokenType.MINUS, "-", LineCol.Companion.getEMPTY()),
            new Token(TokenType.INTEGER, "4", LineCol.Companion.getEMPTY(), new SimpleInteger(4)),
            new Token(TokenType.DIVIDE, "/", LineCol.Companion.getEMPTY()),
            new Token(TokenType.INTEGER, "5", LineCol.Companion.getEMPTY(), new SimpleInteger(5)),
            new Token(TokenType.LOGIC_NOT, "!", LineCol.Companion.getEMPTY()),
            new Token(TokenType.BOOL_FALSE, "false", LineCol.Companion.getEMPTY(), new SimpleBool(false)),
            new Token(TokenType.LOGIC_NOT, "!", LineCol.Companion.getEMPTY()),
            new Token(TokenType.BOOL_TRUE, "true", LineCol.Companion.getEMPTY(), new SimpleBool(true)),
            new Token(TokenType.VAR_NAME, "console", LineCol.Companion.getEMPTY()),
            new Token(TokenType.DOT, ".", LineCol.Companion.getEMPTY()),
            new Token(TokenType.VAR_NAME, "log", LineCol.Companion.getEMPTY()),
            new Token(TokenType.LEFT_PAR, "(", LineCol.Companion.getEMPTY()),
            new Token(TokenType.KEY_NULL, "null", LineCol.Companion.getEMPTY()),
            new Token(TokenType.RIGHT_PAR, ")", LineCol.Companion.getEMPTY()),
            new Token(TokenType.VAR_NAME, "array", LineCol.Companion.getEMPTY()),
            new Token(TokenType.LEFT_BRACKET, "[", LineCol.Companion.getEMPTY()),
            new Token(TokenType.INTEGER, "0", LineCol.Companion.getEMPTY(), new SimpleInteger(0)),
            new Token(TokenType.RIGHT_BRACKET, "]", LineCol.Companion.getEMPTY()),
            new Token(TokenType.VAR_NAME, "a", LineCol.Companion.getEMPTY()),
            new Token(TokenType.PLUS_ASSIGN, "+=", LineCol.Companion.getEMPTY()),
            new Token(TokenType.INTEGER, "1", LineCol.Companion.getEMPTY(), new SimpleInteger(1)),
            new Token(TokenType.VAR_NAME, "a", LineCol.Companion.getEMPTY()),
            new Token(TokenType.MINUS_ASSIGN, "-=", LineCol.Companion.getEMPTY()),
            new Token(TokenType.INTEGER, "2", LineCol.Companion.getEMPTY(), new SimpleInteger(2)),
            new Token(TokenType.VAR_NAME, "a", LineCol.Companion.getEMPTY()),
            new Token(TokenType.MULTIPLY_ASSIGN, "*=", LineCol.Companion.getEMPTY()),
            new Token(TokenType.INTEGER, "3", LineCol.Companion.getEMPTY(), new SimpleInteger(3)),
            new Token(TokenType.VAR_NAME, "a", LineCol.Companion.getEMPTY()),
            new Token(TokenType.DIVIDE_ASSIGN, "/=", LineCol.Companion.getEMPTY()),
            new Token(TokenType.INTEGER, "4", LineCol.Companion.getEMPTY(), new SimpleInteger(4)),
            new Token(TokenType.VAR_NAME, "a", LineCol.Companion.getEMPTY()),
            new Token(TokenType.CMP_GT, ">", LineCol.Companion.getEMPTY()),
            new Token(TokenType.VAR_NAME, "b", LineCol.Companion.getEMPTY()),
            new Token(TokenType.VAR_NAME, "c", LineCol.Companion.getEMPTY()),
            new Token(TokenType.CMP_LT, "<", LineCol.Companion.getEMPTY()),
            new Token(TokenType.VAR_NAME, "d", LineCol.Companion.getEMPTY()),
            new Token(TokenType.VAR_NAME, "e", LineCol.Companion.getEMPTY()),
            new Token(TokenType.CMP_GE, ">=", LineCol.Companion.getEMPTY()),
            new Token(TokenType.VAR_NAME, "f", LineCol.Companion.getEMPTY()),
            new Token(TokenType.VAR_NAME, "g", LineCol.Companion.getEMPTY()),
            new Token(TokenType.CMP_LE, "<=", LineCol.Companion.getEMPTY()),
            new Token(TokenType.VAR_NAME, "h", LineCol.Companion.getEMPTY()),
            new Token(TokenType.VAR_NAME, "i", LineCol.Companion.getEMPTY()),
            new Token(TokenType.CMP_NE, "!=", LineCol.Companion.getEMPTY()),
            new Token(TokenType.VAR_NAME, "j", LineCol.Companion.getEMPTY()),
            new Token(TokenType.VAR_NAME, "k", LineCol.Companion.getEMPTY()),
            new Token(TokenType.CMP_EQ, "==", LineCol.Companion.getEMPTY()),
            new Token(TokenType.VAR_NAME, "l", LineCol.Companion.getEMPTY()),
            new Token(TokenType.VAR_NAME, "test1", LineCol.Companion.getEMPTY()),
            new Token(TokenType.DOT, ".", LineCol.Companion.getEMPTY()),
            new Token(TokenType.VAR_NAME, "a", LineCol.Companion.getEMPTY()),
            new Token(TokenType.LOGIC_AND, "&&", LineCol.Companion.getEMPTY()),
            new Token(TokenType.VAR_NAME, "test2", LineCol.Companion.getEMPTY()),
            new Token(TokenType.DOT, ".", LineCol.Companion.getEMPTY()),
            new Token(TokenType.VAR_NAME, "b", LineCol.Companion.getEMPTY()),
            new Token(TokenType.VAR_NAME, "test1", LineCol.Companion.getEMPTY()),
            new Token(TokenType.DOT, ".", LineCol.Companion.getEMPTY()),
            new Token(TokenType.VAR_NAME, "c", LineCol.Companion.getEMPTY()),
            new Token(TokenType.LOGIC_OR, "||", LineCol.Companion.getEMPTY()),
            new Token(TokenType.VAR_NAME, "test2", LineCol.Companion.getEMPTY()),
            new Token(TokenType.DOT, ".", LineCol.Companion.getEMPTY()),
            new Token(TokenType.VAR_NAME, "d", LineCol.Companion.getEMPTY()),
            new Token(TokenType.COLON, ":", LineCol.Companion.getEMPTY()),
            new Token(TokenType.COMMA, ",", LineCol.Companion.getEMPTY()),
            new Token(TokenType.STRING, "'abc'", LineCol.Companion.getEMPTY(), new SimpleString("abc"))
        ), token("1+2*3-4/5 !false !true console.log (null) array[0] a+=1 a-=2 a*=3 a/=4 a>b c<d e>=f g<=h i!=j k==l test1.a&&test2.b test1.c||test2.d : ,'abc'"));
    }
}
