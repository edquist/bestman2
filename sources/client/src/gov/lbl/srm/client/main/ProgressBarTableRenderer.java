/**
 *
 * *** Copyright Notice ***
 *
 * BeStMan Copyright (c) 2010, The Regents of the University of California, 
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
 * Email questions to SRM@LBL.GOV
 * Scientific Data Management Research Group
 * Lawrence Berkeley National Laboratory
 * http://sdm.lbl.gov/bestman
 *
*/

package gov.lbl.srm.client.main;

import javax.swing.JTable;
import javax.swing.table.*;
import java.awt.*;
import javax.swing.JProgressBar;
import java.util.Vector;

import gov.lbl.srm.client.util.*;

public class ProgressBarTableRenderer extends JProgressBar implements 
		TableCellRenderer {

    public Component getTableCellRendererComponent(JTable table,
        Object value, boolean isSelected,
        boolean hasFocus, int rowIndex, int vColIndex) {

          if(value == null) return this;
          int intValue = Integer.parseInt(((FileProgress)value).getValue());
          setStringPainted(true);
          if(hasFocus) {}
          if(isSelected) {}
          setValue(intValue);
          setString(value.toString());
          return this;
    }

}
