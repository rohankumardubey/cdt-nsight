package org.eclipse.cdt.core.model;

/*
 * (c) Copyright QNX Software Systems Ltd. 2002.
 * All Rights Reserved.
 */
 
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IAdaptable;

/**
 * Common protocol for all elements provided by the C model.
 */
public interface ICElement extends IAdaptable {

	/**
	 * IResource from 10-20
	 */ 

	/**
	 * Constant representing a C Root workspace (IWorkspaceRoot object).
	 * A C element with this type can be safely cast to <code>ICRoot</code>.
	 */
	public static final int C_ROOT = 10;

	/**
	 * Constant representing a C project(IProject object).
	 * A C element with this type can be safely cast to <code>ICProject</code>.
	 */
	public static final int C_PROJECT = 11;

	/**
	 * Constant representing a folder(ICFolder object).
	 * A C element with this type can be safely cast to <code>ICFolder</code>.
	 */
	public static final int C_FOLDER = 12;

	/**
	 * Constant representing a file(ICFile object).
	 * A C element with this type can be safely cast to <code>ICFile</code>.
	 */
	public static final int C_FILE = 13;

	/**
	 * Virtual container serving as a place holder.
	 */
	public static final int C_CONTAINER = 30;

	/**
	 * Constant representing a C/C++ children of a Translation Unit
	 */
	public static final int C_UNIT = 60;

	/**
	 * Namespace.
	 */
	public static final int C_NAMESPACE = 61;

	/**
	 * Using.
	 */
	public static final int C_USING = 62;

	/**
	 * Enumeration.
	 */
	public static final int C_ENUMERATION = 63;

	/**
	 * Constant representing a class structure.
	 */
	public static final int C_CLASS = 64;

	/**
	 * Constant representing a struct structure.
	 */
	public static final int C_STRUCT = 65;

	/**
	 * Constant representing a union structure.
	 */
	public static final int C_UNION = 66;

	/**
	 * A method definition part of a structure(class, struct, union).
	 */
	public static final int C_METHOD = 67;

	/**
	 * A method declaration part of a structure(class, struct, union).
	 */
	public static final int C_METHOD_DECLARATION = 68;

	/**
	 * A Field definition part of a structure(class, struct, union).
	 */
	public static final int C_FIELD = 69;

	/**
	 * a C/C++ function prototype.
	 */
	public static final int C_FUNCTION_DECLARATION = 70;

	/**
	 * a C/C++ function.
	 */
	public static final int C_FUNCTION = 71;

	/**
	 * Preprocessor #include directive.
	 */
	public static final int C_INCLUDE = 72;

	/**
	 * C++ template class.
	 */
	public static final int C_TEMPLATE = 73;

	/**
	 * Global variable.
	 */
	public static final int C_VARIABLE = 74;

	/**
	 * variable Declaration.
	 */
	public static final int C_VARIABLE_DECLARATION = 75;

	/**
	 * Local Variable.
	 */
	public static final int C_VARIABLE_LOCAL = 76;

	/**
	 * A preprocessor macro.
	 */
	public static final int C_MACRO = 77;

	/**
	 * a Typedef.
	 */
	public static final int C_TYPEDEF = 78;
	
	/**
	 * Modifier indicating a class constructor
	 */
	public static final int C_CLASS_CTOR = 0x100;
	
	/**
	 * Modifier indicating a class destructor
	 */
	public static final int C_CLASS_DTOR = 0x200;
		
	/**
	 * Modifier indicating a static storage attribute
	 */
	public static final int C_STORAGE_STATIC = 0x400;
		
	/**
	 * Modifier indicating an extern storage attribute
	 */
	public static final int C_STORAGE_EXTERN = 0x800;

	/**
	 * Modifier indicating a private class
	 */
	public static final int CPP_PRIVATE = 0x1000;

	/**
	 * Modifier indicating a public class
	 */

	public static final int CPP_PUBLIC = 0x2000;

	/**
	 * Modifier indicating a friend class
	 */
	public static final int CPP_FRIEND = 0x4000;

	/**
	 * Returns whether this C element exists in the model.
	 *
	 * @return <code>true</code> if this element exists in the C model
	 */
	boolean exists();

	/**
	 * Returns the resource that corresponds directly to this element,
	 * or <code>null</code> if there is no resource that corresponds to
	 * this element.
	 * <p>
	 * For example, the corresponding resource for an <code>ATranslationUnit</code>
	 * is its underlying <code>IFile</code>.
	 *
	 * @return the corresponding resource, or <code>null</code> if none
	 * @exception CModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource
	 */
	IResource getCorrespondingResource() throws CModelException;

	/**
	 * Returns the name of this element.
	 *
	 * @return the element name
	 */
	String getElementName();

	/**
	 * Returns this element's kind encoded as an integer.
	 * This is a handle-only method.
	 *
	 * @return the kind of element; one of the constants declared in
	 *   <code>ICElement</code>
	 * @see ICElement
	 */
	public int getElementType();

	/**
	 * Returns the C model.
	 *
	 * @return the C model
	 */
	ICRoot getCRoot();

	/**
	 * Returns the C project this element is contained in,
	 * or <code>null</code> if this element is not contained in any C project
	 *
	 * @return the containing C project, or <code>null</code> if this element is
	 *   not contained in a C project
	 */
	ICProject getCProject();

	/**
	 * Returns the element directly containing this element,
	 * or <code>null</code> if this element has no parent.
	 *
	 * @return the parent element, or <code>null</code> if this element has no parent
	 */
	ICElement getParent();

	/**
	 * Returns the path to the innermost resource enclosing this element. 
	 * If this element is not included in an external archive, 
	 * the path returned is the full, absolute path to the underlying resource, 
	 * relative to the workbench. 
	 * If this element is included in an external archive, 
	 * the path returned is the absolute path to the archive in the file system.
	 * This is a handle-only method.
	 * 
	 */
	IPath getPath();

	/**
	 * Returns the underlying resource that contains
	 * this element, or <code>null</code> if this element is not contained
	 * in a resource.
	 *
	 * @return the underlying resource, or <code>null</code> if none
	 * @exception CModelException if this element does not exist or if an
	 *		exception occurs while accessing its underlying resource
	 */
	IResource getUnderlyingResource() throws CModelException;

	/**
	 * Returns whether this C element is read-only. An element is read-only
	 * if its structure cannot be modified by the C model. 
	 *
	 * @return <code>true</code> if this element is read-only
	 */
	boolean isReadOnly();

	/**
	 * Returns whether the structure of this element is known. For example, for a
	 * translation unit that could not be parsed, <code>false</code> is returned.
	 * If the structure of an element is unknown, navigations will return reasonable
	 * defaults. For example, <code>getChildren</code> will return an empty collection.
	 * <p>
	 * Note: This does not imply anything about consistency with the
	 * underlying resource/buffer contents.
	 * </p>
	 *
	 * @return <code>true</code> if the structure of this element is known
	 * @exception CModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource
	 */
	boolean isStructureKnown() throws CModelException;
}
