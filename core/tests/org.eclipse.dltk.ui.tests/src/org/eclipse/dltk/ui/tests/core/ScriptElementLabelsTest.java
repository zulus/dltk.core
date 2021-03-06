/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 
 *******************************************************************************/
package org.eclipse.dltk.ui.tests.core;

import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.tests.model.AbstractModelTests;
import org.eclipse.dltk.ui.ScriptElementLabels;
import org.eclipse.dltk.ui.tests.ScriptProjectHelper;
import org.eclipse.dltk.ui.tests.StringAsserts;

public class ScriptElementLabelsTest extends AbstractModelTests {

	public static final String PROJECT_NAME = "TestSetupProject";

	// private static final Class THIS= ScriptElementLabelsTest.class;

	private IScriptProject fJProject1;

	public ScriptElementLabelsTest(String name) {
		super(name);
	}

	@Override
	public void setUpSuite() throws Exception {
		super.setUpSuite();
		fJProject1 = ScriptProjectHelper.createScriptProject(PROJECT_NAME);
	}

	@Override
	public void tearDownSuite() throws Exception {
		deleteProject(PROJECT_NAME);
		super.tearDownSuite();
	}

	public static void assertEqualString(String actual, String expected) {
		StringAsserts.assertEqualString(actual, expected);
	}

	public void testTypeLabelOuter() throws Exception {

		IProjectFragment sourceFolder = ScriptProjectHelper.addSourceContainer(
				fJProject1, "src");
		//
		IScriptFolder pack1 = sourceFolder.getScriptFolder(""); // sourceFolder.createScriptFolder("org.test",
		// false, null);

		StringBuffer buf = new StringBuffer();
		// buf.append("namespace eval Outer {\n");
		// buf.append("}\n");
		buf.append("# parseme!\n");
		buf.append("enterType Outer\n");
		buf.append("exitType");

		String content = buf.toString();
		ISourceModule cu = pack1.createSourceModule("Outer.txt", content,
				false, null);

		IModelElement elem = cu.getElementAt(content.indexOf("Outer"));
		String lab = ScriptElementLabels.getDefault().getTextLabel(elem,
				ScriptElementLabels.T_FULLY_QUALIFIED);
		assertEqualString(lab, "src Outer");

		lab = ScriptElementLabels.getDefault().getTextLabel(elem,
				ScriptElementLabels.T_CONTAINER_QUALIFIED);
		assertEqualString(lab, "Outer");

		lab = ScriptElementLabels.getDefault().getTextLabel(elem,
				ScriptElementLabels.T_POST_QUALIFIED);
		assertEqualString(lab, "Outer");

		lab = ScriptElementLabels.getDefault().getTextLabel(
				elem,
				ScriptElementLabels.T_FULLY_QUALIFIED
						| ScriptElementLabels.APPEND_ROOT_PATH);
		assertEqualString(lab, "src Outer - TestSetupProject/src");

		lab = ScriptElementLabels.getDefault().getTextLabel(
				elem,
				ScriptElementLabels.T_FULLY_QUALIFIED
						| ScriptElementLabels.PREPEND_ROOT_PATH);
		assertEqualString(lab, "TestSetupProject/src - src Outer");
	}

