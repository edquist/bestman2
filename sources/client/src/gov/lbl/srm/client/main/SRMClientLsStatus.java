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
import java.io.*;
import java.beans.XMLEncoder;
import java.beans.XMLDecoder;
import gov.lbl.srm.client.util.MyGlobusURL;

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

import gov.lbl.srm.client.intf.*;
import gov.lbl.srm.client.util.*;
import gov.lbl.srm.client.exception.*;
import gov.lbl.srm.client.wsdl.*;

//import srm.common.StorageResourceManager.*;
import gov.lbl.srm.StorageResourceManager.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.*;


//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
// Class SRMClientLsStatus
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

public class SRMClientLsStatus
{

private GSSCredential mycred;
private XMLParseConfig pConfig = new XMLParseConfig();

private Properties properties = new Properties();
private String configFileLocation = "";
private String inputFile="";
private String _password ="";
private String userKey="";
private String userCert="";
private String proxyFile="";
private boolean isRenew = false;
private String storageInfo="";
private String sourceUrl="";
private int servicePortNumber=0;
private String serviceHandle="";
private boolean serviceURLGiven=false;
private String delegationNeeded="";
private String serviceURL="";
//private String targetUrl="";
private String serviceUrl="";
private String uid="";
private Request request;
private String requestType="";
//private String fileToken;
private String log4jlocation="";
private String logPath="";
private String eventLogPath="";
private String outputFile="";
private boolean textReport=true;

private static Log logger;
private boolean onlyList=false;
private boolean doGsiFTPList=false;
private boolean recursive;
private boolean fullDetailedList;
private int numLevels=-1;
private int count = 0; 
private int offset = 0;
private int proxyType;
private char tokenType;
private int connectionTimeOutAllowed=600;
private int setHTTPConnectionTimeOutAllowed=600;
private boolean gotConnectionTimeOut;
private boolean gotHTTPConnectionTimeOut;

private SRMWSDLIntf srmCopyClient;

private boolean _debug=false;
private boolean silent=false;
private boolean useLog=false;
private TFileStorageType fileStorageType = null;
private boolean doStatus = false;
private String requestToken = "";
//private int statusMaxTimeAllowed=600;
private int statusMaxTimeAllowed=-1;
private int statusWaitTime=30;
private boolean statusWaitTimeGiven=false;
private java.util.logging.Logger _theLogger =
        java.util.logging.Logger.getLogger
            (gov.lbl.srm.client.main.SRMClientN.class.getName());
private java.util.logging.FileHandler _fh;
private Vector inputVec = new Vector ();
private PrintIntf pIntf;


//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
// SRMClientLsStatus
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::


public SRMClientLsStatus(String[] args, PrintIntf pIntf) {

  this.pIntf = pIntf;
  if(args.length == 0) {
    showUsage(true);
  }

  for(int i = 0; i < args.length; i++) {
    boolean ok = false;
    if(i == 0 && !args[i].startsWith("-")) {
      requestToken=args[i];
      ok = true;
    }
    /*
    else if(i == 1 && !args[i].startsWith("-")) {
      targetUrl=args[i];
      ok = true;
    }
    */
    else if(args[i].equalsIgnoreCase("-conf") && i+1 < args.length) {
      configFileLocation = args[i+1];
      i++;
    }
    else if(args[i].equalsIgnoreCase("-requesttoken") && i+1 < args.length) {
      requestToken = args[i+1];
      i++;
    }
    /*
    else if(args[i].equalsIgnoreCase("-log4jlocation") && i+1 < args.length) {
      log4jlocation = args[i+1];
      i++;
    }
    */
    else if(args[i].equalsIgnoreCase("-authid") && i+1 < args.length) {
      uid = args[i+1];
      i++;
    }
    else if(args[i].equalsIgnoreCase("-nogui")) {
      ;
    }
    else if(args[i].equalsIgnoreCase("-recursive")) { 
      recursive=true;
    }
    else if(args[i].equalsIgnoreCase("-fulldetailed")) { 
      fullDetailedList=true;
    }
    else if(args[i].equalsIgnoreCase("-lite")) {
       ;
    }
    else if(args[i].equalsIgnoreCase("-count")) { 
      try {
       count = Integer.parseInt(args[i+1]);
      }catch (NumberFormatException nfe) {
        util.printMessage("\nSRM-DIR: Given count is not valid" + count, 
			logger,silent);
        util.printMessage("SRM-DIR: Using the default value",logger,silent);
      }
      i++;
    }
    else if(args[i].equalsIgnoreCase("-offset")) { 
      try {
       offset = Integer.parseInt(args[i+1]);
      }catch (NumberFormatException nfe) {
        util.printMessage("\nSRM-DIR: Given offset is not valid" + count, 
			logger,silent);
        util.printMessage("SRM-DIR: Using the default value",logger,silent);
      }
      i++;
    }
    else if(args[i].equalsIgnoreCase("-numlevels")) { 
      try {
       numLevels = Integer.parseInt(args[i+1]);
      }catch (NumberFormatException nfe) {
        util.printMessage("\nSRM-DIR: Given numLevels is not valid" + numLevels, 
			logger,silent);
        util.printMessage("SRM-DIR: Using the default value",logger,silent);
      }
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
    else if(args[i].equalsIgnoreCase("-type") && i+1 < args.length) {
      String temp = args[i+1];
      if(temp.toUpperCase().startsWith("P")) {
         tokenType = 'p';
      }
      else if(temp.toUpperCase().startsWith("D")) {
         tokenType = 'd';
      }
      else if(temp.toUpperCase().startsWith("V")) {
         tokenType = 'v';
      }
      i++;
    }
    else if(args[i].equalsIgnoreCase("-log") && i+1 < args.length) {
      useLog=true;
      eventLogPath = args[i+1];
      i++;
    }
    else if(args[i].equalsIgnoreCase("-report") && i+1 < args.length) {
      outputFile = args[i+1];
      i++;
    }
    /*
    else if(args[i].equalsIgnoreCase("-textreport")) {
      textReport = true;
    }
    */
    else if(args[i].equalsIgnoreCase("-serviceurl") && i+1 < args.length) {
      serviceUrl = args[i+1];
      serviceURLGiven=true;
      i++;
    }
    else if(args[i].equalsIgnoreCase("-f") && i+1 < args.length) {
      inputFile = args[i+1];
      i++;
    }
    else if(args[i].equalsIgnoreCase("-s") && i+1 < args.length) {
      sourceUrl = args[i+1];
      i++;
    }
    /*
    else if(args[i].equalsIgnoreCase("-t") && i+1 < args.length) {
      targetUrl = args[i+1];
      i++;
    }
    */
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
      try {
        statusMaxTimeAllowed = Integer.parseInt(temp);
      }catch(NumberFormatException nfe) {
          System.out.println("SRM-DIR: Given -statusmaxtime is not " +
				"valid, using the default " + statusMaxTimeAllowed);
      }
      i++;
    }
    else if(args[i].equalsIgnoreCase("-statuswaittime") && i+1 < args.length) {
      String temp = args[i+1];
      statusWaitTimeGiven=true;
      try {
        statusWaitTime = Integer.parseInt(temp);
      }catch(NumberFormatException nfe) {
          System.out.println("SRM-DIR: Given -statuswaittime is not valid, using the default " +
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
      boolean b = gov.lbl.srm.client.util.Util.parseSrmCpCommands(args[i],0);
      if(b) ;
      else {
        if(!ok) {
          System.out.println("Args " + i + "  " + args[i]);
          showUsage (true);
        }
      }
    }
  }

  Properties sys_config = new Properties(); 

  try {
    if(!configFileLocation.equals("")) {
       File f = new File(configFileLocation);
       if(!f.exists()) {
         System.out.println("\nSRM-DIR: Config file did not exists " + 	
			configFileLocation);
         showUsage(false);
       }
       if(_debug) {
         System.out.println("\nSRM-DIR: Parsing config file " + configFileLocation);
       } 
       sys_config = gov.lbl.srm.client.util.Util.parsefile(configFileLocation,"SRM-DIR",silent,useLog, _theLogger); 
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
        sys_config = gov.lbl.srm.client.util.Util.parsefile(configFileLocation,"SRM-DIR",silent,useLog, _theLogger); 
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


 String detailedLogDate = util.getDetailedDate();

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
      logPath="./srm-client-detailed-"+detailedLogDate+".log";
    }
  }
  */

  if(outputFile.equals("")) {
    String temp = (String) sys_config.get("output");
    if(temp != null) {
      outputFile = temp;
    }
  }

  /*
  if(silent) {
    if(logPath.equals("")) {
      System.out.println("\nSRM-DIR: For the option quiet, -log is needed to " + 
        " forward to output to the logfile");
      System.out.println("SRM-DIR: Please provide -log <path to logfile> ");
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

  if(!outputFile.equals("")) {
    //check existence of outputFile.
    if(outputFile.endsWith("/")) {
       outputFile = outputFile.substring(0,outputFile.length()-1);
    }

    int idx = outputFile.lastIndexOf("/");
    if(idx != -1) {
      try {
        File f = new File(outputFile.substring(0,idx));
        if(!f.exists()) {
           System.out.println("SRM-DIR: Given outputFile location does not eixsts " +
            outputFile); 
           showUsage(false); 
        }
      }catch(Exception e) {
         util.printMessage("\nSRM-CLIENT:Exception from client="+e.getMessage(),logger,silent);
                    util.printEventLogException(_theLogger,"",e);
         showUsage(false); 
      }
    }
  } 

  //check existence of logfile.
  /*
  if(!logPath.equals("")) {
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
           System.out.println("SRM-DIR: Given logpath location is a directory " +
            " please provide the full path with desired file name logPath"); 
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
         Class c = cl.loadClass("gov.lbl.srm.client.main.SRMClientDirectory");
         logger = LogFactory.getLog(c.getName());
      }catch(ClassNotFoundException cnfe) {
         System.out.println("ClassNotFoundException " + cnfe.getMessage());
                    util.printEventLogException(_theLogger,"",cnfe);
      }
   }
  }
  else {
     logPath="./srm-client-detailed.log";
  }
  */

  try {
  PropertyConfigurator.configure(log4jlocation); 
  }catch(Exception ee){;}
  
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
         //System.out.println
		   //("\nProxyFile or UserCert and UserKey is not provided");
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
           //System.out.println
		     //("\nUserCert and UserKey both should be provided");
           showUsage(false);
         }
       }
    }

