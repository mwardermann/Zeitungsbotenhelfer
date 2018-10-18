package lawa;

import com.google.gson.Gson;
import lawa.dataModel.*;
import lawa.parser.AddressInfo;
import lawa.parser.Converter;
import lawa.parser.Parser;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

            ArrayList<Bezirk> bezirke = Converter.Convert(addressInfos);

            Gson gsonSerializer = new Gson();
            File bezirkeResult = new File(rootPath + "\\Bezirke.json");

            ArrayList<Bezirk> oldBezirke;

            if (bezirkeResult.exists()){
                try (BufferedReader reader = new BufferedReader(new FileReader(bezirkeResult)))
                {
                    oldBezirke = gsonSerializer.fromJson(reader, bezirke.getClass());
                }
            }
            else {
                oldBezirke = new ArrayList<>();
            }

            List<DiffNode> diffNodes = Comparer.FindDiffs(bezirke, oldBezirke);


            String serialized = gsonSerializer.toJson(bezirke);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter((bezirkeResult)))) {
                writer.write(serialized);
            }

            printAddressInfos(rootPath, addressInfos);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void getLocation(Address address) {
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
