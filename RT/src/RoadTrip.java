import java.util.*;

public class RoadTrip {
    private final List<City> cities;
    private final List<Road> roads;
    private boolean[] known;
    private int[] paths;
    private int[] costs;

    public RoadTrip(Graph graph) {
        this.cities = new ArrayList<City>(graph.getCities());
        this.roads = new ArrayList<Road>(graph.getRoads());
        initializeValues();

    }

    public List<City> route(City start, City end, List<String> attractions) {
        if(attractions.size() == 0) {
            this.dijkstra(start);
            return path(cities.indexOf(end), true);
        }
        else if(attractions.size() == 1) {
            //orient from start
            this.dijkstra(start);

            Map<String,City> attractionsDictionary = Repository.shared.getAttractions();
            City attractionCity = attractionsDictionary.get(attractions.get(0)); //first and only element of the list

            List<City> path = path(cities.indexOf(attractionCity), true); //first leg of trip (start to attraction)

            //orient from attraction
            this.dijkstra(attractionCity);
            path.addAll(path(cities.indexOf(end), false)); //second leg of trip (attraction to end) omitting start to avoid duplicate

            return path;
        }
        else {
            /**
             * NOTE: The following code can be difficult to comprehend at times so I will make it easier by way of illustration.
             * I imagined the problem to be easy with 0 and 1 attraction routes, so those are very straightforward. With 2 or
             * more attractions I thought it would be better to break it up outside-in. Let me elaborate. If there is Start, End and
             * 3 attractions, it seems optimal to break the problem into smaller chunks through recursion. The chunks are as follows:
             * 1: Start to the attraction closest to the start
             * 2: The attraction closest to the end to the end
             * 3: Recursion through repeating the process with the two border attractions and whatever attractions remain
             *      through this recursion the attraction closest to start becomes the start in the new recursive layer
             *      and the attraction closest to the end becomes the end. The process then repeats itself.
             * With that being said, the code below is quite straightforward.
             */


            //to track the attractions left over for the recursion below
            List<String> remainingAttractions = new ArrayList<String>(attractions);

            //note: here we use a hashSet so that with all of the additions to the set below, there will be no duplicates
            List<City> path = new ArrayList<City>();

            //~~~~~~~first we find closest attraction to the start ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

            //orient from start
            this.dijkstra(start);

            //minFromStart will track the index of the closest attraction to the start
            int minFromStart = -1;
            String minAttractionFromStart = null;
            for(String attraction : attractions) {
                City city = Repository.shared.getAttractions().get(attraction);

                if(minFromStart == -1 || this.costs[cities.indexOf(city)] < this.costs[minFromStart]) {
                    minFromStart = this.cities.indexOf(city);
                    minAttractionFromStart = attraction;
                }
            }
            path.addAll(path(minFromStart, true)); //start to closest attraction
            remainingAttractions.remove(minAttractionFromStart); //remove attraction because when we recurse this will be the start

            //~~~~~~~then we find closest attraction to the end ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            RoadTrip finalLeg = new RoadTrip(Repository.shared.getGraph());
            finalLeg.dijkstra(end); //orient from end

            int minFromEnd = -1;
            String minAttractionFromEnd = null;
            for(String attraction : remainingAttractions) {
                City city = Repository.shared.getAttractions().get(attraction);

                if(minFromEnd == -1 || finalLeg.costs[cities.indexOf(city)] < finalLeg.costs[minFromEnd]) {
                    minFromEnd = cities.indexOf(city);
                    minAttractionFromEnd = attraction;
                }
            }
            remainingAttractions.remove(minAttractionFromEnd);

            //now we recurse and find the path between the two attractions
            RoadTrip middleLeg = new RoadTrip(Repository.shared.getGraph());
            List<City> pathBetweenAttractions = middleLeg.route(cities.get(minFromStart), cities.get(minFromEnd), remainingAttractions);
            path.addAll(pathBetweenAttractions);

            //and we add the path from closest to end to end
            List<City> pathToEnd = finalLeg.path(minFromEnd, true); //we will be reversing this as it is in the wrong direction so we need the start (because it is actually end)
            Collections.reverse(pathToEnd);
            path.addAll(pathToEnd);

            removeDuplicates(path);
            return path;
        }
    }

