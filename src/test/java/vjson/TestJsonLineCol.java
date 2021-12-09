package vjson;

import org.junit.Test;
import vjson.cs.LineCol;
import vjson.cs.LineColCharStream;
import vjson.parser.ParserOptions;
import vjson.parser.ParserUtils;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestJsonLineCol {
    private void assertLineCol(LineCol expected, LineCol actual) {
        assertEquals("filename", expected.getFilename(), actual.getFilename());
        assertEquals("line", expected.getLine(), actual.getLine());
        assertEquals("col", expected.getCol(), actual.getCol());
    }

    @Test
    public void allInOne() {
        JSON.Instance<?> json = ParserUtils.buildFrom(new LineColCharStream(CharStream.from(" {\n" +
                "  \"a\": \"xx\",\n" +
                "  \"b\":    1,\n" +
                "     \"c\": 10000000000000000,\n" +
                "  \"d\": 1.0,\r\n" +
                "  \"e\": 2e3,\r" +
                "  \"f\": [\n" +
                "    \"x\", { \"m\": \"n\" }\n" +
                "  ],\n" +
                "  \"g\": true\n" +
                "  h = (hello\n" +
                "world)\n" +
                "  i j\n" +
                "  k = 'kkk'\n" +
                "}"), "test"),
            new ParserOptions()
                .setStringSingleQuotes(true)
                .setKeyNoQuotes(true)
                .setKeyNoQuotesAnyChar(true)
                .setAllowSkippingCommas(true)
                .setAllowObjectEntryWithoutValue(true)
                .setEqualAsColon(true)
                .setAllowParenthesesString(true));
        assertLineCol(new LineCol("test", 1, 2), json.lineCol());
        JSON.Object o = (JSON.Object) json;
        List<JSON.ObjectEntry> entries = o.entryList();
        JSON.ObjectEntry entryA = entries.get(0);
        assertLineCol(new LineCol("test", 2, 3), entryA.getLineCol());
        assertLineCol(new LineCol("test", 2, 8), entryA.getValue().lineCol());
        JSON.ObjectEntry entryB = entries.get(1);
        assertLineCol(new LineCol("test", 3, 3), entryB.getLineCol());
        assertLineCol(new LineCol("test", 3, 11), entryB.getValue().lineCol());
        JSON.ObjectEntry entryC = entries.get(2);
        assertLineCol(new LineCol("test", 4, 6), entryC.getLineCol());
        assertLineCol(new LineCol("test", 4, 11), entryC.getValue().lineCol());
        JSON.ObjectEntry entryD = entries.get(3);
        assertLineCol(new LineCol("test", 5, 3), entryD.getLineCol());
        assertLineCol(new LineCol("test", 5, 8), entryD.getValue().lineCol());
        JSON.ObjectEntry entryE = entries.get(4);
        assertLineCol(new LineCol("test", 6, 3), entryE.getLineCol());
        assertLineCol(new LineCol("test", 6, 8), entryE.getValue().lineCol());
        JSON.ObjectEntry entryF = entries.get(5);
        assertLineCol(new LineCol("test", 7, 3), entryF.getLineCol());
        assertLineCol(new LineCol("test", 7, 8), entryF.getValue().lineCol());
        JSON.Array fArray = (JSON.Array) entryF.getValue();
        assertLineCol(new LineCol("test", 7, 8), fArray.lineCol());
        assertLineCol(new LineCol("test", 8, 5), fArray.get(0).lineCol());
        JSON.Object fArrayObj = (JSON.Object) fArray.get(1);
        assertLineCol(new LineCol("test", 8, 10), fArrayObj.lineCol());
        List<JSON.ObjectEntry> fArrayObjEntries = fArrayObj.entryList();
        assertLineCol(new LineCol("test", 8, 12), fArrayObjEntries.get(0).getLineCol());
        assertLineCol(new LineCol("test", 8, 17), fArrayObjEntries.get(0).getValue().lineCol());
        JSON.ObjectEntry entryG = entries.get(6);
        assertLineCol(new LineCol("test", 10, 3), entryG.getLineCol());
        assertLineCol(new LineCol("test", 10, 8), entryG.getValue().lineCol());
        JSON.ObjectEntry entryH = entries.get(7);
        assertLineCol(new LineCol("test", 11, 3), entryH.getLineCol());
        assertLineCol(new LineCol("test", 11, 7), entryH.getValue().lineCol());
        JSON.ObjectEntry entryI = entries.get(8);
        assertLineCol(new LineCol("test", 13, 3), entryI.getLineCol());
        assertLineCol(new LineCol("test", 13, 5), entryI.getValue().lineCol());
        JSON.ObjectEntry entryJ = entries.get(9);
        assertLineCol(new LineCol("test", 13, 5), entryJ.getLineCol());
        assertLineCol(new LineCol("test", 14, 3), entryJ.getValue().lineCol());
        JSON.ObjectEntry entryK = entries.get(10);
        assertLineCol(new LineCol("test", 14, 3), entryK.getLineCol());
        assertLineCol(new LineCol("test", 14, 7), entryK.getValue().lineCol());
    }
}
