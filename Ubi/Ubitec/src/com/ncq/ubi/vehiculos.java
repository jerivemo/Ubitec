package com.ncq.ubi;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.StrictMode;
import android.sax.RootElement;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.OnTabChangeListener;

public class vehiculos extends Fragment {

	ArrayList<String> listAutos = new ArrayList<String>();
	ArrayList<String> listAutosInfototal = new ArrayList<String>();
	ArrayList<String> listAutosDescripcion = new ArrayList<String>();
	ArrayList<String> listAutosPorPLca = new ArrayList<String>();
	ArrayList<String> listAutosDescripcionBus = new ArrayList<String>();
	public vehiculos() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		listAutosPorPLca = listAutos;
		listAutosDescripcionBus=listAutosDescripcion;
		View rootView = inflater.inflate(R.layout.vehiculos, container, false);

		Resources res = getResources();

		TabHost tabs = (TabHost) rootView.findViewById(R.id.tabhostAutos);
		tabs.setup();

		TabHost.TabSpec spec = tabs.newTabSpec("mitab1");
		spec.setContent(R.id.tab1AutosPlaca);
		spec.setIndicator("Placa",
				res.getDrawable(android.R.drawable.ic_dialog_map));
		tabs.addTab(spec);

		spec = tabs.newTabSpec("mitab2");
		spec.setContent(R.id.tab2AutosDescripcion);
		spec.setIndicator("Descripción",
				res.getDrawable(android.R.drawable.ic_dialog_map));
		tabs.addTab(spec);

		tabs.setCurrentTab(0);

