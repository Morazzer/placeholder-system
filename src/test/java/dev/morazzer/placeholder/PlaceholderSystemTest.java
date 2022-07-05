package dev.morazzer.placeholder;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class PlaceholderSystemTest {
    @BeforeAll
    public static void initialize() {
        PlaceholderSystem.initialize();
    }

    @Test
    public void testReplacementWithOneType() {
        String message = "Testing %replacement% and the %otherReplacement% replacement";
        String replaced = PlaceholderSystem.replacePlaceholders(String.class, message, new ReplacementOne());

        assert replaced.equals("Testing test replacement and the other replacement");
    }

    @Test
    public void testReplacementWithOneIgnore() {
        String message = "Testing %replacement% and %otherReplacement% replacement";
        String replaced = PlaceholderSystem.replacePlaceholders(String.class, message, new ReplacementTwo(), new ReplacementOne());

        assert replaced.equals("Testing test replacement and that replacement");
    }

    @Test
    public void testReplacementWithOneOverride() {
        String message = "Testing %replacement% and the %otherReplacement% replacement";
        String replaced = PlaceholderSystem.replacePlaceholders(String.class, message, new ReplacementOne(), new ReplacementThree());

        assert replaced.equals("Testing third replacement and the other replacement");
    }

    @Test
    public void testReplacementWithOneOverrideAndOneIgnore() {
        String message = "Testing %replacement% and the %otherReplacement% replacement";
        String replaced = PlaceholderSystem.replacePlaceholders(String.class, message,
                new ReplacementOne(), new ReplacementThree(), new ReplacementTwo());

        assert replaced.equals("Testing third replacement and the other replacement");
    }

    @Test
    public void testReplacementWithMap() {
        String message = "Testing %replacement% and the %otherReplacement% replacement";
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("replacement", "test");
        placeholders.put("otherReplacement", "that");
        String replaced = PlaceholderSystem.replacePlaceholders(String.class, message, placeholders);

        assert replaced.equals("Testing test and the that replacement");
    }

    static class ReplacementOne {
        public String replacement = "test replacement";
        public String otherReplacement = "other";
    }

    static class ReplacementTwo {
        @Placeholder(ignore = true)
        public String replacement = "second replacement";
        public String otherReplacement = "that";
    }

    public static class ReplacementThree {
        @Placeholder(overrideOthers = true)
        public String replacement = "third replacement";
        public String otherReplacement = "third";
    }
}
