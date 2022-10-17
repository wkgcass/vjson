package vjson;

import org.junit.runner.RunWith;
import vpreprocessor.*;

@RunWith(org.junit.runners.Suite.class)
@org.junit.runners.Suite.SuiteClasses({
    TestParserCacheHolder.class, // must be the first

    TestCharArrayCharStream.class,
    TestPeekCharStream.class,
    TestIncludeCharStream.class,
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
    TestUpdateParser.class,
    TestEncoding.class,
    TestDeserialize.class,
    TestDeserializeNull.class,
    TestUtil.class,
    TestKNParserCacheHolder.class,
    TestPreprocessorTokenizer.class,
    TestPreprocessorParser.class,
    TestPreprocessorContext.class,
    TestPreprocessor.class,
    TestPreprocessorToken.class,
    TestPreprocessorAST.class,
    TestVList.class,
    TestKotlinDSL.class,
    TestJavaDSL.class,
    TestStringCache.class,
    TestJsonLineCol.class,

    TestExprTokenizer.class,
    TestExprParser.class,
    TestASTGen.class,
    TestTypeContext.class,
    TestTypeCheck.class,
    TestInterpreter.class,
    TestInterpreterSamplePrograms.class,

    TestIssues.class,
})
public class Suite {
}
