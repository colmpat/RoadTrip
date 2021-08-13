public class Road {
    private final City source;
    private final City destination;
    private final int miles;

    public Road(City source, City destination, int miles) {
        this.source = source;
        this.destination = destination;
        this.miles = miles;
    }

    public City getSource() {
        return source;
    }

    public City getDestination() {
        return destination;
    }

    public int getMiles() {
        return miles;
    }

}
