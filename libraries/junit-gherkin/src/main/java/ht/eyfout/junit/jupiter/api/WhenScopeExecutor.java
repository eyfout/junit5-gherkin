package ht.eyfout.junit.jupiter.api;

import java.util.Optional;

public interface WhenScopeExecutor {
    default Optional<String> displayName(){
        return Optional.empty();
    }
    <R> R exec();
}
