/*******************************************************************************
 * Copyright (c) 2000, 2018 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.corext.refactoring.changes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.core.BuildpathEntry;
import org.eclipse.dltk.internal.corext.refactoring.RefactoringCoreMessages;
import org.eclipse.dltk.internal.corext.refactoring.base.DLTKChange;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.NullChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.osgi.util.NLS;

public class AddToBuildpathChange extends DLTKChange {

	private IScriptProject fProjectHandle;
	private IBuildpathEntry fEntryToAdd;

	public AddToBuildpathChange(IScriptProject project,
			IBuildpathEntry entryToAdd) {
		fProjectHandle = project;
		fEntryToAdd = entryToAdd;
	}

	public AddToBuildpathChange(IScriptProject project,
			String sourceFolderName) {
		this(project, DLTKCore
				.newSourceEntry(project.getPath().append(sourceFolderName)));
	}

	/**
	 * Adds a new project class path entry to the project.
	 *
	 * @param project
	 * @param newProjectEntry
	 *                            (must be absolute <code>IPath</code>)
	 */
	public AddToBuildpathChange(IScriptProject project, IPath newProjectEntry) {
		this(project, DLTKCore.newProjectEntry(newProjectEntry));
	}

	public AddToBuildpathChange(IScriptProject project, int entryKind,
			IPath path) {
		this(project, createNewBuildpathEntry(entryKind, path));
	}

	@Override
	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException {
		return super.isValid(pm, READ_ONLY | DIRTY);
	}

	@Override
	public Change perform(IProgressMonitor pm) throws CoreException {
		pm.beginTask(getName(), 1);
		try {
			if (validateBuildpath()) {
				getScriptProject().setRawBuildpath(getNewBuildpathEntries(),
						new SubProgressMonitor(pm, 1));
				IPath buildpathEntryPath = fEntryToAdd.getPath();
				return new DeleteFromBuildpathChange(buildpathEntryPath,
						getScriptProject());
			}
			return new NullChange();
		} finally {
			pm.done();
		}
	}

	public boolean validateBuildpath() throws ModelException {
		IScriptProject scriptProject = getScriptProject();
		IBuildpathEntry[] newClasspathEntries = getNewBuildpathEntries();
		return BuildpathEntry
				.validateBuildpath(scriptProject, newClasspathEntries).isOK();
	}

	private IBuildpathEntry[] getNewBuildpathEntries() throws ModelException {
		IBuildpathEntry[] entries = getScriptProject().getRawBuildpath();
		List<IBuildpathEntry> cp = new ArrayList<>(entries.length + 1);
		cp.addAll(Arrays.asList(entries));
		cp.add(fEntryToAdd);
		return cp.toArray(new IBuildpathEntry[cp.size()]);
	}

	private static IBuildpathEntry createNewBuildpathEntry(int kind,
			IPath path) {
		switch (kind) {
		case IBuildpathEntry.BPE_LIBRARY:
			return DLTKCore.newLibraryEntry(path);
		case IBuildpathEntry.BPE_PROJECT:
			return DLTKCore.newProjectEntry(path);
		case IBuildpathEntry.BPE_SOURCE:
			return DLTKCore.newSourceEntry(path);
		case IBuildpathEntry.BPE_CONTAINER:
			return DLTKCore.newContainerEntry(path);
		case IBuildpathEntry.BPE_VARIABLE:
			return DLTKCore.newVariableEntry(path);
		default:
			Assert.isTrue(false);
			return null;
		}
	}

	private IScriptProject getScriptProject() {
		return fProjectHandle;
	}

	@Override
	public String getName() {
		return NLS.bind(RefactoringCoreMessages.AddToClasspathChange_add,
				getScriptProject().getElementName());

	}

	@Override
	public Object getModifiedElement() {
		return getScriptProject();
	}

	public IBuildpathEntry getBuildpathEntry() {
		return fEntryToAdd;
	}
}
