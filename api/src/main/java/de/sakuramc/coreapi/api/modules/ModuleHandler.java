package de.sakuramc.coreapi.api.modules;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author Simon Stögerer
 * copyright - all rights reserved
 * created: 06.02.2025 - 22:01
 */

public class ModuleHandler {
    private final Map<Class<?>, Object> modules = new HashMap<>();

    public <T> void registerModule(Class<T> clazz, Supplier<T> supplier) {
        if (this.modules.containsKey(clazz)) {
            throw new IllegalArgumentException("Module already registered");
        }

        this.modules.put(clazz, supplier.get());
    }

    public <T> T module(Class<T> clazz) {
        final var module = this.modules.get(clazz);

        if (module == null) {
            throw new IllegalArgumentException("Module " + clazz.getSimpleName() + " not found");
        }

        return clazz.cast(module);
    }

    public void unregisterModule(Class<?> clazz) {
        this.modules.remove(clazz);
    }
}
