package lawa.dataModel;

import org.jetbrains.annotations.Contract;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Bezirk implements Diffable<Bezirk>, Comparable<Bezirk> {
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

        if (result == 0) {
            result = this.datum.compareTo(o.datum);
        }

        return result;
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == this.getClass() && this.compareTo((Bezirk)obj) == 0;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode() ^ this.datum.hashCode() * 31;
    }

    @Override
    public String getId() {
        return this.name;
    }

    @Override
    public List<String> getChangedFields(Diffable<?> o) {
        Bezirk other = (Bezirk)o;

        ArrayList<String> result = new ArrayList<>();

        if (!this.summe.equals(other.summe)) {
            result.add("summe");
        }

        return result;
    }

    @Override
    public ArrayList<? extends Diffable<?>> getChildren() {
        return addresses;
    }
}
