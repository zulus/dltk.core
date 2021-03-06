/*******************************************************************************
 * Copyright (c) 2006, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.ui.text.completion;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;

public final class ProposalSorterRegistry {
	private static final String EXTENSION_POINT = "scriptCompletionProposalSorters"; //$NON-NLS-1$
	private static final String DEFAULT_ID = "org.eclipse.dltk.ui.RelevanceSorter"; //$NON-NLS-1$

	private static ProposalSorterRegistry fInstance;

	public static ProposalSorterRegistry getDefault() {
		if (fInstance == null) {
			synchronized (ProposalSorterRegistry.class) {
				if (fInstance == null) {
					fInstance = new ProposalSorterRegistry(DLTKUIPlugin.getDefault().getPreferenceStore(),
							PreferenceConstants.CODEASSIST_SORTER);
				}
			}
		}

		return fInstance;
	}

	private final IPreferenceStore fPreferenceStore;
	private final String fKey;

	private Map<String, ProposalSorterHandle> fSorters = null;
	private ProposalSorterHandle fDefaultSorter;

	private ProposalSorterRegistry(final IPreferenceStore preferenceStore, final String key) {
		Assert.isTrue(preferenceStore != null);
		Assert.isTrue(key != null);
		fPreferenceStore = preferenceStore;
		fKey = key;
	}

	public ProposalSorterHandle getCurrentSorter() {
		ensureSortersRead();
		String id = fPreferenceStore.getString(fKey);
		ProposalSorterHandle sorter = fSorters.get(id);
		return sorter != null ? sorter : fDefaultSorter;
	}

	private synchronized void ensureSortersRead() {
		if (fSorters != null)
			return;

		Map<String, ProposalSorterHandle> sorters = new LinkedHashMap<>();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		List<IConfigurationElement> elements = new ArrayList<>(
				Arrays.asList(registry.getConfigurationElementsFor(DLTKUIPlugin.PLUGIN_ID, EXTENSION_POINT)));

		for (IConfigurationElement element : elements) {

			try {

				ProposalSorterHandle handle = new ProposalSorterHandle(element);
				final String id = handle.getId();
				sorters.put(id, handle);
				if (DEFAULT_ID.equals(id))
					fDefaultSorter = handle;

			} catch (InvalidRegistryObjectException x) {
				/*
				 * Element is not valid any longer as the contributing plug-in was unloaded or
				 * for some other reason. Do not include the extension in the list and inform
				 * the user about it.
				 */
				String message = MessageFormat.format(
						ScriptTextMessages.CompletionProposalComputerRegistry_invalid_message, element.toString());
				IStatus status = new Status(IStatus.WARNING, DLTKUIPlugin.PLUGIN_ID, IStatus.OK, message, x);
				informUser(status);
			}
		}

		fSorters = sorters;
	}

	private void informUser(IStatus status) {
		DLTKUIPlugin.log(status);
		String title = ScriptTextMessages.CompletionProposalComputerRegistry_error_dialog_title;
		String message = status.getMessage();
		MessageDialog.openError(DLTKUIPlugin.getActiveWorkbenchShell(), title, message);
	}

	public ProposalSorterHandle[] getSorters() {
		ensureSortersRead();
		Collection<ProposalSorterHandle> sorters = fSorters.values();
		return sorters.toArray(new ProposalSorterHandle[sorters.size()]);
	}

	public void select(ProposalSorterHandle handle) {
		Assert.isTrue(handle != null);
		String id = handle.getId();

		fPreferenceStore.setValue(fKey, id);
	}
}
