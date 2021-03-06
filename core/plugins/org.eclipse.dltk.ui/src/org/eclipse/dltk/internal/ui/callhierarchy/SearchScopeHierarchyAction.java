/*******************************************************************************
 * Copyright (c) 2000, 2018 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.callhierarchy;

import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.dltk.internal.ui.search.DLTKSearchScopeFactory;
import org.eclipse.dltk.ui.DLTKUIPlugin;

class SearchScopeHierarchyAction extends SearchScopeAction {
	private final SearchScopeActionGroup fGroup;

	public SearchScopeHierarchyAction(SearchScopeActionGroup group) {
		super(group,
				CallHierarchyMessages.SearchScopeActionGroup_hierarchy_text);
		this.fGroup = group;
		setToolTipText(
				CallHierarchyMessages.SearchScopeActionGroup_hierarchy_tooltip);
//		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.CALL_HIERARCHY_SEARCH_SCOPE_ACTION);
		if (DLTKCore.DEBUG) {
			System.err.println("Add help support here..."); //$NON-NLS-1$
		}

	}

	@Override
	public IDLTKSearchScope getSearchScope() {
		try {
			IMethod method = this.fGroup.getView().getMethod();

			if (method != null) {
				return SearchEngine
						.createHierarchyScope(method.getDeclaringType());
			}
			return null;
		} catch (ModelException e) {
			DLTKUIPlugin.log(e);
		}

		return null;
	}

	@Override
	public int getSearchScopeType() {
		return SearchScopeActionGroup.SEARCH_SCOPE_TYPE_HIERARCHY;
	}

	@Override
	public String getFullDescription() {
		IMethod method = this.fGroup.getView().getMethod();
		return DLTKSearchScopeFactory.getInstance()
				.getHierarchyScopeDescription(method.getDeclaringType());
	}

}
