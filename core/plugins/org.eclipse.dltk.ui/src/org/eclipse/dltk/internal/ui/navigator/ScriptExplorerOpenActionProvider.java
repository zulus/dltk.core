/*******************************************************************************
 * Copyright (c) 2003, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.navigator;

import org.eclipse.dltk.ui.actions.OpenAction;
import org.eclipse.dltk.ui.actions.OpenEditorActionGroup;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

public class ScriptExplorerOpenActionProvider extends CommonActionProvider {
	private IAction fOpenAndExpand;
	private OpenEditorActionGroup fOpenGroup;

	private boolean fInViewPart = false;

	@Override
	public void fillActionBars(IActionBars actionBars) {
		if (fInViewPart) {
			fOpenGroup.fillActionBars(actionBars);

			if (fOpenAndExpand == null
					&& fOpenGroup.getOpenAction().isEnabled()) // TODO: is not
																// updated!
				actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN,
						fOpenGroup.getOpenAction());
			else if (fOpenAndExpand.isEnabled())
				actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN,
						fOpenAndExpand);
		}
	}

	@Override
	public void fillContextMenu(IMenuManager menu) {
		if (fInViewPart) {
			if (fOpenGroup.getOpenAction().isEnabled()) {
				fOpenGroup.fillContextMenu(menu);
			}
		}
	}

	@Override
	public void init(ICommonActionExtensionSite site) {
		ICommonViewerWorkbenchSite workbenchSite = null;
		if (site.getViewSite() instanceof ICommonViewerWorkbenchSite)
			workbenchSite = (ICommonViewerWorkbenchSite) site.getViewSite();

		if (workbenchSite != null) {
			if (workbenchSite.getPart() != null
					&& workbenchSite.getPart() instanceof IViewPart) {
				IViewPart viewPart = (IViewPart) workbenchSite.getPart();

				fOpenGroup = new OpenEditorActionGroup(viewPart);

				if (site.getStructuredViewer() instanceof TreeViewer)
					fOpenAndExpand = new OpenAndExpand(workbenchSite.getSite(),
							(OpenAction) fOpenGroup.getOpenAction(),
							(TreeViewer) site.getStructuredViewer());
				fInViewPart = true;
			}
		}
	}

	@Override
	public void setContext(ActionContext context) {
		super.setContext(context);
		if (fInViewPart) {
			fOpenGroup.setContext(context);
		}
	}
}
