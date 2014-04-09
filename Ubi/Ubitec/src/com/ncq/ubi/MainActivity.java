package com.ncq.ubi;

import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.ncq.ubi.R;

import android.R.string;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.ClipData.Item;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SearchView.OnQueryTextListener;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class MainActivity extends Activity implements OnQueryTextListener {
	@SuppressLint("NewApi")
	private String[] titulos;
	private DrawerLayout NavDrawerLayout;
	private ListView NavList;
	private ArrayList<Item_objct> NavItms;
	private TypedArray NavIcons;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	NavigationAdapter NavAdapter, NavAdapter2;
	ArrayList<String> listNombComp= new ArrayList<String> ();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// Drawer Layout
		NavDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		NavList = (ListView) findViewById(R.id.lista);
		// Declaramos el header el cual sera el layout de header.xml
		View header = getLayoutInflater().inflate(R.layout.header, null);
		// Establecemos header
		NavList.addHeaderView(header);
		// Tomamos listado de imgs desde drawable
		NavIcons = getResources().obtainTypedArray(R.array.navigation_iconos);
		// Tomamos listado de titulos desde el string-array de los recursos
		// @string/nav_options
		titulos = getResources().getStringArray(R.array.nav_options);
		// Listado de titulos de barra de navegacion
		NavItms = new ArrayList<Item_objct>();
		// Agregamos objetos Item_objct al array
		// Perfil
		NavItms.add(new Item_objct(titulos[0], NavIcons.getResourceId(0, -1)));
		// Favoritos
		NavItms.add(new Item_objct(titulos[1], NavIcons.getResourceId(1, -1)));
		// Eventos
		NavItms.add(new Item_objct(titulos[2], NavIcons.getResourceId(2, -1)));
		// Lugares
		NavItms.add(new Item_objct(titulos[3], NavIcons.getResourceId(3, -1)));
		// Etiquetas
		NavItms.add(new Item_objct(titulos[4], NavIcons.getResourceId(4, -1)));
		// Declaramos y seteamos nuestro adaptador al cual le pasamos el array
		// con los titulos
		NavAdapter = new NavigationAdapter(this, NavItms);
		NavList.setAdapter(NavAdapter);
		// Siempre vamos a mostrar el mismo titulo
		mTitle = mDrawerTitle = getTitle();

		Resources res = getResources();

		TabHost tabs = (TabHost) findViewById(android.R.id.tabhost);
		tabs.setup();

		TabHost.TabSpec spec = tabs.newTabSpec("mitab1");
		spec.setContent(R.id.tab1);
		spec.setIndicator("Placa",
				res.getDrawable(android.R.drawable.ic_dialog_map));
		tabs.addTab(spec);

		spec = tabs.newTabSpec("mitab2");
		spec.setContent(R.id.tab2);
		spec.setIndicator("Descripción",
				res.getDrawable(android.R.drawable.ic_dialog_map));
		tabs.addTab(spec);

		tabs.setCurrentTab(0);
		tabs.setOnTabChangedListener(ontabCompchange);

		// Declaramos el mDrawerToggle y las imgs a utilizar
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		NavDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* Icono de navegacion */
		R.string.app_name, /* "open drawer" description */
		R.string.hello_world /* "close drawer" description */
		) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				Log.e("Cerrado completo", "!!");
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				Log.e("Apertura completa", "!!");
			}
		};

		// Establecemos que mDrawerToggle declarado anteriormente sea el
		// DrawerListener
		NavDrawerLayout.setDrawerListener(mDrawerToggle);

		// Establecemos que el ActionBar muestre el Boton Home
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Establecemos la accion al clickear sobre cualquier item del menu.
		// De la misma forma que hariamos en una app comun con un listview.
		NavList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				MostrarFragment(position);
			}
		});

		EditText edtBusquedaComp = (EditText) NavDrawerLayout
				.findViewById(R.id.textoBusquedaComp);

		edtBusquedaComp.addTextChangedListener(filtroCompañias);
		setLista();
		// Cuando la aplicacion cargue por defecto mostrar la opcion Home
		MostrarFragment(1);

	}

	int positionVentana;

	/*
	 * Pasando la posicion de la opcion en el menu nos mostrara el Fragment
	 * correspondiente
	 */
	private void MostrarFragment(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		// Fragment
		// positionVentana=position;
		switch (position) {
		case 1:
			fragment = new HomeFragment();
			break;
		case 2:
			// ListView listaAutos = (ListView)
			// findViewById(R.id.listAutosPlaca);
			// ArrayAdapter<String> adapter = new
			// ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,
			// listNombComp);
			// listaAutos.setAdapter(adapter);
			fragment = new vehiculos();

			// cargarAutos();
			break;
		case 3:
			break;
		case 4:
			break;
		case 5:
			logOut();

			break;

		default:
			// si no esta la opcion mostrara un toast y nos mandara a Home
			Toast.makeText(getApplicationContext(),
					"Opcion " + titulos[position - 1] + "no disponible!",
					Toast.LENGTH_SHORT).show();
			fragment = new HomeFragment();
			position = 1;
			break;
		}
		// Validamos si el fragment no es nulo
		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, fragment).commit();

			// Actualizamos el contenido segun la opcion elegida
			NavList.setItemChecked(position, true);
			NavList.setSelection(position);
			// Cambiamos el titulo en donde decia "
			setTitle(titulos[position - 1]);
			// Cerramos el menu deslizable
			NavDrawerLayout.closeDrawer(NavList);
		} else {
			// Si el fragment es nulo mostramos un mensaje de error.
			Log.e("Error  ", "MostrarFragment" + position);
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (item.getItemId() == R.id.action_search) {
			NavDrawerLayout.openDrawer(NavDrawerLayout.findViewById(R.id.tab));
		}
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			Log.e("mDrawerToggle pushed", "x");

			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String text) {
		return false;

	}

	/*
	 * Obtener Nombre de Compañias del web service
	 */
	public void ObtenerCompanias(String datos, int tipo) {
		SharedPreferences prefe = getSharedPreferences("datosUsuario",
				Context.MODE_PRIVATE);
		String usuario = prefe.getString("usuario", "");
		String clave = prefe.getString("clave", "");

		if (datos == "") {
			setLista();
		} else {
			ArrayList<String> listComp;
			if (tipo == 0) {
				// Filtro po Placa
				setLista();
				listComp = filtroCompania(usuario, clave, datos, "");
				CargarCompañias(listComp, 0);

			} else if (tipo == 1) {
				// Filtro po Descripcion
				setLista();
				listComp = filtroCompania(usuario, clave, "", datos);
				CargarCompañias(listComp, 1);
			}
		}
	}

	public ArrayList<String> filtroCompania(String user, String pass,
			String placa, String compa) {

		ArrayList<String> listComp = new ArrayList<String>();
		listNombComp = new ArrayList<String>();

		WebService ncqtrack = new WebService();

		String resultado = ncqtrack.cargarCompaniasFiltro(user, pass, placa,
				compa); // connection result

		if (resultado.equals("No hubo conexi—n")) {
			Toast.makeText(getApplicationContext(), "No hubo conexión",
					Toast.LENGTH_LONG).show(); // Connection failed
			return listComp;
		} else {

			boolean validate = false;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(validate);
			dbf.setNamespaceAware(true);
			dbf.setIgnoringElementContentWhitespace(true);
			Document document = null;

			try {

				// delete garbage in the web service response like "[" or "]"
				if ((resultado.substring(0, 1)).equals("[")
						&& resultado.substring(resultado.length() - 1,
								resultado.length()).equals("]")) {
					if (resultado.substring(resultado.length() - 12,
							resultado.length()).equals(", anyType{}]")) {
						resultado = resultado.substring(1,
								resultado.length() - 12);
					} else {
						resultado = resultado.substring(1,
								resultado.length() - 1);
					}
				}

				DocumentBuilder builder = dbf.newDocumentBuilder();
				document = builder.parse(new InputSource(new StringReader(
						resultado)));// leer XML
				NodeList nodeList = document.getElementsByTagName("Respuesta");

				String respuesta = nodeList.item(0).getTextContent();
				if (respuesta.compareTo("N") == 0) {
					nodeList = document.getElementsByTagName("Companias");
					for (int i = 0; i < nodeList.getLength(); i++) {
						Element nodo = (Element) nodeList.item(i);
						NodeList nodeList2 = nodo
								.getElementsByTagName("NOMBRE_CLIENTE");
						String datos = nodeList2.item(0).getTextContent();
						listComp.add(datos);
						nodeList2 = nodo.getElementsByTagName("COMPANIA");
						datos = nodeList2.item(0).getTextContent();
						listNombComp.add(datos);
					}

					return listComp;

				} else if (respuesta.compareTo("S") == 0) {
					nodeList = document.getElementsByTagName("Detalle");
					Toast.makeText(getApplicationContext(),
							nodeList.item(0).getTextContent(),
							Toast.LENGTH_LONG).show();
					return listComp;
				}

			} catch (Exception e) {
				// Toast.makeText(getApplicationContext(), e.toString(),
				// Toast.LENGTH_LONG).show();
				return listComp;

			}

		}
		return listComp;
	}

	public void logOut() {
		Context contexto = this.getApplicationContext();
		SharedPreferences sp = contexto.getSharedPreferences("datosUsuario",
				Context.MODE_PRIVATE);
		sp.edit().clear().commit();
		Intent i = new Intent(this, Login.class);// Creamos un nuevo intent para
													// llamar a la siguiente
													// actividad
		startActivity(i);// Ejecutamos la actividad para que muestre la segunda
							// actividad

	}

	@Override
	public boolean onQueryTextChange(String newText) {
		return false;
	}

	/*
	 * Cargar Nombre de la compañia en listView para la selección
	 */

	InformacionComania informacionC = null;

	public void setLista() {
		ArrayList<String> companias = new ArrayList<String>();
		ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, companias);
		ListView list = (ListView) NavDrawerLayout.findViewById(R.id.listCompPlaca);
		list.setAdapter(adaptador);
		ListView list2 = (ListView) NavDrawerLayout.findViewById(R.id.listCompDescrip);
		list2.setAdapter(adaptador);

	}

	public void CargarCompañias(ArrayList<String> compañias, int tipo) {
		ListView list;
		ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, compañias);
		if (tipo == 0) {
			list = (ListView) NavDrawerLayout.findViewById(R.id.listCompPlaca);
			list.setAdapter(adaptador);

			list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					setLista();

					SharedPreferences preferencias = getSharedPreferences(
							"datosCompania", Context.MODE_PRIVATE);
					Editor editor = preferencias.edit();
					editor.putString("codigo_compania", listNombComp.get(arg2));
					editor.commit();

					cargarInfoCompania(listNombComp.get(arg2));
					informacionC = new InformacionComania();

					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.content_frame, informacionC).commit();

				}
			});

		} else if (tipo == 1) {
			list = (ListView) NavDrawerLayout
					.findViewById(R.id.listCompDescrip);
			list.setAdapter(adaptador);

			list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {

					SharedPreferences preferencias = getSharedPreferences(
							"datosCompania", Context.MODE_PRIVATE);
					Editor editor = preferencias.edit();
					editor.putString("codigo_compania", listNombComp.get(arg2));
					editor.commit();

					cargarInfoCompania(listNombComp.get(arg2));
					setLista();
					informacionC = new InformacionComania();

					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.content_frame, informacionC).commit();
				}
			});

		}

	}

	public void cargarInfoCompania(String compa) {

		SharedPreferences prefe = getSharedPreferences("datosUsuario",
				Context.MODE_PRIVATE);
		String usuario = prefe.getString("usuario", "");
		String clave = prefe.getString("clave", "");

		Boolean flag = cargarCompaniasInformacion2(usuario, clave, compa);
		if (flag == true) {
		}
	}

	private boolean cargarCompaniasInformacion2(String user, String pass,
			String comp) {
		WebService ncqtrack = new WebService();

		String resultado = ncqtrack
				.cargarCompaniasInformacion(user, pass, comp); // connection
																// result

		if (resultado.equals("No hubo conexi—n")) {
			Toast.makeText(getApplicationContext(), "No hubo conexión",
					Toast.LENGTH_LONG).show(); // Connection failed
			return false;
		} else {

			boolean validate = false;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(validate);
			dbf.setNamespaceAware(true);
			dbf.setIgnoringElementContentWhitespace(true);
			Document document = null;
			try {

				// delete garbage in the web service response like "[" or "]"
				if ((resultado.substring(0, 1)).equals("[")
						&& resultado.substring(resultado.length() - 1,
								resultado.length()).equals("]")) {
					if (resultado.substring(resultado.length() - 12,
							resultado.length()).equals(", anyType{}]")) {
						resultado = resultado.substring(1,
								resultado.length() - 12);
					} else {
						resultado = resultado.substring(1,
								resultado.length() - 1);
					}
				}

				DocumentBuilder builder = dbf.newDocumentBuilder();
				document = builder.parse(new InputSource(new StringReader(
						resultado)));// leer XML
				NodeList nodeList = document.getElementsByTagName("Respuesta");

				Toast.makeText(getApplicationContext(), resultado,
						Toast.LENGTH_LONG).show();
				String respuesta = nodeList.item(0).getTextContent();
				if (respuesta.compareTo("N") == 0) {

					return true;

				} else if (respuesta.compareTo("S") == 0) {
					nodeList = document.getElementsByTagName("Detalle");
					Toast.makeText(getApplicationContext(),
							nodeList.item(0).getTextContent(),
							Toast.LENGTH_LONG).show();
					return false;
				}

			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), e.toString(),
						Toast.LENGTH_LONG).show();
				return false;

			}

		}
		return false;
	}

	

	TextWatcher filtroCompañias = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			EditText texto = (EditText) NavDrawerLayout
					.findViewById(R.id.textoBusquedaComp);
			TabHost tabs = (TabHost) findViewById(android.R.id.tabhost);
			
				ObtenerCompanias(texto.getText().toString(),
						tabs.getCurrentTab());
			

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}
	};

	OnTabChangeListener ontabCompchange = new OnTabChangeListener() {

		@Override
		public void onTabChanged(String tabId) {
			EditText texto = (EditText) NavDrawerLayout
					.findViewById(R.id.textoBusquedaComp);
			TabHost tabs = (TabHost) findViewById(android.R.id.tabhost);
			ObtenerCompanias(texto.getText().toString(), tabs.getCurrentTab());

		}
	};

}