		cargarAutos();
		ListView listaAutos2 = (ListView) rootView
				.findViewById(R.id.listAutosPlaca);
		ListView listaAutos3 = (ListView) rootView
				.findViewById(R.id.listAutosDescrip);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, listAutosInfototal);
		listaAutos2.setAdapter(adapter);
		listaAutos3.setAdapter(adapter);

		EditText edtBusquedaAutos = (EditText) rootView
				.findViewById(R.id.textoBusquedaAutos);

		edtBusquedaAutos.addTextChangedListener(filtroAutos);

		return rootView;

	}

	MainActivity main = new MainActivity();

	public ArrayList<String> cargarAutos() {

		SharedPreferences prefe = getActivity().getSharedPreferences(
				"datosUsuario", Context.MODE_PRIVATE);
		SharedPreferences prefecompania = getActivity().getSharedPreferences(
				"datosCompania", Context.MODE_PRIVATE);
		String usuario = prefe.getString("usuario", "");
		String clave = prefe.getString("clave", "");
		String compania = prefecompania.getString("codigo_compania", "");
		int num = 2;

		WebService ncqtrack = new WebService();

		String resultado = ncqtrack.cargarCombos(num, compania, usuario, clave); // connection
		// result

		if (resultado.equals("No hubo conexi—n")) {
			Toast.makeText(getActivity(), "No hubo conexión", Toast.LENGTH_LONG)
					.show(); // Connection failed
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
					nodeList = document.getElementsByTagName("Vehiculos");
					for (int i = 0; i < nodeList.getLength(); i++) {
						Element nodo = (Element) nodeList.item(i);
						NodeList nodeList2 = nodo.getElementsByTagName("PLACA");
						NodeList nodeList3 = nodo
								.getElementsByTagName("DESCRIPCION");
						String datos = nodeList2.item(0).getTextContent();
						String datosDescripcion = nodeList3.item(0)
								.getTextContent();
						listAutosInfototal.add("Placa: " + datos +"  Descripcicón: "
						 + datosDescripcion);
						listAutos.add(datos);
						listAutosDescripcion.add(datosDescripcion);

						// Toast.makeText(getActivity().getApplicationContext(),
						// datos,
						// Toast.LENGTH_LONG).show();
						i++;

					}
					return listAutosInfototal;
				} else if (respuesta.compareTo("S") == 0) {
					nodeList = document.getElementsByTagName("Detalle");
					Toast.makeText(getActivity(),
							nodeList.item(0).getTextContent(),
							Toast.LENGTH_LONG).show();
				}

			} catch (Exception e) {
				// Toast.makeText(getApplicationContext(), e.toString(),
				// Toast.LENGTH_LONG).show();
			}

		}
		return listAutosInfototal;

	}

	public void CargarAutos(ArrayList<String> autos, int tipo) {
		ListView list;
		ArrayAdapter<String> adaptador = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_list_item_1, autos);
		if (tipo == 0) {
			list = (ListView) getView().findViewById(R.id.listAutosPlaca);
			list.setAdapter(adaptador);

			list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					setLista();
					Toast.makeText(getActivity().getApplicationContext(),
							"Hola placa", Toast.LENGTH_LONG).show();
				}
			});

		} else if (tipo == 1) {
			list = (ListView) getActivity().findViewById(R.id.listAutosDescrip);
			list.setAdapter(adaptador);

			list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					setLista();
					Toast.makeText(getActivity().getApplicationContext(),
							"Hola placa", Toast.LENGTH_LONG).show();
				}
			});

		}

	}

	/*
	 * Obtener Nombre de Compañias del web service
	 */
	public void ObtenerAutos(String datos, int tipo) {

		if (datos == "") {
			setLista();
		} else {
			ArrayList<String> listAutos2;
			if (tipo == 0) {
				// Filtro po Placa
				setLista();

				listAutos2 = buscarAutoPorPlaca(datos, listAutosPorPLca);
				CargarAutos(listAutos2, 0);
				;

			} else if (tipo == 1) {
				// Filtro po Descripcion
				setLista();

				listAutos2 = buscarAutoPordescrip(datos, listAutosDescripcionBus);
				CargarAutos(listAutos2, 1);
			}
		}
	}

	TextWatcher filtroAutos = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			EditText texto = (EditText) getActivity().findViewById(
					R.id.textoBusquedaAutos);

			TabHost tabs = (TabHost) getActivity().findViewById(
					R.id.tabhostAutos);
			// cargarAutos();
			ObtenerAutos(texto.getText().toString(), tabs.getCurrentTab());

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
			EditText texto = (EditText) getActivity().findViewById(
					R.id.textoBusquedaAutos);
			TabHost tabs = (TabHost) getActivity().findViewById(
					R.id.tabhostAutos);
			 ObtenerAutos(texto.getText().toString(), tabs.getCurrentTab());

		}
	};

	public void setLista() {
		listAutos = new ArrayList<String>();
		ArrayAdapter<String> adaptador = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_list_item_1, listAutos);
		ListView list = (ListView) getActivity().findViewById(
				R.id.listAutosPlaca);
		list.setAdapter(adaptador);
		ListView list2 = (ListView) getActivity().findViewById(
				R.id.listAutosDescrip);
		list2.setAdapter(adaptador);

	}

	// @SuppressWarnings("unused")
	public ArrayList<String> buscarAutoPorPlaca(String placa,
			ArrayList<String> autos) {
		ArrayList<String> autoEncont = new ArrayList<String>();
		int auto = autos.size();

		for (int i = 0; i < auto; i++) {

			if (autos.get(i).equals(placa)) {
				autoEncont.clear();
				autoEncont.add(listAutosInfototal.get(i));
				return autoEncont;
			}

			String posiAuto = autos.get(i);

			int y = 0;
			while (y < placa.length()) {
				if (posiAuto.charAt(y) == placa.charAt(y)) {
					if (autoEncont.contains(posiAuto)) {
					} else {
						autoEncont.add(posiAuto);
					}
					// return autoEncont;
				} else {
					autoEncont.clear();
				}

				y++;

			}

			// return autoEncont;

		}

		return autoEncont;
	}
	
	// @SuppressWarnings("unused")
		public ArrayList<String> buscarAutoPordescrip(String descrip,
				ArrayList<String> autos) {
			ArrayList<String> autoEncont = new ArrayList<String>();
			int auto = autos.size();

			for (int i = 0; i < auto; i++) {

				if (autos.get(i).equals(descrip)) {
					autoEncont.clear();
					autoEncont.add(listAutosInfototal.get(i));
					return autoEncont;
				}

				String posiAuto = autos.get(i);

				int y = 0;
				while (y < descrip.length()) {
					if (posiAuto.charAt(y) == descrip.charAt(y)) {
						if (autoEncont.contains(posiAuto)) {
						} else {
							autoEncont.add(posiAuto);
						}
						// return autoEncont;
					} else {
						autoEncont.clear();
					}

					y++;

				}

				// return autoEncont;

			}

			return autoEncont;
		}
		
}
