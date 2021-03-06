/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.core.tests.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IParent;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.tests.util.Util;
import org.eclipse.dltk.internal.core.ModelElement;

/*
 * Tests that modify resources in the workspace.
 */
public class ModifyingResourceTests extends AbstractModelTests {

	public ModifyingResourceTests(String name) {
		super(name);
	}

	protected void assertElementDescendants(String message, String expected, IModelElement element)
			throws CoreException {
		String actual = expandAll(element);
		if (!expected.equals(actual)) {
			System.out.println(Util.displayString(actual, 4));
		}
		assertEquals(message, expected, actual);
	}

	protected IFile createFile(String path, InputStream content) throws CoreException {
		IFile file = getFile(path);
		file.create(content, true, null);
		return file;
	}

	protected IFile createFile(String path, byte[] content) throws CoreException {
		return createFile(path, new ByteArrayInputStream(content));
	}

	protected IFile createFile(String path, String content) throws CoreException {
		return createFile(path, content.getBytes());
	}

	protected IFile createFile(String path, String content, String charsetName)
			throws CoreException, UnsupportedEncodingException {
		return createFile(path, content.getBytes(charsetName));
	}

	protected static IFolder createFolder(String path) throws CoreException {
		return createFolder(new Path(path));
	}

	protected void deleteFile(String filePath) throws CoreException {
		deleteResource(this.getFile(filePath));
	}

	protected void deleteFolder(String folderPath) throws CoreException {
		deleteFolder(new Path(folderPath));
	}

	protected IFile editFile(String path, String content) throws CoreException {
		IFile file = this.getFile(path);
		InputStream input = new ByteArrayInputStream(content.getBytes());
		file.setContents(input, IResource.FORCE, null);
		return file;
	}

	/*
	 * Expands (i.e. open) the given element and returns a toString() representation
	 * of the tree.
	 */
	protected String expandAll(IModelElement element) throws CoreException {
		StringBuilder buffer = new StringBuilder();
		this.expandAll(element, 0, buffer);
		return buffer.toString();
	}

	private void expandAll(IModelElement element, int tab, StringBuilder buffer) throws CoreException {
		IModelElement[] children = null;
		// force opening of element by getting its children
		if (element instanceof IParent) {
			IParent parent = (IParent) element;
			children = parent.getChildren();
		}
		((ModelElement) element).toStringInfo(tab, buffer);
		if (children != null) {
			for (int i = 0, length = children.length; i < length; i++) {
				buffer.append("\n");
				this.expandAll(children[i], tab + 1, buffer);
			}
		}
	}

	protected void renameProject(String project, String newName) throws CoreException {
		getProject(project).move(new Path(newName), true, null);
	}

	@Override
	protected ISourceModule getSourceModule(String path) {
		return (ISourceModule) DLTKCore.create(getFile(path));
	}

	protected IFolder getFolder(String path) {
		return getFolder(new Path(path));
	}

	protected IScriptFolder getPackage(String path) {
		if (path.indexOf('/', 1) != -1) { // if path as more than one segment
			IModelElement element = DLTKCore.create(this.getFolder(path));
			if (element instanceof IProjectFragment) {
				return ((IProjectFragment) element).getScriptFolder(new Path(""));
			}
			return (IScriptFolder) element;
		}
		IProject project = getProject(path);
		return DLTKCore.create(project).getProjectFragment(project).getScriptFolder(new Path(""));
	}

	protected IProjectFragment getProjectFragment(String path) {
		if (path.indexOf('/', 1) != -1) { // if path as more than one segment
			if (path.endsWith(".jar")) {
				return (IProjectFragment) DLTKCore.create(this.getFile(path));
			}
			return (IProjectFragment) DLTKCore.create(this.getFolder(path));
		}
		IProject project = getProject(path);
		return DLTKCore.create(project).getProjectFragment(project);
	}

	protected void moveFile(String sourcePath, String destPath) throws CoreException {
		this.getFile(sourcePath).move(this.getFile(destPath).getFullPath(), false, null);
	}

