/**
*  WebService.java
*
* Copyright (c) 2012 NCQ Solutions
*
* This software is the confidential and proprietary information of
* NCQ Solutions. ("Confidential Information"). You shall not
* disclose such Confidential Information and shall use it only in
* accordance with the terms of the license agreement you entered into
* with NCQ.
 */

package com.ncq.ubi;


import java.io.IOException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

/**
 * This class allows the app comunicate with the web services of UBITEC
 * and get all information required
 * 
 *  *@author NCQ SOLUTIONS
 *
 */
public class WebService {
	/**Some data to connect with the web service*/
	private static final String LOGTAG = "LogsAndroid";
	String SOAP_ACTION; 
	String NAMESPACE = "http://tempuri.org/";
	String URL = "http://ubitec.co.cr/wsUbitecProyecto/wstrack.asmx";

	SoapObject request;	

	/** 
	* create web service request for metodo 
	* */
	public void crearSoap(String metodo){
	try {
	request = new SoapObject(NAMESPACE, metodo);
	      SOAP_ACTION = NAMESPACE+metodo;
	} catch (Exception e) {
	Log.e(LOGTAG, "Divisi�n por cero!", e);
	// TODO: handle exception
	}
	}

	/**
	* Query web service for login activity 
	* @param name user name
	* @param password user password
	* @return xml result, user profile details or error in login
	*/
	public String Login(String name,String password){
	crearSoap("LoginXML"); // request LoginXML
	request.addProperty("pUsuario",name); //add user name to xml
	request.addProperty("pClave",password); // add password to xml

	String resultado=conexion_aux(); 
	return resultado;
	}



	/**
	* send the request to web service in hopes get a answer
	* we will communicate through SOAP protocol
	* @return xml result
	*/
	public String conexion_aux(){
	Object result = null;
	   
	        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	        envelope.dotNet=true;
	        envelope.setOutputSoapObject(request);
	        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);	        
	        try {
	androidHttpTransport.call(SOAP_ACTION, envelope);
	result = envelope.getResponse();
	} catch (IOException e) {
	return "No hubo conexi�n"; // the connection was refused
	//e.printStackTrace();
	} catch (XmlPullParserException e) {
	return "No hubo conexi�n"; //the connection was refused
	//e.printStackTrace();
	}
	        return result.toString();	   
	    }
	}