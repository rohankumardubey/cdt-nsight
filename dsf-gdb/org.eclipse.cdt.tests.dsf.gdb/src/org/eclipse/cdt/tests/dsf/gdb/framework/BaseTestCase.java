/*******************************************************************************
 * Copyright (c) 2007 Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ericsson			  - Initial Implementation
 *******************************************************************************/
package org.eclipse.cdt.tests.dsf.gdb.framework;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.dsf.gdb.IGDBLaunchConfigurationConstants;
import org.eclipse.cdt.dsf.gdb.launching.GdbLaunch;
import org.eclipse.cdt.dsf.mi.service.command.events.MIStoppedEvent;
import org.eclipse.cdt.tests.dsf.gdb.launching.TestsPlugin;
import org.eclipse.cdt.utils.spawner.ProcessFactory;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * This is the base class for the GDB/MI Unit tests.
 * It provides the @Before and @After methods which setup
 * and teardown the launch, for each test.
 * If these methods are overwridden by a subclass, the new method
 * must call super.baseSetup or super.baseTeardown itself, if this
 * code is to be run.
 */
public class BaseTestCase {

	public static final String ATTR_DEBUG_SERVER_NAME = TestsPlugin.PLUGIN_ID + ".DEBUG_SERVER_NAME";
	private static final String DEFAULT_TEST_APP = "data/launch/bin/GDBMIGenericTestApp";
	
    private static GdbLaunch fLaunch;
	private static Map<String, Object> attrs = new HashMap<String, Object>();
    private static Process gdbserverProc = null;
    
	private MIStoppedEvent fInitialStoppedEvent = null;
	
    public GdbLaunch getGDBLaunch() { return fLaunch; }
    
    public static void setLaunchAttribute(String key, Object value) { 
    	attrs.put(key, value);
    }
    
    public MIStoppedEvent getInitialStoppedEvent() { return fInitialStoppedEvent; }
    
    @BeforeClass
    public static void baseBeforeClassMethod() {
		// Setup information for the launcher
   		attrs.put(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_NAME, DEFAULT_TEST_APP);

		attrs.put(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_STOP_AT_MAIN, true);
		attrs.put(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_STOP_AT_MAIN_SYMBOL, ICDTLaunchConfigurationConstants.DEBUGGER_STOP_AT_MAIN_SYMBOL_DEFAULT);
		attrs.put(IGDBLaunchConfigurationConstants.ATTR_GDB_INIT, ".gdbinit");

    	if (attrs.get(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_START_MODE) == null) {
    		attrs.put(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_START_MODE, ICDTLaunchConfigurationConstants.DEBUGGER_MODE_RUN );
    	}
		
		// Set these up in case we will be running Remote tests.  They will be ignored if we don't
    	attrs.put(ATTR_DEBUG_SERVER_NAME, "gdbserver");
    	attrs.put(IGDBLaunchConfigurationConstants.ATTR_REMOTE_TCP, true);
    	attrs.put(IGDBLaunchConfigurationConstants.ATTR_HOST, "localhost");
    	attrs.put(IGDBLaunchConfigurationConstants.ATTR_PORT, "9999");
    }
    
    @Before
 	public void baseBeforeMethod() throws Exception {
    	System.out.println("====================================================================");
		System.out.println("Launching test application: " + attrs.get(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_NAME));
		System.out.println("====================================================================");
		
 		// First check if we should launch gdbserver in the case of a remote session
		launchGdbServer();
		
 		ILaunchManager launchMgr = DebugPlugin.getDefault().getLaunchManager();
 		ILaunchConfigurationType lcType = launchMgr.getLaunchConfigurationType("org.eclipse.cdt.tests.dsf.gdb.TestLaunch");
 		assert lcType != null;

 		ILaunchConfigurationWorkingCopy lcWorkingCopy = lcType.newInstance(
 				null, 
 				launchMgr.generateUniqueLaunchConfigurationNameFrom("Test Launch")); //$NON-NLS-1$
 		assert lcWorkingCopy != null;
 		lcWorkingCopy.setAttributes(attrs);

 		final ILaunchConfiguration lc = lcWorkingCopy.doSave();
 		assert lc != null;

 		fLaunch = (GdbLaunch)lc.launch(ILaunchManager.DEBUG_MODE, new NullProgressMonitor());
 		assert fLaunch != null;
 		 		
		try {
			// Also wait for the program to stop before allowing tests to start
			// This should be done as soon as we have the launch, to avoid missing the Stopped
			// event.  If we do miss it, we'll just have a 10 second delay.
			final ServiceEventWaitor<MIStoppedEvent> eventWaitor =
				new ServiceEventWaitor<MIStoppedEvent>(
						fLaunch.getSession(),
						MIStoppedEvent.class);
			fInitialStoppedEvent = eventWaitor.waitForEvent(10000);
		} catch (Exception e) {}

 		// If we started a gdbserver add it to the launch to make sure it is killed at the end
 		if (gdbserverProc != null) {
            DebugPlugin.newProcess(fLaunch, gdbserverProc, "gdbserver");
 		}
 		
 		// Now initialize our SyncUtility, since we have the launcher
 		SyncUtil.initialize(fLaunch.getSession());

	}

 	@After
	public void baseAfterMethod() throws Exception {
 		if (fLaunch != null) {
 			fLaunch.terminate();
            fLaunch = null;
 		}
	}
 	
 	@AfterClass
 	public static void baseAfterClassMehod() throws Exception {
 	}
 	
 	/**
 	 * This method start gdbserver on the localhost.
 	 * If the user specified a different host, things won't work.
 	 */
 	private static void launchGdbServer() {
 		if (attrs.get(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_START_MODE)
 				              .equals(IGDBLaunchConfigurationConstants.DEBUGGER_MODE_REMOTE)) {
 			if (attrs.get(IGDBLaunchConfigurationConstants.ATTR_REMOTE_TCP).equals(Boolean.TRUE)) {
 				String server = (String)attrs.get(ATTR_DEBUG_SERVER_NAME);
 				String port = (String)attrs.get(IGDBLaunchConfigurationConstants.ATTR_PORT);
 				String program = (String)attrs.get(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_NAME);
 				String commandLine = server + " :" + port + " " + program;
 				try {
                    System.out.println("Staring gdbserver with command: " + commandLine);

 					gdbserverProc = ProcessFactory.getFactory().exec(commandLine);
                    Reader r = new InputStreamReader(gdbserverProc.getErrorStream());
                    BufferedReader reader = new BufferedReader(r);
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                        line = line.trim();
                        if (line.startsWith("Listening on port")) {
                            break;
                        }
                    }
 				} catch (Exception e) {
 					System.out.println("Error while launching command: " + commandLine);
 					e.printStackTrace();
 					assert false;
 				} 				
 			}
 		}
	}
}