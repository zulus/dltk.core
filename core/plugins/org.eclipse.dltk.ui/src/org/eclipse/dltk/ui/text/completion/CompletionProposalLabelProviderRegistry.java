/*******************************************************************************
 * Copyright (c) 2010, 2018 xored software, Inc. and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.ui.text.completion;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.utils.NatureExtensionManager;

public class CompletionProposalLabelProviderRegistry {

	public static CompletionProposalLabelProvider create(String natureId) {
		final NatureExtensionManager<CompletionProposalLabelProvider> manager = new NatureExtensionManager<CompletionProposalLabelProvider>(
				DLTKUIPlugin.PLUGIN_ID + ".completion",
				CompletionProposalLabelProvider.class) {
			@Override
			protected boolean isValidElement(IConfigurationElement element) {
				return "proposalLabelProvider".equals(element.getName());
			}
		};
		Object[] instances = manager.getInstances(natureId);
		if (instances != null && instances.length != 0) {
			return (CompletionProposalLabelProvider) instances[0];
		}
		return new CompletionProposalLabelProvider();
	}
}
