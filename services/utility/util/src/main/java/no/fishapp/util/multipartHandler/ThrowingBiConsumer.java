package no.fishapp.util.multipartHandler;

@FunctionalInterface
public interface ThrowingBiConsumer<T, U, E extends Exception> {
    void accept(T var1, U var2) throws E;

}
