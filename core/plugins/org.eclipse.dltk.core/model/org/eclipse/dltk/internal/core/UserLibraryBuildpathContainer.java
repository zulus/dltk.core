/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.internal.core;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathContainer;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.internal.core.util.Util;

/**
 *
 */
public class UserLibraryBuildpathContainer implements IBuildpathContainer {
	
	private String name;
	private IDLTKLanguageToolkit toolkit;
	public UserLibraryBuildpathContainer(String name, IDLTKLanguageToolkit languageToolkit) {
		this.name = name;
		this.toolkit = languageToolkit;
	}
	
	@Override
	public IBuildpathEntry[] getBuildpathEntries() {
		UserLibrary library= getUserLibrary();
		if (library != null) {
			return library.getEntries();
		}
		return new IBuildpathEntry[0];
	}

	@Override
	public String getDescription() {
		return this.name;
	}

	@Override
	public int getKind() {
		UserLibrary library= getUserLibrary();
		if (library != null && library.isSystemLibrary()) {
			return K_SYSTEM;
		}
		return K_APPLICATION;
	}

	@Override
	public IPath getPath() {
		return new Path(DLTKCore.USER_LIBRARY_CONTAINER_ID).append(this.name);
	}
	
	private UserLibrary getUserLibrary() {
		UserLibrary userLibrary = ModelManager.getUserLibraryManager().getUserLibrary(this.name, toolkit);
		if (userLibrary == null && ModelManager.BP_RESOLVE_VERBOSE) {
			verbose_no_user_library_found(this.name);
		}
		return userLibrary;
	}

	private void verbose_no_user_library_found(String userLibraryName) {
		Util.verbose(
			"UserLibrary INIT - FAILED (no user library found)\n" + //$NON-NLS-1$
			"	userLibraryName: " + userLibraryName); //$NON-NLS-1$
	}

}
