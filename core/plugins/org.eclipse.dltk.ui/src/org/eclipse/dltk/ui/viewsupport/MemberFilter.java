/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.ui.viewsupport;

import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IType;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;


/**
 * Filter for the methods viewer.
 * Changing a filter property does not trigger a refiltering of the viewer
 */
public class MemberFilter extends ViewerFilter {

	public static final int FILTER_FIELDS= 1;
	public static final int FILTER_LOCALTYPES= 2;

	private int fFilterProperties;


	/**
	 * Modifies filter and add a property to filter for
	 */
	public final void addFilter(int filter) {
		fFilterProperties |= filter;
	}
	/**
	 * Modifies filter and remove a property to filter for
	 */
	public final void removeFilter(int filter) {
		fFilterProperties &= (-1 ^ filter);
	}
	/**
	 * Tests if a property is filtered
	 */
	public final boolean hasFilter(int filter) {
		return (fFilterProperties & filter) != 0;
	}

	public boolean isFilterProperty(Object element, Object property) {
		return false;
	}
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof IMember) {
			IMember member= (IMember) element;
			int memberType= member.getElementType();

			if (hasFilter(FILTER_FIELDS) && memberType == IModelElement.FIELD) {
				return false;
			}

			if (hasFilter(FILTER_LOCALTYPES) && memberType == IModelElement.TYPE && isLocalType((IType) member)) {
				return false;
			}

			if (member.getElementName().startsWith("<")) { // filter out <clinit> //$NON-NLS-1$
				return false;
			}
		}
		return true;
	}

	private boolean isLocalType(IType type) {
		IModelElement parent= type.getParent();
		return parent instanceof IMember && !(parent instanceof IType);
	}
}
