package lawa.parser;

import lawa.Logger;
import lawa.MyLogger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Parser {
    public static ArrayList<AddressInfo> Parse(MyLogger logger,  String[] druckerzeugnisse, File file) throws IOException {
        ArrayList<AddressInfo> addressInfos = new ArrayList<>();
        ArrayList<Header> headers;

        try (PDDocument document = PDDocument.load(file)) {
            logger.setFile(file);
                String text = extractTextFromPage(document);

                Automaton automaton = new Automaton(druckerzeugnisse);

                parseText(automaton, text, logger);

                headers = automaton.getHeaders();
                addressInfos = automaton.getAddressInfos();
        }

        Map<String, List<Header>> hs = headers.stream().filter(h -> h.bezirk != null).collect(Collectors.groupingBy(h -> h.bezirk));
        for (Map.Entry<String, List<Header>> e : hs.entrySet())
        {
            Optional<Header> mainHeader = e.getValue().stream().filter(h -> h.summe != null).findFirst();

            if (mainHeader.isPresent()){
                for (Header header: e.getValue())
                {
                    header.summe = mainHeader.get().summe;
                }
            }
        }

        return addressInfos;
    }

    static void parseText(Automaton automaton, String text, Logger logger) {
        Pattern newlinePattern = Pattern.compile("\\r\\n");

        newlinePattern.splitAsStream(text).
                filter(s -> !s.trim().isEmpty()).
                forEach(line -> {
                    for (State state : automaton.getNextStates()) {
                        if (state.evaluate(line)) {
                            automaton.setState(state);
                            logger.log(state.getName() + "\t" + line);
                            return;
                        }
                    }

                    logger.log((String.format("Konnte Zeile '%s' nicht auswerten. Status: \r\n%s", line, automaton.toString())));
                });
    }

    private static String extractTextFromPage(PDDocument page) throws IOException {
        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setSortByPosition(true);
        return stripper.getText(page);
    }

}
