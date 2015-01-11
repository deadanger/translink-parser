import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;

/**
 * Created by Richard on 2014-12-28.
 */
public class ParserStopTimes {

	// Map<route_short_name, Set<stop_code>>
	private Map<String, Set<String>> routeStop = new HashMap<String, Set<String>>();

	private Map<String, Set<String>> routeTrip = new HashMap<String, Set<String>>();

	private Map<String, Set<String>> tripStop = new HashMap<String, Set<String>>();

	private Map<String, Set<String>> routeStopCode = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> routeNameStop = new HashMap<String, Set<String>>();

	// Start the sequence
	public ParserStopTimes() {
		parseTrips();
		parseStopTimes();
		replacement(routeTrip, tripStop);
		parseRoute();
		parseStopID();
		System.out.println("done parsing");
	}

	// Given route_id -- find trip_id -- in trips (alot)
	// Map<route_id, Set<trip_id>>
	private void parseTrips() {
		String[] column = new String[] { "route_id", "trip_id" };
		List<TripsTransit> beans;

		ColumnPositionMappingStrategy<TripsTransit> strategy = new ColumnPositionMappingStrategy<TripsTransit>();
		strategy.setType(TripsTransit.class);
		strategy.setColumnMapping(column);

		CsvToBean<TripsTransit> csvToBean = new CsvToBean<>();
		CSVReader csvReader;
		try {
			csvReader = new CSVReader(new FileReader(
					"D:\\JavaProj\\XMLPreProcessor\\src\\trips.csv"));
			beans = csvToBean.parse(strategy, csvReader);

			for (TripsTransit data : beans) {
				if (routeTrip.containsKey(data.getRoute_id())) {
					routeTrip.get(data.getRoute_id()).add(data.getTrip_id());
				} else {
					Set<String> info = new HashSet<String>();
					info.add(data.getTrip_id());
					routeTrip.put(data.getRoute_id(), info);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Given trip_id -- find stop_id -- in stop_times
	// Map<trip_id, Set<Stop_id>>
	private void parseStopTimes() {
		String[] column = new String[] { "trip_id", "stop_id" };
		List<StopTimesTransit> beans;

		ColumnPositionMappingStrategy<StopTimesTransit> strategy = new ColumnPositionMappingStrategy<StopTimesTransit>();
		strategy.setType(StopTimesTransit.class);
		strategy.setColumnMapping(column);

		CsvToBean<StopTimesTransit> csvToBean = new CsvToBean<>();

		CSVReader csvReader;
		try {
			csvReader = new CSVReader(new FileReader(
					"D:\\JavaProj\\XMLPreProcessor\\src\\stop_times.csv"));
			beans = csvToBean.parse(strategy, csvReader);
			for (StopTimesTransit data : beans) {
				if (tripStop.containsKey(data.getTrip_id())) {
					tripStop.get(data.getTrip_id()).add(data.getStop_id());
				} else {
					Set<String> info = new HashSet<String>();
					info.add(data.getStop_id());
					tripStop.put(data.getTrip_id(), info);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// Map<route_id, Set<trip_id>> Map<trip_id, Set<Stop_id>>
	// --> Map<route_id, Set<stop_id>>
	// Replace key and values
	private void replacement(Map<String, Set<String>> routeToTrip,
			Map<String, Set<String>> tripToStop) {

		for (String routeID : routeToTrip.keySet()) {
			Set<String> tripSet = routeToTrip.get(routeID);
			for (String tripID : tripSet) {
				Set<String> stopSet = tripToStop.get(tripID);
				routeStop.put(routeID, stopSet);
			}
		}
	}

	// Given route_short_name -- find route_id -- in routes (single)
	// Map<route_id, Set<stop_id>> -> Map<route_short_name, Set<Stop_id>>
	private void parseRoute() {

		String[] column = new String[] { "route_id", "route_short_name" };
		List<RoutesTransit> beans;

		ColumnPositionMappingStrategy<RoutesTransit> strategy = new ColumnPositionMappingStrategy<RoutesTransit>();
		strategy.setType(RoutesTransit.class);
		strategy.setColumnMapping(column);

		CsvToBean<RoutesTransit> csvToBean = new CsvToBean<>();

		CSVReader csvReader;
		try {
			csvReader = new CSVReader(new FileReader(
					"D:\\JavaProj\\XMLPreProcessor\\src\\routes.csv"));
			beans = csvToBean.parse(strategy, csvReader);

			for (RoutesTransit data : beans) {
				String short_name = data.getRoute_short_name();
				Set<String> info = routeStop.get(data.getRoute_id());
				routeNameStop.put(short_name, info);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("parseRoute: " + routeNameStop.size());
	}

	// Given stop_id -- find stop_code -- in stops
	// Map<route_short_name, Set<stop_id>> ->
	// Map<route_short_name, Set<stop_code>>
	private void parseStopID() {
		String[] column = new String[] { "stop_id", "stop_code" };

		System.out.println("start parseStopID: " + routeNameStop.size());
		List<StopsTransit> beans;

		ColumnPositionMappingStrategy<StopsTransit> strategy = new ColumnPositionMappingStrategy<StopsTransit>();
		strategy.setType(StopsTransit.class);
		strategy.setColumnMapping(column);

		CsvToBean<StopsTransit> csvToBean = new CsvToBean<>();

		try {
			CSVReader csvReader = new CSVReader(new FileReader(
					"D:\\JavaProj\\XMLPreProcessor\\src\\stops.csv"));
			beans = csvToBean.parse(strategy, csvReader);

			Set<String> keys = routeNameStop.keySet();
			System.out.println("1 in parseStopID: " + keys.size());
			for (String key : keys) {
				Set<String> stopIDs = routeNameStop.get(key);
				if(stopIDs == null){
				System.out.println("no stop found for route: " + key);
				continue;
				}
				for (String original : stopIDs) {
					for (StopsTransit data : beans) {
						if(data.getStop_id().equalsIgnoreCase(original)){
							if (routeStopCode.containsKey(key)) {
								routeStopCode.get(key).add(data.getStop_code());
							} else {
								Set<String> info = new HashSet<String>();
								info.add(data.getStop_code());
								routeStopCode.put(key, info);
							}
						}
					}
				}

			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("parseStopID: " + routeStopCode.size());
	}

	public Map<String, Set<String>> getRouteNameStop() {
		return routeStopCode;
	}



}
