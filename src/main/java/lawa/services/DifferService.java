package lawa.services;

import lawa.dataModel.Address;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DifferService {
    public XWPFDocument createWordDocument() {
        XWPFDocument wordDocument = new XWPFDocument();
        return wordDocument;
    }

    public XWPFRun getAddressParagraphFromAddress(Address address, XWPFDocument documentWord) {
        XWPFParagraph paragraph = documentWord.createParagraph();

        XWPFRun addressParagraph = paragraph.createRun();
        addressParagraph.setText(address.toString());
        addressParagraph.addBreak();

        return addressParagraph;
    }

    void writeAddressToDocument(File file, Address address) throws IOException {
        try(FileOutputStream out = new FileOutputStream(file)) {
            XWPFDocument wordDocument = createWordDocument();
            getAddressParagraphFromAddress(address, wordDocument);
            wordDocument.write(out);
        }
    }
}
