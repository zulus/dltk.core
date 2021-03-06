/*******************************************************************************
 * Copyright (c) 2000, 2018 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.workingsets;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.internal.ui.scriptview.BuildPathContainer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.IWorkingSet;

/**
 * Working set filter for Script viewers.
 */
public class WorkingSetFilter extends ViewerFilter {

	private IWorkingSet fWorkingSet = null;
	private IAdaptable[] fCachedWorkingSet = null;

	/**
	 * Returns the working set which is used by this filter.
	 *
	 * @return the working set
	 */
	public IWorkingSet getWorkingSet() {
		return fWorkingSet;
	}

	/**
	 * Sets this filter's working set.
	 *
	 * @param workingSet
	 *                       the working set
	 */
	public void setWorkingSet(IWorkingSet workingSet) {
		fWorkingSet = workingSet;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (fWorkingSet == null || (fWorkingSet.isAggregateWorkingSet()
				&& fWorkingSet.getElements().length == 0))
			return true;

		if (element instanceof IModelElement)
			return isEnclosing((IModelElement) element);

		if (element instanceof IResource)
			return isEnclosing(((IResource) element).getFullPath());

		if (element instanceof BuildPathContainer) {
			return isEnclosing((BuildPathContainer) element);
		}

		if (element instanceof IAdaptable) {
			IAdaptable adaptable = (IAdaptable) element;
			IModelElement je = adaptable.getAdapter(IModelElement.class);
			if (je != null)
				return isEnclosing(je);

			IResource resource = adaptable.getAdapter(IResource.class);
			if (resource != null)
				return isEnclosing(resource.getFullPath());
		}

		return true;
	}

	private boolean isEnclosing(BuildPathContainer container) {
		// check whether the containing ScriptFolder root is enclosed
		Object[] roots = container.getProjectFragments();
		if (roots.length > 0)
			return isEnclosing((IProjectFragment) roots[0]);
		return false;
	}

	@Override
	public Object[] filter(Viewer viewer, Object parent, Object[] elements) {
		Object[] result = null;
		if (fWorkingSet != null)
			fCachedWorkingSet = fWorkingSet.getElements();
		try {
			result = super.filter(viewer, parent, elements);
		} finally {
			fCachedWorkingSet = null;
		}
		return result;
	}

	private boolean isEnclosing(IPath elementPath) {
		if (elementPath == null)
			return false;

		IAdaptable[] cachedWorkingSet = fCachedWorkingSet;
		if (cachedWorkingSet == null)
			cachedWorkingSet = fWorkingSet.getElements();

		int length = cachedWorkingSet.length;
		for (int i = 0; i < length; i++) {
			if (isEnclosing(cachedWorkingSet[i], elementPath))
				return true;
		}
		return false;
	}

	public boolean isEnclosing(IModelElement element) {
		Assert.isNotNull(element);

		IAdaptable[] cachedWorkingSet = fCachedWorkingSet;
		if (cachedWorkingSet == null)
			cachedWorkingSet = fWorkingSet.getElements();

		boolean isElementPathComputed = false;
		IPath elementPath = null; // will be lazy computed if needed

		int length = cachedWorkingSet.length;
		for (int i = 0; i < length; i++) {
			IModelElement scopeElement = cachedWorkingSet[i]
					.getAdapter(IModelElement.class);
			if (scopeElement != null) {
				// compare Script elements
				IModelElement searchedElement = element;
				while (searchedElement != null) {
					if (searchedElement.equals(scopeElement)) {
						return true;
					}
					if (scopeElement
							.getElementType() == IModelElement.SCRIPT_PROJECT
							&& searchedElement
									.getElementType() == IModelElement.PROJECT_FRAGMENT) {
						IProjectFragment pkgRoot = (IProjectFragment) searchedElement;
						if (pkgRoot.isExternal() && pkgRoot.isArchive()) {
							if (((IScriptProject) scopeElement)
									.isOnBuildpath(searchedElement))
								return true;
						}
					}
					searchedElement = searchedElement.getParent();
					if (searchedElement != null && searchedElement
							.getElementType() == IModelElement.SOURCE_MODULE) {
						ISourceModule unit = (ISourceModule) searchedElement;
						unit = unit.getPrimary();
					}
				}
				while (scopeElement != null) {
					if (element.equals(scopeElement)) {
						return true;
					}
					scopeElement = scopeElement.getParent();
				}
			} else {
				// compare resource paths
				if (!isElementPathComputed) {
					IResource elementResource = element
							.getAdapter(IResource.class);
					if (elementResource != null)
						elementPath = elementResource.getFullPath();
				}
				if (isEnclosing(cachedWorkingSet[i], elementPath))
					return true;
			}
		}
		return false;
	}

	private boolean isEnclosing(IAdaptable element, IPath path) {
		if (path == null)
			return false;

		IPath elementPath = null;

		IResource elementResource = element.getAdapter(IResource.class);
		if (elementResource != null)
			elementPath = elementResource.getFullPath();

		if (elementPath == null) {
			IModelElement ScriptElement = element
					.getAdapter(IModelElement.class);
			if (ScriptElement != null)
				elementPath = ScriptElement.getPath();
		}

		if (elementPath == null && element instanceof IStorage)
			elementPath = ((IStorage) element).getFullPath();

		if (elementPath == null)
			return false;

		if (elementPath.isPrefixOf(path))
			return true;

		if (path.isPrefixOf(elementPath))
			return true;

		return false;
	}

}
