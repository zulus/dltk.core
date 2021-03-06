/*******************************************************************************
 * Copyright (c) 2005, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.dbgp.internal.packets;

import org.w3c.dom.Element;

public class DbgpStreamPacket extends DbgpPacket {
	private static final String STDERR = "stderr"; //$NON-NLS-1$

	private static final String STDOUT = "stdout"; //$NON-NLS-1$

	private final String type;

	private final String textContent;

	public DbgpStreamPacket(String type, String textContent, Element content) {
		super(content);

		if (!STDERR.equalsIgnoreCase(type) && !STDOUT.equalsIgnoreCase(type)) {
			throw new IllegalArgumentException(
					Messages.DbgpStreamPacket_invalidTypeValue);
		}

		if (textContent == null) {
			throw new IllegalArgumentException(
					Messages.DbgpStreamPacket_contentCannotBeNull);
		}

		this.type = type;
		this.textContent = textContent;
	}

	public boolean isStdout() {
		return STDOUT.equalsIgnoreCase(type);
	}

	public boolean isStderr() {
		return STDERR.equalsIgnoreCase(type);
	}

	public String getTextContent() {
		return textContent;
	}

	@Override
	public String toString() {
		return "DbgpStreamPacket (Type: " + type + "; Content: " + textContent //$NON-NLS-1$ //$NON-NLS-2$
				+ ";)"; //$NON-NLS-1$
	}
}
