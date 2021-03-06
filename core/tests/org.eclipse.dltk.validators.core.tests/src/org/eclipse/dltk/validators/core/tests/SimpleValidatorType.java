/*******************************************************************************
 * Copyright (c) 2005, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.validators.core.tests;

import org.eclipse.dltk.validators.core.AbstractValidatorType;
import org.eclipse.dltk.validators.core.ISourceModuleValidator;
import org.eclipse.dltk.validators.core.IValidator;
import org.eclipse.dltk.validators.core.ValidatorRuntime;

public class SimpleValidatorType extends AbstractValidatorType {
	private static final String ID = "org.eclipse.dltk.validators.core.tests.simpleValidator";

	@Override
	public IValidator createValidator(String id) {
		return new SimpleValidator(id, this);
	}

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public String getName() {
		return "Simple Test Validator";
	}

	@Override
	public String getNature() {
		return ValidatorRuntime.ANY_NATURE;
	}

	@Override
	public boolean isBuiltin() {
		return false;
	}

	@Override
	public boolean supports(Class validatorType) {
		return ISourceModuleValidator.class.equals(validatorType);
	}
}
