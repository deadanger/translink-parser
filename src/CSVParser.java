import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;


/**
 * Created by Richard on 2014-12-28.
 */
public class CSVParser {
	
    public Set<Integer> getStops() {
        return stops;
    }

    private Set<Integer> stops = new HashSet<Integer>();

    // Start the sequence
    public CSVParser(String routeNo){
        parseRoute(routeNo);
    }

    // Given route_short_name -- find route_id -- in routes (single)
    private void parseRoute(String routeNo){

        String[] column = new String[]{"route_id", "route_short_name"};
        Set<String> routeIDs = new HashSet<String>();


        List<RoutesTransit> beans;

        ColumnPositionMappingStrategy<RoutesTransit> strategy =
                new ColumnPositionMappingStrategy<RoutesTransit>();
        strategy.setType(RoutesTransit.class);
        strategy.setColumnMapping(column);

        CsvToBean<RoutesTransit> csvToBean = new CsvToBean<>();
        
        CSVReader csvReader;
		try {
			csvReader = new CSVReader(new FileReader("D:\\JavaProj\\XMLPreProcessor\\src\\routes.csv"));
	        beans = csvToBean.parse(strategy, csvReader);

            Iterator<RoutesTransit> itr = beans.iterator();
            while(itr.hasNext()) {
                RoutesTransit i = itr.next();
                if (!i.getRoute_short_name().equalsIgnoreCase(routeNo)) {
                    itr.remove();
                }
            }


            for(RoutesTransit data: beans){
                routeIDs.add(data.getRoute_id());
            }
            parseTrips(routeIDs);


		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


    }

    // Given route_id         -- find trip_id  -- in trips (alot)
    private void parseTrips(Set<String> routeID){
    	System.out.println("start trips parsing");
        String[] column = new String[]{"route_id","trip_id"};
        Set<String> tripID = new HashSet<String>();

        List<TripsTransit> beans;

        ColumnPositionMappingStrategy<TripsTransit> strategy =
                new ColumnPositionMappingStrategy<TripsTransit>();
        strategy.setType(TripsTransit.class);
        strategy.setColumnMapping(column);

        CsvToBean<TripsTransit> csvToBean = new CsvToBean<>();

        CSVReader csvReader;
		try {
			csvReader = new CSVReader(new FileReader("D:\\JavaProj\\XMLPreProcessor\\src\\trips.csv"));
			 beans = csvToBean.parse(strategy, csvReader);

	            Iterator<TripsTransit> itr = beans.iterator();
	            while(itr.hasNext()) {
	                TripsTransit i = itr.next();
	                String id = i.getRoute_id();
	                if (!routeID.contains(id)) {
	                    itr.remove();
	                }
	            }

	            for(TripsTransit data: beans){
	                tripID.add(data.getTrip_id());
	            }
	            parseStopTimes(tripID);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("end trips parsing");
    }

    // Given trip_id          -- find stop_id  -- in stop_times
    private void parseStopTimes(Set<String> tripID){
    	System.out.println("start stop_times parsing");
        String[] column = new String[]{"trip_id","stop_id"};
        Set<String> stopID = new HashSet<String>();

        List<StopTimesTransit> beans;

        ColumnPositionMappingStrategy<StopTimesTransit> strategy =
                new ColumnPositionMappingStrategy<StopTimesTransit>();
        strategy.setType(StopTimesTransit.class);
        strategy.setColumnMapping(column);

        CsvToBean<StopTimesTransit> csvToBean = new CsvToBean<>();

        CSVReader csvReader;
		try {
			csvReader = new CSVReader(new FileReader("D:\\JavaProj\\XMLPreProcessor\\src\\stop_times.csv"));
	        beans = csvToBean.parse(strategy, csvReader);

            Iterator<StopTimesTransit> itr = beans.iterator();
            while(itr.hasNext()) {
                StopTimesTransit i = itr.next();
                String id = i.getTrip_id();
                if (!tripID.contains(id)) {
                    itr.remove();
                }
            }

            for(StopTimesTransit data: beans){
                stopID.add(data.getStop_id());
            }

            parseStopID(stopID);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("end stop_times parsing");

    }

    // Given stop_id          -- find stop_code -- in stops
    private void parseStopID(Set<String> stopID){
    	System.out.println("start stop parsing");
        String[] column = new String[]{
                "stop_id","stop_code"
        };

        List<StopsTransit> beans;


         ColumnPositionMappingStrategy<StopsTransit> strategy =
                 new ColumnPositionMappingStrategy<StopsTransit>();
         strategy.setType(StopsTransit.class);
         strategy.setColumnMapping(column);

         CsvToBean<StopsTransit> csvToBean = new CsvToBean<>();

         try {
			CSVReader csvReader = new CSVReader(new FileReader(
					"D:\\JavaProj\\XMLPreProcessor\\src\\stops.csv"));
			 beans = csvToBean.parse(strategy, csvReader);

	            Iterator<StopsTransit> itr = beans.iterator();
	            while(itr.hasNext()) {
	                StopsTransit i = itr.next();
	                String id = i.getStop_id();
	                if (!stopID.contains(id)) {
	                    itr.remove();
	                }
	            }

	            for(StopsTransit data: beans){
	                stops.add(Integer.parseInt(data.getStop_code()));
	            }

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         System.out.println("end stop parsing");
        
    }




}
