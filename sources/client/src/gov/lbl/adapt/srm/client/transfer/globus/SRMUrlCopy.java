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

package gov.lbl.adapt.srm.client.transfer.globus;

import org.ietf.jgss.GSSCredential;
import org.gridforum.jgss.ExtendedGSSManager;
//import org.apache.log4j.Logger;

import java.io.IOException;
import org.globus.io.urlcopy.UrlCopy;
import org.globus.io.urlcopy.UrlCopyListener;
import org.globus.io.urlcopy.UrlCopyException;
import org.globus.util.GlobusURL;

import org.globus.gsi.gssapi.auth.Authorization;

import java.lang.Thread;
import gov.lbl.adapt.srm.client.transfer.ISRMFileTransfer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.*;

import gov.lbl.adapt.srm.client.main.util;


public class SRMUrlCopy extends Thread implements ISRMFileTransfer {
//	protected static Logger logger = Logger.getLogger("GET");
//    private SRMGSICredential credential = null;
//    private GSSCredential proxy;
	protected static String myname;
    private GlobusURL fromurl;
    private GlobusURL tourl;
    private int parallelism = 1;
	private SRMTransferListener listen = null;
	//private int buffersize = 2097152;
	private int buffersize = 0;
// AS 090707
	private int blocksize = 0;
	private UrlCopy ucopy = new UrlCopy();
	private String status = null;
	private boolean transferdone=false;
	private boolean transferok=false;
	private long txfTimeInMilliSeconds = -1;
    private static Log logger;
    private boolean silent;
    private boolean useLog;
    private boolean debug;

public SRMUrlCopy (String fromu, String tou) {
	try {
		fromurl = new GlobusURL (fromu);
		tourl = new GlobusURL (tou);
		myname = fromu;
//    	credential = new SRMGSICredential();
	} catch (Exception e) {
		e.printStackTrace();
		System.out.println(e.toString());
		status = e.toString();
	}
}

public void setLogger(Log logger, boolean silent,boolean useLog,
	boolean debug) {
  this.logger = logger;
  this.silent = silent;
  this.useLog = useLog;
  this.debug = debug;
}

public void setSessionType(int sessionType) {
}

public void setSessionMode(int sessionType) {
}

public SRMUrlCopy (String fromu, String tou, int parallelu) {
	try {
		fromurl = new GlobusURL (fromu);
		tourl = new GlobusURL (tou);
		myname = fromu;
//    	credential = new SRMGSICredential();
	} catch (Exception e) {
		e.printStackTrace();
		System.out.println(e.toString());
		status = e.toString();
	}
	parallelism = parallelu;
}

public SRMUrlCopy (GlobusURL fromu, GlobusURL tou) {
    try {
        fromurl = fromu;
        tourl = tou;
        myname = fromu.getURL();
    } catch (Exception e) {
        e.printStackTrace();
        System.out.println(e.toString());
		status = e.toString();
    }
}

public SRMUrlCopy (String fromu, String tou, GSSCredential credentials) {
	try {
		fromurl = new GlobusURL (fromu);
		tourl = new GlobusURL (tou);
		myname = fromu;
//    	credential = new SRMGSICredential();
		ucopy.setSourceCredentials(credentials);
		ucopy.setDestinationCredentials(credentials);
	} catch (Exception e) {
		e.printStackTrace();
		System.out.println(e.toString());
		status = e.toString();
	}
}

public SRMUrlCopy (GlobusURL fromu, GlobusURL tou, int parallelu, boolean listener) {
    try {
        fromurl = fromu;
        tourl = tou;
		myname = fromu.getURL();
        if (listener) listen = new SRMTransferListener(logger,silent);
    	parallelism = parallelu;
    } catch (Exception e) {
        e.printStackTrace();
        System.out.println(e.toString());
		status = e.toString();
    }
}


public SRMUrlCopy (String fromu, String tou, int parallelu, boolean listener) {
	try {
		fromurl = new GlobusURL (fromu);
		tourl = new GlobusURL (tou);
		myname = fromurl.getURL();
		if (listener) listen = new SRMTransferListener(logger,silent);
	} catch (Exception e) {
		e.printStackTrace();
		System.out.println(e.toString());
		status = e.toString();
	}
	parallelism = parallelu;
}

public String getTransferName() {
	return myname;
}

public boolean transferDone() {
	return transferdone;
}

public boolean transferOk() {
	return transferok;
}

public String getStatus() {
	return status;
}

public void run() {
	copy();
}

public void transfer () {
	this.start();
}

public void transfer(String fromu, String tou) {
	try {
		fromurl = new GlobusURL (fromu);
		tourl = new GlobusURL (tou);
		myname = fromu;
		this.start();
	} catch (Exception e) {
		e.printStackTrace();
		System.out.println(e.toString());
		status = e.toString();
	}
}

public void transferSync (String fromu, String tou) {
	try {
		fromurl = new GlobusURL (fromu);
		tourl = new GlobusURL (tou);
		myname = fromu;
		copy();
	} catch (Exception e) {
		e.printStackTrace();
		System.out.println(e.toString());
		status = e.toString();
	}
}

public void transferSync () {
	copy();
}

public void setParallel(int parallelu) {
	parallelism = parallelu;
}

public void setDCAU(boolean dcau) {
	ucopy.setDCAU(dcau);
}

public void setCredentials(GSSCredential credentials) {
	ucopy.setSourceCredentials(credentials);
	ucopy.setDestinationCredentials(credentials);
}

public void setDestinationCredentials(GSSCredential dstCredentials) {
    ucopy.setDestinationCredentials(dstCredentials);
}

public void setSourceCredentials(GSSCredential srcCredentials) {
    ucopy.setSourceCredentials(srcCredentials);
}

public void setSourceAuthorization(Authorization auth) {
    ucopy.setSourceAuthorization(auth);
}

public void setDestinationAuthorization(Authorization auth) { 
    ucopy.setDestinationAuthorization(auth);
}

public void setBufferSize(int size) {
	buffersize = size;
}

public void setBlockSize(int size) {
	blocksize = size;
}

public void setAppendMode(boolean appendMode) {
    ucopy.setAppendMode(appendMode);
}

public void setSourceUrl(String surl) throws IOException {
	GlobusURL tempurl = null;
	try {
		tempurl = new GlobusURL (surl);
		String urlPath = tempurl.getPath();
	    if (urlPath == null || urlPath.length() == 0) {
			status = "The '" + tempurl.getURL() + 
                              "' url does not specify the file location.";
			throw new IOException(status);
	    }
	    fromurl = tempurl; 
	} catch (Exception e) {
        e.printStackTrace();
        System.out.println(e.toString());
		status = e.toString();
	}
}

public void setDestinationUrl(String surl) throws IOException {
	GlobusURL tempurl = null;
	try {
		tempurl = new GlobusURL (surl);
		String urlPath = tempurl.getPath();
	    if (urlPath == null || urlPath.length() == 0) {
			status = "The '" + tempurl.getURL() + 
                              "' url does not specify the file location.";
			throw new IOException(status);
	    }
	    tourl = tempurl; 
	} catch (java.net.MalformedURLException e) {
        e.printStackTrace();
        System.out.println(e.toString());
		status = e.toString();
	}
}

public void setUseThirdPartyCopy() {
    ucopy.setUseThirdPartyCopy(true);
}

public void cancel() {
    ucopy.cancel();
}

public void setListener(boolean listener) {
	if (listener) {
		if (listen == null) listen = new SRMTransferListener(logger,silent);
	}
	else {
		listen = null;
	}
}

private void copy() {
    if(debug) {
	util.printMessage("Copying...",logger,silent);
	util.printMessage("from: " + fromurl.getURL(),logger,silent);
	util.printMessage("to: " + tourl.getURL(),logger,silent);
    }
	long st = System.currentTimeMillis();
	try {
		if (ucopy.getSourceCredentials() == null) util.printMessage("cred null",logger,silent);
// AS 090707
//		if (buffersize > 0) ucopy.setTCPBufferSize(buffersize);
//		if (blocksize > 0) ucopy.setBufferSize(blocksize);
		if (buffersize > 0) ucopy.setBufferSize(buffersize);
		ucopy.setSourceUrl(fromurl);
		ucopy.setDestinationUrl(tourl);
		if (listen != null) ucopy.addUrlCopyListener(listen);
		ucopy.copy();
		if (!ucopy.isCanceled()) transferdone=true;
	} catch (Exception e) {
		e.printStackTrace();
		System.out.println(e.toString());
		status = e.toString();
		transferdone=true;
	}

	long ft = System.currentTimeMillis();
    transferok =true;
    if(debug) {
	util.printMessage("DONE: " + fromurl.getURL()  + 
			", time took (milliseconds) =" + (ft-st),logger,silent);
    }
	txfTimeInMilliSeconds = ft-st;
}

//
// added by junmin
//
public long getSourceFileSize() {
	return -1;
}
public long getTransferTimeInMilliSeconds() {
	return txfTimeInMilliSeconds;	 
}
}

class SRMTransferListener implements UrlCopyListener {
    
Exception _exception;
Log logger;
boolean silent;


public SRMTransferListener (Log logger, boolean silent) { 
  this.logger = logger;
  this.silent = silent;
}
// it's int in java cog 1.1, but globus 3.2 uses long
// for now, we'll have long, but will be changed to int
// public void transfer(int current, int total) {
public void transfer(long current, long total) {
    if (total == -1) {
        if (current == -1) {
	        util.printMessage("<thrid party transfer: progress unavailable>",logger,silent);
        } else {
	        util.printMessage(""+current,logger,silent);
        }
    } else {
        util.printMessage(current + " out of " + total,logger,silent);
    }
}
    
public void transferError(Exception e) {
    _exception = e;
}
    
public void transferCompleted() {
    if (_exception == null) {
        util.printMessage("Transfer completed successfully",logger,silent);
    } else {
        util.printMessage("Transfer failed: " + _exception.getMessage(),logger,silent);
    }
}
}

