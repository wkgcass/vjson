package vjson.util;

import kotlin.Unit;
import vjson.JSON;
import vjson.ParserListener;
import vjson.parser.*;

import java.util.List;
import java.util.Map;

public abstract class AbstractUnsupportedParserListener implements ParserListener {
    @Override
    public void onObjectBegin(ObjectParser obj) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onObjectKey(ObjectParser obj, String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onObjectValue(ObjectParser obj, String key, JSON.Instance value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onObjectValueJavaObject(ObjectParser obj, String key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onObjectEnd(ObjectParser obj) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onObject(JSON.Object obj) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onObject(Map<String, ?> obj) {
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
    public void onArray(List<?> array) {
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
    public void onBool(boolean bool) {
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
    public void onNull(Unit n) {
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
