package piat.opendatasearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Miguel Amarís Martos 54022315F
 *
 */
public class ManejadorXML extends DefaultHandler implements ParserCatalogo {

	// Nombre de la categoría
	private String sNombreCategoria;

	private String sCodigoConcepto;

	// Lista con los uris de los elementos <concept> que pertenecen a la categoría
	private List<String> lConcepts;

	// Mapa con información de los dataset que pertenecen a la categoría
	private Map<String, HashMap<String, String>> hDatasets;

	private HashMap<String, String> hm;
	private String tempConceptUri;
	private String titleValue;
	private String descriptionValue;
	private String themeValue;
	private String tempDatasetUri;
	private String element;
	private boolean isConcept;
	private boolean categoryFound;
	private boolean isDataset;
	private int level;

	/**
	 * @param sCodigoConcepto código de la categoría a procesar
	 * @throws ParserConfigurationException
	 */
	public ManejadorXML(String sCodigoConcepto) throws SAXException, ParserConfigurationException {
		this.sCodigoConcepto = sCodigoConcepto;
		level = 0;
		this.lConcepts = new ArrayList<String>();
		this.hDatasets = new HashMap<String, HashMap<String, String>>();
		this.hm = new HashMap<String, String>();

	}

	// ===========================================================
	// Métodos a implementar de la interfaz ParserCatalogo
	// ===========================================================

	/**
	 * <code><b>getLabel</b></code>
	 * 
	 * @return Valor de la cadena del elemento <code>label</code> del
	 *         <code>concept</code> cuyo elemento <code><b>code</b></code> sea
	 *         <b>igual</b> al criterio a búsqueda. <br>
	 *         null si no se ha encontrado el concept pertinente o no se dispone de
	 *         esta información
	 */
	@Override
	public String getLabel() {
		return sNombreCategoria;
	}

	/**
	 * <code><b>getConcepts</b></code> Devuelve una lista con información de los
	 * <code><b>concepts</b></code> resultantes de la búsqueda. <br>
	 * Cada uno de los elementos de la lista contiene la <code><em>URI</em></code>
	 * del <code>concept</code>
	 * 
	 * <br>
	 * Se considerarán pertinentes el <code><b>concept</b></code> cuyo código sea
	 * igual al criterio de búsqueda y todos sus <code>concept</code> descendientes.
	 * 
	 * @return - List con la <em>URI</em> de los concepts pertinentes. <br>
	 *         - null si no hay concepts pertinentes.
	 * 
	 */
	@Override
	public List<String> getConcepts() {
		return lConcepts;
	}

	/**
	 * <code><b>getDatasets</b></code>
	 * 
	 * @return Mapa con información de los <code>dataset</code> resultantes de la
	 *         búsqueda. <br>
	 *         Si no se ha realizado ninguna búsqueda o no hay dataset pertinentes
	 *         devolverá el valor <code>null</code> <br>
	 *         Estructura de cada elemento del map: <br>
	 *         . <b>key</b>: valor del atributo ID del elemento
	 *         <code>dataset</code>con la cadena de la <code><em>URI</em></code>
	 *         <br>
	 *         . <b>value</b>: Mapa con la información a extraer del
	 *         <code>dataset</code>. Cada <code>key</code> tomará los valores
	 *         <em>title</em>, <em>description</em> o <em>theme</em>, y
	 *         <code>value</code> sus correspondientes valores.
	 * 
	 * @return - Map con información de los <code>dataset</code> resultantes de la
	 *         búsqueda. <br>
	 *         - null si no hay datasets pertinentes.
	 */
	@Override
	public Map<String, HashMap<String, String>> getDatasets() {
		return hDatasets;
	}

	// ===========================================================
	// Métodos a implementar de SAX DocumentHandler
	// ===========================================================

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);

		switch (qName) {
			case "concept":
				isConcept = true;
				tempConceptUri = attributes.getValue(0);
				if (level > 0) {
					lConcepts.add(tempConceptUri);
					level++;
				}
				if (isDataset) {
					if (lConcepts.contains(attributes.getValue(0))) {
						hm = new HashMap<String, String>();
						hm.put("title", titleValue);
						hm.put("description", descriptionValue);
						hm.put("theme", themeValue);
						hDatasets.put(tempDatasetUri, hm);
					}
				}
				break;

			case "dataset":
				isDataset = true;
				tempDatasetUri = attributes.getValue(0);
				break;

		}

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);

		switch (qName) {
			case "concept":
				if (level > 0) {
					level--;
					isConcept = level > 0 ? true : false;
				}
				break;

			case "code":
				if (isConcept && element.equals(sCodigoConcepto)) {
					lConcepts.add(tempConceptUri);
					categoryFound = true;
					level = 1;
				}
				break;

			case "label":
				if (categoryFound && level > 0) {
					categoryFound = false;
					sNombreCategoria = element;
				}
				break;

			case "title":
				titleValue = element;
				break;

			case "description":
				descriptionValue = element;
				break;

			case "theme":
				themeValue = element;
				break;

			case "concepts":
				categoryFound = false;
				break;

		}

		element = "";

	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		element = new String(ch, start, length);

	}

}
