package com.aiden.pvp;

/**
 * Platform-specific configuration for all registry registrations.
 * In NeoForge, all registrations must go through DeferredRegister/RegisterEvent,
 * so Registry.register() in static initializers must be skipped.
 * Set to true by the NeoForge mod constructor before any Mod* class loads.
 */
public final class ModRegistrationConfig {
    private ModRegistrationConfig() {}

    /**
     * When true, all Registry.register() calls in common static initializers
     * and initialize() methods will be skipped. The NeoForge mod will handle
     * registration via RegisterEvent/DeferredRegister instead.
     */
    public static boolean SKIP_REGISTRY_REGISTER = false;
}