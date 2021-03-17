package com.sxtanna.mc.acolytes.hook;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Drag and Drop Placeholder Replacement Wrapper
 *
 * @see Replace#set(OfflinePlayer, String)
 */
public final class Replace implements BiFunction<@Nullable OfflinePlayer, @NotNull String, @NotNull String>
{

    @NotNull
    private static final String PAPI_PLUGIN_NAME = "PlaceholderAPI";
    @NotNull
    private static final String MVDW_PLUGIN_NAME = "MVdWPlaceholderAPI";


    @Nullable
    private static Replace instance;

    /**
     * @return The singleton instance of {@link Replace}
     * 
     * @apiNote Lazily initializes a singleton instance of this wrapper,
     *          automatically registering the main two placeholder plugin integrations
     */
    public static @NotNull Replace get()
    {
        // check if singleton is initialized
        if (instance == null)
        {
            // create new instance
            instance = new Replace();

            // attempt to register placeholderapi
            instance.attemptToRegister(PAPI_PLUGIN_NAME,
                                       () -> me.clip.placeholderapi.PlaceholderAPI::setPlaceholders);

            // attempt to register mvdwplaceholderapi
            instance.attemptToRegister(MVDW_PLUGIN_NAME,
                                       () -> be.maximvdw.placeholderapi.PlaceholderAPI::replacePlaceholders);
        }

        // return cached instance
        return instance;
    }


    /**
     * Convenience function for setting the placeholders using the singleton {@link Replace} instance
     *
     * @see Replace#apply(OfflinePlayer, String)
     */
    @Contract(pure = true)
    public static @NotNull String set(@Nullable final OfflinePlayer player, @NotNull final String string)
    {
        return get().apply(player, string);
    }


    /**
     * A list of replacement functions, could technically be anything, but here we focus on these spigot plugins
     */
    @NotNull
    private final List<BiFunction<@Nullable OfflinePlayer, @NotNull String, @NotNull String>> replacers = new ArrayList<>();


    /**
     * Function for setting the placeholders using the stored replacer BiFunctions
     *
     * @param player The player to use as the context for the placeholders
     * @param string The string containing placeholders
     *                  
     * @return A new string containing the replaced values for applicable placeholders
     */
    @Contract(pure = true)
    @Override
    public @NotNull String apply(@Nullable final OfflinePlayer player, @NotNull final String string)
    {
        return this.replacers
                // create stream of replacer functions
                .stream()
                // reduce the collection of replacers using the input string as our identity, and each function as the accumulator
                .reduce(string,
                        (text, replacer) -> replacer.apply(player, text),
                        // this is kinda not what's supposed to happen as a combiner, this is not a parallel stream, so it doesn't matter anyway...
                        String::concat);
    }


    /**
     * Checks if the source plugin is enabled, and attempts to register it's replacement function
     *
     * @param name     The name of the source plugin
     * @param supplier A supplier that returns the replacement function
     */
    private void attemptToRegister(@NotNull final String name, @NotNull final Supplier<BiFunction<OfflinePlayer, String, String>> supplier)
    {
        // check if a plugin by this name is enabled
        if (!Bukkit.getPluginManager().isPluginEnabled(name))
        {
            // if not, do nothing, fail gracefully
            return;
        }

        try
        {
            // attempt to retrieve the replacer function from the supplier, and add it to our list
            this.replacers.add(supplier.get());
        }
        catch (final Throwable ignored /* most likely would be a NCDFE */)
        {
            // maybe pass in a logger?
        }
    }

}