package lawa.dataModel;

import java.net.URLEncoder;
import java.util.ArrayList;

public class Address implements Comparable<Address> {
    private final String stadt;
    private final String strasse;
    private final Integer hausnr;
    private final Integer bis;
    private final String zusatz;
    private ArrayList<Auftrag> auftraege = new ArrayList<>();
    private Double latitude;
    private Double length;

    public Address(String stadt, String strasse, Integer hausnr, Integer bis, String zusatz) {
        this.stadt = stadt;
        this.strasse = strasse;
        this.hausnr = hausnr;
        this.bis = bis;
        this.zusatz = zusatz;
    }

    Address(){
        this(null, null, 0, 0, null);
    }

    public boolean hasLocation(){
        return latitude != null;
    }

    @Override
    public int compareTo(Address o) {
        int result = strasse.compareTo(o.strasse);

        if (result == 0)
        {
            result = hausnr.compareTo(o.hausnr);
        }

        if (result == 0 && this.bis != null)
        {
            result = bis.compareTo(o.bis);
        }

        if (result == 0 && this.zusatz != null){
            result = this.zusatz.compareTo(o.zusatz);
        }

        return result;
    }

    public void addAuftrag(Auftrag auftrag) {
        this.auftraege.add(auftrag);
    }

    public String getForPost(String delimiter) {
        return String.join("+", URLEncoder.encode(this.stadt), URLEncoder.encode(this.strasse), this.hausnr.toString());
    }

    public void setLocation(Double latitude, Double length) {

        this.latitude = latitude;
        this.length = length;
    }
}
