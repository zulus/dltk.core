/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.debug.ui.actions;

import org.eclipse.debug.core.model.IExpression;
import org.eclipse.debug.ui.DebugPopup;
import org.eclipse.debug.ui.InspectPopupDialog;
import org.eclipse.dltk.debug.core.eval.EvaluatedScriptExpression;
import org.eclipse.dltk.debug.core.eval.IScriptEvaluationResult;
import org.eclipse.dltk.debug.ui.DLTKDebugUIPlugin;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IWorkbenchPart;

public class PopupScriptInspectAction extends ScriptInspectAction {
	private void showPopup(StyledText textWidget, IExpression expression) {
		// TODO: add real command id
		DebugPopup displayPopup = new InspectPopupDialog(getShell(),
				getPopupAnchor(textWidget), null, expression);
		displayPopup.open();
	}

	@Override
	protected void displayResult(final IScriptEvaluationResult result) {
		IWorkbenchPart part = getPart();
		final StyledText styledText = getStyledText(part);

		if (styledText != null) {
			final IExpression expression = new EvaluatedScriptExpression(
					result);
			DLTKDebugUIPlugin.getStandardDisplay()
					.asyncExec(() -> showPopup(styledText, expression));
		}

		evaluationCleanup();
	}
}
