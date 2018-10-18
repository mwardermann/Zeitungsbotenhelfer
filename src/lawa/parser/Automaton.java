package lawa.parser;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.regex.Matcher;

class Automaton {
    private final String[] druckerzeugnisse;
    private State activeState;
    private Dictionary<State, State[]> nextStatesByOldState = new Hashtable<>();
    private AddressInfo activeAddressInfo = null;
    private ArrayList<AddressInfo> addressInfos = new ArrayList<>();
    private State[] overridingNextStates;
    private boolean isInHeader;
    private Header activeHeader;
    private ArrayList<Header> headers = new ArrayList<>();
    private State[] nextStates;

    Automaton(String[] druckerzeugnisse) {
        this.druckerzeugnisse = druckerzeugnisse;
        State bezirk = new State(this, "Zusteller-Buch\\s+Bezirk\\s+(\\S+)(\\s+Seite\\s+([0-9]+))?", "BEZIRK") {
            @Override
            void apply(Matcher matcher) {
                Header header = this.automaton.getHeader();
                header.bezirk = matcher.group(1);
                header.seite = matcher.group(2);

                if (header.seite == null) {
                    header.seite = "1";
                }
            }
        };

        State bezirksreserve = new State(this, "Bezirksreserve", "BEZIRKSRESERVE");

        State bezirksreserve2ndLine = new State(this, ".", "2ND");

        State any = new State(this, ".", "ANY");

        State auftragsmenge = new State(this, "(?<Auftragsmenge>\\s*Auftragsmenge)", "AUFTRAGSMENGE");

        State strasseHausnrKommentar = new State(this, String.join(" +",
                "\\s*(?<Strasse>[^0-9]+)",
                "(?<Hausnr>[0-9-]+( [a-zA-Z]\\b)?)(?<Kommentar>.+)?$"), "STRASSE") {
            @Override
            void apply(Matcher matcher) {
                this.automaton.finishAddressInfo();
                AddressInfo addressInfo = this.automaton.getAddressInfo();
                addressInfo.strasse = matcher.group("Strasse").trim();
                addressInfo.hausnr = matcher.group("Hausnr").trim();
                addressInfo.kommentar = matcher.group("Kommentar");
            }
        };

        State name = new State(this, "(?<Name>[^, ]+\\b)(, (?<Vorname>.+\\b)?)?  (?<Stadt>.+)", "NAME") {
            @Override
            void apply(Matcher matcher) {
                AddressInfo addressInfo = this.automaton.getAddressInfo();
                addressInfo.name = matcher.group("Name");
                addressInfo.vorname = matcher.group("Vorname");
                addressInfo.stadt = matcher.group("Stadt");
            }
        };

        String datePart = "[0-9][0-9]\\.[0-9][0-9]\\.[0-9][0-9][0-9][0-9]";

        State auftragsmengeInAddress = new State(this, "Auftragsmenge.+(?<Von>" + datePart + ") +(?<Bis>" + datePart + ") +\\b(?<AuftragsmengeAdresse>[0-9]+)", "AUFTRAGSMENGE ADRESSE") {
            @Override
            void apply(Matcher matcher) {
                AddressInfo addressInfo = this.automaton.getAddressInfo();
                addressInfo.auftragsmenge = matcher.group("AuftragsmengeAdresse");
                addressInfo.teilaboVon = matcher.group("Von");
                addressInfo.teilaboBis = matcher.group("Bis");
            }
        };

        State unterbrechung = new State(this, "(\\s*Unterbrechung:.+(?<UnterbrechungVon>" + datePart + ") (?<UnterbrechungBis>" + datePart + ")\\s*)?", "UNTERBRECHUNG") {
            @Override
            void apply(Matcher matcher) {
                AddressInfo addressInfo = this.automaton.getAddressInfo();
                addressInfo.unterbrechungVon = matcher.group("UnterbrechungVon");
                addressInfo.unterbrechungBis = matcher.group("UnterbrechungBis");
            }
        };

        State reiseUnterbrechung = new State(this, "(\\s*Reiseunterbrechung:.+(?<UnterbrechungVon>" + datePart + ") (?<UnterbrechungBis>" + datePart + ")\\s*)?", "UNTERBRECHUNG") {
            @Override
            void apply(Matcher matcher) {
                AddressInfo addressInfo = this.automaton.getAddressInfo();
                addressInfo.unterbrechungVon = matcher.group("UnterbrechungVon");
                addressInfo.unterbrechungBis = matcher.group("UnterbrechungBis");
            }
        };

        State details = new State(this, String.join(" +",
                "(?<Auftragsnr>[0-9]+)",
                "(?<Art>\\S+)",
                "(?<PA>\\S+)",
                "(?<Druckerzeugnis>(" + String.join("|", druckerzeugnisse) + "))",
                "(?<PVA>\\S+)",
                "(?<GueltigAb>" + datePart + ")",
                "(?<GueltigBis>" + datePart + ")",
                "(?<Periode>\\S+)",
                "(?<Menge>[0-9]+)( +(?<Zusatz>TA|UB|RU))?"), "DETAILS") {
            @Override
            void apply(Matcher matcher) {
                AddressInfo addressInfo = this.automaton.getAddressInfo();
                addressInfo.auftragsnr = matcher.group("Auftragsnr");
                addressInfo.art = matcher.group("Art");
                addressInfo.pa = matcher.group("PA");
                addressInfo.druckerzeugnis = matcher.group("Druckerzeugnis");
                addressInfo.pva = matcher.group("PVA");
                addressInfo.gueltigAb = matcher.group("GueltigAb");
                addressInfo.gueltigBis = matcher.group("GueltigBis");
                addressInfo.periode = matcher.group("Periode");
                addressInfo.menge = matcher.group("Menge");

                String zusatz = matcher.group("Zusatz");

                if (zusatz != null) {
                    switch (zusatz) {
                        case "TA":
                            this.automaton.setNextStatesOverride(auftragsmengeInAddress, strasseHausnrKommentar);
                            break;
                        case "UB":
                            this.automaton.setNextStatesOverride(unterbrechung, strasseHausnrKommentar);
                            break;
                        case "RU":
                            this.automaton.setNextStatesOverride(reiseUnterbrechung, strasseHausnrKommentar);
                            break;
                    }
                }
            }
        };

        State summeState = new State(this, "Summe +(\\S+) +([0-9]+)", "SUMME") {
            @Override
            void apply(Matcher matcher) {
                this.automaton.isInHeader = false;

                if (matcher.group(1).equals("Bezirk") && matcher.group(2) != null){
                    Header header = this.automaton.getHeader();

                    header.summe = Integer.valueOf(matcher.group(2));
                }
            }
        };

        State headerMarker = new State(this, "^_{70,}", "HEADER") {
            @Override
            void apply(Matcher matcher) {
                if (!this.automaton.isInHeader) {
                    this.automaton.startNewHeader();
                    this.automaton.setNextStatesOverride(bezirk, summeState);
                    this.automaton.isInHeader = true;
                } else {
                    this.automaton.isInHeader = false;
                }
            }
        };

        State selektionsdatum = new State(this, "Selektionsdatum: (" + datePart + ")", "DATUM") {
            @Override
            void apply(Matcher matcher) {
                Header header = this.automaton.getHeader();
                header.selektionsdatum = matcher.group(1);
            }
        };


        State initialState = new State(this, "", "INIT");

        nextStatesByOldState.put(initialState, new State[]{headerMarker});
        nextStatesByOldState.put(bezirk, new State[]{selektionsdatum});
        nextStatesByOldState.put(selektionsdatum, new State[]{headerMarker, any});
        nextStatesByOldState.put(any, new State[]{headerMarker, any});

        nextStatesByOldState.put(headerMarker, new State[]{bezirksreserve, auftragsmenge, strasseHausnrKommentar, headerMarker});
        nextStatesByOldState.put(bezirksreserve, new State[]{bezirksreserve2ndLine});
        nextStatesByOldState.put(bezirksreserve2ndLine, new State[]{bezirksreserve, auftragsmenge, strasseHausnrKommentar});
        nextStatesByOldState.put(auftragsmenge, new State[]{bezirksreserve, auftragsmenge, strasseHausnrKommentar});

        nextStatesByOldState.put(strasseHausnrKommentar, new State[]{name});
        nextStatesByOldState.put(name, new State[]{details});
        nextStatesByOldState.put(details, new State[]{headerMarker, strasseHausnrKommentar});
        nextStatesByOldState.put(auftragsmengeInAddress, new State[]{headerMarker, strasseHausnrKommentar});
        nextStatesByOldState.put(unterbrechung, new State[]{headerMarker, strasseHausnrKommentar});
        nextStatesByOldState.put(reiseUnterbrechung, new State[]{headerMarker, strasseHausnrKommentar});
        nextStatesByOldState.put(summeState, new State[]{headerMarker});

        this.setState(initialState);
    }

