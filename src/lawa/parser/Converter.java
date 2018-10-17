package lawa.parser;

import lawa.FormatException;
import lawa.dataModel.Address;
import lawa.dataModel.Auftrag;
import lawa.dataModel.Bezirk;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Converter {
    private static Pattern hausnrPattern = Pattern.compile("(?<Nummer>[1-9][0-9]*)(\\s*-\\s*(?<Bis>[1-9][0-9]*))?(\\s*(?<Zusatz>[^0-9]+))?");

    private static HashSet<DayOfWeek> convertPeriodToWeekdays(String periode) throws FormatException {
        HashSet<DayOfWeek> wochentage = new HashSet<>();

        for (int pos = 0; pos < periode.length() - 1; pos += 2) {
            String wochentag = periode.substring(pos, pos + 2);

            switch (wochentag) {
                case "MO":
                    wochentage.add(DayOfWeek.MONDAY);
                    break;
                case "DI":
                    wochentage.add(DayOfWeek.TUESDAY);
                    break;
                case "MI":
                    wochentage.add(DayOfWeek.WEDNESDAY);
                    break;
                case "DO":
                    wochentage.add(DayOfWeek.THURSDAY);
                    break;
                case "FR":
                    wochentage.add(DayOfWeek.FRIDAY);
                    break;
                case "SA":
                    wochentage.add(DayOfWeek.SATURDAY);
                    break;
                case "VA":
                    wochentage.addAll(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY));
                    break;
                case "WA":
                    wochentage.addAll(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY));
                    break;
                default:
                    throw new FormatException("Periodenbezeichnung " + wochentag + " nicht bekannt.");
            }

        }

        return wochentage;
    }

    public static ArrayList<Bezirk> Convert(ArrayList<AddressInfo> addressInfos) {
        Map<String, Map<String, Map<Address, List<Auftrag>>>> gruppen = addressInfos.stream().
                collect(Collectors.groupingBy(
                        AddressInfo::getBezirk,
                        Collectors.groupingBy(a -> a.strasse,
                                Collectors.groupingBy(Converter::toAddress,
                                        Collectors.mapping(Converter::toAuftrag, Collectors.toList())))));

        ArrayList<Bezirk> bezirke = new ArrayList<>();

        gruppen.forEach((bezirkName, strassen) ->
        {
            Bezirk bezirk = new Bezirk(bezirkName);
            bezirke.add(bezirk);

            strassen.forEach((strasse, addressen) ->
                    addressen.forEach((address, auftraege) -> {
                        address.setBezirk(bezirk);
                        bezirk.addAddress(address);
                        auftraege.forEach((auftrag) -> {
                            address.addAuftrag(auftrag);
                            auftrag.setAddress(address);
                        });
                    }));
        });

        return bezirke;
    }

    private static Address toAddress(AddressInfo addressInfo) {
        String strasse = addressInfo.strasse;
        Matcher matcher = hausnrPattern.matcher(addressInfo.hausnr);

        Integer hausnr;
        Integer bis;
        String zusatz;

        if (matcher.find()) {
            hausnr = Integer.valueOf(matcher.group("Nummer"));

            if (matcher.group("Bis") != null) {
                bis = Integer.valueOf(matcher.group("Bis"));
            } else {
                bis = null;
            }

            zusatz = matcher.group("Zusatz");
        } else {
            hausnr = null;
            bis = null;
            zusatz = null;
        }

        return new Address(strasse, hausnr, bis, zusatz);
    }

    private static Auftrag toAuftrag(AddressInfo addressInfo) {
        Integer auftragsnr = Integer.valueOf(addressInfo.auftragsnr);
        Integer menge = Integer.valueOf(addressInfo.auftragsmenge == null ? addressInfo.menge : addressInfo.auftragsmenge);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        LocalDate gueltigAb = LocalDate.parse(addressInfo.teilaboVon == null ? addressInfo.gueltigAb : addressInfo.teilaboVon, formatter);
        LocalDate gueltigBis = LocalDate.parse(addressInfo.teilaboBis == null ? addressInfo.gueltigBis : addressInfo.teilaboBis, formatter);
        HashSet<DayOfWeek> wochentage = convertPeriodToWeekdays(addressInfo.periode);
        LocalDate unterbrechungVon = addressInfo.unterbrechungVon == null ? null : LocalDate.parse(addressInfo.unterbrechungVon, formatter);
        LocalDate unterbrechungBis = addressInfo.unterbrechungBis == null ? null : LocalDate.parse(addressInfo.unterbrechungBis, formatter);
        String druckerzeugnis = addressInfo.druckerzeugnis;

        return new Auftrag(auftragsnr, menge, gueltigAb, gueltigBis, wochentage, unterbrechungVon, unterbrechungBis, druckerzeugnis, addressInfo.vorname, addressInfo.name, addressInfo.kommentar);
    }

}
