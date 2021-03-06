/*******************************************************************************
 * Copyright (c) 2005, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.text.completion;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.dltk.internal.ui.editor.SpecificContentAssistExecutor;
import org.eclipse.dltk.ui.text.completion.CompletionProposalComputerRegistry;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

public final class ScriptContentAssistHandler extends AbstractHandler {
	private final SpecificContentAssistExecutor fExecutor= new SpecificContentAssistExecutor(CompletionProposalComputerRegistry.getDefault());

	public ScriptContentAssistHandler() {
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ITextEditor editor= getActiveEditor();
		if (editor == null)
			return null;

		String categoryId= event.getParameter("org.eclipse.dltk.ui.specific_content_assist.category_id"); //$NON-NLS-1$
		if (categoryId == null)
			return null;

		fExecutor.invokeContentAssist(editor, categoryId);

		return null;
	}

	private ITextEditor getActiveEditor() {
		IWorkbenchWindow window= PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IWorkbenchPage page= window.getActivePage();
			if (page != null) {
				IEditorPart editor= page.getActiveEditor();
				if (editor instanceof ITextEditor)
					return (ITextEditor) editor;
			}
		}
		return null;
	}

}
