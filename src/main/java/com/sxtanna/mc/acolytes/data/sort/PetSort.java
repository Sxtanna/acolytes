package com.sxtanna.mc.acolytes.data.sort;

import org.jetbrains.annotations.NotNull;

import com.sxtanna.mc.acolytes.data.Pet;
import com.sxtanna.mc.acolytes.data.attr.PetAttributes;
import com.sxtanna.mc.acolytes.data.cost.Cost;

import java.util.Comparator;
import java.util.Optional;

public enum PetSort implements Comparator<Pet>
{
	NAME
			{
				@Override
				public int compare(@NotNull final Pet thisPet, @NotNull final Pet thatPet)
				{
					final Optional<String> thisPetName = thisPet.select(PetAttributes.NAME);
					final Optional<String> thatPetName = thatPet.select(PetAttributes.NAME);

					if (thisPetName.isPresent() && thatPetName.isPresent())
					{
						return thisPetName.get().compareTo(thatPetName.get());
					}

					return thisPet.getUuid().compareTo(thatPet.getUuid());
				}
			},
	COST
			{
				@Override
				public int compare(@NotNull final Pet thisPet, @NotNull final Pet thatPet)
				{
					final Optional<Cost> thisPetCost = thisPet.select(PetAttributes.COST);
					final Optional<Cost> thatPetCost = thatPet.select(PetAttributes.COST);

					if (thisPetCost.isPresent() && thatPetCost.isPresent())
					{
						return thisPetCost.get().compareTo(thatPetCost.get());
					}

					return thisPetCost.isPresent() ? 1 : -1;
				}
			};


	@Override
	public abstract int compare(@NotNull final Pet thisPet, @NotNull final Pet thatPet);

}