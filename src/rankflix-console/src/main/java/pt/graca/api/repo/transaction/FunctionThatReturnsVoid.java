package pt.graca.api.repo.transaction;

@FunctionalInterface
public interface FunctionThatReturnsVoid<T> {
    void apply(T t) throws Exception;
}
