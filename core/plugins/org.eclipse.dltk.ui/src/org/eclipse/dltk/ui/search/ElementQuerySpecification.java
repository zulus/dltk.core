/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 
 *******************************************************************************/
package org.eclipse.dltk.ui.search;

import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.search.IDLTKSearchScope;

/**
 * <p>
 * Describes a search query by giving the {@link IModelElement} to search
 * for.
 * </p>
 * <p>
 * This class is not intended to be instantiated or subclassed by clients.
 * </p>
 * 
 */
public class ElementQuerySpecification extends QuerySpecification {
	private IModelElement fElement;

	/**
	 * A constructor.
	 * @param modelElement Thescriptelement the query should search for.
	 * @param limitTo		  The kind of occurrence the query should search for.
	 * @param scope		  The scope to search in.
	 * @param scopeDescription A human readable description of the search scope.
	 */
	public ElementQuerySpecification(IModelElement modelElement, int limitTo, IDLTKSearchScope scope, String scopeDescription) {
		super(limitTo, scope, scopeDescription);
		fElement= modelElement;
	}
	
	/**
	 * Returns the element to search for.
	 * @return The element to search for.
	 */
	public IModelElement getElement() {
		return fElement;
	}
}
