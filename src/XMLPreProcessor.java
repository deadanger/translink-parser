import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.opencsv.CSVWriter;

public class XMLPreProcessor {

	Map<String, Set<String>> routeToStops = new HashMap<String, Set<String>>();

	String[] routes;

	public static void main(String[] args) {
		new XMLPreProcessor();
	}

	public XMLPreProcessor() {
		init();
	}

	public void init() {
		try {
			routes = readLines("D:\\JavaProj\\XMLPreProcessor\\src\\routesOptions.csv");
			getStops();
			writer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String[] readLines(String filename) throws IOException {
		FileReader fileReader = new FileReader(filename);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		List<String> lines = new ArrayList<String>();
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			lines.add(line);
		}
		bufferedReader.close();
		return lines.toArray(new String[lines.size()]);
	}

	public void getStops() {
		ParserStopTimes parser = new ParserStopTimes();
		routeToStops = parser.getRouteNameStop();
	}

	public void writer() throws IOException {
		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			/*
			 * // root elements Document doc = docBuilder.newDocument(); Element
			 * BusStops = doc.createElement("BusStops");
			 * doc.appendChild(BusStops);
			 * 
			 * for (String routeKey : routeToStops.keySet()) {
			 * 
			 * // Route elements Element route = doc.createElement("Route");
			 * BusStops.appendChild(route);
			 * 
			 * // RouteNo elements Element routeNo =
			 * doc.createElement("RouteNo");
			 * routeNo.appendChild(doc.createTextNode(routeKey));
			 * route.appendChild(routeNo);
			 * 
			 * for (String stopNumber : routeToStops.get(routeKey)) { // Stop
			 * elements Element stop = doc.createElement("Stop");
			 * stop.appendChild(doc.createTextNode(String.valueOf(stopNumber)));
			 * route.appendChild(stop); } }
			 */
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("string-array");
			doc.appendChild(rootElement);

			Attr attr = doc.createAttribute("name");
			attr.setValue("routeOptions");
			rootElement.setAttributeNode(attr);

			for (String option : routes) {
				if (routeToStops.get(option) != null &&
						!routeToStops.get(option).contains("")) {
					Element item = doc.createElement("item");
					item.appendChild(doc.createTextNode(option));
					rootElement.appendChild(item);
				}
			}
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(
					"C:\\Users\\chiang\\Desktop\\routeOption.txt"));

			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);

			transformer.transform(source, result);

			System.out.println("File saved!");

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}

}
