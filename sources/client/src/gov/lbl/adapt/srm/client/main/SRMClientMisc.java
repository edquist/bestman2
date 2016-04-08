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

import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Vector;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.Properties;
import java.util.Enumeration;
import java.util.*;
import java.io.*;
import java.beans.XMLEncoder;
import java.beans.XMLDecoder;

import javax.swing.JFrame;

import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.gridforum.jgss.ExtendedGSSManager;
import org.gridforum.jgss.ExtendedGSSCredential;

import org.globus.util.Util;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.CertUtil;
import org.globus.gsi.GSIConstants;
import org.globus.gsi.OpenSSLKey;
import org.globus.gsi.bc.*;
import java.security.interfaces.*;
import java.security.PrivateKey;
import java.security.cert.*;

import gov.lbl.adapt.srm.client.intf.*;
import gov.lbl.adapt.srm.client.util.*;
import gov.lbl.adapt.srm.client.exception.*;
import gov.lbl.adapt.srm.client.wsdl.*;

//import srm.common.StorageResourceManager.*;
import gov.lbl.srm.StorageResourceManager.*;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.*;


//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
// Class SRMClientMisc
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

public class SRMClientMisc
{

private GSSCredential mycred;
private XMLParseConfig pConfig = new XMLParseConfig();

private Properties properties = new Properties();
private String configFileLocation = "";
private String inputFile="";
private String _password ="";
private int proxyType;
private String userKey="";
private String userCert="";
private String proxyFile="";
private boolean isRenew = false;
private int servicePortNumber=0;
private String serviceHandle="";
private String storageInfo = "";
private String sourceUrl="";
private String serviceUrl="";
private String uid="hi";
private String delegationNeeded="";
private Request request;
private String requestType="";
private String requestToken;
private String userDesc="";
private String log4jlocation="";
private String logPath="";
private boolean onlyPing=false;
private boolean onlyProtocols=false;
private boolean onlySetPermission=false;
private boolean onlyGetPermission=false;
private boolean onlyCheckPermission=false;

private static Log logger;

private SRMWSDLIntf srmCopyClient;

private boolean _debug=false;
private boolean silent=false;
private boolean useLog=false;
private boolean overrideserviceurl=false;
//private int statusMaxTimeAllowed=600;
private int statusMaxTimeAllowed=-1;
private int statusWaitTime=30;
private String permissionType="";
private String ownerPermission="";
private String otherPermission="";
private String userPermissions="";
private String groupPermissions="";
private String eventLogPath="";
private boolean statusWaitTimeGiven=false;
private boolean statusMaxTimeGiven=false;
private boolean serviceURLGiven=false;
private String serviceURL="";

private java.util.logging.Logger _theLogger =
        java.util.logging.Logger.getLogger
            (gov.lbl.adapt.srm.client.main.SRMClientN.class.getName());
private java.util.logging.FileHandler _fh;
private Vector inputVec = new Vector ();
private PrintIntf pIntf;
private int connectionTimeOutAllowed=600;
private int setHTTPConnectionTimeOutAllowed=600;
private boolean gotConnectionTimeOut;
private boolean gotHTTPConnectionTimeOut;


//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
// SRMClientMisc
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::


public SRMClientMisc(String[] args, PrintIntf pIntf) {

  this.pIntf = pIntf;

  if(args.length == 0) {
    showUsage(true);
  }

  for(int i = 0; i < args.length; i++) {
    boolean ok = false;
    if(i == 0 && !args[i].startsWith("-")) {
      serviceUrl=args[i]; 
      ok = true; 
    }       
    else if(args[i].equalsIgnoreCase("-conf") && i+1 < args.length) {
      configFileLocation = args[i+1];
      i++;
    }
    else if(args[i].equalsIgnoreCase("-sethttptimeout") && i+1 < args.length) {
     try {
        setHTTPConnectionTimeOutAllowed = Integer.parseInt(args[i+1]);
        i++;
        gotHTTPConnectionTimeOut=true;
     }catch(NumberFormatException nfe) {
        setHTTPConnectionTimeOutAllowed = 600; //using the default value
     }
    }
    else if(args[i].equalsIgnoreCase("-connectiontimeout") && i+1 < args.length) {
     try {
        connectionTimeOutAllowed = Integer.parseInt(args[i+1]);
        i++;
        gotConnectionTimeOut=true;
     }catch(NumberFormatException nfe) {
        connectionTimeOutAllowed = 1800; //using the default value
     }
    }
    /*
    else if(args[i].equalsIgnoreCase("-log4jlocation") && i+1 < args.length) {
      log4jlocation = args[i+1];
      i++;
    }
    */
    else if(args[i].equalsIgnoreCase("-nogui")) {
      ;
    }
    else if(args[i].equalsIgnoreCase("-lite")) {
       ;
    }
    else if(args[i].equalsIgnoreCase("-userdesc") && i+1 < args.length) { 
      userDesc = args[i+1];
      i++;
    }
    else if(args[i].equalsIgnoreCase("-log") && i+1 < args.length) {
      useLog=true;
      eventLogPath = args[i+1];
      i++;
    }
    else if(args[i].equalsIgnoreCase("-serviceurl") && i+1 < args.length) {
      serviceUrl = args[i+1];
      serviceURLGiven=true;
      i++;
    }
    else if(args[i].equalsIgnoreCase("-permissiontype") && i+1 < args.length) {
      permissionType = args[i+1];
      i++;
    }
    else if(args[i].equalsIgnoreCase("-ownerpermission") && i+1 < args.length) {
      ownerPermission = args[i+1];
      i++;
    }
    else if(args[i].equalsIgnoreCase("-otherpermission") && i+1 < args.length) {
      otherPermission = args[i+1];
      i++;
    }
    else if(args[i].equalsIgnoreCase("-userpermissions") && i+1 < args.length) {
      userPermissions = args[i+1];
      i++;
    }
    else if(args[i].equalsIgnoreCase("-grouppermissions") 
		&& i+1 < args.length) {
      groupPermissions = args[i+1];
      i++;
    }
    else if(args[i].equalsIgnoreCase("-ping")) {
      onlyPing=true;
    }
    else if(args[i].equalsIgnoreCase("-transferprotocols")) {
      onlyProtocols=true;
    }
    else if(args[i].equalsIgnoreCase("-setpermission") ||
		args[i].startsWith("-setPermissions")) {
      onlySetPermission=true;
    }
    else if(args[i].equalsIgnoreCase("-getpermission") ||
	  args[i].startsWith("-getPermissions")) {
      onlyGetPermission=true;
    }
    else if(args[i].equalsIgnoreCase("-checkpermission") ||
	  args[i].startsWith("-checkPermissions")) {
      onlyCheckPermission=true;
    }
    else if(args[i].equalsIgnoreCase("-f") && i+1 < args.length) {
      inputFile = args[i+1];
      i++;
    }
    else if(args[i].equalsIgnoreCase("-s") && i+1 < args.length) {
      sourceUrl = args[i+1];
      i++;
    }
    else if(args[i].equalsIgnoreCase("-userkey") && i+1 < args.length) {
      userKey = args[i+1];
      i++;
    }
    else if(args[i].equalsIgnoreCase("-usercert") && i+1 < args.length) {
      userCert = args[i+1];
      i++;
    }
    else if(args[i].equalsIgnoreCase("-storageinfo")) {
      if(i+1 < args.length) {
         if(args[i+1].startsWith("for")) {
           storageInfo = args[i+1];
           i++;
         }
         else {
           storageInfo = ""+true;
         }
      }
      else {
        storageInfo=""+true;
      }
    }
    else if(args[i].equalsIgnoreCase("-delegation")) {
       if(i+1 < args.length) {
          delegationNeeded = ""+true;
          i++;
       }
       else {
          delegationNeeded = ""+true;
       }
    }
    else if(args[i].equalsIgnoreCase("-proxyfile") && i+1 < args.length) {
      proxyFile = args[i+1];
      i++;
    }
    else if(args[i].equalsIgnoreCase("-debug")) {
      _debug=true;
    }
    else if(args[i].equalsIgnoreCase("-quiet")) {
      silent=true;
    }
    else if(args[i].equalsIgnoreCase("-renewproxy")) {
      isRenew = true;
    }
    else if(args[i].equalsIgnoreCase("-statusmaxtime") && i+1 < args.length) {
      String temp = args[i+1];
      statusMaxTimeGiven=true;
      try {
        statusMaxTimeAllowed = Integer.parseInt(temp);
      }catch(NumberFormatException nfe) {
          System.out.println("Given -statusmaxtime is not valid, using the default " +
            statusMaxTimeAllowed);
      }
      i++;
    }
    else if(args[i].equalsIgnoreCase("-statuswaittime") && i+1 < args.length) {
      String temp = args[i+1];
      statusWaitTimeGiven=true;
      try {
        statusWaitTime = Integer.parseInt(temp);
      }catch(NumberFormatException nfe) {
          System.out.println("Given -statuswaittime is not valid, using the default " +
            statusMaxTimeAllowed);
      }
      i++;
    }
    else if(args[i].equalsIgnoreCase("-v2")) {
      ;
    }
    else if(args[i].equalsIgnoreCase("-version")) {
      SRMClientN.printVersion();
    }
    else if(args[i].equalsIgnoreCase("-help")) {
      showUsage (true);
    } 
    else {
      boolean b = gov.lbl.adapt.srm.client.util.Util.parseSrmCpCommands(args[i],0);
      if(b) ;
      else { 
       if(!ok) {
        showUsage (true);
       }
      }
    }
  }

  Properties sys_config = new Properties ();
  
  try {
    if(!configFileLocation.equals("")) {
      File f = new File(configFileLocation);
      if(!f.exists()) {
         System.out.println("\nConfig file does not exists " +
            configFileLocation);
         showUsage(false);
      }
      if(_debug) {
        System.out.println("\nParsing config file " + configFileLocation);
      }
      sys_config = gov.lbl.adapt.srm.client.util.Util.parsefile(
		configFileLocation,"SRM-UTIL",silent, useLog,_theLogger);
    }
  }catch(Exception e) {
    util.printMessage("\nSRM-CLIENT:Exception from client="+e.getMessage(),logger,silent);
                    util.printEventLogException(_theLogger,"",e);
    showUsage(false);
  } 

    String stemp = System.getProperty("SRM.HOME");
  if(stemp != null && !stemp.equals("")) {
     configFileLocation = stemp+"/conf/srmclient.conf";
     try {
       File f = new File(configFileLocation);
       if(f.exists()) {
       if(_debug) {
          System.out.println("\nParsing config file " + configFileLocation);
       }
       sys_config  = gov.lbl.adapt.srm.client.util.Util.parsefile(
		   configFileLocation,"SRM-UTIL",silent, useLog,_theLogger);
       }
     } catch(Exception e) {
        util.printMessage("\nSRM-CLIENT:Exception from client="+e.getMessage(),logger,silent);
                    util.printEventLogException(_theLogger,"",e);
        showUsage(false);
     }
  }


  if(proxyFile.equals("")) {
    Properties props = System.getProperties();
    Enumeration ee = props.propertyNames();
    while (ee.hasMoreElements()) {
     String str = (String) ee.nextElement();
     if(str.trim().equals("X509_USER_PROXY")) {
       String ttemp = props.getProperty(str.trim());
       if(ttemp != null) {
         proxyFile=ttemp;
       }
     }
     //System.out.println(str);
    }
  }

   String detailedLogDate=util.getDetailedDate(); 

   if(silent || useLog) {
   if(eventLogPath.equals("")) {
    String temp = (String) sys_config.get("eventlogpath");
    if(temp != null) {
      eventLogPath = temp;
    }
    else {
      eventLogPath = "./srmclient-event-"+detailedLogDate+".log";
    }
  }

  try {
      _fh = new java.util.logging.FileHandler(eventLogPath);
      _fh.setFormatter(new NetLoggerFormatter());
      _theLogger.addHandler(_fh);
      _theLogger.setLevel(java.util.logging.Level.ALL);

      File f = new File(eventLogPath+".lck");
      if(f.exists()) {
        f.delete();
      }
  }catch(Exception e) {
     util.printMessage("\nSRM-CLIENT:Exception from client="+e.getMessage(),logger,silent);
                    util.printEventLogException(_theLogger,"",e);
  }
  }


  /*
  if(logPath.equals("")) {
    String temp = (String) sys_config.get("logpath");
    if(temp != null) {
      logPath = temp;
    }
    else {
      logPath = "./srm-client-detailed-"+detailedLogDate+".log";
    }
  }
  */
   
  /*
  if(silent) {
    if(logPath.equals("")) {
      System.out.println("\nFor the option quiet, -log is needed to " + 
        " forward to output to the logfile");
      System.out.println("Please provide -log <path to logfile> ");
      showUsage(false); 
    }
  }
  */

  String ttemp = System.getProperty("log4j.configuration");
  if(ttemp != null && !ttemp.equals("")) {
     log4jlocation = ttemp;
  }
  
  //setup log4j configuration
  /* 
  String ttemp = System.getProperty("log4j.configuration");
  if(ttemp != null && !ttemp.equals("")) {
     log4jlocation = ttemp;
  }else {
     if(log4jlocation.equals("")) {
       String temp = (String) sys_config.get("log4jlocation");
       if(temp != null) {
         log4jlocation = temp;
       }
       else {
         log4jlocation = "logs/log4j_srmclient.properties";
       }
     }
  }
  */

  /*
  if(!logPath.equals("")) {

    //check existence of logfile.
    if(logPath.endsWith("/")) {
       logPath = logPath.substring(0,logPath.length()-1);
    }
    
    int idx = logPath.lastIndexOf("/");
    if(idx != -1) {
      String logPathDir = logPath.substring(0,idx);
      if(idx == 0) {
        logPathDir = logPath;
      }

      try { 
      File f = new File(logPath);
      if(f.isDirectory()) {
         System.out.println("Given logfile location is a directory, " +
            " please provide full path with desired file name logPath");
         showUsage(false); 
      }
      }catch(Exception e) {
         util.printMessage("\nSRM-CLIENT:Exception from client="+e.getMessage(),logger,silent);
                    util.printEventLogException(_theLogger,"",e);
         showUsage(false);
      }

      //rewrite the log4j conf file with the new log path
      try {
         String ref;
         FileInputStream file = new FileInputStream(log4jlocation);
         BufferedReader in = new BufferedReader(new InputStreamReader(file));
         FileOutputStream outFile =
             new FileOutputStream(logPathDir+"/log4j_srmclient.properties");
         BufferedWriter out =
             new BufferedWriter(new OutputStreamWriter(outFile));

         while((ref= in.readLine()) != null) {
            if(ref.startsWith("log4j.appender.SRMCOPY.File")) {
              out.write("log4j.appender.SRMCOPY.File="+logPath+"\n");
            }
            else {
              out.write(ref+"\n");
            }
         }
         in.close();
         if(file != null) file.close();
         out.close();
         if(outFile != null) outFile.close();

      }catch(IOException ex) {
         util.printMessage("\nSRM-CLIENT:Exception from client="+ex.getMessage(),logger,silent);
                    util.printEventLogException(_theLogger,"",ex);
         showUsage(false);
      }
      log4jlocation=logPathDir+"/log4j_srmclient.properties";
      PropertyConfigurator.configure(log4jlocation);
      ClassLoader cl = this.getClass().getClassLoader();
      try {
         Class c = cl.loadClass("gov.lbl.adapt.srm.client.main.SRMClientRequest");
         logger = LogFactory.getLog(c.getName());
      }catch(ClassNotFoundException cnfe) {
         System.out.println("ClassNotFoundException " + cnfe.getMessage());
                    util.printEventLogException(_theLogger,"",cnfe);
      }
    } 
  }
  else {
      logPath = "./srm-client-detailed.log";
  } 
  */

  try {
  PropertyConfigurator.configure(log4jlocation);
  }catch(Exception ee){;}

  /*
  if(_debug) {
    if(!logPath.equals("")) {
      util.printMessage("Log4jlocation " + log4jlocation, logger,silent);
    }
  }
  */
  

  //default values such as "Enter a Value"
  properties.put("user-cert", pConfig.getUserCert());
  properties.put("user-key", pConfig.getUserKey());
  properties.put("proxy-file", pConfig.getProxyFile());


  try {

    if(!userKey.equals("")) {
      pConfig.setUserKey(userKey);
      properties.put("user-key",userKey);
    }
    else {
      String temp =  (String) sys_config.get("UserKey");
      if(temp != null) {
        userKey = temp;
        pConfig.setUserKey(userKey);
        properties.put("user-key",userKey);
      }
    }
    if(!userCert.equals("")) {
      pConfig.setUserCert(userCert);
      properties.put("user-cert", userCert);
    }
    else {
      String temp =  (String) sys_config.get("UserCert");
      if(temp != null) {
        userCert = temp;
        pConfig.setUserKey(userCert);
        properties.put("user-cert",userCert);
      }
    }
    if(!proxyFile.equals("")) {
      pConfig.setProxyFile(proxyFile);
      properties.put("proxy-file", proxyFile);
    }
    else {
      String temp =  (String) sys_config.get("ProxyFile");
      if(temp != null) {
        proxyFile = temp;
        pConfig.setUserKey(proxyFile);
        properties.put("proxy-file",proxyFile);
      }
    }

     if(!gotHTTPConnectionTimeOut) {
      String temp = (String) sys_config.get("SetHTTPConnectionTimeOut");
      if(temp != null) {
        int x = 0;
        try {
           x = Integer.parseInt(temp);
           setHTTPConnectionTimeOutAllowed = x;
        }catch(NumberFormatException nfe) {
           setHTTPConnectionTimeOutAllowed=600;
        }
      }
     }

     if(!gotConnectionTimeOut) {
      String temp = (String) sys_config.get("ConnectionTimeOut");
      if(temp != null) {
        int x = 0;
        try {
           x = Integer.parseInt(temp);
           connectionTimeOutAllowed = x;
        }catch(NumberFormatException nfe) {
           connectionTimeOutAllowed=1800;
        }
      }
     }


    String xtemp = (String) sys_config.get("ServicePortNumber");
    if(xtemp != null) {
        int x = 0;
        try {
           x = Integer.parseInt(xtemp);
           servicePortNumber = x;
        }catch(NumberFormatException nfe) { }
    }

    String xxtemp = (String) sys_config.get("ServiceHandle");
    if(xxtemp != null) {
         serviceHandle = xxtemp;
    }


     if(!statusWaitTimeGiven) {
        String temp = (String) sys_config.get("StatusWaitTime");
        if(temp != null) {
          int x = 0;
          try {
            x = Integer.parseInt(temp); 
            statusWaitTime = x;
          }catch(NumberFormatException nfe) {
             System.out.println( "SRM-CLIENT: " +
				"Warning StatusWaitTime is not a valid integer " + temp);
		  }
        }
     }

     if(!statusMaxTimeGiven) {
        String temp = (String) sys_config.get("StatusMaxTime");
        if(temp != null) {
          int x = 0;
          try {
            x = Integer.parseInt(temp); 
            statusMaxTimeAllowed = x;
          }catch(NumberFormatException nfe) {
             System.out.println( "SRM-CLIENT: " +
				"Warning StatusMaxTime is not a valid integer " + temp);
		  }
        }
     }

     if(!serviceURLGiven) {
        String temp = (String) sys_config.get("ServiceURL");
        if(temp != null) {
           serviceURL = temp;
        }
     }

    //if all three not provided, using default proxy.
    //if one of userCert or userKey is provided, the other is needed
    if(proxyFile.equals("")) {
       if(userCert.equals("") && userKey.equals("")) {
         try {
           //proxyFile ="/tmp/x509up_u"+MyConfigUtil.getUID();
           proxyFile ="/tmp/x509up_u"+MyConfigUtil.getUID2();
         }catch(Exception e) {
           System.out.println("\nSRM-CLIENT: Exception from client="+e.getMessage());
                    util.printEventLogException(_theLogger,"",e);
           proxyFile ="/tmp/x509up_u"+MyConfigUtil.getUID();
         }
         pConfig.setProxyFile(proxyFile);
         properties.put("proxy-file", proxyFile);
         //System.out.println("\nUsing default user proxy " + proxyFile);
       }
       else {
         if(userCert.equals("") || userKey.equals("")) {
           inputVec.clear();
           inputVec.addElement("UserCert and UserKey both should be provided");
           util.printEventLog(_theLogger,"SrmClientMisc",inputVec,silent,useLog);
           System.out.println
		     ("\nSRM-MISC: UserCert and UserKey both should be provided");
           showUsage(false);
         }
       }
    }

    if(isRenew) {
      String v1 = (String) properties.get("user-cert");
      String v2 = (String) properties.get("user-key");
      if(v1.startsWith("Enter") || v2.startsWith("Enter")) {
        inputVec.clear();
        inputVec.addElement("If you want to renew proxy automatically, "+
         "you need to enter user-cert location and user-key location.");
        util.printEventLog(_theLogger,"SrmClientMisc",inputVec,silent,useLog);
        System.out.println("\nSRM-MISC: If you want to renew proxy automatically,\n "+
         "you need to enter user-cert location and user-key location.");
        inputVec.clear();
        inputVec.addElement("StatusCode=93");
        util.printEventLog(_theLogger,"StatusExit",inputVec,silent,useLog);
        System.exit(93); 
      }
      String v3 = (String)properties.get("proxy-file");
      if(v3.startsWith("Enter")) {
        inputVec.clear();
        inputVec.addElement("If you want to renew proxy automatically,\n "+
          "please enter your proxy file location.");
        util.printEventLog(_theLogger,"SrmClientMisc",inputVec,silent,useLog);
        System.out.println("\nSRM-MISC: If you want to renew proxy automatically,\n "+
          "please enter your proxy file location.");
        inputVec.clear();
        inputVec.addElement("StatusCode=93");
        util.printEventLog(_theLogger,"StatusExit",inputVec,silent,useLog);
        System.exit(93); 
      }
      else {
        String line = PasswordField.readPassword("Enter GRID passphrase: ");
        inputVec.clear();
        inputVec.addElement("Enter GRID passphrase");
        util.printEventLog(_theLogger,"SrmClientMisc",inputVec,silent,useLog);
        _password = line;
      }
      //else there is no need to renew proxy.
    }

    String[] surl = null;
    surl = new String[1];
    String[] turl = null;
    turl = new String[1];

    Vector fileInfo = new Vector();
    String temp="";

    if(onlySetPermission || onlyGetPermission || onlyCheckPermission) {
       if(inputFile.equals("")) { 
          if(sourceUrl.equals("")) {
             util.printMessage 
	   	       ("\nPlease provide -s <sourceUrl>\n",logger,silent);
             util.printMessage 
		     ("or -f <inputfile>\n",logger,silent);
             inputVec.clear();
             inputVec.addElement("Please provide -s <sourceUrl> " +
               " or -f <inputfile>");
             util.printEventLog(_theLogger,"SrmClientMisc",inputVec,silent,useLog);
             showUsage(false);
          }
          surl[0] = sourceUrl;
          request = gov.lbl.adapt.srm.client.util.Util.createRequest(surl[0],"","",_debug,silent,useLog,
            "SRM-UTIL",false,_theLogger,logger); 
       } 
       else {
        //by default request is get, so target dir check is done in parseXML
         request = gov.lbl.adapt.srm.client.util.Util.parseXML(
			inputFile,"SRM-UTIL",silent,useLog,_theLogger);
       }

       requestType = request.getModeType();

       fileInfo = validateURL(request);
        if(fileInfo.size() == 0) {
          inputVec.clear();
          inputVec.addElement("No files in the request for transfer");
          inputVec.addElement("Cannot proceed further, please check input");
          util.printEventLog(_theLogger,"SrmClientMisc",inputVec,silent,useLog);
          util.printMessage("\nSRM-MISC: No files in the request for transfer",
				logger,silent);
          util.printMessage("Cannot proceed further, please check input",
				logger,silent);
          showUsage(false);
        }
        else {
          for(int i = 0; i < fileInfo.size(); i++) {
             FileIntf fIntf = (FileIntf) fileInfo.elementAt(i);
             temp = fIntf.getSURL();
             if(fIntf.getSURL().startsWith("srm:/")) {       
              overrideserviceurl = true;
             }
          }
        }
    }

    if(overrideserviceurl) {
       serviceUrl = gov.lbl.adapt.srm.client.util.Util.getServiceUrl(
			temp,serviceURL,serviceHandle,servicePortNumber,1, silent,useLog,_theLogger,logger);
       if(serviceUrl == null) showUsage(false);
        for(int i = 0; i < fileInfo.size(); i++) {
          FileIntf fIntf = (FileIntf) fileInfo.elementAt(i);
          String temp1 = fIntf.getSURL();
          String sfn = gov.lbl.adapt.srm.client.util.Util.getSFN(temp1);
          fIntf.setSURL(serviceUrl.replace("httpg","srm")+sfn);
        }
    }

    if(serviceUrl.equals("")) {
       String tt = (String) sys_config.get("ServiceUrl");
       if(tt != null) {
         serviceUrl = tt;
       }
       else {
         util.printMessage 
          ("\nPlease provide the -serviceurl full SRM service url",
				logger,silent);
         util.printMessage 
		  ("  example:srm://<hostname>:<port>//wsdlpath",logger,silent);
              showUsage(false);
         inputVec.clear();
         inputVec.addElement("Please provide the -serviceurl full SRM service url");
		 inputVec.addElement(" example:srm://<hostname>:<port>//wsdlpath");
         util.printEventLog(_theLogger,"SrmClientMisc",inputVec,silent,useLog);
         }
      }

       GSSCredential credential=null;
     try {
       credential = gov.lbl.adapt.srm.client.util.Util.checkTimeLeft
            (pConfig,properties,_password, _theLogger,silent,useLog,logger,pIntf,_debug);
       proxyType=gov.lbl.adapt.srm.client.util.Util.getProxyType(properties);

     }catch(Exception ee) {
        util.printMessage("\nSRM-CLIENT:Exception from client="+ee.getMessage(),logger,silent);
                    util.printEventLogException(_theLogger,"",ee);
        inputVec.clear();
        inputVec.addElement("StatusCode=92");
        util.printEventLog(_theLogger,"StatusExit",inputVec,silent,useLog);
        util.printHException(ee,pIntf);
        System.exit(92);
     }

      serviceUrl = gov.lbl.adapt.srm.client.util.Util.getServiceUrl(
			serviceUrl,serviceURL,serviceHandle,servicePortNumber,0,
		    silent,useLog,_theLogger,logger);
      if(serviceUrl == null) showUsage(false);
      inputVec.clear();
      inputVec.addElement("ServiceUrl="+serviceUrl);
      util.printEventLog(_theLogger,"SrmClientMisc",inputVec,silent,useLog);

      if(_debug) {
      util.printMessage("\n===================================",logger,silent);
      util.printMessage("Listing parameters information",logger,silent);
      util.printMessage("\nserviceUrl   : " + serviceUrl,logger,silent);
      if(requestToken != null) { 
        util.printMessage("RequestToken : " + requestToken,logger,silent);
      }
      util.printMessage("Debug ON       : " + _debug,logger,silent);
      util.printMessage("Quiet ON       : " + silent,logger,silent);
      }
      inputVec.clear();
      inputVec.addElement("ServiceUrl="+serviceUrl);
      inputVec.addElement("RequestToken="+requestToken);
      inputVec.addElement("Debug="+_debug);
      inputVec.addElement("Quiet="+silent);
      util.printEventLog(_theLogger,"Initialization",inputVec,silent,useLog);

      Properties props = System.getProperties();
      String uTemp = props.getProperty("user.name");
      if(uTemp != null) {
         uid = uTemp;
      }

        util.printMessage("SRM-CLIENT: " + "Connecting to serviceurl " +
            serviceUrl,logger,silent);

      SRMUtilClient utilClient = new SRMUtilClient(serviceUrl,uid,userDesc,
			   credential, _theLogger, logger, pIntf, _debug,silent,useLog,
			   false, false,
			   statusMaxTimeAllowed,statusWaitTime,storageInfo,proxyType,
			   connectionTimeOutAllowed,setHTTPConnectionTimeOutAllowed,
		       delegationNeeded,3,60);
      TStatusCode sCode = null;
      if(onlyPing) {
        sCode = TStatusCode.SRM_SUCCESS;
        utilClient.doSrmPing();
      }
      else if(onlyProtocols) {
        sCode = utilClient.doSrmGetProtocols();
      }
      else if(onlyGetPermission) {
        sCode = utilClient.doSrmGetPermission(fileInfo);
      }
      else if(onlyCheckPermission) {
        sCode = utilClient.doSrmCheckPermission(fileInfo);
      }
      else if(onlySetPermission) {
        if(permissionType.equals("")) {
         inputVec.clear();
         inputVec.addElement("Please provide -permissiontype");
         util.printEventLog(_theLogger,"SrmClientMisc",inputVec,silent,useLog);
         util.printMessage("\nPlease provide -permissiontype ",logger,silent);
         showUsage(false);
        }
        TPermissionType permType = null;
        if(permissionType.equalsIgnoreCase("ADD")) {
           permType = TPermissionType.ADD;
        }
        else if(permissionType.equalsIgnoreCase("REMOVE")) {
           permType = TPermissionType.REMOVE;
        }
        else if(permissionType.equalsIgnoreCase("CHANGE")) {
           permType = TPermissionType.CHANGE;
        }
        else {
         inputVec.clear();
         inputVec.addElement("Given -permissiontype is not supported " + permissionType);
         util.printEventLog(_theLogger,"SrmClientMisc",inputVec,silent,useLog);
         util.printMessage("\nSRM-MISC: Given -permissiontype is not supported "
				+ permissionType ,logger,silent);
         showUsage(false);
        }
        TPermissionMode ownerPerm = 
			getPermissionModeForString(ownerPermission);
        TPermissionMode otherPerm = 
			getPermissionModeForString(otherPermission);
        Vector vec = new Vector ();
        StringTokenizer stk = new StringTokenizer(userPermissions,",");
        while(stk.hasMoreTokens()) {
          String aaa = stk.nextToken(); 
          int idx = aaa.indexOf(":");
          if(idx != -1) {
            TUserPermission up = new TUserPermission();
            up.setUserID(aaa.substring(0,idx));
            TPermissionMode pp = 
			   getPermissionModeForString(aaa.substring(idx+1));
            up.setMode(pp);
            vec.addElement(up);
          }
        }
        TUserPermission[] userPerms = new TUserPermission[vec.size()];
        for(int i = 0; i < vec.size(); i++) {
          userPerms[i] = (TUserPermission)vec.elementAt(i);
        }
        vec = new Vector ();
        stk = new StringTokenizer(userPermissions,",");
        while(stk.hasMoreTokens()) {
          String bbb = stk.nextToken(); 
          int idx = bbb.indexOf(":");
          if(idx != -1) {
            TGroupPermission up = new TGroupPermission();
            up.setGroupID(bbb.substring(0,idx));
            TPermissionMode pp = 
			   getPermissionModeForString(bbb.substring(idx+1));
            up.setMode(pp);
            vec.addElement(up);
          }
        }
        TGroupPermission[] groupPerms = new TGroupPermission[vec.size()];
        for(int i = 0; i < vec.size(); i++) {
          groupPerms[i] = (TGroupPermission)vec.elementAt(i);
        }
        sCode = utilClient.doSrmSetPermission(fileInfo,permType,ownerPerm,	
              otherPerm, userPerms, groupPerms);
      }
      else {
        util.printMessage("\nPlease provide your options such as ",
			logger,silent);
        util.printMessage("\t -ping",logger,silent);
        util.printMessage("\t -protocols",logger,silent);
        util.printMessage("\t -setpermission",logger,silent);
        util.printMessage("\t -getpermission",logger,silent);
        util.printMessage("\t -checkpermission",logger,silent);
        inputVec.clear();
        inputVec.addElement("Please provide your options such as " + 
			"-ping, -protocols, -setpermission, -getpermission, -checkpermission");
        util.printEventLog(_theLogger,"SrmClientMisc",inputVec,silent,useLog);
      }
      int exitValue = util.mapStatusCode(sCode);
        inputVec.clear();
        inputVec.addElement("StatusCode="+exitValue);
        util.printEventLog(_theLogger,"StatusExit",inputVec,silent,useLog);
      System.exit(exitValue);
    }catch(Exception e) {
        util.printMessage("\nSRM-CLIENT:Exception from client="+e.getMessage(),logger,silent);
                    util.printEventLogException(_theLogger,"",e);
        inputVec.clear();
        inputVec.addElement("StatusCode=92");
        util.printEventLog(_theLogger,"StatusExit",inputVec,silent,useLog);
        util.printHException(e,pIntf);
      System.exit(92);
    }
}

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
// getRequestType
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

public String getRequestType() {
  return requestType;
}

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
// validateURL
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

private Vector  validateURL(Request request) {
  Vector result = new Vector ();
  Vector fInfo = request.getFiles();
  int size = fInfo.size();
  for(int i = 0; i < size; i++) {
    boolean skip = false;
    FileInfo f = (FileInfo) fInfo.elementAt(i);
    String surl = f.getOrigSURL();
    String turl = f.getOrigTURL();
    if(request.getModeType().equalsIgnoreCase("req")) {
       if((!surl.startsWith("srm://")) && (!surl.startsWith("http://")) &&
		  (!surl.startsWith("gsiftp://")) && (!surl.startsWith("ftp://"))) {
          util.printMessage("\nSRM-MISC: source url is not valid " + surl, logger,silent);
          util.printMessage("\nskipping this url in the request", 
				logger,silent);
          inputVec.clear();
          inputVec.addElement("SRM-MISC: Source url is not valid " + surl);
          inputVec.addElement("skipping this url in the request");
          util.printEventLog(_theLogger,"ValidateURL",inputVec,silent,useLog);
          skip = true;
       }
    }
    if(!skip) {
        result.add(f);
     }
   }
   size = result.size();  
   for(int i =0; i < result.size(); i++) { 
      FileInfo f = (FileInfo) result.elementAt(i);
      f.setLabel(i);
   }
   return result;
}

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
// showUsage
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

public void showUsage (boolean b) {
 if(b) { 
    System.out.println("Usage :\n"+
            "\tsrm-util [command line options] -s <sourceurl> -setpermission\n"+
            "\tor srm-util [command line options] -s <sourceurl> -getpermission\n"+
            "\tor srm-util [command line options] -s <sourceurl> -setpermission\n"+
            "\tor srm-util [command line options] -s <sourceurl> -checkpermission\n"+
            "\tor srm-util [command line options] -ping\n"+ 
            "\tor srm-util [command line options] -transferprotocols\n"+ 
            "\tor srm-util [command line options] -f <file>\n" +
            "\t  where <file> is the path to the xml file containing\n"+
            "\t  the sourceurl information.\n"+
            "\tfor requests with more than one file -f option is used.\n" +
            "\tcommand line options will override the options from conf file\n"+
            "\n"+
            "\t-conf              <path> \n"+
            "\t-s                 <sourceUrl>  \n" +
            "\t-f                 <fileName> (input file in xml format)\n"+
            "\t                      either -s sourceurl \n"+
            "\t                      or -f <filename> is needed.\n"+
            "\t-serviceurl        <full wsdl service url> \n" +
            "\t                      example srm://host:port/wsdlpath \n"+
            "\t                      (required for requests when sourceurl\n" +
            "\t                       does not have wsdl information)\n"+
            "\t-ping              (pings the srm server)   \n" +
            "\t-transferprotocols (returns the list of transfer protocols SRM supports)\n" +
            "\t-permissiontype    <add|remove|change> \n"+
            "\t-ownerpermission   <none|x|w|wx|r|rx|rw|rwx> \n"+
            "\t-otherpermission   <none|x|w|wx|r|rx|rw|rwx> \n"+
            "\t-userpermissions   <user:mode> e.g. foo:x,...\n"+
            "\t-grouppermissions   <user:mode> e.g. foo:x,...\n"+
            "\t-setpermission      \n" +
            "\t-getpermission      \n" +
            "\t-checkpermission    \n" +
            "\t-delegation         uses delegation (default:no delegation)\n"+
	        "\t-proxyfile         <path to proxyfile> \n"+
            "\t                      (default:from user default proxy location)\n"+
            "\t-storageinfo       <true|false|string> extra storage access information \n"+
			"\t                   when needed. a formatted input separated by comma is \n"+
		    "\t                   used with following keywords:\n"+
		    "\t                   for:<source|target|sourcetarget>,login:<string>,\n"+
		    "\t                   passwd:<string>,projectid:<string>,\n"+
			"\t                   readpasswd:<string>,writepasswd<string>(default:false)\n"+
            "\t-usercert          <path to usercert> \n" +
            "\t-userkey           <path to userkey> \n" +
            "\t-statuswaittime    (wait time between status checking in seconds)default:30\n"+
            "\t-statusmaxtime     (maximum time for status checking before\n"+
			"\t                     timeout in seconds)default:600\n"+
            //"\t-renewproxy        (renews proxy automatically) \n" +
            //"\t-connectiontimeout <integer in seconds> (enforce http connection timeout in the given seconds)default:600 \n"+
            "\t-sethttptimeout    <integer in seconds> (enforce SRM/httpg connection timeout and sets client-side http connection timeout in the given seconds)default:600 \n"+
	        "\t-quiet             default:false\n" +
            "\t                      suppress output in the console.\n" +
            "\t                      this option prints the output to the logfile default:./srmclient-event-date-random.log\n" +
            "\t-log               <path to logfile>\n"+
            "\t-debug             default:false\n" +
            "\t-help              show this message.");
 } 
        inputVec.clear();
        inputVec.addElement("StatusCode=93");
        util.printEventLog(_theLogger,"StatusExit",inputVec,silent,useLog);
 System.exit(93);
}

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
// getConfigFileLocation
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

public String getConfigFileLocation () {
  return configFileLocation;
}

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
// getProperties
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

public Properties getProperties() {
  return properties;
}

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
// setConfig
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

public void setConfig (XMLParseConfig config) {
  pConfig = config;
}

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
// setPassword
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

public void setPassword(String str) {
  _password = str;
  boolean b = false;
  if(_password != null && _password.trim().length() > 0) {
    b = true;
  }
}

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
// getPassword
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

public String getPassword() {
  return _password;
}

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
// getPermissionModeForString
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

private TPermissionMode getPermissionModeForString(String str) {

  TPermissionMode ownerPerm = TPermissionMode.NONE;
  if(str.equalsIgnoreCase("X")) {
   ownerPerm = TPermissionMode.X;
  }
  else if(str.equalsIgnoreCase("W")) {
   ownerPerm = TPermissionMode.W;
  }
  else if(str.equalsIgnoreCase("WX")) {
   ownerPerm = TPermissionMode.WX;
  }
  else if(str.equalsIgnoreCase("R")) {
   ownerPerm = TPermissionMode.R;
  }
  else if(str.equalsIgnoreCase("RX")) {
   ownerPerm = TPermissionMode.RX;
  }
  else if(str.equalsIgnoreCase("RW")) {
   ownerPerm = TPermissionMode.RW;
  }
  else if(str.equalsIgnoreCase("RWX")) {
   ownerPerm = TPermissionMode.RWX;
  }
  return ownerPerm;
}

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
//  main driver method
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

public static void main(String[] args) {
   new SRMClientMisc(args,null);
}

}

