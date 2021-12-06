package vjson;

import org.junit.Test;
import vjson.pl.ast.Type;
import vjson.pl.type.*;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestTypeContext {
    @SuppressWarnings("ConstantConditions")
    @Test
    public void globalInit() {
        MemoryAllocator globalMem = new MemoryAllocator();
        TypeContext ctx = new TypeContext(globalMem);
        assertTrue(ctx.hasType(new Type("int")));
        assertTrue(ctx.hasType(new Type("long")));
        assertTrue(ctx.hasType(new Type("double")));
        assertTrue(ctx.hasType(new Type("bool")));
        assertTrue(ctx.hasType(new Type("string")));

        IntType intType = (IntType) ctx.getType(new Type("int"));
        LongType longType = (LongType) ctx.getType(new Type("long"));
        FloatType floatType = (FloatType) ctx.getType(new Type("float"));
        DoubleType doubleType = (DoubleType) ctx.getType(new Type("double"));
        BoolType boolType = (BoolType) ctx.getType(new Type("bool"));
        StringType stringType = (StringType) ctx.getType(new Type("string"));

        assertEquals(longType, intType.field(ctx, "toLong", intType).getType());
        assertEquals(floatType, intType.field(ctx, "toFloat", intType).getType());
        assertEquals(doubleType, intType.field(ctx, "toDouble", intType).getType());
        assertEquals(ctx.getFunctionDescriptor(Collections.emptyList(), stringType, DummyMemoryAllocatorProvider.INSTANCE),
            intType.field(ctx, "toString", intType).getType().functionDescriptor(ctx));

        assertEquals(intType, longType.field(ctx, "toInt", longType).getType());
        assertEquals(floatType, longType.field(ctx, "toFloat", longType).getType());
        assertEquals(doubleType, longType.field(ctx, "toDouble", longType).getType());
        assertEquals(ctx.getFunctionDescriptor(Collections.emptyList(), stringType, DummyMemoryAllocatorProvider.INSTANCE),
            longType.field(ctx, "toString", longType).getType().functionDescriptor(ctx));

        assertEquals(intType, floatType.field(ctx, "toInt", floatType).getType());
        assertEquals(longType, floatType.field(ctx, "toLong", floatType).getType());
        assertEquals(doubleType, floatType.field(ctx, "toDouble", floatType).getType());
        assertEquals(ctx.getFunctionDescriptor(Collections.emptyList(), stringType, DummyMemoryAllocatorProvider.INSTANCE),
            floatType.field(ctx, "toString", floatType).getType().functionDescriptor(ctx));

        assertEquals(intType, doubleType.field(ctx, "toInt", doubleType).getType());
        assertEquals(longType, doubleType.field(ctx, "toLong", doubleType).getType());
        assertEquals(floatType, doubleType.field(ctx, "toFloat", doubleType).getType());
        assertEquals(ctx.getFunctionDescriptor(Collections.emptyList(), stringType, DummyMemoryAllocatorProvider.INSTANCE),
            doubleType.field(ctx, "toString", doubleType).getType().functionDescriptor(ctx));

        assertEquals(ctx.getFunctionDescriptor(Collections.emptyList(), stringType, DummyMemoryAllocatorProvider.INSTANCE),
            boolType.field(ctx, "toString", boolType).getType().functionDescriptor(ctx));
    }
}
