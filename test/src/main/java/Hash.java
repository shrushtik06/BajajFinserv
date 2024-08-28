import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Random;

public class Hash {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java -jar DestinationHashGenerator.jar <PRN Number> <JSON File Path>");
            System.exit(1);
        }

        String prnNumber = args[0].trim().toLowerCase();
        String jsonFilePath = args[1];
        File jsonFile = new File(jsonFilePath);

        if (!jsonFile.exists()) {
            System.err.println("File not found: " + jsonFilePath);
            System.exit(1);
        }

        try {
            // Parse the JSON file
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonFile);

            // Find the "destination" key value
            String destinationValue = findDestinationValue(rootNode);
            if (destinationValue == null) {
                System.err.println("Key 'destination' not found in the JSON file.");
                System.exit(1);
            }

            // Generate a random 8-character alphanumeric string
            String randomString = generateRandomString(8);

            // Generate the MD5 hash
            String concatenatedString = prnNumber + destinationValue + randomString;
            String md5Hash = generateMD5Hash(concatenatedString);

            // Output the result
            System.out.println(md5Hash + ";" + randomString);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static String findDestinationValue(JsonNode node) {
        if (node.isObject()) {
            Iterator<String> fieldNames = node.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                JsonNode childNode = node.get(fieldName);

                if (fieldName.equals("destination")) {
                    return childNode.asText();
                } else {
                    String result = findDestinationValue(childNode);
                    if (result != null) {
                        return result;
                    }
                }
            }
        } else if (node.isArray()) {
            for (JsonNode childNode : node) {
                String result = findDestinationValue(childNode);
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
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashInBytes = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
