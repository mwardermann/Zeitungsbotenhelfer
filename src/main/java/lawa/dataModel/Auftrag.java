package lawa.dataModel;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Auftrag implements Diffable<Auftrag> {
    private final Integer auftragsnr;
    private final Integer menge;
    private final LocalDate gueltigAb;
    private final LocalDate gueltigBis;
    private final HashSet<DayOfWeek> wochentage;
    private final LocalDate unterbrechungVon;
    private final LocalDate unterbrechungBis;

    private final String druckerzeugnis;
    private final String vorname;
    private final String name;
    private final String kommentar;

    public Auftrag(Integer auftragsnr, Integer menge, LocalDate gueltigAb, LocalDate gueltigBis, HashSet<DayOfWeek> wochentage, LocalDate unterbrechungVon, LocalDate unterbrechungBis, String druckerzeugnis, String vorname, String name, String kommentar) {
        this.auftragsnr = auftragsnr;
        this.menge = menge;
        this.gueltigAb = gueltigAb;
        this.gueltigBis = gueltigBis;
        this.wochentage = wochentage;
        this.unterbrechungVon = unterbrechungVon;
        this.unterbrechungBis = unterbrechungBis;
        this.druckerzeugnis = druckerzeugnis;
        this.vorname = vorname;
        this.name = name;
        this.kommentar = kommentar;
    }

    public boolean isActive(LocalDate date) {
        boolean result = false;
        if (!date.isBefore(gueltigAb) && !date.isAfter(gueltigBis)) {
            result = true;
        }

        if (result && unterbrechungVon != null && unterbrechungBis != null) {
            result = !(!date.isBefore(unterbrechungVon) && !date.isBefore(unterbrechungBis));
        }

        if (result)
        {
            result = this.wochentage.contains(date.getDayOfWeek());
        }

        return result;
    }

    @Override
    public String getId() {
        return this.auftragsnr.toString();
    }

    @Override
    public List<String> getChangedFields(Diffable<?> o) {
        Auftrag other = (Auftrag)o;

        ArrayList<String> result =new ArrayList<>();

        if (!this.menge.equals(other.menge)){
            result.add("menge");
        }

        if (!this.gueltigAb.equals(other.gueltigAb)){
            result.add("gueltigAb");
        }

        if (!this.gueltigBis.equals(other.gueltigBis)){
            result.add("gueltigBis");
        }

        if (!this.wochentage.containsAll(other.wochentage) || !other.wochentage.containsAll(this.wochentage))
        {
            result.add("wochentage");
        }

        if ((this.unterbrechungVon == null) != (other.unterbrechungVon == null) ||
                other.unterbrechungVon != null &&
                !this.unterbrechungVon.equals(other.unterbrechungVon))
        {
            result.add("unterbrechungVon");
        }

        if ((this.unterbrechungBis == null) != (other.unterbrechungBis == null) ||
                other.unterbrechungBis != null &&
                        !this.unterbrechungBis.equals(other.unterbrechungBis))
        {
            result.add("unterbrechungBis");
        }

        if (!this.druckerzeugnis.equals(other.druckerzeugnis)){
            result.add("druckerzeugnis");
        }

        if ((this.vorname == null) != (other.vorname == null) ||
                other.vorname != null &&
                        !this.vorname.equals(other.vorname))
        {
            result.add("vorname");
        }

        if (!this.name.equals(other.name)){
            result.add("name");
        }

        if ((this.kommentar == null) != (other.kommentar == null) ||
                other.kommentar != null &&
                        !this.kommentar.trim().equals(other.kommentar.trim()))
        {
            result.add("kommentar");
        }

        return result;
    }

    @Override
    public ArrayList<? extends Diffable<?>> getChildren() {
        return new ArrayList<>();
    }

}
