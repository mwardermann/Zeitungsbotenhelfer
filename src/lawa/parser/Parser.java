package lawa.parser;

import lawa.MyLogger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Parser {
    public static ArrayList<AddressInfo> Parse(MyLogger logger,  String[] druckerzeugnisse, File file) throws IOException {
        ArrayList<AddressInfo> addressInfos = new ArrayList<>();

        try (PDDocument document = PDDocument.load(file)) {
            logger.setFile(file);
            for (int pageNumber = 0; pageNumber < document.getNumberOfPages(); pageNumber++) {
                logger.setPageNumber(pageNumber);
                String text = extractTextFromPage(document.getPage(pageNumber));

                Automaton automaton = new Automaton(druckerzeugnisse);

                parseText(automaton, text, logger);

                addressInfos.addAll(automaton.getAddressInfos());
            }
        }

        return addressInfos;
    }

    private static String extractTextFromPage(PDPage page) throws IOException {
        PDFTextStripperByArea stripper = new PDFTextStripperByArea();
        stripper.setSortByPosition(true);

        PDRectangle origRect = page.getBBox();
        Rectangle rect = new Rectangle((int) origRect.getLowerLeftX(), (int) origRect.getLowerLeftY(), (int) origRect.getWidth(), (int) origRect.getHeight());
        stripper.addRegion("class1", rect);

        stripper.extractRegions(page);

        return stripper.getTextForRegion("class1");
    }

    private static void parseText(Automaton automaton, String text, MyLogger logger) {
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

}
