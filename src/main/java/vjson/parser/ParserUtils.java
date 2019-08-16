/*
 * The MIT License
 *
 * Copyright 2019 wkgcass (https://github.com/wkgcass)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package vjson.parser;

import vjson.CharStream;
import vjson.JSON;
import vjson.Parser;
import vjson.ex.JsonParseException;

public class ParserUtils {
    private ParserUtils() {
    }

    public static boolean isWhiteSpace(char c) {
        return c == '\n'
            || c == '\r'
            || c == ' '
            || c == '\t';
    }

    static void checkEnd(CharStream cs, ParserOptions opts, String type) {
        if (opts.isEnd()) {
            cs.skipBlank();
            if (cs.hasNext()) {
                String err = "input stream contain extra characters other than " + type;
                opts.getListener().onError(err);
                throw new JsonParseException(err);
            }
        }
    }

    static JsonParseException err(ParserOptions opts, String msg) {
        opts.getListener().onError(msg);
        return new JsonParseException(msg);
    }

    static ParserOptions subParserOptions(ParserOptions opts) {
        if (opts == ParserOptions.DEFAULT || opts == ParserOptions.DEFAULT_NO_END) {
            return ParserOptions.DEFAULT_NO_END;
        }
        return new ParserOptions(opts).setEnd(false);
    }

    public static JSON.Instance buildFrom(CharStream cs) throws NullPointerException, JsonParseException {
        return build(cs, new ParserOptions());
    }

    public static JSON.Instance buildFrom(CharStream cs, ParserOptions opts) throws NullPointerException, JsonParseException {
        if (cs == null) {
            throw new NullPointerException();
        }
        if (opts == null) {
            throw new NullPointerException();
        }
        return build(cs, opts);
    }

    @SuppressWarnings("DuplicateBranchesInSwitch")
    private static Parser parser(CharStream cs, ParserOptions opts) throws JsonParseException {
        cs.skipBlank();
        if (!cs.hasNext()) {
            throw new JsonParseException("empty input string");
        }
        char first = cs.peekNext();
        switch (first) {
            case '{':
                return new ObjectParser(opts);
            case '[':
                return new ArrayParser(opts);
            case '"':
                return new StringParser(opts);
            case 'n':
                return new NullParser(opts);
            case 't':
                return new BoolParser(opts);
            case 'f':
                return new BoolParser(opts);
            case '-':
                return new NumberParser(opts);
            default:
                if (first >= '0' && first <= '9') {
                    return new NumberParser(opts);
                }
                // invalid json
                throw new JsonParseException("not valid json string");
        }
    }

    private static JSON.Instance build(CharStream cs, ParserOptions opts) throws IllegalArgumentException, JsonParseException {
        return parser(cs, opts).build(cs, true);
    }
}
