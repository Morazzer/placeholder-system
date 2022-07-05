package dev.morazzer.placeholder;

public interface PlaceholderConverter <T> {

    T convert(String string);

}
