/*******************************************************************************
 * Copyright (c) 2000, 2018 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.callhierarchy;

import org.eclipse.dltk.internal.corext.callhierarchy.CallLocation;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

class LocationLabelProvider extends LabelProvider implements ITableLabelProvider {
	private static final int COLUMN_ICON = 0;
	private static final int COLUMN_LINE = 1;
	private static final int COLUMN_INFO = 2;

	LocationLabelProvider() {
		// Do nothing
	}

	@Override
	public String getText(Object element) {
		return getColumnText(element, COLUMN_INFO);
	}

	@Override
	public Image getImage(Object element) {
		return getColumnImage(element, COLUMN_ICON);
	}

	private String removeWhitespaceOutsideStringLiterals(CallLocation callLocation) {
		StringBuilder buf = new StringBuilder();
		boolean withinString = false;

		String s = callLocation.getCallText();
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);

			if (ch == '"') {
				withinString = !withinString;
			}

			if (withinString) {
				buf.append(ch);
			} else if (Character.isWhitespace(ch)) {
				if ((buf.length() == 0) || !Character.isWhitespace(buf.charAt(buf.length() - 1))) {
					if (ch != ' ') {
						ch = ' ';
					}

					buf.append(ch);
				}
			} else {
				buf.append(ch);
			}
		}

		return buf.toString();
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == COLUMN_ICON) {
			return DLTKPluginImages.get(DLTKPluginImages.IMG_OBJS_SEARCH_OCCURRENCE);
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof CallLocation) {
			CallLocation callLocation = (CallLocation) element;

			switch (columnIndex) {
			case COLUMN_LINE:
				int lineNumber = callLocation.getLineNumber();
				if (lineNumber == CallLocation.UNKNOWN_LINE_NUMBER) {
					return CallHierarchyMessages.LocationLabelProvider_unknown;
				}
				return String.valueOf(lineNumber);
			case COLUMN_INFO:
				return removeWhitespaceOutsideStringLiterals(callLocation);
			}
		}

		return ""; //$NON-NLS-1$
	}
}
