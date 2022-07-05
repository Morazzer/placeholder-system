package dev.morazzer.placeholder;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderSystem {

    private static HashMap<Class<?>, PlaceholderConverter<?>> converters;
    private static final Pattern pattern = Pattern.compile("%([a-zA-Z\u005f\u0024]+)%"); // u005f is underscore and u0024 is dollar sign

    public static void initialize() {
        converters = new HashMap<>();
        registerConverter(String.class, string -> string);
    }

    public static <T> void registerConverter(Class<T> clazz, PlaceholderConverter<T> converter) {
        converters.put(clazz, converter);
    }

    public static <T> PlaceholderConverter<T> unregisterConverter(Class<T> clazz) {
        return (PlaceholderConverter<T>) converters.remove(clazz);
    }

    public static <T> T replacePlaceholders(Class<T> returnType, String string, Object... placeholders) {
        PlaceholderConverter<T> converter = (PlaceholderConverter<T>) converters.get(returnType);
        if (converter == null) {
            throw new IllegalArgumentException("No converter registered for class " + returnType.getName());
        }

        StringBuffer stringBuffer = new StringBuffer();
        Matcher matcher = pattern.matcher(string);

        while (matcher.find()) {
            String replacementString = null;
            String fieldName = matcher.group(1);

            for (Object placeholder : placeholders) {
                if (placeholder instanceof Map<?,?>) {
                    Map<?,?> placeholderMap = (Map<?,?>) placeholder;
                    if (placeholderMap.containsKey(fieldName)) {
                        replacementString = placeholderMap.get(fieldName).toString();
                        continue;
                    }
                }

                try {
                    Field field = placeholder.getClass().getField(fieldName);
                    boolean overrideOthers = false;
                    boolean ignore = false;
                    if (field.getAnnotation(Placeholder.class) != null) {
                        Placeholder placeholderAnnotation = field.getAnnotation(Placeholder.class);
                        overrideOthers = placeholderAnnotation.overrideOthers();
                        ignore = placeholderAnnotation.ignore();
                    }
                    if (ignore) {
                        continue;
                    }
                    if (!overrideOthers && replacementString != null) {
                        continue;
                    }
                    Object value = field.get(placeholder);

                    replacementString = value.toString();
                } catch (Exception exception) {
                    // Ignored
                }
            }

            if (replacementString == null) {
                replacementString = matcher.group(0);
                System.out.println("No replacement found for placeholder " + fieldName);
            }

            matcher.appendReplacement(stringBuffer, replacementString);
        }

        matcher.appendTail(stringBuffer);

        return converter.convert(stringBuffer.toString());
    }
}
