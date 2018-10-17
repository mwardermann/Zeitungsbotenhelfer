package lawa.dataModel;

import java.time.DayOfWeek;
import java.time.LocalDate;

import java.util.List;

public class Auftrag {
    private final Integer auftragsnr;
    private final Integer menge;
    private final LocalDate gueltigAb;
    private final LocalDate gueltigBis;
    private final List<DayOfWeek> wochentage;
    private final LocalDate unterbrechungVon;
    private final LocalDate unterbrechungBis;

    private final String druckerzeugnis;
    private final String vorname;
    private final String name;
    private final String kommentar;
    private Address address;


    public Auftrag(Integer auftragsnr, Integer menge, LocalDate gueltigAb, LocalDate gueltigBis, List<DayOfWeek> wochentage, LocalDate unterbrechungVon, LocalDate unterbrechungBis, String druckerzeugnis, String vorname, String name, String kommentar) {

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

    public Boolean isActive(LocalDate date) {
        Boolean result = false;
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

    public String getDruckerzeugnis() {
        return druckerzeugnis;
    }

    public void setAddress(Address address) {

        this.address = address;
    }
}
