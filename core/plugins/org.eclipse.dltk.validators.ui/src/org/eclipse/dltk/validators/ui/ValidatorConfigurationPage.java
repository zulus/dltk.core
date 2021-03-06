/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 
 *******************************************************************************/
package org.eclipse.dltk.validators.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.dltk.validators.core.IValidator;
import org.eclipse.swt.widgets.Composite;

public abstract class ValidatorConfigurationPage {
	protected IValidator validator;
	private IStatusHandler statusHandler;

	public interface IStatusHandler {
		public void updateStatus();
	}

	public void setValidator(IValidator validator) {
		this.validator = validator;
	}

	public IValidator getValidator() {
		return this.validator;
	}

	public abstract void applyChanges();

	public abstract void createControl(Composite parent, int columns);

	public String getErrorMessage() {
		return null;
	}

	/**
	 * Called from new validator dialog, to switch configurations.
	 */
	public void dispose() {
	}

	public IStatus getStatus() {
		return null;
	}

	public void setStatusHandler(IStatusHandler handler) {
		this.statusHandler = handler;
	}

	public void updateStatus() {
		if (this.statusHandler != null) {
			this.statusHandler.updateStatus();
		}
	}
}
