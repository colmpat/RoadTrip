import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

//repository for attractions.csv and roads.csv

public class Repository {
    //singleton to prevent multiple extremely costly calculations at global access
    public static Repository shared = new Repository();

    private Graph graph;
    private Map<String, City> attractions;

    public Graph getGraph() {
        return graph;
    }

    public Map<String, City> getAttractions() {
        return attractions;
    }

    //parse roads.csv and populate this.graph with the populated graph
    public void loadRoads(String path) {

        File roadsCSV = new File(path);

        try {
            Scanner sc = new Scanner(roadsCSV);

            //If we've made it here the file is valid
            List<City> cities = new ArrayList<City>();
            List<Road> roads = new ArrayList<Road>();

            while(sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] toks = line.split(",");
                //ROADS FILE PATTERN: city1,city2,miles,minutes
                City city1 = new City(toks[0]);
                City city2 = new City(toks[1]);
                int miles = Integer.parseInt(toks[2]);

                //if the city is new, add it to the list
                if(!cities.contains(city1)) {
                    cities.add(city1);
                }
                if(!cities.contains(city2)) {
                    cities.add(city2);
                }

                //now add to roads and although there should be no duplicates, check for duplicity just for safety
                Road road = new Road(city1, city2, miles);
                if(!roads.contains(road)) {
                    roads.add(road);
                }
            }

            //after while loop, build graph and populate this.graph
            this.graph = new Graph(cities, roads);

            //finally, we must close sc to prevent leaks
            sc.close();

        } catch (FileNotFoundException e) {
            System.out.println("ERROR: The path you provided to roads.csv is invalid. Double check the inputted path file then retry.\nExiting...");
            System.exit(-1);
        }

    }

    //parses attractions.csv and populate this.attractions
    public void loadAttractions(String path) {

        File attractionsCSV = new File(path);

        try {
            Scanner sc = new Scanner(attractionsCSV);

            //If we've made it here the file is valid
            Map<String, City> attractions = new HashMap<String, City>();

            while(sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] toks = line.split(",");

                //ATTRACTIONS FILE PATTERN: attraction,city
                String attraction = toks[0];
                City city = new City(toks[1]);

                //put key value pair to dictionary
                attractions.put(attraction, city);
            }

            //after while loop, set attractions dictionary
            this.attractions = new HashMap<String, City>(attractions);

            //finally, we must close sc to prevent leaks
            sc.close();

        } catch (FileNotFoundException e) {
            System.out.println("ERROR: The path you provided to attractions.csv is invalid. Double check the inputted path file then retry.\nExiting...");
            System.exit(-1);
        }

    }

}
