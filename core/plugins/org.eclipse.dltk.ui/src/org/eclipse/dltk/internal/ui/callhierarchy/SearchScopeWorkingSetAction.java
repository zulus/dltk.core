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
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.internal.ui.search.DLTKSearchScopeFactory;
import org.eclipse.ui.IWorkingSet;



class SearchScopeWorkingSetAction extends SearchScopeAction {
	private IWorkingSet[] fWorkingSets;
	SearchScopeActionGroup group;
	public SearchScopeWorkingSetAction(SearchScopeActionGroup group, IWorkingSet[] workingSets, String name) {
		super(group, name);
		this.group = group;
		setToolTipText(CallHierarchyMessages.SearchScopeActionGroup_workingset_tooltip);
//		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.CALL_HIERARCHY_SEARCH_SCOPE_ACTION);
		if (DLTKCore.DEBUG) {
			System.err.println("Add help support here..."); //$NON-NLS-1$
		}


		this.fWorkingSets = workingSets;
	}

	@Override
	public IDLTKSearchScope getSearchScope() {
		return DLTKSearchScopeFactory.getInstance().createSearchScope(fWorkingSets, true, group.getLangaugeToolkit());
	}

	/**
	 *
	 */
	public IWorkingSet[] getWorkingSets() {
		return fWorkingSets;
	}

	@Override
	public int getSearchScopeType() {
		return SearchScopeActionGroup.SEARCH_SCOPE_TYPE_WORKING_SET;
	}

	@Override
	public String getFullDescription() {
		return DLTKSearchScopeFactory.getInstance().getWorkingSetScopeDescription(fWorkingSets, true);
	}
}
