/*******************************************************************************
 * Copyright (c) 2000, 2018 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.wizards.buildpath;

import org.eclipse.dltk.core.IAccessRule;
import org.eclipse.dltk.internal.ui.wizards.NewWizardMessages;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class AccessRulesLabelProvider extends LabelProvider
		implements ITableLabelProvider {

	public AccessRulesLabelProvider() {
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (element instanceof IAccessRule) {
			IAccessRule rule = (IAccessRule) element;
			if (columnIndex == 0) {
				return getResolutionImage(rule.getKind());
			}
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof IAccessRule) {
			IAccessRule rule = (IAccessRule) element;
			if (columnIndex == 0) {
				return getResolutionLabel(rule.getKind());
			}
			return rule.getPattern().toString();
		}
		return element.toString();
	}

	public static Image getResolutionImage(int kind) {
		switch (kind) {
		case IAccessRule.K_ACCESSIBLE:
			return DLTKPluginImages
					.get(DLTKPluginImages.IMG_OBJS_NLS_TRANSLATE);
		case IAccessRule.K_DISCOURAGED:
			return DLTKPluginImages
					.get(DLTKPluginImages.IMG_OBJS_REFACTORING_WARNING);
		case IAccessRule.K_NON_ACCESSIBLE:
			return DLTKPluginImages
					.get(DLTKPluginImages.IMG_OBJS_REFACTORING_ERROR);
		}
		return null;
	}

	public static String getResolutionLabel(int kind) {
		switch (kind) {
		case IAccessRule.K_ACCESSIBLE:
			return NewWizardMessages.AccessRulesLabelProvider_kind_accessible;
		case IAccessRule.K_DISCOURAGED:
			return NewWizardMessages.AccessRulesLabelProvider_kind_discouraged;
		case IAccessRule.K_NON_ACCESSIBLE:
			return NewWizardMessages.AccessRulesLabelProvider_kind_non_accessible;
		}
		return ""; //$NON-NLS-1$
	}
}
