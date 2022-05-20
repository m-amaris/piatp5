/* No modificar este fichero */


package piat.opendatasearch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Interfaz que debe implementar la clase ManejadorXML.
 *
 */

public interface ParserCatalogo {

	/**
	 * Devuelve el valor de la cadena del elemento <code>label</code> del <code>concept</code> cuyo
	 * elemento <code><b>code</b></code> sea <b>igual</b> al criterio a búsqueda.
	 * @return
	 *  - Valor de la cadena del elemento <code>label</code> del <code>concept</code> cuyo 
	 * elemento <code><b>code</b></code> sea <b>igual</b> al criterio a búsqueda.
	 * <br>
	 *  - null si no se ha encontrado el <code>concept</code> pertinente o no se dispone de esta información.
	 */
	public String getLabel();
	/**
	 *	Devuelve una lista con información de los <code><b>concepts</b></code> resultantes de la búsqueda. 
	 * <br> Cada uno de los elementos de la lista contiene el <code><em>URI</em></code> del <code>concept</code>
	 * 
	 *  <br>Se considerarán pertinentes el <code><b>concept</b></code> cuyo código (elemento <code>code</code>) coincida 
	 *  con el criterio de búsqueda y todos los <code>concepts</code> descendientes del mismo.
	 *  
	 * @return
	 * - Lista con el <em>URI</em> de los concepts pertinentes.
	 * <br>
	 * - null  si no hay <code>concepts</code> pertinentes.
	 * 
	 */
	public List<String> getConcepts();

	/**
	 * Devuelve un mapa con información de los <code>dataset</code> resultantes de la búsqueda.
	 * <br> Si no se ha realizado ninguna búsqueda o no hay dataset pertinentes devolverá el valor <code>null</code>
	 * <br> Estructura de cada elemento del mapa:
	 * <ul>
	 * 		<li><b>key</b>: valor del atributo ID del elemento <code>dataset</code>con la cadena del <code><em>URI</em></code>  
	 * 		<li><b>value</b>: Mapa con la información a extraer del <code>dataset</code>. Cada <code>key</code> tomará los valores <em>title</em>, <em>description</em> o <em>theme</em>, y <code>value</code> sus correspondientes valores.
	 * </ul>
   
	 * @return
	 *  - Mapa con información de los <code>dataset</code> resultantes de la búsqueda.
	 *  <br>
	 *  - null si no hay <code>datasets</code> pertinentes.  
	 */
	public Map<String, HashMap<String, String>> getDatasets();

}