    if(isRenew) {
      String v1 = (String) properties.get("user-cert");
      String v2 = (String) properties.get("user-key");
      if(v1.startsWith("Enter") || v2.startsWith("Enter")) {
        inputVec.clear(); 
        inputVec.addElement("If you want to renew proxy automatically ");
        inputVec.addElement("you need to enter user-cert location and " +
			"user-key location.");
        util.printEventLog(_theLogger,"Error",inputVec,silent,useLog);
        System.out.println("\nSRM-DIR: If you want to renew proxy automatically,\n "+
         "you need to enter user-cert location and user-key location.");
        inputVec.clear(); 
        inputVec.addElement("StatusCode=93");
        util.printEventLog(_theLogger,"StatusExit",inputVec,silent,useLog);
        System.exit(93); 
      }
      String v3 = (String)properties.get("proxy-file");
      if(v3.startsWith("Enter")) {
        inputVec.clear(); 
        inputVec.addElement("If you want to renew proxy automatically, ");
        inputVec.addElement ("please enter your proxy file location.");
        util.printEventLog(_theLogger,"Error",inputVec,silent,useLog);
        System.out.println("\nSRM-DIR: If you want to renew proxy automatically,\n "+
          "please enter your proxy file location.");
        inputVec.clear(); 
        inputVec.addElement("StatusCode=93");
        util.printEventLog(_theLogger,"StatusExit",inputVec,silent,useLog);
        System.exit(93); 
      }
      else {
        inputVec.clear(); 
        util.printEventLog(_theLogger,"Enter Grid Passphrase",inputVec,silent,useLog);
        String line = PasswordField.readPassword("Enter GRID passphrase: ");
        _password = line;
      }
      //else there is no need to renew proxy.
    }

