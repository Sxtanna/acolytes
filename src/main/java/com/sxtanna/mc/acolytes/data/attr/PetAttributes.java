package com.sxtanna.mc.acolytes.data.attr;

import com.sxtanna.mc.acolytes.data.PetAttribute;

public enum PetAttributes
{
	;


	public static final PetAttribute<String> UUID = PetAttribute.make(String.class, "", false);
	public static final PetAttribute<String> NAME = PetAttribute.make(String.class, "");

}