    State[] getNextStates() {
        return this.nextStates;
    }

    void setState(State state) {
        if (this.overridingNextStates != null) {
            this.nextStates = this.overridingNextStates;
        } else {
            this.nextStates = (this.nextStatesByOldState.get(state));
        }

        this.overridingNextStates = null;
        this.activeState = state;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        if (this.activeHeader != null) {
            result.append("Bezirk: ").
                    append(this.activeHeader.bezirk).
                    append(", Datum: ").
                    append(this.activeHeader.selektionsdatum).
                    append(", Seite: ").
                    append(this.activeHeader.seite);
            result.append("\r\n");
        }

        if (this.activeState != null) {
            result.append("Vorige Zeile:").append(this.activeState.toString());
            result.append("\r\n");
            result.append("Mögliche nächste Zustände:");
            for (State state : this.getNextStates()) {
                result.append(state.toString());
                result.append("\r\n");
            }
        }

        return result.toString();
    }

    ArrayList<AddressInfo> getAddressInfos() {
        return this.addressInfos;
    }

    ArrayList<Header> getHeaders(){
        return this.headers;
    }
    
    private void setNextStatesOverride(State... states) {
        this.overridingNextStates = states;
    }

    private void startNewHeader() {
        if (this.activeHeader != null) {
            this.headers.add(this.activeHeader);
        }

        this.activeHeader = new Header();
    }

    private Header getHeader() {
        if (this.activeHeader == null) {
            this.activeHeader = new Header();
        }

        return this.activeHeader;
    }

    private void finishAddressInfo() {
        if (this.activeAddressInfo != null) {
            this.addressInfos.add(this.activeAddressInfo);
            this.activeAddressInfo = null;
        }
    }

    private AddressInfo getAddressInfo() {
        if (this.activeAddressInfo == null) {
            this.activeAddressInfo = new AddressInfo(this.getHeader());
        }
        return this.activeAddressInfo;
    }
}