package lawa;

import com.google.gson.Gson;
import lawa.dataModel.Address;
import lawa.dataModel.Bezirk;
import lawa.parser.AddressInfo;
import lawa.parser.Converter;
import lawa.parser.Parser;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

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

            for (Bezirk bezirk : bezirks) {
                for (Address address : bezirk.getAddresses()) {
                    String addresInUrl = address.getForPost("+");
                    String urlString = "https://maps.googleapis.com/maps/api/geocode/json?address=" +
                            addresInUrl +
                            "&key=AIzaSyBzbDOe5-AB9iNQnH8IqlWYjS_LbpCP29s";

                    try {
                        URL url = new URL(urlString);
                        try (InputStream is = url.openStream()) {
                            Reader reader = new InputStreamReader(is);
                            Map<String, Object> response = new Gson().fromJson(reader, Map.class);
                            Map<String, Object> location =(Map<String, Object>)
                                    ((Map<String, Object>)((Map<String, Object>)((ArrayList)response.get("results")).get(0)).get("geometry")).get("location");


                            Double latitude = (Double) location.get("lat");
                            Double length = (Double) location.get("lng");

                            address.setLocation(latitude, length);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            Gson gsonSerializer = new Gson();
            String serialized = gsonSerializer.toJson(bezirks);

            File bezirkeResult = new File(rootPath + "\\Bezirke.json");

            try (BufferedWriter writer = new BufferedWriter(new FileWriter((bezirkeResult)))) {
                writer.write(serialized);
            }

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
