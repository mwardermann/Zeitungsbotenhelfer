package lawa.dataModel;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class Address implements Diffable<Address>, Comparable<Address> {
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

    public boolean hasLocation(){
        return latitude != null;
    }

    @Override
    public int compareTo(Address o) {
        int result = stadt.compareTo(o.stadt);

        if (result == 0){
            result = strasse.compareTo(o.strasse);
        }

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

    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == this.getClass() && this.compareTo((Address)obj) == 0;
    }

    @Override
    public int hashCode() {
        return (stadt == null ? 0 :stadt.hashCode()) ^
                (strasse == null ? 0:strasse.hashCode() * 31) ^
                (hausnr == null ? 0: hausnr) ^ (bis == null ? 0: bis) * 113 ^ (zusatz == null ? 0 : zusatz.hashCode());
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

    @Override
    public String getId() {
        return String.join("@", stadt, strasse, hausnr == null ? "" : hausnr.toString(), bis == null ? "" : bis.toString(), zusatz);
    }

    @Override
    public List<String> getChangedFields(Diffable<?> other) {
        return new ArrayList<>();
    }

    @Override
    public ArrayList<? extends Diffable<?>> getChildren() {
        return this.auftraege;
    }
}