    String[] surl = null;
    surl = new String[1];
    String[] turl = null;
    turl = new String[1];

    Vector fileInfo = new Vector();

   /*
    if(inputFile.equals("")) { 
       if(sourceUrl.equals("")) {
           inputVec.clear(); 
           inputVec.addElement("Please provide <sourceUrl>");
		   inputVec.addElement (" or -f <inputfile>");
           util.printMessage 
	   	     ("\nPlease provide <sourceUrl>\n",logger,silent);
           util.printMessage 
		     ("or -f <inputfile>\n",logger,silent);
           showUsage(false);
        }
        surl[0] = sourceUrl;

        


        if(surl[0].startsWith("gsiftp://")) {
          doGsiFTPList = true;
        }
        request = gov.lbl.srm.client.util.Util.createRequest(surl[0],"","",_debug,silent,useLog,
			"SRM-DIR",recursive,_theLogger,logger);
      request.setModeType("dir");
    } 
    else {
      //by default request is get, so target dir check is done in parseXML
      request = gov.lbl.srm.client.util.Util.parseXML(inputFile,"SRM-DIR",silent,useLog,_theLogger);
      request.setModeType("dir");
    }

    requestType = request.getModeType();
    fileInfo = validateURL(request);

    if(fileInfo.size() == 0) {
       inputVec.clear(); 
       inputVec.addElement("No files in the request for transfer");
       inputVec.addElement("Cannot proceed further, please check input");
       util.printEventLog(_theLogger,"Error",inputVec,silent,useLog);
       util.printMessage("\nSRM-DIR: No files in the request for transfer",
				logger,silent);
       util.printMessage("SRM-DIR: Cannot proceed further, please check input",
				logger,silent);
       showUsage(false);
    }
    */

