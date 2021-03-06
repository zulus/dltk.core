/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
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
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.internal.ui.search.DLTKSearchScopeFactory;


class SearchScopeProjectAction extends SearchScopeAction {
	private final SearchScopeActionGroup fGroup;

	public SearchScopeProjectAction(SearchScopeActionGroup group) {
		super(group, CallHierarchyMessages.SearchScopeActionGroup_project_text);
		this.fGroup = group;
		setToolTipText(CallHierarchyMessages.SearchScopeActionGroup_project_tooltip);
//		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.CALL_HIERARCHY_SEARCH_SCOPE_ACTION);
		if (DLTKCore.DEBUG) {
			System.err.println("Add help support here..."); //$NON-NLS-1$
		}
	}

	@Override
	public IDLTKSearchScope getSearchScope() {
		IMethod method = this.fGroup.getView().getMethod();
		if (method == null) {
			return null;
		}

		DLTKSearchScopeFactory factory= DLTKSearchScopeFactory.getInstance();
		return factory.createProjectSearchScope(method.getScriptProject(), true);
	}

	@Override
	public int getSearchScopeType() {
		return SearchScopeActionGroup.SEARCH_SCOPE_TYPE_PROJECT;
	}

	@Override
	public String getFullDescription() {
		IMethod method = this.fGroup.getView().getMethod();
		if (method != null) {
			DLTKSearchScopeFactory factory= DLTKSearchScopeFactory.getInstance();
			return factory.getProjectScopeDescription(method.getScriptProject(), true);
		}
		return ""; //$NON-NLS-1$
	}
}
