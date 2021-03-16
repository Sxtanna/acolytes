package com.sxtanna.mc.acolytes.conf.mapper;

import ch.jalu.configme.beanmapper.MapperImpl;
import ch.jalu.configme.beanmapper.leafvaluehandler.CombiningLeafValueHandler;
import ch.jalu.configme.beanmapper.leafvaluehandler.StandardLeafValueHandlers;
import ch.jalu.configme.beanmapper.propertydescription.BeanDescriptionFactoryImpl;

public final class MenuButtonMapper extends MapperImpl
{

	public MenuButtonMapper()
	{
		super(new BeanDescriptionFactoryImpl(),
		      new CombiningLeafValueHandler(StandardLeafValueHandlers.getDefaultLeafValueHandler(),
		                                    new MenuButtonLeafHandler()));
	}

}