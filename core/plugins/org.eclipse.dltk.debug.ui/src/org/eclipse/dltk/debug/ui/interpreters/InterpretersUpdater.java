/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.debug.ui.interpreters;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.debug.ui.DLTKDebugUIPlugin;
import org.eclipse.dltk.internal.debug.ui.interpreters.InterpretersMessages;
import org.eclipse.dltk.internal.launching.InterpreterDefinitionsContainer;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterInstallType;
import org.eclipse.dltk.launching.ScriptRuntime;
import org.eclipse.dltk.launching.ScriptRuntime.DefaultInterpreterEntry;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * Processes add/removed/changed Interpreters.
 */
public class InterpretersUpdater {

	// The interpreters defined when this updated is instantiated
	private InterpreterDefinitionsContainer fOriginalInterpreters;

	/**
	 * Constructs a new Interpreter updater to update Interpreter install
	 * settings.
	 */
	public InterpretersUpdater() {
		saveCurrentAsOriginal();
	}

	private void saveCurrentAsOriginal() {
		fOriginalInterpreters = new InterpreterDefinitionsContainer();

		final DefaultInterpreterEntry[] entries = ScriptRuntime
				.getDefaultInterpreterIDs();
		for (int i = 0; i < entries.length; i++) {
			final DefaultInterpreterEntry entry = entries[i];

			IInterpreterInstall def = ScriptRuntime
					.getDefaultInterpreterInstall(entry);

			if (def != null) {
				fOriginalInterpreters.setDefaultInterpreterInstallCompositeID(
						entry,
						ScriptRuntime.getCompositeIdFromInterpreter(def));
			}
		}

		final IInterpreterInstallType[] types = ScriptRuntime
				.getInterpreterInstallTypes();
		for (int i = 0; i < types.length; i++) {
			IInterpreterInstall[] installs = types[i].getInterpreterInstalls();
			if (installs != null) {
				for (int j = 0; j < installs.length; j++) {
					fOriginalInterpreters.addInterpreter(installs[j]);
				}
			}
		}
	}

	/**
	 * Updates Interpreter settings and returns whether the update was
	 * successful.
	 *
	 * @param interpreters
	 *            new installed InterpreterEnvironments
	 * @param defaultInterpreters
	 *            new default Interpreter
	 * @return whether the update was successful
	 */
	public boolean updateInterpreterSettings(String langNatureId,
			IInterpreterInstall[] interpreters,
			IInterpreterInstall[] defaultInterpreters) {
		// Create a Interpreter definition container
		InterpreterDefinitionsContainer container = new InterpreterDefinitionsContainer();

		// Default interpreter id for natureId
		final Set<String> envIds = new HashSet<>();
		if (defaultInterpreters != null) {
			for (int i = 0; i < defaultInterpreters.length; i++) {
				final String defaultId = ScriptRuntime
						.getCompositeIdFromInterpreter(defaultInterpreters[i]);
				final String environmentId = defaultInterpreters[i]
						.getEnvironmentId();
				if (environmentId != null) {
					DefaultInterpreterEntry entry = new DefaultInterpreterEntry(
							langNatureId, environmentId);
					container.setDefaultInterpreterInstallCompositeID(entry,
							defaultId);
					envIds.add(environmentId);
				}
			}
		}
		for (IEnvironment environment : EnvironmentManager.getEnvironments()) {
			if (!envIds.contains(environment.getId())) {
				DefaultInterpreterEntry entry = new DefaultInterpreterEntry(
						langNatureId, environment.getId());
				container.setDefaultInterpreterInstallCompositeID(entry, null);
			}
		}

		// Interpreters for natureId
		for (int i = 0; i < interpreters.length; i++) {
			container.addInterpreter(interpreters[i]);
		}

		// Default interpreters for other languages
		for (final DefaultInterpreterEntry entry : fOriginalInterpreters
				.getInterpreterNatures()) {
			if (!langNatureId.equals(entry.getNature())) {
				final String defaultId = fOriginalInterpreters
						.getDefaultInterpreterInstallCompositeID(entry);
				container.setDefaultInterpreterInstallCompositeID(entry,
						defaultId);
			}
		}

		// Save interpreters from other languages to the container
		for (final IInterpreterInstall install : fOriginalInterpreters
				.getInterpreterList()) {
			if (!langNatureId.equals(
					install.getInterpreterInstallType().getNatureId())) {
				container.addInterpreter(install);
			}
		}

		// Generate XML for the interpreter definitions and save it as the new
		// value of the Interpreter preference
		saveInterpreterDefinitions(container);

		saveCurrentAsOriginal();

		return true;
	}

	private void saveInterpreterDefinitions(
			final InterpreterDefinitionsContainer container) {
		IRunnableWithProgress runnable = monitor -> {
			try {
				monitor.beginTask(InterpretersMessages.InterpretersUpdater_0,
						100);
				final String xml = container.getAsXML();
				monitor.worked(40);
				ScriptRuntime.getPreferences()
						.put(ScriptRuntime.PREF_INTERPRETER_XML, xml);
				monitor.worked(30);
				ScriptRuntime.savePreferences();
				monitor.worked(30);
			} catch (IOException ioe) {
				DLTKDebugUIPlugin.log(ioe);
			} catch (ParserConfigurationException e1) {
				DLTKDebugUIPlugin.log(e1);
			} catch (TransformerException e2) {
				DLTKDebugUIPlugin.log(e2);
			} finally {
				monitor.done();
			}

		};
		try {
			DLTKDebugUIPlugin.getDefault().getWorkbench().getProgressService()
					.busyCursorWhile(runnable);
		} catch (InvocationTargetException e) {
			DLTKDebugUIPlugin.log(e);
		} catch (InterruptedException e) {
			DLTKDebugUIPlugin.log(e);
		}
	}
}
