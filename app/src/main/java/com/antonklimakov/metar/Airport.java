package com.antonklimakov.metar;

public class Airport {

    private String icao;

    private String description;

    public Airport() {
        super();
    }

    public Airport(String icao, String description) {
        this.icao = icao.toUpperCase();
        this.description = description;
    }

    public String getIcao() {
        return icao;
    }

    public void setIcao(String icao) {
        this.icao = icao;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Airport airport = (Airport) o;

        return icao.equals(airport.icao);
    }

    @Override
    public int hashCode() {
        return icao.hashCode();
    }

    @Override
    public String toString() {
        return "Airport{" +
                "icao='" + icao + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
