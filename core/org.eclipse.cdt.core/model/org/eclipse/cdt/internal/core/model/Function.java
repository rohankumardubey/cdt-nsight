package org.eclipse.cdt.internal.core.model;

/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */

import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.IFunction;

public class Function extends SourceManipulation implements IFunction {
	
	public Function(ICElement parent, String name) {
		super(parent, name, CElement.C_FUNCTION);
	}

	public String[] getExceptions() throws CModelException {
		return new String[] {};
	}

	public int getNumberOfParameters() {
		return 0;
	}

	public String getParameterInitializer(int pos) {
		return "";
	}

	public String[] getParameterTypes() {
		return new String[0];
	}

	public String getReturnType() throws CModelException {
		return  "";
	}

	public int getAccessControl() throws CModelException {
		return getFunctionInfo().getAccessControl();
	}

	public FunctionInfo getFunctionInfo() {
		return (FunctionInfo)getElementInfo();
	} 

	protected CElementInfo createElementInfo () {
		return new FunctionInfo(this);
	}
}
