package com.sxtanna.mc.acolytes.data;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;

import com.sxtanna.mc.acolytes.file.ObjectCodec;
import com.sxtanna.mc.acolytes.file.ObjectReader;
import com.sxtanna.mc.acolytes.file.ObjectWriter;

import java.util.function.Function;

public interface PetAttribute<T> extends ObjectCodec<T>
{

	@NotNull T getDefaultAttr();

	@NotNull T getCopiedValue(@NotNull final T value);

	@NotNull String getName();

	@NotNull Class<T> getType();

	boolean isMutable();


	static <T> PetAttribute.Builder<T> builder(@NotNull final Class<T> type, @NotNull final String name)
	{
		return new Builder<>(name, type);
	}


	class Builder<T>
	{

		private final String name;

		private final Class<T> type;

		private T defaultAttr;

		private boolean mutable = true;

		private Function<T, T> copy;

		private ObjectReader<T> reader;

		private ObjectWriter<T> writer;


		private Builder(final String name, final Class<T> type)
		{
			this.name = name;
			this.type = type;
			this.copy = Function.identity();
		}


		public Builder<T> defaultAttr(@NotNull final T defaultAttr)
		{
			this.defaultAttr = defaultAttr;
			return this;
		}

		public Builder<T> mutable(final boolean mutable)
		{
			this.mutable = mutable;
			return this;
		}

		public Builder<T> copier(final Function<T, T> copy)
		{
			this.copy = copy;
			return this;
		}

		public Builder<T> reader(final ObjectReader<T> reader)
		{
			this.reader = reader;
			return this;
		}

		public Builder<T> writer(final ObjectWriter<T> writer)
		{
			this.writer = writer;
			return this;
		}


		public PetAttribute<T> build()
		{
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