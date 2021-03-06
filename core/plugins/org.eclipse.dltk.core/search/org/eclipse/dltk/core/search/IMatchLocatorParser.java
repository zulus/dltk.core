/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 
 *******************************************************************************/
package org.eclipse.dltk.core.search;

import org.eclipse.dltk.ast.declarations.MethodDeclaration;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.declarations.TypeDeclaration;
import org.eclipse.dltk.core.search.matching.PossibleMatch;
import org.eclipse.dltk.internal.core.search.matching.MatchingNodeSet;

public interface IMatchLocatorParser {

	void setNodeSet(MatchingNodeSet nodeSet);

	ModuleDeclaration parse(PossibleMatch possibleMatch);

	void parseBodies(ModuleDeclaration unit);
	
	MethodDeclaration processMethod(MethodDeclaration m);
	TypeDeclaration processType(TypeDeclaration t);
}
