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
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.jface.action.Action;


/**
 * This class is copied from the
 * org.eclipse.search2.internal.ui.CancelSearchAction class.
 */
public class CancelSearchAction extends Action {
	private CallHierarchyViewPart fView;

	public CancelSearchAction(CallHierarchyViewPart view) {
		super(CallHierarchyMessages.CancelSearchAction_label);
		fView = view;
		setToolTipText(CallHierarchyMessages.CancelSearchAction_tooltip);
		DLTKPluginImages.setLocalImageDescriptors(this, "ch_cancel.png"); //$NON-NLS-1$
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(this,
		// IJavaHelpContextIds.CALL_HIERARCHY_CANCEL_SEARCH_ACTION);
		if (DLTKCore.DEBUG) {
			System.err.println("Add help support here..."); //$NON-NLS-1$
		}
	}

	@Override
	public void run() {
		fView.cancelJobs();
	}
}
