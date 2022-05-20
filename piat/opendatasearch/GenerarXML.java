package piat.opendatasearch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Miguel Amarís Martos 54022315F
 *
 */
public class GenerarXML {
	private static final String conceptPattern = "\n\t\t\t<concept id=\"#ID#\"/>";
	private static final String datasetPattern = "\n\t\t\t<dataset id=\"#ID#\">\n\t\t\t\t<title>#TITLE#</title>"
			+ "\n\t\t\t\t<description>#DESCRIPTION#</description>\n\t\t\t\t<theme>#THEME#</theme>\n\t\t\t</dataset>";
	private static final String resourcePattern = "\n\t\t\t<resource id=\"#ID#\">\n\t\t\t\t<concept id=\"#CONCEPTID#\"/>\n\t\t\t\t<link>#LINK#</link>\n\t\t\t\t<title>#TITLE#</title>"
			+ "\n\t\t\t\t<location>\n\t\t\t\t\t\t<eventLocation>#EVENTLOCATION#</eventLocation>\n\t\t\t\t\t\t<area>#AREA#</area>\n\t\t\t\t\t\t<timetable>\n\t\t\t\t\t\t\t<start>#START#</start>\n\t\t\t\t\t\t\t<end>#END#</end></timetable>\n\t\t\t\t\t\t<georeference>#GEOREFERENCE#</georeference>\n\t\t\t\t</location>\n\t\t\t\t<organization>\n\t\t\t\t\t\t<accesibility>#ACCESIBILITY#</accesibility>\n\t\t\t\t\t\t<organizationName>#ORGANIZATIONNAME#</organizationName>\n\t\t\t\t</organization>\n\t\t\t\t<description>#DESCRIPTION#</description>\n\t\t\t</resource>";

	private static String genConcepts(List<String> lConcepts) {
		StringBuilder sbSalida = new StringBuilder();
		sbSalida.append("\n\t\t<concepts>");

		for (String unConcepto : lConcepts) {
			sbSalida.append(conceptPattern.replace("#ID#", unConcepto));
		}
		sbSalida.append("\n\t\t</concepts>");
		return sbSalida.toString();
	}

	private static String genDatasets(Map<String, HashMap<String, String>> hDatasets) {
		StringBuilder sbSalida = new StringBuilder();
		sbSalida.append("\n\t\t<datasets>");

		for (Map.Entry<String, HashMap<String, String>> entry : hDatasets.entrySet()) {
			String s = datasetPattern.replace("#ID#", entry.getKey());
			s = s.replace("#TITLE#", entry.getValue().get("title"));
			s = s.replace("#DESCRIPTION#", entry.getValue().get("description"));
			s = s.replace("#THEME#", entry.getValue().get("theme"));
			sbSalida.append(s);
		}
		sbSalida.append("\n\t\t</datasets>");
		return sbSalida.toString();
	}

	private static String genResources(Map<String, List<Map<String, String>>> mDatasetConcepts) {
		StringBuilder sbSalida = new StringBuilder();
		sbSalida.append("\n\t\t<resources>");
		for (Map.Entry<String, List<Map<String, String>>> entry : mDatasetConcepts.entrySet()) {
			for (Map<String, String> map : entry.getValue()) {
				String s = resourcePattern.replace("#ID#", entry.getKey());
				s = s.replace("#CONCEPTID#", map.get("type"));
				s = s.replace("#LINK#", "<![CDATA[" + map.get("link") + "]]>");
				s = s.replace("#TITLE#", map.get("title"));
				s = s.replace("#EVENTLOCATION#", map.get("location"));
				s = s.replace("#AREA#", map.get("area"));
				s = s.replace("#START#", map.get("start"));
				s = s.replace("#END#", map.get("end"));
				s = s.replace("#GEOREFERENCE#", map.get("latitude") + " " + map.get("longitude"));
				s = s.replace("#ACCESIBILITY#", map.get("accesibility"));
				s = s.replace("#ORGANIZATIONNAME#", map.get("organization-name"));
				s = s.replace("#DESCRIPTION#", map.get("description"));
				sbSalida.append(s);
			}
		}
		sbSalida.append("\n\t\t</resources>");

		return sbSalida.toString();
	}

	public static void genXML(String query, int numConcepts, int numDatasets, List<String> lConcepts,
			Map<String, HashMap<String, String>> hDatasets, Map<String, List<Map<String, String>>> mDatasetConcepts,
			Path path) {
		StringBuilder sbSalida = new StringBuilder();
		sbSalida.append(
				"<?xml version = \"1.0\" encoding=\"UTF-8\"?>\n<searchResults xmlns=\"http://www.piat.dte.upm.es/practica4\""
						+ "\nxmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\nxsi:schemaLocation=\"http://www.piat.dte.upm.es/practica4 ./ResultadosBusquedaP4.xsd\">");
		sbSalida.append("\n\t<summary>" + "\n\t\t<query>" + query + "</query>" + "\n\t\t<numConcepts>" + numConcepts
				+ "</numConcepts>" + "\n\t\t<numDatasets>" + numDatasets + "</numDatasets>"
				+ "\n\t</summary>\n\t<results>");
		sbSalida.append(genConcepts(lConcepts));
		sbSalida.append(genDatasets(hDatasets));
		sbSalida.append(genResources(mDatasetConcepts));
		sbSalida.append("\n\t</results>\n</searchResults>");

		try {
			Files.write(path, sbSalida.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
