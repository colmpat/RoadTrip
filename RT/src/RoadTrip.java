import java.util.*;

public class RoadTrip {
    private final List<City> cities;
    private final List<Road> roads;
    private Set<City> knownCities;
    private Set<City> unknownCities;
    private Map<City, City> predecessors;
    private Map<City, Integer> distances;

    public RoadTrip(Graph graph) {
        this.cities = new ArrayList<City>(graph.getCities());
        this.roads = new ArrayList<Road>(graph.getRoads());
    }

    public static void main(String[] args) {

        //guard for incorrect usage
        if(args.length != 2) {
            System.out.println("ERROR: INCORRECT USAGE\nEXPECTED: java RoadTrip PATH_TO_roads.csv PATH_TO_attractions.csv");
        }

        Repository.shared.loadRoads(args[0]);
        Repository.shared.loadAttractions(args[1]);

        RoadTrip roadTrip = new RoadTrip(Repository.shared.getGraph());
    }

    public LinkedList<City> route(City source, City destination, List<String> attractions) {
        //TODO
        return new LinkedList<City>();
    }

    public void dijkstra(City source) {
        this.knownCities = new HashSet<City>();
        this.unknownCities = new HashSet<City>();
        this.distances = new HashMap<City, Integer>();
        this.predecessors = new HashMap<City, City>();

        this.distances.put(source, 0);
        this.unknownCities.add(source);

        //for all unknown cities
        while(unknownCities.size() > 0) {
            //find smallest unknown city
            City city = getMinimum(unknownCities);

            //update its knownness
            knownCities.add(city);
            unknownCities.remove(city);

            //update this cities distances to neighbors
            findMinimumDistances(city);

        }
    }

    private LinkedList<City> getPath(City destination) {

        LinkedList<City> path = new LinkedList<City>();
        City city = destination;

        if(predecessors.get(city) == null) //if city has no path to source initialized in dijkstra()
            return null;
        path.add(city);
        while(predecessors.get(city) != null) {
            //iterate
            city = predecessors.get(city);
            //add to path
            path.add(city);
        }

        //reverse path because of filo linkedlist insertion
        Collections.reverse(path);

        return path;
    }

    private int cost(City destination) {
        //TODO
        return -1;
    }

    private int getDistance(City source, City destination) {
        //TODO
        return -1;
    }

    private List<City> getNeighbors(City city) {
        //TODO
        return new ArrayList<City>();
    }

    private City getMinimum(Set<City> cities) {
        //TODO
        return new City("");
    }

    private void findMinimumDistances(City city) {
        //find all neighbors
        List<City> neighbors = getNeighbors(city);

        for(City neighbor : neighbors) {
            if(cost(neighbor) > cost(city) + getDistance(city, neighbor)) {
                //update distance
                distances.put(city, cost(city) + getDistance(city, neighbor));
                //update path
                predecessors.put(neighbor, city);
                //add neighbor to unknownCities for further analysis
                unknownCities.add(neighbor);
            }
        }
    }


}
