package utils;

import spark.Request;

import java.io.File;
import java.util.Optional;

public final class General {

    private General() {
    }

    public static boolean isInsideContainer() {
        return new File("/.dockerenv").exists();
    }

    public static Optional<String> getRequestParameter(final Request request, final String key) {
        if (request.queryMap() == null
                || !request.queryMap().hasKey(key)
                || request.queryMap().value(key).trim().isEmpty()) return Optional.empty();

        return Optional.of(request.queryMap().value(key).trim());
    }
}
