/*******************************************************************************
 * Copyright (c) 2000, 2018 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.typehierarchy;

import java.util.List;

import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ITypeHierarchy;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;

/**
 * A viewer including the content provider for the subtype hierarchy. Used by
 * the TypeHierarchyViewPart which has to provide a TypeHierarchyLifeCycle on
 * construction (shared type hierarchy)
 */
public class SubTypeHierarchyViewer extends TypeHierarchyViewer {

	public SubTypeHierarchyViewer(Composite parent,
			TypeHierarchyLifeCycle lifeCycle, IWorkbenchPart part,
			IPreferenceStore store) {
		super(parent, new SubTypeHierarchyContentProvider(lifeCycle), lifeCycle,
				part, store);
	}

	@Override
	public String getTitle() {
		if (isMethodFiltering()) {
			return TypeHierarchyMessages.SubTypeHierarchyViewer_filtered_title;
		}
		return TypeHierarchyMessages.SubTypeHierarchyViewer_title;
	}

	@Override
	public void updateContent(boolean expand) {
		getTree().setRedraw(false);
		refresh();

		if (expand) {
			int expandLevel = 2;
			if (isMethodFiltering()) {
				expandLevel++;
			}
			expandToLevel(expandLevel);
		}
		getTree().setRedraw(true);
	}

	/**
	 * Content provider for the subtype hierarchy
	 */
	public static class SubTypeHierarchyContentProvider
			extends TypeHierarchyContentProvider {
		public SubTypeHierarchyContentProvider(
				TypeHierarchyLifeCycle lifeCycle) {
			super(lifeCycle);
		}

		@Override
		protected final void getTypesInHierarchy(IType type, List res) {
			ITypeHierarchy hierarchy = getHierarchy();
			if (hierarchy != null) {
				IType[] types = hierarchy.getSubtypes(type);
				/*
				 * if (isObject(type)) { for (int i= 0; i < types.length; i++) {
				 * IType curr= types[i]; if (!isAnonymousFromInterface(curr)) {
				 * res.add(curr); } } } else
				 */ {
					for (int i = 0; i < types.length; i++) {
						res.add(types[i]);
					}
				}
			}

		}

		@Override
		protected IType[] getParentType(IType type) {
			ITypeHierarchy hierarchy = getHierarchy();
			if (hierarchy != null) {
				return hierarchy.getSuperclass(type);
				// dont handle interfaces
			}
			return null;
		}

	}

}
