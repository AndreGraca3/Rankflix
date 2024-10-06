package pt.graca.repo.transaction;

@FunctionalInterface
public interface FunctionThatReturnsVoid<T> {
    void apply(T t) throws Exception;
}
