/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 
 *******************************************************************************/
package org.eclipse.dltk.dbgp.internal.utils;

import org.eclipse.dltk.dbgp.internal.packets.DbgpNotifyPacket;
import org.eclipse.dltk.dbgp.internal.packets.DbgpResponsePacket;
import org.eclipse.dltk.dbgp.internal.packets.DbgpStreamPacket;
import org.w3c.dom.Element;

public class DbgpXmlPacketParser extends DbgpXmlParser {
	protected DbgpXmlPacketParser() {

	}

	public static DbgpResponsePacket parseResponsePacket(Element element) {
		final String ATTR_TRANSACTION_ID = "transaction_id"; //$NON-NLS-1$

		int id = Integer.parseInt(element.getAttribute(ATTR_TRANSACTION_ID));
		return new DbgpResponsePacket(element, id);
	}

	public static DbgpNotifyPacket parseNotifyPacket(Element element) {
		final String ATTR_NAME = "name"; //$NON-NLS-1$

		String name = element.getAttribute(ATTR_NAME);
		return new DbgpNotifyPacket(element, name);
	}

	public static DbgpStreamPacket parseStreamPacket(Element element) {
		final String ATTR_TYPE = "type"; //$NON-NLS-1$

		String type = element.getAttribute(ATTR_TYPE);
		String textContent = DbgpXmlParser.parseBase64Content(element);
		return new DbgpStreamPacket(type, textContent, element);
	}
}