	public void testTypeLabelInner() throws Exception {

		IProjectFragment sourceFolder = ScriptProjectHelper.addSourceContainer(
				fJProject1, "src");

		IScriptFolder pack1 = sourceFolder.getScriptFolder("");

		StringBuffer buf = new StringBuffer();

		// buf.append("package require Tk\n");
		// buf.append("namespace eval Outer {\n");
		// buf.append("    proc foo{vec} {\n");
		// buf.append("    }\n");
		// buf.append("    namespace eval Inner {\n");
		// buf.append("        proc inner {vec} {\n");
		// buf.append("        }\n");
		// buf.append("    }\n");
		// buf.append("}\n");

		buf.append("# parseme!\n");
		buf.append("enterType Outer\n");
		buf.append("enterMethod foo\n");
		buf.append("exitMethod\n");
		buf.append("enterType Inner\n");
		buf.append("enterMethod inner\n");
		buf.append("exitMethod\n");
		buf.append("exitType\n");
		buf.append("exitType");

		String content = buf.toString();
		ISourceModule cu = pack1.createSourceModule("Outer2.txt", content,
				false, null);

		IModelElement elem = cu.getElementAt(content.indexOf("Inner"));

		String lab = ScriptElementLabels.getDefault().getTextLabel(elem,
				ScriptElementLabels.T_FULLY_QUALIFIED);
		assertEqualString(lab, "src Outer.Inner");

		lab = ScriptElementLabels.getDefault().getTextLabel(elem,
				ScriptElementLabels.T_CONTAINER_QUALIFIED);
		assertEqualString(lab, "Outer.Inner");

		lab = ScriptElementLabels.getDefault().getTextLabel(elem,
				ScriptElementLabels.T_POST_QUALIFIED);
		assertEqualString(lab, "Inner - src Outer");

		lab = ScriptElementLabels.getDefault().getTextLabel(
				elem,
				ScriptElementLabels.T_FULLY_QUALIFIED
						| ScriptElementLabels.APPEND_ROOT_PATH);
		assertEqualString(lab, "src Outer.Inner - TestSetupProject/src");

		lab = ScriptElementLabels.getDefault().getTextLabel(
				elem,
				ScriptElementLabels.T_FULLY_QUALIFIED
						| ScriptElementLabels.PREPEND_ROOT_PATH);
		assertEqualString(lab, "TestSetupProject/src - src Outer.Inner");
	}

	public void testTypeLabelLocal() throws Exception {

		IProjectFragment sourceFolder = ScriptProjectHelper.addSourceContainer(
				fJProject1, "src");

		IScriptFolder pack1 = sourceFolder.createScriptFolder("", false, null);

		StringBuffer buf = new StringBuffer();

		// buf.append("package require Vector\n");
		// buf.append("namespace eval Outer {\n");
		// buf.append("    proc foo {vec} {\n");
		// buf.append("        namespace eval Local {\n");
		// buf.append("        }\n");
		// buf.append("    }\n");
		// buf.append("}\n");

		buf.append("# parseme!\n");
		buf.append("enterType Outer\n");
		buf.append("enterMethod foo\n");
		buf.append("enterType Local\n");
		buf.append("exitType\n");
		buf.append("exitMethod\n");
		buf.append("exitType");

		String content = buf.toString();
		ISourceModule cu = pack1.createSourceModule("Outer3.txt", content,
				false, null);

		IModelElement elem = cu.getElementAt(content.indexOf("Local"));

		String lab = ScriptElementLabels.getDefault().getTextLabel(elem,
				ScriptElementLabels.T_FULLY_QUALIFIED);
		assertEqualString(lab, "src src Outer.foo().Local");

		lab = ScriptElementLabels.getDefault().getTextLabel(elem,
				ScriptElementLabels.T_CONTAINER_QUALIFIED);
		assertEqualString(lab, "src Outer.foo().Local");

		lab = ScriptElementLabels.getDefault().getTextLabel(elem,
				ScriptElementLabels.T_POST_QUALIFIED);
		assertEqualString(lab, "Local - src Outer.foo()");

		lab = ScriptElementLabels.getDefault().getTextLabel(
				elem,
				ScriptElementLabels.T_FULLY_QUALIFIED
						| ScriptElementLabels.APPEND_ROOT_PATH);
		assertEqualString(lab,
				"src src Outer.foo().Local - TestSetupProject/src");

		lab = ScriptElementLabels.getDefault().getTextLabel(
				elem,
				ScriptElementLabels.T_FULLY_QUALIFIED
						| ScriptElementLabels.PREPEND_ROOT_PATH);
		assertEqualString(lab,
				"TestSetupProject/src - src src Outer.foo().Local");
	}

	/**
	 * @return
	 */
	public static Suite suite() {
		return new Suite(ScriptElementLabelsTest.class);
	}

}
