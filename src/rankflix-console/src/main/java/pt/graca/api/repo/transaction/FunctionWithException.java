package pt.graca.api.repo.transaction;

import pt.graca.api.service.exceptions.RankflixException;

@FunctionalInterface
public interface FunctionWithException<T, R> {
    R apply(T t) throws RankflixException;
}

