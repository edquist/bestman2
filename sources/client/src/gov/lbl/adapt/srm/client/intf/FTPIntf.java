/**
 *
 * *** Copyright Notice ***
 *
 * BeStMan Copyright (c) 2013-2014, The Regents of the University of California, 
 * through Lawrence Berkeley National Laboratory (subject to receipt of any 
 * required approvals from the U.S. Dept. of Energy).  This software was 
 * developed under funding from the U.S. Department of Energy and is 
 * associated with the Berkeley Lab Scientific Data Management Group projects.
 * All rights reserved.
 * 
 * If you have questions about your rights to use or distribute this software, 
 * please contact Berkeley Lab's Technology Transfer Department at TTD@lbl.gov.
 * 
 * NOTICE.  This software was developed under funding from the 
 * U.S. Department of Energy.  As such, the U.S. Government has been granted 
 * for itself and others acting on its behalf a paid-up, nonexclusive, 
 * irrevocable, worldwide license in the Software to reproduce, prepare 
 * derivative works, and perform publicly and display publicly.  
 * Beginning five (5) years after the date permission to assert copyright is 
 * obtained from the U.S. Department of Energy, and subject to any subsequent 
 * five (5) year renewals, the U.S. Government is granted for itself and others
 * acting on its behalf a paid-up, nonexclusive, irrevocable, worldwide license
 * in the Software to reproduce, prepare derivative works, distribute copies to
 * the public, perform publicly and display publicly, and to permit others to
 * do so.
 *
*/
/**
 *
 * Email questions to SDMSUPPORT@LBL.GOV
 * Scientific Data Management Research Group
 * Lawrence Berkeley National Laboratory
 * http://sdm.lbl.gov/bestman
 *
*/

package gov.lbl.adapt.srm.client.intf;

import javax.swing.JPanel;
import javax.swing.JFrame;
import java.util.Vector;

import java.io.File;

import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;

import gov.lbl.adapt.srm.client.util.FileStatusGUI;
import gov.lbl.adapt.srm.client.exception.*;
import gov.lbl.adapt.srm.client.main.*;

public interface FTPIntf {
  public void enableTransferButton(boolean b, boolean ok);
  public void prepareView();
  public void updateView(FileStatusGUI fgui);
  public void refreshView();
  public void scrollView ();
  public void setCurrentTargetIndex(int idx);
  public boolean isRequestCancel();
  public JFrame getParentWindow();
  public GSSCredential initProxy() throws Exception;
  public GSSCredential checkProxy() throws ProxyNotFoundException;
  public boolean isGui();
  public boolean checkDiskSpaceFull(SharedObjectLock sLock,File file,long size) 
	throws DiskSpaceFullException;
  public void resetValues(String targetDir, boolean isOverwrite);
}
