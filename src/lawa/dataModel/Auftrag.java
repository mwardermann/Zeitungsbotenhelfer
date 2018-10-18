package lawa.dataModel;

import java.time.DayOfWeek;
import java.time.LocalDate;

import java.util.HashSet;

public class Auftrag {
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
}
