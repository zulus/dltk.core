/*******************************************************************************
 * Copyright (c) 2000, 2018 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.search;

import org.eclipse.core.resources.IFile;
import org.eclipse.dltk.core.IExternalSourceModule;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.core.SourceModule;
import org.eclipse.dltk.internal.ui.editor.EditorUtility;
import org.eclipse.dltk.internal.ui.editor.ExternalStorageEditorInput;
import org.eclipse.dltk.ui.DLTKUILanguageManager;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.IDLTKUILanguageToolkit;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IReusableEditor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;

public class DLTKSearchEditorOpener {

	private static class ReusedEditorWatcher implements IPartListener {

		private IEditorPart fReusedEditor;
		private IPartService fPartService;

		public ReusedEditorWatcher() {
			fReusedEditor = null;
			fPartService = null;
		}

		public IEditorPart getReusedEditor() {
			return fReusedEditor;
		}

		public void initialize(IEditorPart editor) {
			if (fReusedEditor != null) {
				fPartService.removePartListener(this);
			}
			fReusedEditor = editor;
			if (editor != null) {
				fPartService = editor.getSite().getWorkbenchWindow()
						.getPartService();
				fPartService.addPartListener(this);
			} else {
				fPartService = null;
			}
		}

		@Override
		public void partOpened(IWorkbenchPart part) {
		}

		@Override
		public void partDeactivated(IWorkbenchPart part) {
		}

		@Override
		public void partClosed(IWorkbenchPart part) {
			if (part == fReusedEditor) {
				initialize(null);
			}
		}

		@Override
		public void partActivated(IWorkbenchPart part) {
		}

		@Override
		public void partBroughtToTop(IWorkbenchPart part) {
		}
	}

	private ReusedEditorWatcher fReusedEditorWatcher;

	public IEditorPart openElement(Object element)
			throws PartInitException, ModelException {
		IWorkbenchPage wbPage = DLTKUIPlugin.getActivePage();
		final IEditorPart editor;
		if (NewSearchUI.reuseEditor())
			editor = showWithReuse(element, wbPage);
		else
			editor = showWithoutReuse(element, wbPage);
		if (element instanceof IModelElement)
			EditorUtility.revealInEditor(editor, (IModelElement) element);
		return editor;
	}

	public IEditorPart openMatch(Match match)
			throws PartInitException, ModelException {
		Object element = getElementToOpen(match);
		return openElement(element);
	}

	protected Object getElementToOpen(Match match) {
		return match.getElement();
	}

	private IEditorPart showWithoutReuse(Object element, IWorkbenchPage wbPage)
			throws PartInitException, ModelException {
		return EditorUtility.openInEditor(element, false);
	}

	private IEditorPart showWithReuse(Object element, IWorkbenchPage wbPage)
			throws ModelException, PartInitException {
		if (element instanceof IModelElement) {
			IModelElement module = ((IModelElement) element)
					.getAncestor(IModelElement.SOURCE_MODULE);
			if (module instanceof IExternalSourceModule) {
				String editorID = getEditorID(module);
				return showInEditor(wbPage, new ExternalStorageEditorInput(
						(IExternalSourceModule) module), editorID);
			} else if (module instanceof SourceModule) {
				String editorID = getEditorID(module);
				IFile file = getFile(element);
				return showInEditor(wbPage, new FileEditorInput(file),
						editorID);
			}
		}

		IFile file = getFile(element);
		if (file != null) {
			String editorID = getEditorID(file);
			return showInEditor(wbPage, new FileEditorInput(file), editorID);
		}
		return null;
	}

	private IFile getFile(Object element) throws ModelException {
		if (element instanceof IFile)
			return (IFile) element;
		if (element instanceof IModelElement) {
			IModelElement jElement = (IModelElement) element;
			ISourceModule cu = (ISourceModule) jElement
					.getAncestor(IModelElement.SOURCE_MODULE);
			if (cu != null) {
				return (IFile) cu.getCorrespondingResource();
			}
		}
		return null;
	}

	private String getEditorID(IFile file) throws PartInitException {
		IEditorDescriptor desc = null;
		if (desc == null) {
			return DLTKUIPlugin.getDefault().getWorkbench().getEditorRegistry()
					.findEditor(IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID)
					.getId();
		}
		return desc.getId();
	}

	private String getEditorID(IModelElement module) throws PartInitException {
		IDLTKUILanguageToolkit toolkit = DLTKUILanguageManager
				.getLanguageToolkit(module);
		String editorId = toolkit.getEditorId(module);

		IEditorDescriptor desc = null;
		if (editorId != null) {
			desc = DLTKUIPlugin.getDefault().getWorkbench().getEditorRegistry()
					.findEditor(editorId);
		}

		if (desc == null) {
			return DLTKUIPlugin.getDefault().getWorkbench().getEditorRegistry()
					.findEditor(IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID)
					.getId();
		}
		return desc.getId();
	}

	private boolean isPinned(IEditorPart editor) {
		if (editor == null)
			return false;

		IEditorReference[] editorRefs = editor.getEditorSite().getPage()
				.getEditorReferences();
		int i = 0;
		while (i < editorRefs.length) {
			if (editor.equals(editorRefs[i].getEditor(false)))
				return editorRefs[i].isPinned();
			i++;
		}
		return false;
	}

	private IEditorPart showInEditor(IWorkbenchPage page, IEditorInput input,
			String editorId) {
		IEditorPart editor = page.findEditor(input);
		if (editor != null)
			page.bringToTop(editor);
		else {
			IEditorPart reusedEditor = getReusedEditor();
			boolean isOpen = false;
			if (reusedEditor != null) {
				IEditorReference[] parts = page.getEditorReferences();
				int i = 0;
				while (!isOpen && i < parts.length)
					isOpen = reusedEditor == parts[i++].getEditor(false);
			}

			boolean canBeReused = isOpen && !reusedEditor.isDirty()
					&& !isPinned(reusedEditor);
			boolean showsSameInputType = reusedEditor != null
					&& reusedEditor.getSite().getId().equals(editorId);
			if (canBeReused && !showsSameInputType) {
				page.closeEditor(reusedEditor, false);
				setReusedEditor(null);
			}

			if (canBeReused && showsSameInputType) {
				((IReusableEditor) reusedEditor).setInput(input);
				page.bringToTop(reusedEditor);
				editor = reusedEditor;
			} else {
				try {
					editor = page.openEditor(input, editorId, false);
					if (editor instanceof IReusableEditor)
						setReusedEditor(editor);
					else
						setReusedEditor(null);
				} catch (PartInitException ex) {
					MessageDialog.openError(
							DLTKUIPlugin.getActiveWorkbenchShell(),
							SearchMessages.Search_Error_openEditor_title,
							SearchMessages.Search_Error_openEditor_message);
					return null;
				}
			}
		}
		return editor;
	}

	private IEditorPart getReusedEditor() {
		if (fReusedEditorWatcher != null)
			return fReusedEditorWatcher.getReusedEditor();
		return null;
	}

	private void setReusedEditor(IEditorPart editor) {
		if (fReusedEditorWatcher == null) {
			fReusedEditorWatcher = new ReusedEditorWatcher();
		}
		fReusedEditorWatcher.initialize(editor);
	}
}
