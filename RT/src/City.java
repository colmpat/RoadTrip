public class City {
    private final String name;

    public City(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean equals(City city) {
        return this == city || this.getName().equals(city.getName());
    }

}
