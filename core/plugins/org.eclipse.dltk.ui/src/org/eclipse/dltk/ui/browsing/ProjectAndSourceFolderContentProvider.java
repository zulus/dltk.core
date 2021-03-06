/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.ui.browsing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * @since 2.0
 */
public class ProjectAndSourceFolderContentProvider
		extends ScriptBrowsingContentProvider {

	public ProjectAndSourceFolderContentProvider(
			ScriptBrowsingPart browsingPart,
			IDLTKLanguageToolkit languageToolkit) {
		super(false, browsingPart, languageToolkit);
	}

	@Override
	public Object[] getChildren(Object element) {
		if (!exists(element))
			return NO_CHILDREN;

		try {
			startReadInDisplayThread();
			if (element instanceof IStructuredSelection) {
				Assert.isLegal(false);
				Object[] result = new Object[0];
				Class<?> clazz = null;
				Iterator<?> iter = ((IStructuredSelection) element).iterator();
				while (iter.hasNext()) {
					Object item = iter.next();
					if (clazz == null)
						clazz = item.getClass();
					if (clazz == item.getClass())
						result = concatenate(result, getChildren(item));
					else
						return NO_CHILDREN;
				}
				return result;
			}
			if (element instanceof IStructuredSelection) {
				Assert.isLegal(false);
				Object[] result = new Object[0];
				Iterator<?> iter = ((IStructuredSelection) element).iterator();
				while (iter.hasNext())
					result = concatenate(result, getChildren(iter.next()));
				return result;
			}
			if (element instanceof IScriptProject)
				return getProjectFragments((IScriptProject) element);
			if (element instanceof IProjectFragment)
				return NO_CHILDREN;

			Object[] children = super.getChildren(element);

			// We need to filter all elements with different nature
			List<Object> newObjs = new ArrayList<>();
			for (int i = 0; i < children.length; i++) {
				if (children[i] instanceof IModelElement) {
					IDLTKLanguageToolkit languageToolkit = DLTKLanguageManager
							.getLanguageToolkit((IModelElement) children[i]);
					if (getToolkit().equals(languageToolkit)) {
						newObjs.add(children[i]);
					}
				} else {
					newObjs.add(children[i]);
				}
			}
			return newObjs.toArray();
		} catch (ModelException e) {
			return NO_CHILDREN;
		} finally {
			finishedReadInDisplayThread();
		}
	}

	@Override
	protected Object[] getProjectFragments(IScriptProject project)
			throws ModelException {
		if (!project.getProject().isOpen())
			return NO_CHILDREN;

		IProjectFragment[] roots = project.getProjectFragments();
		List<IProjectFragment> list = new ArrayList<>(roots.length);
		// filter out package fragments that correspond to projects and
		// replace them with the package fragments directly
		for (int i = 0; i < roots.length; i++) {
			IProjectFragment root = roots[i];
			if (!isProjectProjectFragment(root))
				list.add(root);
		}
		return list.toArray();
	}

	@Override
	public boolean hasChildren(Object element) {
		return element instanceof IScriptProject && super.hasChildren(element);
	}
}
