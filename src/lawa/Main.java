package lawa;

import lawa.dataModel.Address;
import lawa.dataModel.Auftrag;
import lawa.dataModel.Bezirk;
import lawa.parser.AddressInfo;
import lawa.parser.Converter;
import lawa.parser.Parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        // write your code here
        try {

            String rootPath = "pdfs";
            File logFile = new File(rootPath + "\\Errors.log");

            MyLogger logger = new MyLogger(logFile);

            String[] druckerzeugnisse = new String[]{"MT", "DIE ZEIT", "HB", "FAZ", "TAZ"};

            ArrayList<AddressInfo> addressInfos = new ArrayList<>();

            for (File file : new File(rootPath).listFiles()) {
                if (file.getName().endsWith(".pdf")) {
                    addressInfos.addAll(Parser.Parse(logger, druckerzeugnisse, file));
                }
            }

            ArrayList<Bezirk> bezirks = Converter.Convert(addressInfos);

            printAddressInfos(rootPath, addressInfos);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void printAddressInfos(String rootPath, ArrayList<AddressInfo> addressInfos) throws IOException {
        File addressInfoFile = new File(rootPath + "\\AddressInfos.csv");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter((addressInfoFile)))) {
            writer.write(AddressInfo.getHeader());
            writer.newLine();

            for (AddressInfo addressInfo : addressInfos) {
                writer.write(addressInfo.toString());
                writer.newLine();
            }
        }
    }


}
