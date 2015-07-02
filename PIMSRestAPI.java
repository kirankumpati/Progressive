package com.triumvir.progressive;

import java.util.Set;

import sailpoint.rest.SailPointRestApplication;
import sailpoint.tools.GeneralException;

public class PIMSRestAPI extends SailPointRestApplication {

	public PIMSRestAPI() throws GeneralException{
		
		super();
		
		
	}
	@Override
	public Set<Class<?>> getClasses() {
		
		Set<Class<?>> classes= super.getClasses();
		classes.add(PIMSPasswordManagementResource.class);
		return classes;
		
		
	}
	
	
	
}
