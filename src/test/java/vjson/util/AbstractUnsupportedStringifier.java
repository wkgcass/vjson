package vjson.util;

import vjson.JSON;
import vjson.Stringifier;

public abstract class AbstractUnsupportedStringifier implements Stringifier {
    @Override
    public void beforeObjectBegin(StringBuilder sb, JSON.Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void afterObjectBegin(StringBuilder sb, JSON.Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void beforeObjectKey(StringBuilder sb, JSON.Object object, String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void afterObjectKey(StringBuilder sb, JSON.Object object, String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void beforeObjectColon(StringBuilder sb, JSON.Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void afterObjectColon(StringBuilder sb, JSON.Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void beforeObjectValue(StringBuilder sb, JSON.Object object, String key, JSON.Instance value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void afterObjectValue(StringBuilder sb, JSON.Object object, String key, JSON.Instance value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void beforeObjectComma(StringBuilder sb, JSON.Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void afterObjectComma(StringBuilder sb, JSON.Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void beforeObjectEnd(StringBuilder sb, JSON.Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void afterObjectEnd(StringBuilder sb, JSON.Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void beforeArrayBegin(StringBuilder sb, JSON.Array array) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void afterArrayBegin(StringBuilder sb, JSON.Array array) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void beforeArrayValue(StringBuilder sb, JSON.Array array, JSON.Instance value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void afterArrayValue(StringBuilder sb, JSON.Array array, JSON.Instance value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void beforeArrayComma(StringBuilder sb, JSON.Array array) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void afterArrayComma(StringBuilder sb, JSON.Array array) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void beforeArrayEnd(StringBuilder sb, JSON.Array array) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void afterArrayEnd(StringBuilder sb, JSON.Array array) {
        throw new UnsupportedOperationException();
    }
}