	protected void moveFolder(String sourcePath, String destPath) throws CoreException {
		this.getFolder(sourcePath).move(this.getFolder(destPath).getFullPath(), false, null);
	}

	protected void swapFiles(String firstPath, String secondPath) throws CoreException {
		final IFile first = this.getFile(firstPath);
		final IFile second = this.getFile(secondPath);
		IWorkspaceRunnable runnable = monitor -> {
			IPath tempPath = first.getParent().getFullPath().append("swappingFile.temp");
			first.move(tempPath, false, monitor);
			second.move(first.getFullPath(), false, monitor);
			getWorkspaceRoot().getFile(tempPath).move(second.getFullPath(), false, monitor);
		};
		getWorkspace().run(runnable, null);
	}

	/*
	 * Returns a new buildpath from the given source folders and their respective
	 * exclusion/inclusion patterns. The folder path is an absolute
	 * workspace-relative path. The given array as the following form: [<folder>,
	 * "<pattern>[|<pattern]*"]* E.g. new String[] { "/P/src1", "p/A.java", "/P",
	 * "*.txt|com.tests/**" }
	 */
	protected IBuildpathEntry[] createBuildpath(String[] foldersAndPatterns) {
		int length = foldersAndPatterns.length;
		int increment = 1;
		IBuildpathEntry[] buildpath = new IBuildpathEntry[length / increment];
		for (int i = 0; i < length; i += increment) {
			String src = foldersAndPatterns[i];
			IPath folderPath = new Path(src);
			buildpath[i / increment] = DLTKCore.newSourceEntry(folderPath);
		}
		return buildpath;
	}

	/*
	 * Returns a new buildpath from the given source folders and their respective
	 * exclusion/inclusion patterns. The folder path is an absolute
	 * workspace-relative path. The given array as the following form: [<folder>,
	 * "<pattern>[|<pattern]*"]* E.g. new String[] { "/P/src1", "p/A.java", "/P",
	 * "*.txt|com.tests/**" }
	 */
	protected IBuildpathEntry[] createBuildpath(String[] foldersAndPatterns, boolean hasInclusionPatterns,
			boolean hasExclusionPatterns) {
		int length = foldersAndPatterns.length;
		int increment = 1;
		if (hasInclusionPatterns)
			increment++;
		if (hasExclusionPatterns)
			increment++;
		IBuildpathEntry[] buildpath = new IBuildpathEntry[length / increment];
		for (int i = 0; i < length; i += increment) {
			String src = foldersAndPatterns[i];
			IPath[] accessibleFiles = new IPath[0];
			if (hasInclusionPatterns) {
				String patterns = foldersAndPatterns[i + 1];
				StringTokenizer tokenizer = new StringTokenizer(patterns, "|");
				int patternsCount = tokenizer.countTokens();
				accessibleFiles = new IPath[patternsCount];
				for (int j = 0; j < patternsCount; j++) {
					accessibleFiles[j] = new Path(tokenizer.nextToken());
				}
			}
			IPath[] nonAccessibleFiles = new IPath[0];
			if (hasExclusionPatterns) {
				String patterns = foldersAndPatterns[i + increment - 1];
				StringTokenizer tokenizer = new StringTokenizer(patterns, "|");
				int patternsCount = tokenizer.countTokens();
				nonAccessibleFiles = new IPath[patternsCount];
				for (int j = 0; j < patternsCount; j++) {
					nonAccessibleFiles[j] = new Path(tokenizer.nextToken());
				}
			}
			IPath folderPath = new Path(src);
			buildpath[i / increment] = DLTKCore.newSourceEntry(folderPath, accessibleFiles, nonAccessibleFiles);
		}
		return buildpath;
	}

	public void setReadOnly(IResource resource, boolean readOnly) throws CoreException {
		org.eclipse.dltk.internal.core.util.Util.setReadOnly(resource, readOnly);
	}

	public boolean isReadOnly(IResource resource) throws CoreException {
		return org.eclipse.dltk.internal.core.util.Util.isReadOnly(resource);
	}
}
