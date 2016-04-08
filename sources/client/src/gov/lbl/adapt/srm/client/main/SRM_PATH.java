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

package gov.lbl.adapt.srm.client.main;

import java.util.Vector;
import java.util.Hashtable;

//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
// SRM_PATH
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

public class SRM_PATH {

private Vector fids = new Vector ();
private Vector sub_path = new Vector ();
private String dir="";  
private boolean isDir;

//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
// SRM_PATH
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

public SRM_PATH () throws Exception { 
}


//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
// setFids
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

public void setFids (Object obj) {
  fids.addElement(obj);
}


//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
// setSubPath 
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

public void setSubPath (Object obj) {
  sub_path.addElement(obj);
}

//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
// getFids
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

public Vector getFids() {
  return fids;
}

//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
// getFids
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

public Vector getSubPath() {
  return sub_path;
}

//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
// getFids
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

public SRM_FILE getFids(int i) {
  return (SRM_FILE)fids.elementAt(i);
}

//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
// getSubPath
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

public SRM_PATH getSubPath(int i) {
  return (SRM_PATH)sub_path.elementAt(i);
}

//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
// setDir
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

public void setDir(String directory) {
  this.dir = directory;
}

//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
// getDir
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

public String getDir() {
  return dir;
}

//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
//isDir
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

public void isDir(boolean b) {
  isDir = b;
}

//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
//isDir
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

public boolean isDir() {
  return isDir;
}

}
