package piat.opendatasearch;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.stream.JsonReader;

/* En esta clase se comportará como un hilo */

/**
 * @author Miguel Amarís Martos 54022315F
 *
 */
public class JSONDatasetParser implements Runnable {
	private String fichero;
	private List<String> lConcepts;
	private Map<String, List<Map<String, String>>> mDatasetConcepts;
	private String nombreHilo;
	private boolean finProcesar;

	public JSONDatasetParser(String fichero, List<String> lConcepts,
			Map<String, List<Map<String, String>>> mDatasetConcepts) {
		this.fichero = fichero;
		this.lConcepts = lConcepts;
		this.mDatasetConcepts = mDatasetConcepts;
	}

	@Override
	public void run() {

		// Aquí se almacenarán todos los graphs de un dataset cuyo objeto de nombre
		// @type se corresponda con uno de los valores pasados en el la lista lConcepts
		List<Map<String, String>> graphs = new ArrayList<Map<String, String>>();

		// Para detener el parser si se han agregado a la lista graphs 5 graph
		finProcesar = false;

		Thread.currentThread().setName("JSON " + fichero);
		nombreHilo = "[" + Thread.currentThread().getName() + "] ";
		System.out.println(nombreHilo + "Empezar a descargar de internet el JSON");
		try {
			InputStreamReader inputStream = new InputStreamReader(new URL(fichero).openStream(), "UTF-8");

			// Crear objeto JsonReader a partir de inputStream
			JsonReader jsonReader = new JsonReader(inputStream);

			// Consumir el primer "{" del fichero
			jsonReader.beginObject();

			// Procesar los elementos del fichero JSON, hasta el final de fichero o hasta
			// que finProcesar=true
			while (!finProcesar && jsonReader.hasNext()) {
				if (jsonReader.nextName().equals("@graph")) {

					// Si se encuentra el objeto @graph, invocar a procesar_graph()
					procesar_graph(jsonReader, graphs, lConcepts);

				} else {

					// Descartar el resto de objetos
					jsonReader.skipValue();
				}
			}

			// Si se ha llegado al fin del fichero, consumir el último "}" del fichero
			if (!jsonReader.hasNext()) {
				jsonReader.endObject();
			}

			// Cerrar el objeto JsonReader
			jsonReader.close();
			inputStream.close();
		} catch (FileNotFoundException e) {
			System.out.println(nombreHilo + "El fichero no existe. Ignorándolo");
		} catch (IOException e) {
			System.out.println(nombreHilo + "Hubo un problema al abrir el fichero. Ignorándolo" + e);
		}

		// Se añaden al Mapa de concepts de los Datasets
		mDatasetConcepts.put(fichero, graphs);

	}

	/*
	 * procesar_graph()
	 * Procesa el array @graph
	 * Devuelve true si ya se han añadido 5 objetos a la lista graphs
	 */
	private boolean procesar_graph(JsonReader jsonReader, List<Map<String, String>> graphs, List<String> lConcepts)
			throws IOException {
		this.finProcesar = false;

		// Consumir el primer "[" del array @graph
		jsonReader.beginArray();

		// Procesar todos los objetos del array, hasta el final de fichero o hasta que
		// finProcesar=true
		while (!finProcesar && jsonReader.hasNext()) {
			// Consumir el primer "{" del objeto
			jsonReader.beginObject();

			// Procesar un objeto del array invocando al método procesar_un_graph()
			procesar_un_graph(jsonReader, graphs, lConcepts);

			// Consumir el último "}" del objeto
			jsonReader.endObject(); // consumes "}"

			// Ver si se han añadido 5 graph a la lista, para en ese caso poner la variable
			// finProcesar a true
			if (graphs.size() >= 5)
				finProcesar = true;
		}

		// Si se ha llegado al fin del array, consumir el último "]" del array
		if (!jsonReader.hasNext())
			jsonReader.endArray(); // consumes "]"

		return finProcesar;
	}

	/*
	 * procesar_un_graph()
	 * Procesa un objeto del array @graph y lo añade a la lista graphs si en el
	 * objeto de nombre @type hay un valor que se corresponde con uno de la lista
	 * lConcepts
	 */

	private void procesar_un_graph(JsonReader jsonReader, List<Map<String, String>> graphs, List<String> lConcepts)
			throws IOException {
		String id = "";
		String type = "";
		String title = "";
		String link = "";
		String eventLocation = "";
		String area = "";
		String latitude = "0";
		String longitude = "0";
		String start = "";
		String end = "";
		String accesibility = "";
		String organizationName = "";
		String description = "";

		// Procesar todas las propiedades de un objeto del array @graph, guardándolas en
		// variables temporales
		while (jsonReader.hasNext()) {
			switch (jsonReader.nextName()) { // All caseNames must match the values of the JSON
				case "@id":
					id = jsonReader.nextString();
					break;
				case "@type":
					type = jsonReader.nextString();
					break;
				case "title":
					title = jsonReader.nextString();
					break;
				case "link":
					link = jsonReader.nextString();
					break;
				case "location":
					jsonReader.beginObject(); // consumes "{"
					if (jsonReader.nextName().equals("latitude")) {
						latitude = jsonReader.nextString();
					}
					if (jsonReader.nextName().equals("longitude")) {
						longitude = jsonReader.nextString();
					}
					jsonReader.endObject(); // consumes "}"
					break;
				case "address":
					jsonReader.beginObject(); 
					
					if (jsonReader.nextName().equals("district")) {
						jsonReader.skipValue();
					}
					if (jsonReader.nextName().equals("area")) {
						jsonReader.beginObject();
						if(jsonReader.nextName().equals("@id")){
							area = jsonReader.nextString();
						}
						while (jsonReader.hasNext()) {
							jsonReader.skipValue();
						}
						jsonReader.endObject();
					}
					jsonReader.endObject(); 
					break;
				case "dtstart":
					start = jsonReader.nextString();
					break;
				case "dtend":
					end = jsonReader.nextString();
					break;
				case "organization":
					jsonReader.beginObject();
					if (jsonReader.nextName().equals("organization-name")) {
						organizationName = jsonReader.nextString();
					}
					if (jsonReader.nextName().equals("accesibility")) {
						accesibility = jsonReader.nextString();
					}
					jsonReader.endObject();
					break;
				case "event-location":
					eventLocation = jsonReader.nextString();
					break;
				case "description":
					description = jsonReader.nextString();
					break;
				default:
					jsonReader.skipValue();
					break;
			}
		}

		// Una vez procesadas todas las propiedades, ver si la clave @type tiene un
		// valor igual a alguno de los concept de la lista lConcepts. Si es así guardar
		// en un mapa Map<String,String> todos los valores de las variables temporales
		// recogidas en el paso anterior y añadir este mapa al mapa graphs
		if (lConcepts.contains(type)) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("id", id);
			map.put("type", type);
			map.put("title", title);
			map.put("description", description);
			map.put("start", start);
			map.put("end", end);
			map.put("link", link);
			map.put("location", eventLocation);
			map.put("area", area);
			map.put("latitude", latitude);
			map.put("longitude", longitude);
			map.put("organization-name", organizationName);
			map.put("accesibility", accesibility);

			// add map to list of graphs
			graphs.add(map);
		}
	}
}
