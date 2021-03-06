/*******************************************************************************
 * Copyright (c) 2000, 2018 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.internal.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IAccessRule;
import org.eclipse.dltk.core.IBuildpathAttribute;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.internal.core.util.Messages;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Internal model element to represent a user library and code to serialize /
 * deserialize.
 */
public class UserLibrary {

	private static final String CURRENT_VERSION = "1"; //$NON-NLS-1$

	private static final String TAG_VERSION = "version"; //$NON-NLS-1$
	private static final String TAG_USERLIBRARY = "userlibrary"; //$NON-NLS-1$
	private static final String TAG_PATH = "path"; //$NON-NLS-1$
	private static final String TAG_ARCHIVE = "archive"; //$NON-NLS-1$
	private static final String TAG_SYSTEMLIBRARY = "systemlibrary"; //$NON-NLS-1$
	private static final String ATTRIBUTE_PREFIX = "__attribute__"; //$NON-NLS-1$

	private boolean isSystemLibrary;
	private IBuildpathEntry[] entries;
	private Map<String, String> attributes;

	public UserLibrary(IBuildpathEntry[] entries, boolean isSystemLibrary) {
		this(entries, isSystemLibrary, null);
	}

	public UserLibrary(IBuildpathEntry[] entries, boolean isSystemLibrary, Map<String, String> attributes) {
		Assert.isNotNull(entries);
		this.entries = entries;
		this.isSystemLibrary = isSystemLibrary;
		this.attributes = new HashMap<>();
		if (attributes != null) {
			this.attributes.putAll(attributes);
		}
	}

	public IBuildpathEntry[] getEntries() {
		return this.entries;
	}

	public boolean isSystemLibrary() {
		return this.isSystemLibrary;
	}

