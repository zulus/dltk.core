/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.core.search.matching;

import org.eclipse.dltk.compiler.util.SimpleSet;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IModelElement;

public class DeclarationOfReferencedTypesPattern extends TypeReferencePattern {
	protected SimpleSet knownTypes;
	protected IModelElement enclosingElement;

	public DeclarationOfReferencedTypesPattern(IModelElement enclosingElement) {
		super(null, null, R_PATTERN_MATCH, DLTKLanguageManager
				.getLanguageToolkit(enclosingElement));
		this.enclosingElement = enclosingElement;
	}
}