    if(serviceUrl.equals("")) {
      String temp = "";
      if(fileInfo.size() > 0) {
        FileIntf fIntf = (FileIntf) fileInfo.elementAt(0);
        temp = fIntf.getSURL();
      }
      serviceUrl = gov.lbl.srm.client.util.Util.getServiceUrl(
		   temp,serviceURL,serviceHandle,servicePortNumber,1, silent,useLog,_theLogger,logger);
      if(serviceUrl == null) showUsage(false);
      for(int i = 0; i < fileInfo.size(); i++) {
         FileIntf fIntf = (FileIntf) fileInfo.elementAt(i);
         temp = fIntf.getSURL();
         String sfn = gov.lbl.srm.client.util.Util.getSFN(temp);
         fIntf.setSURL(serviceUrl.replace("httpg","srm")+sfn);
       }
    }

    if(serviceUrl.equals("")) {
       String tt = (String) sys_config.get("ServiceUrl");
       if(tt != null) {
         serviceUrl = tt;
       }
       else {
         inputVec.clear(); 
         inputVec.addElement("Please provide the -serviceurl full " +
			"SRM service url");
		 inputVec.addElement (" example:srm://<hostname>:<port>//wsdlpath");
         util.printEventLog(_theLogger,"Error",inputVec,silent,useLog); 
         util.printMessage 
          ("\nPlease provide the -serviceurl full SRM service url",
				logger,silent);
         util.printMessage 
		  ("  example:srm://<hostname>:<port>//wsdlpath",logger,silent);
              showUsage(false);
         }
      }


     GSSCredential credential=null;
     try {
       credential = gov.lbl.srm.client.util.Util.checkTimeLeft
	 		(pConfig,properties,_password, _theLogger,silent,useLog,logger,pIntf,_debug);
       proxyType = gov.lbl.srm.client.util.Util.getProxyType(properties);
     }catch(Exception ee) {
        util.printMessage("\nSRM-CLIENT:Exception from client="+ee.getMessage(),logger,silent);
                    util.printEventLogException(_theLogger,"",ee);
        inputVec.clear(); 
        inputVec.addElement("StatusCode=92");
        util.printEventLog(_theLogger,"StatusExit",inputVec,silent,useLog);
        util.printHException(ee,pIntf);
        System.exit(92);
     }

     serviceUrl = gov.lbl.srm.client.util.Util.getServiceUrl(
				serviceUrl,serviceURL,serviceHandle,servicePortNumber,0, silent,useLog,_theLogger,logger);
     if(serviceUrl == null) showUsage(false);
 
      //if(!doGsiFTPList) {
        //util.printMessage("\nSRM-DIR: ServiceUrl="+serviceUrl,logger,silent);
      //}

      inputVec.clear(); 
      inputVec.addElement("ServiceUrl="+serviceUrl);
     if(requestToken == null || requestToken.equals("")) {
      util.printMessage("\nPlease provide a valid request token",logger,silent);
      showUsage(false);
     }

