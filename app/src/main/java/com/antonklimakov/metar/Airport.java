package com.antonklimakov.metar;

public class Airport {

    private String icao;

    public Airport() {
        super();
    }

    public Airport(String icao) {
        this.icao = icao.toUpperCase();
    }

    public String getIcao() {
        return icao;
    }

    public void setIcao(String icao) {
        this.icao = icao;
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
                '}';
    }
}
