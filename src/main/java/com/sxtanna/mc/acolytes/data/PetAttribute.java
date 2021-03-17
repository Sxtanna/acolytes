package com.sxtanna.mc.acolytes.data;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.configuration.ConfigurationSection;

import com.sxtanna.mc.acolytes.file.ObjectCodec;
import com.sxtanna.mc.acolytes.file.ObjectReader;
import com.sxtanna.mc.acolytes.file.ObjectWriter;

import java.util.function.Function;

import static com.google.common.base.Preconditions.checkNotNull;

public interface PetAttribute<T> extends ObjectCodec<T>
{

	@NotNull T getDefaultAttr();

	@NotNull T getCopiedValue(@NotNull final T value);

	@NotNull String getName();

	@NotNull Class<T> getType();

	boolean isMutable();


	@Contract(value = "_, _ -> new", pure = true)
	static <T> @NotNull Builder<T> builder(@NotNull final Class<T> type, @NotNull final String name)
	{
		return new Builder<>(name, type);
	}


	final class Builder<T>
	{

		@NotNull
		private final String name;

		@NotNull
		private final Class<T> type;

		@Nullable
		private T defaultAttr;

		private boolean mutable = true;

		@NotNull
		private Function<T, T> copy;

		@Nullable
		private ObjectReader<T> reader;

		@Nullable
		private ObjectWriter<T> writer;


		@Contract(pure = true)
		private Builder(final @NotNull String name, final @NotNull Class<T> type)
		{
			this.name = name;
			this.type = type;
			this.copy = Function.identity();
		}


		@Contract(value = "_ -> this", mutates = "this")
		public Builder<T> defaultAttr(@NotNull final T defaultAttr)
		{
			this.defaultAttr = defaultAttr;
			return this;
		}

		@Contract(value = "_ -> this", mutates = "this")
		public Builder<T> mutable(final boolean mutable)
		{
			this.mutable = mutable;
			return this;
		}

		@Contract(value = "_ -> this", mutates = "this")
		public Builder<T> copier(@NotNull final Function<T, T> copy)
		{
			this.copy = copy;
			return this;
		}

		@Contract(value = "_ -> this", mutates = "this")
		public Builder<T> reader(@NotNull final ObjectReader<T> reader)
		{
			this.reader = reader;
			return this;
		}

		@Contract(value = "_ -> this", mutates = "this")
		public Builder<T> writer(@NotNull final ObjectWriter<T> writer)
		{
			this.writer = writer;
			return this;
		}


		@Contract(value = " -> new", pure = true)
		public @NotNull PetAttribute<T> build()
		{
			checkNotNull(defaultAttr, "default attribute value must be provided!");

			return new PetAttribute<T>()
			{
				@Override
				public @NotNull T getDefaultAttr()
				{
					return defaultAttr;
				}

				@Override
				public @NotNull T getCopiedValue(@NotNull final T value)
				{
					return copy.apply(value);
				}

				@Override
				public @NotNull String getName()
				{
					return name;
				}

				@Override
				public @NotNull Class<T> getType()
				{
					return type;
				}

				@Override
				public boolean isMutable()
				{
					return mutable;
				}

				@Override
				public @NotNull T pull(final @NotNull ConfigurationSection yaml)
				{
					if (reader != null)
					{
						return reader.pull(yaml);
					}
					else
					{
						return getType().cast(yaml.get(getName()));
					}
				}

				@Override
				public void push(final @NotNull ConfigurationSection yaml, @NotNull final T value)
				{
					if (writer != null)
					{
						writer.push(yaml, value);
					}
					else
					{
						yaml.set(getName(), value);
					}
				}
			};
		}

	}

}