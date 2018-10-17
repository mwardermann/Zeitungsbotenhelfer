package lawa.dataModel;

import java.awt.*;
import java.util.ArrayList;

public class Bezirk {
    private final String name;
    private ArrayList<Address> addresses = new ArrayList<>();

    public Bezirk(String name) {
        this.name = name;
    }

    public void addAddress(Address address) {
        this.addresses.add(address);
    }

    public ArrayList<Address> getAddresses() {
        return this.addresses;
    }
}
