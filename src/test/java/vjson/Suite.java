package vjson;

import org.junit.runner.RunWith;

@RunWith(org.junit.runners.Suite.class)
@org.junit.runners.Suite.SuiteClasses({
    TestCharArrayCharStream.class,
    TestConvenience.class,
    TestCorner.class,
    TestListener.class,
    TestParamValidationReport.class,
    TestParse.class,
    TestParseFail.class,
    TestSpec.class,
    TestStringify.class,
    TestTextBuilder.class,
    TestToJavaObject.class,
    TestToString.class,
    TestUpdateParser.class,
})
public class Suite {
}
