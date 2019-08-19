package vjson;

import org.junit.runner.RunWith;

@RunWith(org.junit.runners.Suite.class)
@org.junit.runners.Suite.SuiteClasses({
    TestCharArrayCharStream.class,
    TestConvenience.class,
    TestCorner.class,
    TestFeature.class,
    TestFeatureFail.class,
    TestListener.class,
    TestListenerJavaObject.class,
    TestParamValidationReport.class,
    TestParse.class,
    TestParseFail.class,
    TestSpec.class,
    TestStringifier.class,
    TestStringify.class,
    TestTextBuilder.class,
    TestToJavaObject.class,
    TestToString.class,
    TestTransformer.class,
    TestUpdateParser.class,
})
public class Suite {
}
