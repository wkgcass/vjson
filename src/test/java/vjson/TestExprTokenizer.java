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
        ExprTokenizer tokenizer = new ExprTokenizer(str, new LineCol("", 0, 0));
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
        ExprTokenizer tokenizer2 = new ExprTokenizer(str2, new LineCol("", 0, 0));
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
        assertEquals(Collections.singletonList(new Token(TokenType.VAR_NAME, "a", new LineCol("", 0, 0))), token("a"));
        assertEquals(Collections.singletonList(new Token(TokenType.VAR_NAME, "A", new LineCol("", 0, 0))), token("A"));
        assertEquals(Collections.singletonList(new Token(TokenType.VAR_NAME, "abc", new LineCol("", 0, 0))), token("abc"));
        assertEquals(Collections.singletonList(new Token(TokenType.VAR_NAME, "a1", new LineCol("", 0, 0))), token("a1"));
        assertEquals(Collections.singletonList(new Token(TokenType.VAR_NAME, "$", new LineCol("", 0, 0))), token("$"));
        assertEquals(Collections.singletonList(new Token(TokenType.VAR_NAME, "_", new LineCol("", 0, 0))), token("_"));
        assertEquals(Collections.singletonList(new Token(TokenType.VAR_NAME, "hUDH78S9", new LineCol("", 0, 0))), token("hUDH78S9"));
    }

    @Test
    public void aNull() {
        assertEquals(Collections.singletonList(new Token(TokenType.KEY_NULL, "null", new LineCol("", 0, 0))), token("null"));
    }

    @Test
    public void parentheses() {
        assertEquals(Arrays.asList(
            new Token(TokenType.LEFT_PAR, "(", new LineCol("", 0, 0)),
            new Token(TokenType.RIGHT_PAR, ")", new LineCol("", 0, 0)),
            new Token(TokenType.LEFT_BRACKET, "[", new LineCol("", 0, 0)),
            new Token(TokenType.RIGHT_BRACKET, "]", new LineCol("", 0, 0))
        ), token("()[]"));
    }

    @Test
    public void operators() {
        assertEquals(Arrays.asList(
            new Token(TokenType.PLUS, "+", new LineCol("", 0, 0)),
            new Token(TokenType.MINUS, "-", new LineCol("", 0, 0)),
            new Token(TokenType.MULTIPLY, "*", new LineCol("", 0, 0)),
            new Token(TokenType.DIVIDE, "/", new LineCol("", 0, 0)),
            new Token(TokenType.PLUS_ASSIGN, "+=", new LineCol("", 0, 0)),
            new Token(TokenType.MINUS_ASSIGN, "-=", new LineCol("", 0, 0)),
            new Token(TokenType.MULTIPLY_ASSIGN, "*=", new LineCol("", 0, 0)),
            new Token(TokenType.DIVIDE_ASSIGN, "/=", new LineCol("", 0, 0)),
            new Token(TokenType.CMP_GT, ">", new LineCol("", 0, 0)),
            new Token(TokenType.CMP_LT, "<", new LineCol("", 0, 0)),
            new Token(TokenType.CMP_GE, ">=", new LineCol("", 0, 0)),
            new Token(TokenType.CMP_LE, "<=", new LineCol("", 0, 0)),
            new Token(TokenType.CMP_NE, "!=", new LineCol("", 0, 0)),
            new Token(TokenType.LOGIC_NOT, "!", new LineCol("", 0, 0)),
            new Token(TokenType.LOGIC_AND, "&&", new LineCol("", 0, 0)),
            new Token(TokenType.LOGIC_OR, "||", new LineCol("", 0, 0))
        ), token("+-*/+=-=*=/= ><>=<=!=!&&||"));
    }

    @Test
    public void numbers() {
        assertEquals(Arrays.asList(
            new Token(TokenType.INTEGER, "1", new LineCol("", 0, 0), new SimpleInteger(1)),
            new Token(TokenType.MINUS, "-", new LineCol("", 0, 0)),
            new Token(TokenType.INTEGER, "1", new LineCol("", 0, 0), new SimpleInteger(1)),
            new Token(TokenType.INTEGER, "123", new LineCol("", 0, 0), new SimpleInteger(123)),
            new Token(TokenType.FLOAT, "1.2", new LineCol("", 0, 0), new SimpleDouble(1.2)),
            new Token(TokenType.MINUS, "-", new LineCol("", 0, 0)),
            new Token(TokenType.FLOAT, "1.2", new LineCol("", 0, 0), new SimpleDouble(1.2)),
            new Token(TokenType.FLOAT, "0.123", new LineCol("", 0, 0), new SimpleDouble(0.123)),
            new Token(TokenType.FLOAT, "5e3", new LineCol("", 0, 0), new SimpleExp(5, 3)),
            new Token(TokenType.INTEGER, "1000", new LineCol("", 0, 0), new SimpleInteger(1000)),
            new Token(TokenType.INTEGER, "10000000000000", new LineCol("", 0, 0), new SimpleLong(10000000000000L))
        ), token("1 -1 123 1.2 -1.2 0.123 5e3 1000 10000000000000"));
    }

    @Test
    public void dot() {
        assertEquals(Arrays.asList(
            new Token(TokenType.VAR_NAME, "a", new LineCol("", 0, 0)),
            new Token(TokenType.DOT, ".", new LineCol("", 0, 0)),
            new Token(TokenType.VAR_NAME, "b", new LineCol("", 0, 0)),
            new Token(TokenType.FLOAT, "1.2", new LineCol("", 0, 0), new SimpleDouble(1.2)),
            new Token(TokenType.VAR_NAME, "c", new LineCol("", 0, 0)),
            new Token(TokenType.DOT, ".", new LineCol("", 0, 0)),
            new Token(TokenType.VAR_NAME, "d", new LineCol("", 0, 0))
        ), token("a.b 1.2 c.d"));
    }

    @Test
    public void string() throws Exception {
        assertEquals(Arrays.asList(
            new Token(TokenType.STRING, "'a'", new LineCol("", 0, 0), new SimpleString("a")),
            new Token(TokenType.STRING, "'a\\'b'", new LineCol("", 0, 0), new SimpleString("a'b")),
            new Token(TokenType.STRING, "''", new LineCol("", 0, 0), new SimpleString("")),
            new Token(TokenType.STRING, "'\\\\'", new LineCol("", 0, 0), new SimpleString("\\")),
            new Token(TokenType.STRING, "'\\b'", new LineCol("", 0, 0), new SimpleString("\\b"))
        ), token("'a' 'a\\'b' '' '\\\\' '\\b'"));
    }

    @Test
    public void all() {
        assertEquals(Arrays.asList(
            new Token(TokenType.INTEGER, "1", new LineCol("", 0, 0), new SimpleInteger(1)),
            new Token(TokenType.PLUS, "+", new LineCol("", 0, 0)),
            new Token(TokenType.INTEGER, "2", new LineCol("", 0, 0), new SimpleInteger(2)),
            new Token(TokenType.MULTIPLY, "*", new LineCol("", 0, 0)),
            new Token(TokenType.INTEGER, "3", new LineCol("", 0, 0), new SimpleInteger(3)),
            new Token(TokenType.MINUS, "-", new LineCol("", 0, 0)),
            new Token(TokenType.INTEGER, "4", new LineCol("", 0, 0), new SimpleInteger(4)),
            new Token(TokenType.DIVIDE, "/", new LineCol("", 0, 0)),
            new Token(TokenType.INTEGER, "5", new LineCol("", 0, 0), new SimpleInteger(5)),
            new Token(TokenType.LOGIC_NOT, "!", new LineCol("", 0, 0)),
            new Token(TokenType.BOOL_FALSE, "false", new LineCol("", 0, 0), new SimpleBool(false)),
            new Token(TokenType.LOGIC_NOT, "!", new LineCol("", 0, 0)),
            new Token(TokenType.BOOL_TRUE, "true", new LineCol("", 0, 0), new SimpleBool(true)),
            new Token(TokenType.VAR_NAME, "console", new LineCol("", 0, 0)),
            new Token(TokenType.DOT, ".", new LineCol("", 0, 0)),
            new Token(TokenType.VAR_NAME, "log", new LineCol("", 0, 0)),
            new Token(TokenType.LEFT_PAR, "(", new LineCol("", 0, 0)),
            new Token(TokenType.KEY_NULL, "null", new LineCol("", 0, 0)),
            new Token(TokenType.RIGHT_PAR, ")", new LineCol("", 0, 0)),
            new Token(TokenType.VAR_NAME, "array", new LineCol("", 0, 0)),
            new Token(TokenType.LEFT_BRACKET, "[", new LineCol("", 0, 0)),
            new Token(TokenType.INTEGER, "0", new LineCol("", 0, 0), new SimpleInteger(0)),
            new Token(TokenType.RIGHT_BRACKET, "]", new LineCol("", 0, 0)),
            new Token(TokenType.VAR_NAME, "a", new LineCol("", 0, 0)),
            new Token(TokenType.PLUS_ASSIGN, "+=", new LineCol("", 0, 0)),
            new Token(TokenType.INTEGER, "1", new LineCol("", 0, 0), new SimpleInteger(1)),
            new Token(TokenType.VAR_NAME, "a", new LineCol("", 0, 0)),
            new Token(TokenType.MINUS_ASSIGN, "-=", new LineCol("", 0, 0)),
            new Token(TokenType.INTEGER, "2", new LineCol("", 0, 0), new SimpleInteger(2)),
            new Token(TokenType.VAR_NAME, "a", new LineCol("", 0, 0)),
            new Token(TokenType.MULTIPLY_ASSIGN, "*=", new LineCol("", 0, 0)),
            new Token(TokenType.INTEGER, "3", new LineCol("", 0, 0), new SimpleInteger(3)),
            new Token(TokenType.VAR_NAME, "a", new LineCol("", 0, 0)),
            new Token(TokenType.DIVIDE_ASSIGN, "/=", new LineCol("", 0, 0)),
            new Token(TokenType.INTEGER, "4", new LineCol("", 0, 0), new SimpleInteger(4)),
            new Token(TokenType.VAR_NAME, "a", new LineCol("", 0, 0)),
            new Token(TokenType.CMP_GT, ">", new LineCol("", 0, 0)),
            new Token(TokenType.VAR_NAME, "b", new LineCol("", 0, 0)),
            new Token(TokenType.VAR_NAME, "c", new LineCol("", 0, 0)),
            new Token(TokenType.CMP_LT, "<", new LineCol("", 0, 0)),
            new Token(TokenType.VAR_NAME, "d", new LineCol("", 0, 0)),
            new Token(TokenType.VAR_NAME, "e", new LineCol("", 0, 0)),
            new Token(TokenType.CMP_GE, ">=", new LineCol("", 0, 0)),
            new Token(TokenType.VAR_NAME, "f", new LineCol("", 0, 0)),
            new Token(TokenType.VAR_NAME, "g", new LineCol("", 0, 0)),
            new Token(TokenType.CMP_LE, "<=", new LineCol("", 0, 0)),
            new Token(TokenType.VAR_NAME, "h", new LineCol("", 0, 0)),
            new Token(TokenType.VAR_NAME, "i", new LineCol("", 0, 0)),
            new Token(TokenType.CMP_NE, "!=", new LineCol("", 0, 0)),
            new Token(TokenType.VAR_NAME, "j", new LineCol("", 0, 0)),
            new Token(TokenType.VAR_NAME, "k", new LineCol("", 0, 0)),
            new Token(TokenType.CMP_EQ, "==", new LineCol("", 0, 0)),
            new Token(TokenType.VAR_NAME, "l", new LineCol("", 0, 0)),
            new Token(TokenType.VAR_NAME, "test1", new LineCol("", 0, 0)),
            new Token(TokenType.DOT, ".", new LineCol("", 0, 0)),
            new Token(TokenType.VAR_NAME, "a", new LineCol("", 0, 0)),
            new Token(TokenType.LOGIC_AND, "&&", new LineCol("", 0, 0)),
            new Token(TokenType.VAR_NAME, "test2", new LineCol("", 0, 0)),
            new Token(TokenType.DOT, ".", new LineCol("", 0, 0)),
            new Token(TokenType.VAR_NAME, "b", new LineCol("", 0, 0)),
            new Token(TokenType.VAR_NAME, "test1", new LineCol("", 0, 0)),
            new Token(TokenType.DOT, ".", new LineCol("", 0, 0)),
            new Token(TokenType.VAR_NAME, "c", new LineCol("", 0, 0)),
            new Token(TokenType.LOGIC_OR, "||", new LineCol("", 0, 0)),
            new Token(TokenType.VAR_NAME, "test2", new LineCol("", 0, 0)),
            new Token(TokenType.DOT, ".", new LineCol("", 0, 0)),
            new Token(TokenType.VAR_NAME, "d", new LineCol("", 0, 0)),
            new Token(TokenType.COLON, ":", new LineCol("", 0, 0)),
            new Token(TokenType.COMMA, ",", new LineCol("", 0, 0)),
            new Token(TokenType.STRING, "'abc'", new LineCol("", 0, 0), new SimpleString("abc"))
        ), token("1+2*3-4/5 !false !true console.log (null) array[0] a+=1 a-=2 a*=3 a/=4 a>b c<d e>=f g<=h i!=j k==l test1.a&&test2.b test1.c||test2.d : ,'abc'"));
    }
}
