package lawa.dataModel;

import java.util.ArrayList;

public class Address implements Comparable<Address> {
    private final String strasse;
    private final Integer hausnr;
    private final Integer bis;
    private final String zusatz;
    private Bezirk bezirk;
    private ArrayList<Auftrag> auftraege = new ArrayList<>();

    public Address(String strasse, Integer hausnr, Integer bis, String zusatz) {
        this.strasse = strasse;
        this.hausnr = hausnr;
        this.bis = bis;
        this.zusatz = zusatz;
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

    public void setBezirk(Bezirk bezirk) {

        this.bezirk = bezirk;
    }

    public void addAuftrag(Auftrag auftrag) {
        this.auftraege.add(auftrag);
    }
}
