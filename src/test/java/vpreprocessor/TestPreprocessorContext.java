package vpreprocessor;

import kotlin.jvm.internal.Reflection;
import org.junit.Test;
import vpreprocessor.semantic.Definition;

import static org.junit.Assert.*;

public class TestPreprocessorContext {
    @Test
    public void defaultConstructor() {
        PreprocessorContext ctx = new PreprocessorContext();
        assertEquals(PreprocessorOptions.KT, ctx.getOpts());
    }

    @Test
    public void define() {
        PreprocessorContext ctx = new PreprocessorContext(PreprocessorOptions.KT);
        assertNull(ctx.getDefinition("a"));
        assertFalse(ctx.isDefined("a"));
        ctx.define("a");
        assertTrue(ctx.isDefined("a"));
        Definition<?> def = ctx.getDefinition("a");
        assertNotNull(def);
        assertEquals("a", def.getKey());
        assertEquals(Reflection.getOrCreateKotlinClass(String.class), def.getType());
        assertNull(def.getValue());
        assertEquals("define a", def.toString());

        Definition<Integer> intDef = new Definition<>("i", 123, Reflection.getOrCreateKotlinClass(Integer.class));
        assertEquals("i", intDef.getKey());
        assertEquals(123, intDef.getValue().intValue());
        assertEquals("define i 123", intDef.toString());
    }

    @Test
    public void children() {
        PreprocessorContext root = new PreprocessorContext(PreprocessorOptions.KT);
        PreprocessorContext sub1 = root.createChild();
        PreprocessorContext sub11 = sub1.createChild();
        PreprocessorContext sub2 = root.createChild();

        assertFalse(root.isDefined("a"));
        assertFalse(sub1.isDefined("a"));
        assertFalse(sub11.isDefined("a"));
        assertFalse(sub2.isDefined("a"));

        root.define("a");
        assertTrue(root.isDefined("a"));
        assertTrue(sub1.isDefined("a"));
        assertTrue(sub11.isDefined("a"));
        assertTrue(sub2.isDefined("a"));

        sub1.define("b");
        assertFalse(root.isDefined("b"));
        assertTrue(sub1.isDefined("b"));
        assertTrue(sub11.isDefined("b"));
        assertFalse(sub2.isDefined("b"));

        sub2.define("c");
        assertFalse(root.isDefined("c"));
        assertFalse(sub1.isDefined("c"));
        assertFalse(sub11.isDefined("c"));
        assertTrue(sub2.isDefined("c"));
    }
}
