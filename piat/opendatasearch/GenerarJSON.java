package piat.opendatasearch;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import piat.opendatasearch.XPathProcess.Propiedad;

/**
 * @author Miguel Amarís Martos 54022315F
 *
 */
public class GenerarJSON {

    public static void genJSON(String path, List<Propiedad> xPathList) {

        try {

            // Creamos el objecto JSON principal y los dos Arrays que contiene 
            JsonObject json = new JsonObject();
            JsonArray infArray = new JsonArray();
            JsonArray titlesArray = new JsonArray();


            for (Propiedad p : xPathList) {

                // Query
                if (p.getNombre().equals("query")) {
                    json.addProperty(p.getNombre(), p.getValor());
                }

                // numDatasets
                else if (p.getNombre().equals("numDataset")) {
                    json.addProperty(p.getNombre(), p.getValor()); 
                }

                // Title ARRAY
                else if (p.getNombre().equals("title")) {
                    JsonObject titleObject = new JsonObject();
                    titleObject.addProperty(p.getNombre(), p.getValor());
                    titlesArray.add(titleObject);
                }

                // Inf ARRAY
                else if (p.getNombre().contains(".json")) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("id", p.getNombre());
                    jsonObject.addProperty("num", p.getValor());
                    infArray.add(jsonObject);

                }
            }

            // Añadimos los dos arrays al objecto JSON principal
            json.add("infDatasets", infArray);
            json.add("titles", titlesArray);


            // Creamos el GSON BUILDER para escribirlo en el fichero
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            // PrintWriter out = new PrintWriter(new FileWriter(path));
            // out.write(gson.toJson(json));
            // out.close();


            OutputStream os = new FileOutputStream(path.toString());
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));
            pw.write(gson.toJson(json));
            pw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
