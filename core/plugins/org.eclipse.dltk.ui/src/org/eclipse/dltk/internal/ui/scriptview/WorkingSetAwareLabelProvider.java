/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.scriptview;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.dltk.internal.ui.navigator.ScriptExplorerContentProvider;
import org.eclipse.dltk.internal.ui.navigator.ScriptExplorerLabelProvider;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkingSet;

public class WorkingSetAwareLabelProvider extends ScriptExplorerLabelProvider {

	private Map fImages = new HashMap();

	public WorkingSetAwareLabelProvider(ScriptExplorerContentProvider cp,
			IPreferenceStore store) {
		super(cp, store);
	}

	@Override
	public String getText(Object element) {
		if (element instanceof IWorkingSet) {
			return decorateText(((IWorkingSet) element).getLabel(), element);
		}
		return super.getText(element);
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof IWorkingSet) {
			ImageDescriptor image = ((IWorkingSet) element)
					.getImageDescriptor();
			Image result = (Image) fImages.get(image);
			if (result == null) {
				result = image.createImage();
				fImages.put(image, result);
			}
			return decorateImage(result, element);
		}
		return super.getImage(element);
	}

	@Override
	public void dispose() {
		for (Iterator iter = fImages.values().iterator(); iter.hasNext();) {
			((Image) iter.next()).dispose();
		}
		super.dispose();
	}
}
