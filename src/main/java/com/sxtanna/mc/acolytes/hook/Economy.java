package com.sxtanna.mc.acolytes.hook;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Drag and Drop Vault Economy Wrapper
 *
 * @see Response
 * @see <a href="https://gist.github.com/Sxtanna/af4f01d21cd5c35ce047a9e64a0dab48">Gist Source</a>
 */
public final class Economy
{

    /**
     * Represents a transaction response from Vault
     *
     * @see Response#getResult()
     * @see Response#getReason()
     */
    public static final class Response
    {

        /**
         * Represents whether the result of the transaction was passing or failing
         */
        public enum Result
        {
            PASS,
            FAIL,
        }


        @NotNull
        private final Result result;
        @NotNull
        private final String reason;


        @Contract(pure = true)
        private Response(@NotNull final Result result, @NotNull final String reason)
        {
            this.result = result;
            this.reason = reason;
        }


        /**
         * @return Whether the transaction passed or failed
         */
        @Contract(pure = true)
        public @NotNull Result getResult()
        {
            return result;
        }

        /**
         * @return The reason the transaction failed, if it failed...
         */
        @Contract(pure = true)
        public @NotNull String getReason()
        {
            return reason;
        }


        /**
         * @return true if this transaction was successful, false otherwise
         */
        @Contract(pure = true)
        public boolean isPassing()
        {
            return getResult() == Result.PASS;
        }

        /**
         * @return true if this transaction failed, false otherwise
         */
        @Contract(pure = true)
        public boolean isFailing()
        {
            return getResult() == Result.FAIL;
        }


        /**
         * Convenience function for handling the state of this response
         *
         * @param passing What to do if this transaction was successful
         * @param failing What to do if this transaction failed
         */
        public void handle(@NotNull final Runnable passing, @NotNull final Consumer<String> failing)
        {
            // switch over the possible results
            switch (getResult())
            {
                case PASS:
                    // run the passing runnable when this is a passing response
                    passing.run();
                    break;
                case FAIL:
                    // provide the reason to the failing consumer when this is a failing response
                    failing.accept(getReason());
                    break;
                default:
                    // throw an exception when no applicable case is found (should not happen)
                    throw new UnsupportedOperationException(String.format("unexpected response result: %s?", getResult()));
            }
        }


        /**
         * Creates a new response denoting a successful transaction
         *
         * @return The new response holding {@link Result#PASS}
         */
        @Contract(value = " -> new", pure = true)
        public static @NotNull Response passing()
        {
            return new Response(Result.PASS, ""); /* todo: this could probably be a single instance, like Optional.empty()*/
        }

        /**
         * Creates a new response denoting a failing transaction
         *
         * @param reason The reason the transaction failed
         * @return The new response holding {@link Result#FAIL} and a Reason
         */
        @Contract(value = "_ -> new", pure = true)
        public static @NotNull Response failing(@NotNull final String reason)
        {
            return new Response(Result.FAIL, reason);
        }

    }


    /**
     * Attempts to retrieve the current balance of the supplied {@link OfflinePlayer}
     *
     * @param player The player whose balance to retrieve.
     * @return An {@link Optional} containing the player's balance if available, otherwise empty.
     */
    public static @NotNull Optional<BigDecimal> get(@NotNull final OfflinePlayer player)
    {
        try
        {
            // attempt to retrieve the registered provider for vault's economy, will either return null or throw NCDFE
            final RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> registration = Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            if (registration == null)
            {
                // this will be null when there is no economy plugin, simply return an empty optional
                return Optional.empty();
            }

            // wrap the result of their current balance in a big decimal, because using floating point values for money is DUMB
            return Optional.of(BigDecimal.valueOf(registration.getProvider().getBalance(player)));
        }
        catch (final NoClassDefFoundError ignored)
        {
            // this error will be thrown if vault is not loaded on this server, simply return an empty optional
            return Optional.empty();
        }
    }


    /**
     * Attempts to take the supplied amount of money from the player's balance. (withdraw)
     *
     * @param player The player whose balance to take from
     * @param amount The amount of money to take from them
     * @return A response denoting the outcome of this withdrawal attempt
     */
    public static @NotNull Response take(@NotNull final OfflinePlayer player, @NotNull final BigDecimal amount)
    {
        try
        {
            // attempt to retrieve the registered provider for vault's economy, will either return null or throw NCDFE
            final RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> registration = Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            if (registration == null)
            {
                // this will be null when there is no economy plugin, simply return an "no economy" response
                return Response.failing("no economy");
            }

            // attempt to withdraw the double value of the provided BigDecimal amount
            final net.milkbowl.vault.economy.EconomyResponse response = registration.getProvider().withdrawPlayer(player, amount.doubleValue());

            // return either a passing response, or a failing response containing the error message
            return response.transactionSuccess() ? Response.passing() : Response.failing(response.errorMessage);
        }
        catch (final NoClassDefFoundError error)
        {
            // return a failing response with the NCDFE message (probably not ideal? maybe just say "no vault")
            return Economy.Response.failing("no economy");
        }
    }

    /**
     * Attempts to give the supplied amount of money to the player. (deposit)
     *
     * @param player The player whose balance to give to
     * @param amount The amount of money to give them
     * @return A response denoting the outcome of this deposit attempt
     */
    public static @NotNull Response give(@NotNull final OfflinePlayer player, @NotNull final BigDecimal amount)
    {
        try
        {
            // attempt to retrieve the registered provider for vault's economy, will either return null or throw NCDFE
            final RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> registration = Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            if (registration == null)
            {
                // this will be null when there is no economy plugin, simply return an "no economy" response
                return Response.failing("no economy");
            }

            // attempt to deposit the double value of the provided BigDecimal amount
            final net.milkbowl.vault.economy.EconomyResponse response = registration.getProvider().depositPlayer(player, amount.doubleValue());

            // return either a passing response, or a failing response containing the error message
            return response.transactionSuccess() ? Response.passing() : Response.failing(response.errorMessage);
        }
        catch (final NoClassDefFoundError error)
        {
            // return a failing response with the NCDFE message (probably not ideal? maybe just say "no vault")
            return Economy.Response.failing("no economy");
        }
    }

}