     if(_debug) {
      util.printMessage("\n===================================",logger,silent);
      util.printMessage("SRM-DIR configuration",logger,silent);
      if(configFileLocation.equals("")) {
        util.printMessage("\n\tConfFile=none",logger,silent);
        inputVec.addElement("ConfFile=none");
      }
      else {
        util.printMessage("\tConfFile="+configFileLocation,logger,silent);
        inputVec.addElement("ConfFile="+configFileLocation);
      } 
      util.printMessage("\tInputFile="+inputFile,logger,silent);
      //inputVec.addElement("InputFile="+inputFile);

      //if(!logPath.equals("")) {
        //util.printMessage("\tLog4jlocation=" + log4jlocation, logger,silent);
        //util.printMessage("\tlogPath=" + logPath, logger,silent);
        //inputVec.addElement("Log4jlocation=" + log4jlocation);
        //inputVec.addElement("logPath=" + logPath);
      //}   

      if(!outputFile.equals("")) {
        util.printMessage("\toutputFile=" + outputFile, logger,silent);
        //inputVec.addElement("outputFile=" + outputFile);
      }   

      util.printMessage("\tserviceUrl=" + serviceUrl,logger,silent);
      util.printMessage("\tProxyFile="+proxyFile,logger,silent);     
      inputVec.addElement("ProxyFile="+proxyFile);
      util.printMessage("\tRenew Proxy="+isRenew,logger,silent);     
      inputVec.addElement("Renew Proxy="+isRenew);
      util.printMessage("\tdoGsiFTPList="+doGsiFTPList,logger,silent);     
      inputVec.addElement("doGsiFTPList="+doGsiFTPList);
      util.printMessage("\tDebug ON=" + _debug,logger,silent);
      inputVec.addElement("Debug="+_debug);
      util.printMessage("\tQuiet ON=" + silent,logger,silent);
      inputVec.addElement("Quiet="+silent);
     }

     util.printEventLog(_theLogger,"INITIALIZATION",inputVec,silent,useLog);

     util.printMessage("SRM-CLIENT: " + "Connecting to serviceurl " +
            serviceUrl,logger,silent);

      /*
      Properties props = System.getProperties();
      String uTemp = props.getProperty("user.name");
      if(uTemp != null) {
         uid = uTemp;
      }
      */

       int exitValue = 0;
      SRMUtilClient utilClient = new SRMUtilClient(serviceUrl,uid,"",
			   credential, _theLogger, logger, pIntf, 
			   _debug,silent, useLog,doGsiFTPList, false,
			   statusMaxTimeAllowed,statusWaitTime,storageInfo,proxyType,
			   connectionTimeOutAllowed,setHTTPConnectionTimeOutAllowed,
			   delegationNeeded,3,60);

