package com.ncq.ubi;



import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.ncq.ubi.R;

import android.R.string;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class Login extends Activity {
	@SuppressLint("NewApi")
	@Override
	  public void onCreate(Bundle savedInstanceState) {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		  StrictMode.setThreadPolicy(policy);
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.login);//Muestra el layout main.xml
	        verificarDatosAlamcenados();
	 
	    }
	
	
	    public void login(View v)
	    {
	    	EditText edtUser = (EditText)findViewById(R.id.edtUser);
            EditText edtPass = (EditText)findViewById(R.id.edtPassword);
	    	Boolean flag = log(edtUser.getText().toString(),edtPass.getText().toString());
	    	if (flag == true)
	    	{
	    		SharedPreferences preferencias=getSharedPreferences("datosUsuario",Context.MODE_PRIVATE);
	            Editor editor=preferencias.edit();
	            editor.putString("usuario", edtUser.getText().toString());
	            editor.putString("clave", edtPass.getText().toString());
	            editor.commit();
	            finish();
	            Toast.makeText(getApplicationContext(),
							"Usuario Y Contraseña Correctos",
							Toast.LENGTH_LONG).show();
	    		cambiarActividad();
	    	}
	    }
	    public void cambiarActividad(){
	    	Intent i = new Intent(this, MainActivity.class);//Creamos un nuevo intent para llamar a la siguiente actividad
		        startActivity(i);//Ejecutamos la actividad para que muestre la segunda actividad
	       
	    }
	    private boolean log(String user, String pass)
	   	{
	           ProgressBar Progress= (ProgressBar) findViewById(R.id.progressBarLog);
	   		
	   		Progress.setMax(100);
	   		Progress.setProgress(0);
	   		WebService ncqtrack = new WebService();
	           
	             
	             String resultado = ncqtrack.Login(user,pass); // connection result  
	             Progress.setProgress(40);
	   		
	   		//String resultado = ncqtrack.Login("proyecto","12345"); // connection result
	   		if (resultado.equals("No hubo conexi—n")) {
	   			Progress.setProgress(100);
	   			Toast.makeText(getApplicationContext(), "No hubo conexión", 
	   					Toast.LENGTH_LONG).show(); // Connection failed
	   					return false;
	   		}
	   		else {

		    	
	   						
	   			boolean validate = false;
	   			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	   			dbf.setValidating(validate);
	   			dbf.setNamespaceAware(true);
	   			dbf.setIgnoringElementContentWhitespace(true);
	   			Document document = null;
	   			
	   			Progress.setProgress(50);
	   			try {
	   				
	   				// delete garbage in the web service response like "[" or "]" 
	   				if( (resultado.substring(0,1)).equals("[") && 
	   						resultado.substring(resultado.length()-1,resultado.length()).equals("]")){
	   					if(resultado.substring(resultado.length()-12, resultado.length()).equals(", anyType{}]")){
	   						resultado=resultado.substring(1,resultado.length()-12);
	   					} else{
	   						resultado=resultado.substring(1,resultado.length()-1);
	   					}
	   				}
	   											
	   				DocumentBuilder builder = dbf.newDocumentBuilder();
	   				document = builder.parse(new InputSource(
	   				new StringReader(resultado)));//leer XML
	   				Progress.setProgress(75);
	   				NodeList nodeList = document.getElementsByTagName("Respuesta");
	   				
	   				String respuesta = nodeList.item(0).getTextContent();
	   				if (respuesta.compareTo("N")==0) {
	   					
	   					Progress.setProgress(100);
	   					Progress.getProgressDrawable().setColorFilter(Color.GREEN, Mode.SRC_IN);
	   					return true;
	   					
	   				}else if (respuesta.compareTo("S")==0)
	   				{
	   					Progress.setProgress(100);
	   					Progress.getProgressDrawable().setColorFilter(Color.RED, Mode.SRC_IN);
	   					nodeList = document.getElementsByTagName("Detalle");
	   					Toast.makeText(getApplicationContext(),
	   							nodeList.item(0).getTextContent(),
	   							Toast.LENGTH_LONG).show();
	   					return false;
	   				}
	   						
	   			} catch (Exception e) {
	   				Progress.setProgress(100);
	   				Toast.makeText(getApplicationContext(), e.toString(),
	   						Toast.LENGTH_LONG).show();
	   				return false;
	   				
	   			}
	   		
	   		}
	   		return false;
	   	}
	    
	    public void verificarDatosAlamcenados()
	    {
	    	SharedPreferences prefe=getSharedPreferences("datosUsuario",Context.MODE_PRIVATE);
	        String usuario = prefe.getString("usuario","");
	        String clave = prefe.getString("clave","");
	        
	    	if (usuario.equals("")){
	        }else
	        {   Boolean flag = log(usuario,clave);
	        	if (flag == true)
		    	{
		    		cambiarActividad();
		    	}else
		    	{
		    		EditText edtUser = (EditText)findViewById(R.id.edtUser);
		    		edtUser.setText(usuario);
		    	}
	        }
	    	
	    }
}