	public String getAttribute(String name) {
		return attributes.get(name);
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj.getClass() == getClass()) {
			UserLibrary other = (UserLibrary) obj;
			if (this.entries.length == other.entries.length && this.isSystemLibrary == other.isSystemLibrary
					&& attributes.size() == other.attributes.size()) {
				if (!Arrays.equals(this.entries, other.entries)) {
					return false;
				}
				if (!this.attributes.equals(other.attributes)) {
					return false;
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = 0;
		if (this.isSystemLibrary()) {
			hashCode++;
		}
		for (int i = 0; i < this.entries.length; i++) {
			hashCode = hashCode * 17 + this.entries[i].hashCode();
		}
		return hashCode;
	}

	public static String serialize(IBuildpathEntry[] entries, boolean isSystemLibrary) throws IOException {
		return serialize(entries, isSystemLibrary, null);
	}

	public static String serialize(IBuildpathEntry[] entries, boolean isSystemLibrary, Map<String, String> attributes)
			throws IOException {
		ByteArrayOutputStream s = new ByteArrayOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(s, StandardCharsets.UTF_8); // $NON-NLS-1$
		XMLWriter xmlWriter = new XMLWriter(writer, null/*
														 * use the workspace line delimiter
														 */, true/*
																	 * print XML version
																	 */);

		HashMap<String, String> library = new HashMap<>();
		library.put(TAG_VERSION, String.valueOf(CURRENT_VERSION));
		library.put(TAG_SYSTEMLIBRARY, String.valueOf(isSystemLibrary));
		if (attributes != null) {
			for (String key : attributes.keySet()) {
				library.put(ATTRIBUTE_PREFIX + key, attributes.get(key));
			}
		}
		xmlWriter.printTag(TAG_USERLIBRARY, library, true, true, false);

		for (int i = 0, length = entries.length; i < length; ++i) {
			BuildpathEntry cpEntry = (BuildpathEntry) entries[i];

			HashMap<String, String> archive = new HashMap<>();
			archive.put(TAG_PATH, cpEntry.getPath().toString());

			boolean hasExtraAttributes = cpEntry.extraAttributes != null && cpEntry.extraAttributes.length != 0;
			boolean hasRestrictions = cpEntry.getAccessRuleSet() != null; // access
			// rule
			// set
			// is
			// null
			// if
			// no
			// access
			// rules
			xmlWriter.printTag(TAG_ARCHIVE, archive, true, true, !(hasExtraAttributes || hasRestrictions));

			// write extra attributes if necessary
			if (hasExtraAttributes) {
				cpEntry.encodeExtraAttributes(xmlWriter, true, true);
			}

			// write extra attributes and restriction if necessary
			if (hasRestrictions) {
				cpEntry.encodeAccessRules(xmlWriter, true, true);
			}

			// write archive end tag if necessary
			if (hasExtraAttributes || hasRestrictions) {
				xmlWriter.endTag(TAG_ARCHIVE, true/* insert tab */, true/*
																		 * insert new line
																		 */);
			}
		}
		xmlWriter.endTag(TAG_USERLIBRARY, true/* insert tab */, true/*
																	 * insert new line
																	 */);
		writer.flush();
		writer.close();
		return s.toString("UTF8");//$NON-NLS-1$
	}

	public static UserLibrary createFromString(Reader reader) throws IOException {
		Element cpElement;
		try {
			DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			cpElement = parser.parse(new InputSource(reader)).getDocumentElement();
		} catch (SAXException e) {
			throw new IOException(Messages.file_badFormat);
		} catch (ParserConfigurationException e) {
			throw new IOException(Messages.file_badFormat);
		} finally {
			reader.close();
		}

		if (!cpElement.getNodeName().equalsIgnoreCase(TAG_USERLIBRARY)) {
			throw new IOException(Messages.file_badFormat);
		}
		// String version= cpElement.getAttribute(TAG_VERSION);
		// in case we update the format: add code to read older versions

		boolean isSystem = Boolean.valueOf(cpElement.getAttribute(TAG_SYSTEMLIBRARY)).booleanValue();

		Map<String, String> attributes = new HashMap<>();
		for (int i = 0; i < cpElement.getAttributes().getLength(); i++) {
			Node node = cpElement.getAttributes().item(i);
			if (node.getNodeName().startsWith(ATTRIBUTE_PREFIX)) {
				String name = node.getNodeName().substring(ATTRIBUTE_PREFIX.length());
				attributes.put(name, node.getNodeValue());
			}
		}

		NodeList list = cpElement.getChildNodes();
		int length = list.getLength();

		ArrayList<IBuildpathEntry> res = new ArrayList<>(length);
		for (int i = 0; i < length; ++i) {
			Node node = list.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				if (element.getNodeName().equals(TAG_ARCHIVE)) {
					String path = element.getAttribute(TAG_PATH);
					NodeList children = element.getElementsByTagName("*"); //$NON-NLS-1$
					boolean[] foundChildren = new boolean[children.getLength()];
					NodeList attributeList = BuildpathEntry.getChildAttributes(BuildpathEntry.TAG_ATTRIBUTES, children,
							foundChildren);
					IBuildpathAttribute[] extraAttributes = BuildpathEntry.decodeExtraAttributes(attributeList);
					attributeList = BuildpathEntry.getChildAttributes(BuildpathEntry.TAG_ACCESS_RULES, children,
							foundChildren);
					IAccessRule[] accessRules = BuildpathEntry.decodeAccessRules(attributeList);
					IBuildpathEntry entry = DLTKCore.newLibraryEntry(Path.fromPortableString(path), accessRules,
							extraAttributes, false, true);
					res.add(entry);
				}
			}
		}

		IBuildpathEntry[] entries = res.toArray(new IBuildpathEntry[res.size()]);

		return new UserLibrary(entries, isSystem, attributes);
	}

	@Override
	public String toString() {
		if (this.entries == null)
			return "null"; //$NON-NLS-1$
		StringBuilder buffer = new StringBuilder();
		int length = this.entries.length;
		for (int i = 0; i < length; i++) {
			buffer.append(this.entries[i].toString() + '\n');
		}
		return buffer.toString();
	}
}
