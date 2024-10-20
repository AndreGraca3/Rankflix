package pt.graca.api.repo.transaction;

import pt.graca.api.service.exceptions.RankflixException;

@FunctionalInterface
public interface FunctionThatReturnsVoid<T> {
    void apply(T t) throws RankflixException;
}
