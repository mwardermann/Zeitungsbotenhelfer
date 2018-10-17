package lawa.parser;

public class AddressInfo {
    private Header header;
    String teilaboVon;
    String teilaboBis;
    String vorname;
    String strasse;
    String hausnr;
    String kommentar;
    String name;
    String stadt;
    String auftragsnr;
    String art;
    String pa;
    String druckerzeugnis;
    String pva;
    String gueltigAb;
    String gueltigBis;
    String periode;
    String menge;
    String auftragsmenge;
    String unterbrechungVon;
    String unterbrechungBis;

    AddressInfo(Header header){
        this.header = header;
    }

    String getBezirk()
    {
        return this.header.bezirk;
    }

    public static String getHeader(){
        Header header = new Header();
        header.bezirk = "Bezirk";
        header.selektionsdatum = "Selektionsdatum";
        header.seite = "Seite";
        AddressInfo headerContents = new AddressInfo(header);
        headerContents.strasse = "Strasse";
        headerContents.hausnr = "Hausnr";
        headerContents.kommentar = "kommentar";
        headerContents.name = "name";
        headerContents.stadt = "stadt";
        headerContents.auftragsnr = "auftragsnr";
        headerContents.art = "art";
        headerContents.pa = "pa";
        headerContents.druckerzeugnis = "druckerzeugnis";
        headerContents.pva = "pva";
        headerContents.gueltigAb = "gueltigAb";
        headerContents.gueltigBis = "gueltigBis";
        headerContents.periode = "periode";
        headerContents.menge = "menge";
        headerContents.auftragsmenge = "Auftragsmenge";
        headerContents.unterbrechungVon =  "unterbrechungVon";
        headerContents.unterbrechungBis = "unterbrechungBis";

        return headerContents.toString();
    }

    @Override
    public String toString() {
        return String.join("\t", this.header.bezirk, this.header.selektionsdatum, this.header.seite, auftragsnr, name, strasse, hausnr, stadt, druckerzeugnis, gueltigAb, gueltigBis, unterbrechungVon, unterbrechungBis, periode, menge, auftragsmenge, art, pa, pva, kommentar);
    }
}