    public static void main(String[] args) {

        //guard for incorrect usage
        if(args.length != 2) {
            System.out.println("ERROR: INCORRECT USAGE\nEXPECTED: java RoadTrip PATH_TO_roads.csv PATH_TO_attractions.csv");
            System.exit(-1);
        }

        //load roads.csv
        Repository.shared.loadRoads(args[0]);
        //load attractions.csv
        Repository.shared.loadAttractions(args[1]);

        //create RoadTrip instance for user interaction
        RoadTrip roadTrip = new RoadTrip(Repository.shared.getGraph());

        Scanner sc = new Scanner(System.in);

        City start = null;
        do {
            System.out.print("Name of starting city (or EXIT to quit): ");
            start = new City(sc.nextLine());

            if(start.getName().toUpperCase().equals("EXIT")) {
                System.exit(1);
            }
            if(!roadTrip.cities.contains(start)) {
                System.out.println("Sorry, the city you gave was invalid. Try again...");
                start = null;
            }

        } while(start == null);

        City end = null;
        do {
            System.out.print("Name of ending city: ");
            end = new City(sc.nextLine());

            if(!roadTrip.cities.contains(end)) {
                System.out.println("Sorry, the city you gave was invalid. Try again...");
                end = null;
            }

        } while(end == null);

        List<String> attractions = new ArrayList<String>();
        String attraction = "";

        while(true) {
            System.out.print("List an attraction along the way (or ENOUGH to stop listing): ");
            attraction = sc.nextLine();

            if(attraction.toUpperCase().equals("ENOUGH")) { break; }
            if(!Repository.shared.getAttractions().containsKey(attraction)) {
                System.out.println("Sorry, the city you gave was invalid. Try again...");
                continue;
            }
            attractions.add(attraction);
        }

        roadTrip.printPath(roadTrip.route(start, end, attractions));

    }

    //sets all array values to default start state
    private void initializeValues() {
        int numCities = cities.size();
        this.known = new boolean[numCities];
        Arrays.fill(known, false);

        this.paths = new int[numCities];
        Arrays.fill(paths, -1);

        this.costs = new int[numCities];
        Arrays.fill(costs, Integer.MAX_VALUE);
    }

    private void dijkstra(City source) {
        initializeValues();
        int sourceIndex = cities.indexOf(source);
        costs[sourceIndex] = 0;

        while(!allKnown()) {
            int indexOfMin = indexOfLeastCostUnknownCity();
            known[indexOfMin] = true;
            for(int neighborIndex : indicesOfNeighbors(indexOfMin)) {
                if(costs[neighborIndex] > costs[indexOfMin] + distance(indexOfMin, neighborIndex)) {
                    //update distance
                    costs[neighborIndex] = costs[indexOfMin] + distance(indexOfMin, neighborIndex);
                    //update path
                    paths[neighborIndex] = indexOfMin;
                }
            }
        }
    }
    private boolean allKnown() {
        for(boolean bool : known)
            if(!bool) //if contains at least one false, not all are known
                return false;
        return true;
    }

    private int indexOfLeastCostUnknownCity() {
        int minIndex = -1;
        for(int i = 0; i < cities.size(); i++) {
            if(!known[i]) {
                if(minIndex == -1) {
                    minIndex = i; //if minIndex hasn't been instantiated, ensure it is instantiated on an unknown index first
                    continue;
                }
                if(costs[i] < costs[minIndex]) {
                    minIndex = i;
                }
            }
        }
        return minIndex;
    }

    private List<Integer> indicesOfNeighbors(int indexOfCity) {
        List<Integer> neighbors = new ArrayList<Integer>();
        for(Road road : this.roads) {
            //if road.source = given city and the destination of that road is unknown
            if(road.getSource().equals(cities.get(indexOfCity)) && !known[cities.indexOf(road.getDestination())]) {
                neighbors.add(cities.indexOf(road.getDestination()));
            }
            //if road.destination = given city and the source of that road is unknown
            else if(road.getDestination().equals(cities.get(indexOfCity)) && !known[cities.indexOf(road.getSource())]) {
                neighbors.add(cities.indexOf(road.getSource()));
            }
        }
        return neighbors;
    }

    private int distance(int sourceIndex, int destinationIndex) {
        City source = cities.get(sourceIndex);
        City destination = cities.get(destinationIndex);
        for(Road road : roads) {
            if(road.getSource().equals(source) && road.getDestination().equals(destination) || road.getSource().equals(destination) && road.getDestination().equals(source)) {
                return road.getMiles();
            }
        }
        throw new RuntimeException("FATAL ERROR");
    }

    private List<City> path(int destinationIndex, boolean includeStart) {
        List<City> path = new ArrayList<City>();
        int curr = destinationIndex;
        while(paths[curr] != -1) {
            path.add(cities.get(curr));
            curr = paths[curr];
        }
        if(includeStart) //Very useful adjustment for merging multiple paths
            path.add(cities.get(curr));
        Collections.reverse(path);
        return path;
    }
    public void printPath(List<City> path) {
        int totalCost = 0; //cost to get to the last city in path
        System.out.println("=====ROUTE=====");
        for(int i = 0; i < path.size() - 1; i++) {
            City city1 = path.get(i);
            City city2 = path.get(i + 1);
            System.out.println("* " + city1 + " -> " + city2);

            totalCost += this.distance(cities.indexOf(city1), cities.indexOf(city2));
        }

        System.out.println("Total cost: " + totalCost + " miles");
    }
    private void removeDuplicates(List<City> cities) {
        for(int i = 0; i < cities.size() - 1; i++) {
            if(cities.get(i) == cities.get(i + 1)) {
                cities.remove(i);
                i--; //tread water after deletion so no indices are skipped
            }
        }
    }
}
