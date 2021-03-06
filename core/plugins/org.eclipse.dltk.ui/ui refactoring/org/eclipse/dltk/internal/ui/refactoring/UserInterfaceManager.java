/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *

 *******************************************************************************/
package org.eclipse.dltk.internal.ui.refactoring;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

public class UserInterfaceManager {

	private Map<Class<?>, Tuple> fMap = new HashMap<>();

	private static class Tuple {
		private Class<?> starter;
		private Class<?> wizard;

		public Tuple(Class<?> s, Class<?> w) {
			starter = s;
			wizard = w;
		}
	}

	protected void put(Class<?> processor, Class<?> starter, Class<?> wizard) {
		fMap.put(processor, new Tuple(starter, wizard));
	}

	public UserInterfaceStarter getStarter(Refactoring refactoring) {
		RefactoringProcessor processor = refactoring.getAdapter(RefactoringProcessor.class);
		if (processor == null)
			return null;
		Tuple tuple = null;
		Class<?> clazz = processor.getClass();
		do {
			tuple = fMap.get(clazz);
			clazz = clazz.getSuperclass();
		} while (tuple == null);
		try {
			UserInterfaceStarter starter = (UserInterfaceStarter) tuple.starter.getConstructor().newInstance();
			Class<?> wizardClass = tuple.wizard;
			Constructor<?> constructor = wizardClass.getConstructor(Refactoring.class);
			RefactoringWizard wizard = (RefactoringWizard) constructor.newInstance(refactoring);
			starter.initialize(wizard);
			return starter;
		} catch (NoSuchMethodException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		} catch (InstantiationException e) {
			return null;
		} catch (IllegalArgumentException e) {
			return null;
		} catch (InvocationTargetException e) {
			return null;
		}
	}
}
