package piat.opendatasearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * @author Miguel Amarís Martos 54022315F
 *
 */
public class XPathProcess {

    /**
     * Método que se encarga de evaluar las expresiones xpath sobre el fichero XML
     * generado en la práctica 4
     * 
     * @return
     *         - Una lista con la propiedad resultante de evaluar cada expresion
     *         xpath
     * @throws IOException
     * 
     * @throws ParserConfigurationException
     */

    public static List<Propiedad> evaluar(String ficheroXML) throws IOException, XPathExpressionException {

        List<Propiedad> xPathList = new ArrayList<Propiedad>();
        try {
            // Get DOM
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document xml = db.parse(ficheroXML);

            // Get XPath
            XPathFactory xpf = XPathFactory.newInstance();
            XPath xpath = xpf.newXPath();

            // Contenido textual del elemento <query>
            String queryValue = (String) xpath.evaluate("//query", xml, XPathConstants.STRING);
            xPathList.add(new Propiedad("query", queryValue));

            // Número de elementos <dataset> hijos de <datasets>
            String numDatasetsValue = (String) xpath.evaluate("count(//datasets/dataset)", xml, XPathConstants.STRING);
            xPathList.add(new Propiedad("numDataset", numDatasetsValue));

            // Número de elementos <resource> cuyo atributo id es igual a cada uno de los
            // atributos id de los elementos <dataset>

            // Obtener atributo @id de los elementos <dataset>
            List<String> idList = new ArrayList<String>();

            NodeList idNodes = (NodeList) xpath.evaluate("//dataset/@id", xml, XPathConstants.NODESET);
            for (int i = 0; i < idNodes.getLength(); i++) {
                idList.add((String) idNodes.item(i).getNodeValue());
            }

            for (String id : idList) {
                String counter = (String) xpath.evaluate("count(//resources/resource[@id='" + id + "'])", xml,
                        XPathConstants.STRING);
                xPathList.add(new Propiedad(id, counter));
            }

            // Contenido de cada uno de los elemento <title>, hijos de <resource>, presentes
            // en el documento
            NodeList titleNodes = (NodeList) xpath.evaluate("//resource/title/node()", xml, XPathConstants.NODESET);
            for (int i = 0; i < titleNodes.getLength(); i++) {
                xPathList.add(new Propiedad("title", titleNodes.item(i).getNodeValue()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return xPathList;
    }

    /**
     * Esta clase interna define una propiedad equivalente a "nombre":"valor" en
     * JSON
     */
    public static class Propiedad {
        public final String nombre;
        public final String valor;

        public Propiedad(String nombre, String valor) {
            this.nombre = nombre;
            this.valor = valor;
        }

        public String getNombre() {
            return nombre;
        }

        public String getValor() {
            return valor;
        }

        @Override
        public String toString() {
            return this.nombre + ": " + this.valor;

        }

    } // Fin de la clase interna Propiedad

} // Fin de la clase XPathProcess
