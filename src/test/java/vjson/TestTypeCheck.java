package vjson;

import org.junit.Test;
import vjson.parser.ObjectParser;
import vjson.parser.ParserOptions;
import vjson.pl.ASTGen;
import vjson.pl.ast.Access;
import vjson.pl.ast.Statement;
import vjson.pl.inst.RuntimeMemoryTotal;
import vjson.pl.type.*;
import vjson.pl.type.lang.StdTypes;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestTypeCheck {
    @Test
    public void access() {
        MemoryAllocator globalMem = new MemoryAllocator();
        TypeContext ctx = new TypeContext(globalMem);
        ctx.addVariable(new Variable("a", IntType.INSTANCE, true, new MemPos(0, 0)));

        Access access = new Access("a");
        access.check(ctx);
        assertEquals(IntType.INSTANCE, access.typeInstance());

        access = new Access("toString", new Access("a"));
        access.check(ctx);
        FunctionDescriptorTypeInstance type = (FunctionDescriptorTypeInstance) access.typeInstance();
        FunctionDescriptor desc = type.functionDescriptor(ctx);
        assertEquals(0, desc.getParams().size());
        assertEquals(StringType.INSTANCE, desc.getReturnType());
    }

    @Test
    public void pass() {
        //noinspection ConstantConditions
        ASTGen gen = new ASTGen(new ObjectParser(new ParserOptions()
            .setKeyNoQuotesWithDot(true)
            .setAllowObjectEntryWithoutValue(true)
            .setAllowSkippingCommas(true)
            .setStringSingleQuotes(true)
            .setEqualAsColon(true)
        ).last(TestFeature.TEST_PROG));
        List<Statement> stmts = gen.parse();
        MemoryAllocator globalMem = new MemoryAllocator();
        TypeContext ctx = new TypeContext(globalMem);
        StdTypes.INSTANCE.initiateType(ctx, new RuntimeMemoryTotal());
        ctx.checkStatements(stmts);
    }
}
