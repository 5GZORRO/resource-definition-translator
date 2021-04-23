package it.nextworks.sol006_tmf_translator.information_models.commons;

public class Pair<T, U> {
    private final T first;
    private final U second;

    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() { return first; }

    public U getSecond() { return second; }
}
