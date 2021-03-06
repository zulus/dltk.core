/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.ui;


import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IParent;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.ScriptElementImageProvider;
import org.eclipse.dltk.ui.ScriptElementLabels;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;


/**
 * An imlementation of the IWorkbenchAdapter for IModelElements.
 */
public class DLTKWorkbenchAdapter implements IWorkbenchAdapter {

	protected static final Object[] NO_CHILDREN= new Object[0];

	private ScriptElementImageProvider fImageProvider;

	public DLTKWorkbenchAdapter() {
		fImageProvider= new ScriptElementImageProvider();
	}

	@Override
	public Object[] getChildren(Object element) {
		IModelElement je= getModelElement(element);
		if (je instanceof IParent) {
			try {
				return ((IParent)je).getChildren();
			} catch(ModelException e) {
				DLTKUIPlugin.log(e);
			}
		}
		return NO_CHILDREN;
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object element) {
		IModelElement je= getModelElement(element);
		if (je != null)
			return fImageProvider.getScriptImageDescriptor(je, ScriptElementImageProvider.OVERLAY_ICONS | ScriptElementImageProvider.SMALL_ICONS);

		return null;

	}

	@Override
	public String getLabel(Object element) {
		return ScriptElementLabels.getDefault().getTextLabel(getModelElement(element), ScriptElementLabels.ALL_DEFAULT);
	}

	@Override
	public Object getParent(Object element) {
		IModelElement je= getModelElement(element);
		return je != null ? je.getParent() :  null;
	}

	private IModelElement getModelElement(Object element) {
		if (element instanceof IModelElement)
			return (IModelElement)element;
		else if (element instanceof IAdaptable)
			return ((IAdaptable) element).getAdapter(IModelElement.class);

		return null;
	}
}
