package com.sxtanna.mc.acolytes.menu;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.sxtanna.mc.acolytes.AcolytesPlugin;
import com.sxtanna.mc.acolytes.base.State;
import com.sxtanna.mc.acolytes.util.bukkit.Stacks;

import com.google.common.collect.Maps;

import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class Menu implements State, Listener, InventoryHolder
{

	private boolean loaded;

	@NotNull
	private final Size   size;
	@NotNull
	private final String name;


	@Nullable
	private Inventory inventory;

	@NotNull
	private final Map<Integer, Consumer<InventoryClickEvent>> consumers = new HashMap<>();


	protected Menu(@NotNull final Size size, @NotNull final String name)
	{
		this.size = size;
		this.name = name;
	}


	@Override
	public final @NotNull Inventory getInventory()
	{
		if (this.inventory == null)
		{
			this.inventory = Bukkit.getServer().createInventory(this, this.size.bukkitSize(), this.name);
		}

		return this.inventory;
	}


	public final @NotNull Map<Integer, Consumer<InventoryClickEvent>> getConsumers()
	{
		return this.consumers;
	}


	@Override
	public final void load()
	{
		if (this.loaded)
		{
			return;
		}

		this.loaded = true;

		Bukkit.getServer().getPluginManager().registerEvents(this, AcolytesPlugin.get());
	}

	@Override
	public final void kill()
	{
		if (!this.loaded)
		{
			return;
		}

		this.loaded = false;

		HandlerList.unregisterAll(this);

		getConsumers().clear();
		getInventory().clear();
	}


	public void open(@NotNull final HumanEntity entity)
	{
		if (entity instanceof Player)
		{
			opened(((Player) entity));
		}

		if (!this.loaded)
		{
			load();
			make();
		}

		entity.openInventory(getInventory());
	}

	protected void make()
	{

	}

	protected void redo()
	{
		getConsumers().clear();
		getInventory().clear();


		final List<HumanEntity> viewers = getInventory().getViewers();
		if (viewers.isEmpty())
		{
			return;
		}

		final HumanEntity entity = viewers.get(0);
		if (entity instanceof Player)
		{
			opened(((Player) entity));
		}

		make();
	}


	protected void opened(@NotNull final Player player)
	{

	}

	protected void closed(@NotNull final Player player)
	{

	}

	protected void fallback(@NotNull final InventoryClickEvent event)
	{

	}


	protected final void slot(final int slot, @NotNull final ItemStack item)
	{
		getInventory().setItem(slot, item);
	}

	protected final void slot(final int slot, @NotNull final Consumer<InventoryClickEvent> consumer)
	{
		getConsumers().put(slot, consumer);
	}

	protected final void slot(final int slot, @NotNull final ItemStack item, @NotNull final Consumer<InventoryClickEvent> consumer)
	{
		getInventory().setItem(slot, item);
		getConsumers().put(slot, consumer);
	}


	protected final void grid(@NotNull final Rows row, @NotNull final Cols col, @NotNull final ItemStack item)
	{
		slot(Grid.fromGrid(row, col), item);
	}

	protected final void grid(@NotNull final Rows row, @NotNull final Cols col, @NotNull final Consumer<InventoryClickEvent> consumer)
	{
		slot(Grid.fromGrid(row, col), consumer);
	}

	protected final void grid(@NotNull final Rows row, @NotNull final Cols col, @NotNull final ItemStack item, @NotNull final Consumer<InventoryClickEvent> consumer)
	{
		grid(row, col, item);
		grid(row, col, consumer);
	}


	protected final void fill(@NotNull final ItemStack item)
	{
		Grid.iter().forEachRemaining(rac -> {
			if (rac.row.ordinal() > this.size.ordinal())
			{
				return;
			}

			if (getInventory().getItem(rac.slot()) == null)
			{
				grid(rac.row, rac.col, item);
			}
		});
	}

	protected final void fill(@NotNull final Rows row, @NotNull final ItemStack item)
	{
		Cols.iter().forEachRemaining(col -> {
			if (getInventory().getItem(Grid.fromGrid(row, col)) == null)
			{
				grid(row, col, item);
			}
		});
	}

	protected final void fill(@NotNull final Cols col, @NotNull final ItemStack item)
	{
		Rows.iter().forEachRemaining(row -> {
			if (row.ordinal() > this.size.ordinal())
			{
				return;
			}

			if (getInventory().getItem(Grid.fromGrid(row, col)) == null)
			{
				grid(row, col, item);
			}
		});
	}

	protected final void border(@NotNull final ItemStack item)
	{
		final Cols minCol = Cols.C_1;
		final Cols maxCol = Cols.C_1;
		final Rows minRow = Rows.R_1;
		final Rows maxRow = Rows.values()[this.size.ordinal()];

		fill(minCol, item);
		fill(maxCol, item);
		fill(minRow, item);
		fill(maxRow, item);
	}


	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public final void onClick(@NotNull final InventoryClickEvent event)
	{
		if (event.getView().getTopInventory().getHolder() != this)
		{
			// if we aren't the holder, do nothing
			return;
		}

		if (event.getSlotType() == InventoryType.SlotType.OUTSIDE)
		{
			// if they click outside of the inventory, or the slot the click isn't in this inventory, do nothing
			return;
		}

		event.setCancelled(true);

		final Consumer<InventoryClickEvent> consumer = consumers.get(event.getRawSlot());
		if (consumer != null)
		{
			consumer.accept(event);
		}
		else
		{
			fallback(event);
		}
	}

	@EventHandler
	public final void onClose(@NotNull final InventoryCloseEvent event)
	{
		if (event.getView().getTopInventory().getHolder() != this)
		{
			// if we aren't the holder, do nothing
			return;
		}


		if (event.getPlayer() instanceof Player)
		{
			closed(((Player) event.getPlayer()));
		}

		if (getInventory().getViewers().size() > 1)
		{
			// if there is more than one player viewing this inventory, do nothing
			return;
		}

		kill();
	}


	public enum Size
	{
		ROWS_1,
		ROWS_2,
		ROWS_3,
		ROWS_4,
		ROWS_5,
		ROWS_6;


		@NotNull
		public static final Size[] SIZES = values();


		@Contract(pure = true)
		public @Range(from = 0, to = 54) int bukkitSize()
		{
			return (ordinal() + 1) * 9;
		}


		@Contract(pure = true)
		public static @NotNull Size forCount(@Range(from = 1, to = 6) final int count)
		{
			return SIZES[count - 1];
		}

		@Contract(pure = true)
		public static @NotNull Size minFor(@Range(from = 0, to = 53) final int items)
		{
			int ordinal = 0;

			while (ordinal < SIZES.length && items > SIZES[ordinal].bukkitSize())
			{
				ordinal++;
			}

			return SIZES[ordinal];
		}

	}

	// @formatter:off
	/**
	 * <pre>
	 *     C_1 C_2 C_3 C_4 C_5 C_6 C_7 C_8 C_9
	 *    ╔═══╦═══╦═══╦═══╦═══╦═══╦═══╦═══╦═══╗
	 * R_1║  0║  1║  2║  3║  4║  5║  6║  7║  8║ Slots  0..8
	 *    ╠═══╬═══╬═══╬═══╬═══╬═══╬═══╬═══╬═══╣
	 * R_2║  9║ 10║ 11║ 12║ 13║ 14║ 15║ 16║ 17║ Slots  9..17
	 *    ╠═══╬═══╬═══╬═══╬═══╬═══╬═══╬═══╬═══╣
	 * R_3║ 18║ 19║ 20║ 21║ 22║ 23║ 24║ 25║ 26║ Slots 18..26
	 *    ╠═══╬═══╬═══╬═══╬═══╬═══╬═══╬═══╬═══╣
	 * R_4║ 27║ 28║ 29║ 30║ 31║ 32║ 33║ 34║ 35║ Slots 27..35
	 *    ╠═══╬═══╬═══╬═══╬═══╬═══╬═══╬═══╬═══╣
	 * R_5║ 36║ 37║ 38║ 39║ 40║ 41║ 42║ 43║ 44║ Slots 36..44
	 *    ╠═══╬═══╬═══╬═══╬═══╬═══╬═══╬═══╬═══╣
	 * R_6║ 45║ 46║ 47║ 48║ 49║ 50║ 51║ 52║ 53║ Slots 45..53
	 *    ╚═══╩═══╩═══╩═══╩═══╩═══╩═══╩═══╩═══╝
	 * </pre>
	 */
	// @formatter:on
	public enum Grid
	{
		;

		@Contract(value = "_ -> new", pure = true)
		public static @NotNull RaC fromSlot(@Range(from = 0, to = 54) final int slot)
		{
			return new RaC(Rows.ROWS[slot / 9], Cols.COLS[slot % 9]);
		}


		public static @Range(from = 0, to = 54) int fromGrid(@NotNull final Rows row, @NotNull final Cols col)
		{
			return (row.ordinal() * 9) + col.ordinal();
		}


		@Contract(value = " -> new", pure = true)
		public static @NotNull Iterator<RaC> iter()
		{
			return new Iterator<RaC>()
			{

				private int colsIndex = 0;
				private int rowsIndex = 0;

				private final List<Cols> cols = Cols.list();
				private final List<Rows> rows = Rows.list();


				@Override
				public boolean hasNext()
				{
					return rowsIndex < rows.size();
				}


				@Override
				public RaC next()
				{
					final Cols col = this.cols.get(colsIndex);
					final Rows row = this.rows.get(rowsIndex);

					if (++colsIndex == this.cols.size())
					{
						colsIndex = 0;
						rowsIndex = rowsIndex + 1;
					}

					return new RaC(row, col);
				}

			};
		}


		@Contract(" -> new")
		public static @NotNull Stream<RaC> stream()
		{
			return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iter(), Spliterator.ORDERED), false);
		}


		public static final class RaC
		{

			@NotNull
			public final Rows row;
			@NotNull
			public final Cols col;


			private RaC(@NotNull final Rows row, @NotNull final Cols col)
			{
				this.row = row;
				this.col = col;
			}


			public @Range(from = 0, to = 53) int slot()
			{
				return Grid.fromGrid(this.row, this.col);
			}

		}

	}


	public enum Rows
	{
		R_1,
		R_2,
		R_3,
		R_4,
		R_5,
		R_6;


		@NotNull
		public static final Rows[] ROWS = values();


		@Contract(value = " -> new", pure = true)
		public static @NotNull List<Rows> list()
		{
			return Arrays.asList(ROWS);
		}

		public static @NotNull ListIterator<Rows> iter()
		{
			return list().listIterator();
		}

	}


	public enum Cols
	{
		C_1,
		C_2,
		C_3,
		C_4,
		C_5,
		C_6,
		C_7,
		C_8,
		C_9;


		@NotNull
		public static final Cols[] COLS = values();


		@Contract(value = " -> new", pure = true)
		public static @NotNull List<Cols> list()
		{
			return Arrays.asList(COLS);
		}

		public static @NotNull ListIterator<Cols> iter()
		{
			return list().listIterator();
		}

	}


	@FunctionalInterface
	public interface MenuCreationFunction
	{

		void accept(@NotNull final Menu menu, @NotNull final ItemStack item, @NotNull final Consumer<InventoryClickEvent> event);

	}


	public static class MenuButton
	{

		public static @NotNull Builder builder()
		{
			return new Builder();
		}


		@Nullable
		private Material type;

		private int data;

		@Nullable
		private String name;

		@Nullable
		private List<String> lore;


		public MenuButton()
		{ }

		public MenuButton(@Nullable final Material type, final int data, @Nullable final String name, @Nullable final List<String> lore)
		{
			this.type = type;
			this.data = data;
			this.name = name;
			this.lore = lore;
		}

		public @Nullable Material getType()
		{
			return type;
		}

		public void setType(@NotNull final Material type)
		{
			this.type = type;
		}

		public int getData()
		{
			return data;
		}

		public void setData(final int data)
		{
			this.data = data;
		}

		public @Nullable String getName()
		{
			return name;
		}

		public void setName(@NotNull final String name)
		{
			this.name = name;
		}

		public @Nullable List<String> getLore()
		{
			return lore;
		}

		public void setLore(@NotNull final List<String> lore)
		{
			this.lore = lore;
		}


		public @NotNull ItemStack getItemStack()
		{
			if (this.type == null)
			{
				return new ItemStack(Material.AIR);
			}

			return Stacks.item(this.type, 1, this.data, meta ->
			{
				if (this.name != null)
				{
					Stacks.name(meta, this.name);
				}

				if (this.lore != null)
				{
					Stacks.lore(meta, this.lore);
				}
			});
		}


		public static final class Builder
		{

			@Nullable
			private Material     type;
			private int          data;
			@Nullable
			private String       name;
			@Nullable
			private List<String> lore;


			public Builder type(@NotNull final Material type)
			{
				this.type = type;
				return this;
			}

			public Builder data(final int data)
			{
				this.data = data;
				return this;
			}

			public Builder name(@NotNull final String name)
			{
				this.name = name;
				return this;
			}

			public Builder lore(@NotNull final List<String> lore)
			{
				this.lore = lore;
				return this;
			}

			public Builder lore(@NotNull final String... lore)
			{
				return lore(Arrays.asList(lore));
			}


			public @NotNull MenuButton build()
			{
				final MenuButton button = new MenuButton();

				button.type = this.type;
				button.data = this.data;
				button.name = this.name;
				button.lore = this.lore;

				return button;
			}

		}

	}

	public static final class MenuDecoded
	{

		@NotNull
		private final Size                                       requiredSize;
		@NotNull
		private final Map<Character, List<MenuCreationFunction>> creationData;


		private MenuDecoded(@NotNull final Size requiredSize, @NotNull final Map<Character, List<MenuCreationFunction>> creationData)
		{
			this.requiredSize = requiredSize;
			this.creationData = creationData;
		}


		@Contract(pure = true)
		public @NotNull Size getRequiredSize()
		{
			return this.requiredSize;
		}

		@Contract(pure = true)
		public @NotNull Map<Character, List<MenuCreationFunction>> getCreationData()
		{
			return this.creationData;
		}

	}


	@NotNull
	private static final Pattern CLEANSE = Pattern.compile("([═╔╦╗╚╩╝╬╣])|^(║ )|( ║)$", Pattern.MULTILINE);


	public static @NotNull Optional<MenuDecoded> decode(@NotNull final String encoded)
	{
		try
		{
			final Map<Character, List<MenuCreationFunction>> create = Maps.newHashMap();

			final String cleaned = CLEANSE.matcher(encoded).replaceAll("").replaceAll(" ", "");

			final Menu.Rows[] rowValues = Menu.Rows.ROWS;
			final Menu.Cols[] colValues = Menu.Cols.COLS;

			int index = 0;

			int rowIndex = 0;
			int colIndex = 0;

			while (index < cleaned.length())
			{
				if (rowIndex >= rowValues.length)
				{
					throw new IllegalArgumentException("too many rows defined for menu");
				}

				char c = cleaned.charAt(index++);

				switch (c)
				{
					case '║':
						colIndex++;
						continue;
					case '╠':
						rowIndex++;
						colIndex = 0;
						continue;
					case '\n':
						continue;
					default:
						final Cols col = colValues[colIndex];
						final Rows row = rowValues[rowIndex];

						create.computeIfAbsent(c, ArrayList::new).add((menu, item, event) -> menu.grid(row, col, item, event));
				}
			}

			if (rowIndex > Size.values().length)
			{
				rowIndex = 0;
			}

			return Optional.of(new MenuDecoded(Size.values()[rowIndex], create));
		}
		catch (final IllegalArgumentException ex)
		{
			ex.printStackTrace();

			return Optional.empty();
		}
	}

}