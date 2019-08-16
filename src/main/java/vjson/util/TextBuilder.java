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

package vjson.util;

public class TextBuilder {
    private final int BUF_LEN;
    private final char[] buf; // use buf if not oob
    private int len = 0;
    private StringBuilder sb; // use stringBuilder if buf not enough

    public TextBuilder(int bufLen) {
        BUF_LEN = bufLen;
        buf = new char[BUF_LEN];
    }

    public void clear() {
        len = 0;
        if (sb != null) {
            sb.setLength(0);
        }
    }

    private void writeToBuilder() {
        if (sb == null) {
            sb = new StringBuilder(2 * BUF_LEN);
        }
        sb.append(buf, 0, len);
        len = 0;
    }

    public TextBuilder append(char c) {
        // choose to write to buffer or string builder
        boolean writeToBuffer =
            (sb == null) || (sb.capacity() - sb.length() < BUF_LEN);
        // not need to care about when sb.capacity() < BUF_LEN
        // the sb is initialized to 2*BUF_LEN

        if (writeToBuffer) {
            buf[len++] = c;
            if (len == BUF_LEN) {
                writeToBuilder();
            }
        } else {
            sb.append(c);
        }
        return this;
    }

    public TextBuilder removeLast() {
        if (len == 0) {
            if (sb == null) {
                return this;
            }
            if (sb.length() == 0) {
                return this;
            }
            sb.delete(sb.length() - 1, sb.length());
        } else {
            --len;
        }
        return this;
    }

    @Override
    public String toString() {
        if (sb == null || sb.length() == 0) {
            if (len == 0) {
                return "";
            } else {
                return new String(buf, 0, len);
            }
        } else if (len == 0) {
            return sb.toString();
        } else {
            writeToBuilder();
            return sb.toString();
        }
    }
}
