package dev.morazzer.placeholder;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class PlaceholderConverterTest {

    @BeforeAll
    public static void initialize() {
        PlaceholderSystem.initialize();
    }

    @Test
    public void testConversionToCustomReturnType() {
        String message = "Testing conversion to custom return type";
        PlaceholderSystem.registerConverter(TestClass.class, TestClass::new);
        TestClass testClass = PlaceholderSystem.replacePlaceholders(TestClass.class, message);

        assert testClass != null;
        assert testClass.string.equals("Testing conversion to custom return type");
    }


    private static class TestClass {
        public String string;
        public TestClass(String string) {
            this.string = string;
        }
    }
}
