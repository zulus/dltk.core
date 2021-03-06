/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.wizards.buildpath;

import java.util.List;

import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.ListDialogField;
import org.eclipse.dltk.ui.util.PixelConverter;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class BuildpathOrderingWorkbookPage extends BuildPathBasePage {

	private ListDialogField fBuildpathList;

	public BuildpathOrderingWorkbookPage(ListDialogField buildpathList) {
		fBuildpathList = buildpathList;
	}

	@Override
	public Control getControl(Composite parent) {
		PixelConverter converter = new PixelConverter(parent);

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());

		LayoutUtil.doDefaultLayout(composite,
				new DialogField[] { fBuildpathList }, true, SWT.DEFAULT,
				SWT.DEFAULT);
		LayoutUtil.setHorizontalGrabbing(fBuildpathList.getListControl(null));

		int buttonBarWidth = converter.convertWidthInCharsToPixels(24);
		fBuildpathList.setButtonsMinWidth(buttonBarWidth);

		return composite;
	}

	@Override
	public List getSelection() {
		return fBuildpathList.getSelectedElements();
	}

	@Override
	public void setSelection(List selElements, boolean expand) {
		fBuildpathList.selectElements(new StructuredSelection(selElements));
	}

	@Override
	public boolean isEntryKind(int kind) {
		return true;
	}

	@Override
	public void init(IScriptProject scriptProject) {
	}

}
