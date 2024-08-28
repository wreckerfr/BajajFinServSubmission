package DestinationHashGenerator;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Random;

public class DestinationHashGenerator {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar DestinationHashGenerator.jar <PRN Number> <path to JSON file>");
            return;
        }

        String prnNumber = args[0].toLowerCase();
        String jsonFilePath = args[1];

        try (FileInputStream fis = new FileInputStream(jsonFilePath)) {
            JSONObject jsonObject = new JSONObject(new JSONTokener(fis));
            String destinationValue = findDestination(jsonObject);

            if (destinationValue != null) {
                String randomString = generateRandomString(8);
                String concatenatedString = prnNumber + destinationValue + randomString;
                String hash = generateMD5Hash(concatenatedString);
                System.out.println(hash + ";" + randomString);
            } else {
                System.out.println("No 'destination' key found in the JSON file.");
            }

        } catch (IOException e) {
            System.out.println("Error reading the JSON file: " + e.getMessage());
        }
    }

    private static String findDestination(JSONObject jsonObject) {
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = jsonObject.get(key);

            if (key.equals("destination")) {
                return value.toString();
            } else if (value instanceof JSONObject) {
                String result = findDestination((JSONObject) value);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    private static String generateMD5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

