import java.util.Map;

//repository for attractions.csv and roads.csv

public class Repository {
    //singleton to prevent multiple extremely costly calculations at global access
    public static Repository shared;

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
        //TODO
    }

    //parses attractions.csv and populate this.attractions
    public void loadAttractions(String path) {
        //TODO
    }

}
