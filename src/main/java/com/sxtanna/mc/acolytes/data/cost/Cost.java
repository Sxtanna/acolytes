package com.sxtanna.mc.acolytes.data.cost;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;

import com.sxtanna.mc.acolytes.AcolytesPlugin;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Objects;

public final class Cost implements Comparable<Cost>
{

	@NotNull
	public static final Cost FREE = new Cost(Type.VAULT, BigDecimal.ZERO);


	@NotNull
	public static final Comparator<Cost> COMPARE_BY_TYPE = Comparator.comparing(Cost::getType);
	@NotNull
	public static final Comparator<Cost> COMPARE_BY_COST = Comparator.comparing(Cost::getCost);
	@NotNull
	public static final Comparator<Cost> COMPARE_BY_BOTH = COMPARE_BY_TYPE.thenComparing(COMPARE_BY_COST);


	@NotNull
	private final Type       type;
	@NotNull
	private final BigDecimal cost;


	@Contract(pure = true)
	public Cost(@NotNull final Type type, @NotNull final BigDecimal cost)
	{
		this.type = type;
		this.cost = cost;
	}


	@Contract(pure = true)
	public @NotNull Type getType()
	{
		return type;
	}

	@Contract(pure = true)
	public @NotNull BigDecimal getCost()
	{
		return cost;
	}


	@Override
	public int compareTo(@NotNull final Cost that)
	{
		return COMPARE_BY_BOTH.compare(this, that);
	}


	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof Cost))
		{
			return false;
		}

		final Cost that = (Cost) o;
		return Objects.equals(this.getType(), that.getType()) &&
		       Objects.equals(this.getCost(), that.getCost());
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(this.getType(),
		                    this.getCost());
	}

	@Override
	public String toString()
	{
		return String.format("Cost[%s %s]",
		                     this.getCost(),
		                     this.getType());
	}


	public @NotNull String toStringFormatted(@NotNull final Player player)
	{
		return String.format("%s%s%s",
		                     AcolytesPlugin.get().lang(player, getType().getPrefix()),
		                     getCost().toPlainString(),
		                     AcolytesPlugin.get().lang(player, getType().getSuffix()));
	}

}