/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.search;

import org.eclipse.dltk.core.IScriptModel;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.ui.ScriptElementLabels;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.graphics.Image;

public class PostfixLabelProvider extends SearchLabelProvider {
	private ITreeContentProvider fContentProvider;

	public PostfixLabelProvider(DLTKSearchResultPage page) {
		super(page);
		fContentProvider = new LevelTreeContentProvider.FastModelElementProvider();
	}

	@Override
	public Image getImage(Object element) {
		Image image = super.getImage(element);
		if (image != null)
			return image;
		return getParticipantImage(element);
	}

	@Override
	public String getText(Object element) {
		String labelWithCounts = getLabelWithCounts(element, internalGetText(element));

		StringBuilder res = new StringBuilder(labelWithCounts);

		ITreeContentProvider provider = (ITreeContentProvider) fPage.getViewer().getContentProvider();
		Object visibleParent = provider.getParent(element);
		Object realParent = fContentProvider.getParent(element);
		Object lastElement = element;
		while (realParent != null && !(realParent instanceof IScriptModel) && !realParent.equals(visibleParent)) {
			if (!isSameInformation(realParent, lastElement)) {
				if (res.length() != 0) {
					res.append(ScriptElementLabels.CONCAT_STRING);
				}

				res.append(internalGetText(realParent));
			}
			lastElement = realParent;
			realParent = fContentProvider.getParent(realParent);
		}
		return res.toString();
	}

	@Override
	protected boolean hasChildren(Object element) {
		ITreeContentProvider contentProvider = (ITreeContentProvider) fPage.getViewer().getContentProvider();
		return contentProvider.hasChildren(element);
	}

	private String internalGetText(Object element) {
		String text = super.getText(element);
		if (text != null && text.length() > 0)
			return text;
		return getParticipantText(element);
	}

	private boolean isSameInformation(Object realParent, Object lastElement) {
		if (lastElement instanceof IType) {
			IType type = (IType) lastElement;
			if (realParent instanceof ISourceModule) {
				if (type.getSourceModule().equals(realParent))
					return true;
			}
		}
		return false;
	}

}
