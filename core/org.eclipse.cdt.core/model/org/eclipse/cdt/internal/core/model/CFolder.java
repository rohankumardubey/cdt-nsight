package org.eclipse.cdt.internal.core.model;

/*
 * (c) Copyright QNX Software Systems Ltd. 2002.
 * All Rights Reserved.
 */
 
import org.eclipse.core.resources.IFolder;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICFolder;
import org.eclipse.cdt.core.model.CModelException;

public class CFolder extends CResource implements ICFolder {

	public CFolder (ICElement parent, IFolder folder) {
		super (parent, folder, ICElement.C_FOLDER);
	}

	public IFolder getFolder () {
		try {
			return (IFolder)getUnderlyingResource();
		} catch (CModelException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected CElementInfo createElementInfo () {
		return new CFolderInfo(this);
	}
}
