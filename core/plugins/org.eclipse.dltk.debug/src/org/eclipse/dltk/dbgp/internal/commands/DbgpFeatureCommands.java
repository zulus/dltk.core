/*******************************************************************************
 * Copyright (c) 2005, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.dbgp.internal.commands;

import org.eclipse.dltk.dbgp.DbgpBaseCommands;
import org.eclipse.dltk.dbgp.DbgpRequest;
import org.eclipse.dltk.dbgp.IDbgpCommunicator;
import org.eclipse.dltk.dbgp.IDbgpFeature;
import org.eclipse.dltk.dbgp.commands.IDbgpFeatureCommands;
import org.eclipse.dltk.dbgp.exceptions.DbgpException;
import org.eclipse.dltk.dbgp.internal.utils.DbgpXmlEntityParser;
import org.eclipse.dltk.dbgp.internal.utils.DbgpXmlParser;

public class DbgpFeatureCommands extends DbgpBaseCommands
		implements IDbgpFeatureCommands {

	private static final String FEATURE_SET_COMMAND = "feature_set"; //$NON-NLS-1$

	private static final String FEATURE_GET_COMMAND = "feature_get"; //$NON-NLS-1$

	public DbgpFeatureCommands(IDbgpCommunicator communicator) {
		super(communicator);
	}

	@Override
	public IDbgpFeature getFeature(String featureName) throws DbgpException {
		DbgpRequest request = createRequest(FEATURE_GET_COMMAND);
		request.addOption("-n", featureName); //$NON-NLS-1$
		return DbgpXmlEntityParser.parseFeature(communicate(request));
	}

	@Override
	public boolean setFeature(String featureName, String featureValue)
			throws DbgpException {
		DbgpRequest request = createRequest(FEATURE_SET_COMMAND);
		request.addOption("-n", featureName); //$NON-NLS-1$
		request.addOption("-v", featureValue); //$NON-NLS-1$
		return DbgpXmlParser.parseSuccess(communicate(request));
	}
}