      if(!requestToken.equals("")) {
             //util.printMessage("\nSRM-DIR: Doing srm ls status for the request token ", logger,silent);
             inputVec.clear(); 
             inputVec.addElement("Doing srm ls status for the request token ");
             util.printEventLog(_theLogger,"DoListStatus",inputVec,silent,useLog);
             TStatusCode sCode = utilClient.doSrmLsStatus(requestToken,false,count,
                offset, outputFile,false,new Vector(),"","");
             exitValue = util.mapStatusCode(sCode);
           }
           else {
             util.printMessage("\nSRM-DIR: Request token for srmls cannot be null",
				logger,silent); 
             inputVec.clear(); 
             inputVec.addElement("Request token for srmls cannot be null");
             util.printEventLog(_theLogger,"DoListStatus",inputVec,silent,useLog);
             showUsage(false);
           }

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
// checkAllSourceFromSameSRM
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

private boolean checkAllSourceFromSameSRM(Vector fInfo) 
	throws Exception {

  int size = fInfo.size();

  String sourceSRM = "";


  if(request.getModeType().equalsIgnoreCase("dir")) {
    if(size > 0) {
      FileInfo files = (FileInfo) fInfo.elementAt(0);
      String surl = files.getSURL();
      sourceSRM = gov.lbl.srm.client.util.Util.findServiceSRMFromSURL(surl);
    }
    for(int i = 1; i < size; i++) {
      FileInfo files = (FileInfo) fInfo.elementAt(i);
      String surl = files.getSURL();
      String sSRM = gov.lbl.srm.client.util.Util.findServiceSRMFromSURL(surl);
      if(!sourceSRM.equals(sSRM)) {
        inputVec.clear(); 
        inputVec.addElement("Sources from multiple SRMs are not allowed"); 
        util.printEventLog(_theLogger,"CheckAllSourceFromSameSRM",inputVec,silent,useLog);
        util.printMessage("\nSRM-DIR: sources from multiple SRMs are not allowed ",
			logger,silent);        
		return false;
      }
    }
  }
  return true;
}

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
// validateURL
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

private Vector  validateURL(Request request) {
  Vector result = new Vector ();
  Vector fInfo = request.getFiles();
  int size = fInfo.size();
  boolean simpleSURL=false;

  try {
    if (!checkAllSourceFromSameSRM(fInfo)) {  
        showUsage(false);
    }
  }catch(Exception e) {
    util.printMessage("\nSRM-CLIENT:Exception from client="+e.getMessage(),logger,silent);
                    util.printEventLogException(_theLogger,"",e);
    inputVec.clear(); 
    util.printEventLog(_theLogger,"Exception",inputVec,silent,useLog);
    System.out.println("SRM-DIR: Exception="+e.getMessage());
    //util.printStackTrace(e,logger);
  }
  for(int i = 0; i < size; i++) {
    boolean skip = false;
    FileInfo f = (FileInfo) fInfo.elementAt(i);
    String surl = f.getOrigSURL();
    String turl = f.getOrigTURL();
    if(request.getModeType().equalsIgnoreCase("dir")) {
       if(surl.startsWith("gsiftp://")) {
         if(simpleSURL) {
            //mixed type of surls in the request.
            //only allowing either surls with srm:// or surls with gsiftp://
            inputVec.clear(); 
            inputVec.addElement("Mixed type of surls are not allowed");
            inputVec.addElement(" Only surls with gsiftp or surls with ");
            inputVec.addElement("srm is allowed currently for listing");
            util.printEventLog(_theLogger,"ValidateURL",inputVec,silent,useLog);
            util.printMessage("\nSRM-DIR: Mixed type of surls are not allowed",
				logger,silent); 
            util.printMessage("SRM-DIR: Only surls with gsiftp or surls with srm is "+
              "allowed currently for listing", logger, silent);
            showUsage(false); 
         }
         else {
           doGsiFTPList=true;
         }
       }
       else {
         if(doGsiFTPList) {
            //mixed type of surls in the request.
            //only allowing either surls with srm:// or surls with gsiftp://
            inputVec.clear(); 
            inputVec.addElement("Mixed type of surls are not allowed");
            inputVec.addElement("Only surls with gsiftp or surls with srm is "+
              "allowed currently for listing");
            util.printEventLog(_theLogger,"ValidateURL",inputVec,silent,useLog);
            util.printMessage("\nSRM-DIR: Mixed type of surls are not allowed",
				logger,silent); 
            util.printMessage("SRM-DIR: Only surls with gsiftp or surls with srm is "+
              "allowed currently for listing", logger, silent);
            showUsage(false); 
         }
         else {  
           simpleSURL=true;
         }
       }
       if((!surl.startsWith("srm://")) && (!doGsiFTPList)) {
            inputVec.clear(); 
            inputVec.addElement("source url is not valid " + surl);
            inputVec.addElement("skipping this url in the request" );
            util.printEventLog(_theLogger,"ValidateURL",inputVec,silent,useLog);
            util.printMessage("\nSRM-DIR: source url is not valid " + surl, 
                 logger,silent);
            util.printMessage("\nSRM-DIR: skipping this url in the request", 
				logger,silent);
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
//findRequestType
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

private void findRequestType(String surl, String turl, String durl) {
   
   if(!durl.equals("")) {
   } 
}

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
// showUsage
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

public void showUsage (boolean b) {
 if(b) { 
    System.out.println("Usage :\n"+
            "\tsrm-ls-status <request_token> [command line options]\n" +
            "\tor srm-ls-status -requesttoken <request_token> [command line options]\n" +
            "\tcommand line options will override the options from conf file\n"+
            "\n"+
            "\t-conf              <path>\n"+
            "\t-count             <valid integer> default:0\n" +
            "\t-offset            <valid integer> default:0\n" +
            "\t-serviceurl        <full wsdl service url> \n" +
            "\t                      example srm://host:port/wsdlpath \n"+
            "\t-requesttoken      <request_token> (valid request token returned by SRM) \n" +
            //"\t-connectiontimeout <integer in seconds> (enforce http connection timeout in the given seconds)default:600 \n"+
            "\t-sethttptimeout    <integer in seconds> (enforce SRM/httpg connection timeout and sets client-side http connection timeout in the given seconds)default:600 \n"+
            "\t-authid            <user authorization id used in SRM> \n" +
            "\t-delegation         uses delegation (default:no delegation)\n"+
            "\t-proxyfile         <path to proxyfile>default:from user default proxy location \n"+
            "\t-renewproxy        (renews proxy automatically)default:false\n" +
            "\t-usercert          <path to usercert> \n" +
            "\t-userkey           <path to userkey> \n" +
			"\t-quiet             default:false\n" +
            "\t                     suppress output in the console.\n" +
            "\t                     this option writes the output to logfile default:./srmclient-event-date-random.log\n" +
			"\t-log               <path to logfile>\n" +
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

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
//  main driver method
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

public static void main(String[] args) {
   new SRMClientLsStatus(args,null);
}

}

