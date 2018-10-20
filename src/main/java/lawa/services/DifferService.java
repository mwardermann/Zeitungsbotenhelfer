package lawa.services;

import lawa.dataModel.Address;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

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

}
