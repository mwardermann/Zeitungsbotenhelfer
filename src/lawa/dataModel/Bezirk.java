package lawa.dataModel;

import java.time.LocalDate;
import java.util.ArrayList;

public class Bezirk implements Comparable<Bezirk>{
    private final String name;
    private final LocalDate datum;
    private final Integer summe;

    private ArrayList<Address> addresses = new ArrayList<>();

    public Bezirk(String name, LocalDate datum, Integer summe) {

        this.name = name;
        this.datum = datum;
        this.summe = summe;
    }

    public void addAddress(Address address) {
        this.addresses.add(address);
    }

    public ArrayList<Address> getAddresses() {
        return this.addresses;
    }

    @Override
    public int compareTo(Bezirk o) {
        int result = this.name.compareTo(o.name);

        if (result == 0)
        {
            result = this.datum.compareTo(o.datum);
        }

        return result;
    }
}
