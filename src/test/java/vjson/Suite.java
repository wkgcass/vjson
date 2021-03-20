package vjson;

import org.junit.runner.RunWith;
import vpreprocessor.*;

@RunWith(org.junit.runners.Suite.class)
@org.junit.runners.Suite.SuiteClasses({
    TestParserCacheHolder.class, // must be the first

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
    TestEncoding.class,
    TestDeserialize.class,
    TestUtil.class,
    TestKNParserCacheHolder.class,
    TestPreprocessorTokenizer.class,
    TestPreprocessorParser.class,
    TestPreprocessorContext.class,
    TestPreprocessor.class,
    TestPreprocessorToken.class,
    TestPreprocessorAST.class,
    TestVList.class,
})
public class Suite {
}
