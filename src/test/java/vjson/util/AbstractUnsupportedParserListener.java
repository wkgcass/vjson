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

import vjson.JSON;
import vjson.ParserListener;
import vjson.parser.*;

import java.util.List;
import java.util.Map;

public abstract class AbstractUnsupportedParserListener implements ParserListener {
    @Override
    public void onObjectBegin(ObjectParser object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onObjectKey(ObjectParser object, String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onObjectValue(ObjectParser object, String key, JSON.Instance value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onObjectValueJavaObject(ObjectParser object, String key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onObjectEnd(ObjectParser object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onObject(JSON.Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onObject(Map<String, Object> object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onArrayBegin(ArrayParser array) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onArrayValue(ArrayParser array, JSON.Instance value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onArrayValueJavaObject(ArrayParser array, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onArrayEnd(ArrayParser array) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onArray(JSON.Array array) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onArray(List<Object> array) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onBoolBegin(BoolParser bool) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onBoolEnd(BoolParser bool) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onBool(JSON.Bool bool) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onBool(Boolean bool) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onNullBegin(NullParser n) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onNullEnd(NullParser n) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onNull(JSON.Null n) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onNull(Void n) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onNumberBegin(NumberParser number) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onNumberFractionBegin(NumberParser number, long integer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onNumberExponentBegin(NumberParser number, double base) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onNumberEnd(NumberParser number) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onNumber(JSON.Number number) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onNumber(Number number) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onStringBegin(StringParser string) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onStringChar(StringParser string, char c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onStringEnd(StringParser string) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onString(JSON.String string) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onString(String string) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onError(String err) {
        throw new UnsupportedOperationException();
    }
}
