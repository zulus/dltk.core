/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.corext.refactoring.reorg;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.ISourceModule;


class ArrayTypeConverter {

	private ArrayTypeConverter() {
	}

	static IFile[] toFileArray(Object[] objects){
		List l= Arrays.asList(objects);
		return (IFile[]) l.toArray(new IFile[l.size()]);
	}
		
	static IFolder[] toFolderArray(Object[] objects){
		List l= Arrays.asList(objects);
		return (IFolder[]) l.toArray(new IFolder[l.size()]);
	}

	static ISourceModule[] toCuArray(Object[] objects){
		List l= Arrays.asList(objects);
		return (ISourceModule[]) l.toArray(new ISourceModule[l.size()]);
	}
	
	static IProjectFragment[] toProjectFragmentArray(Object[] objects){
		List l= Arrays.asList(objects);
		return (IProjectFragment[]) l.toArray(new IProjectFragment[l.size()]);
	}
	
	static IScriptFolder[] toPackageArray(Object[] objects){
		List l= Arrays.asList(objects);
		return (IScriptFolder[]) l.toArray(new IScriptFolder[l.size()]);
	}
}
