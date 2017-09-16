package org.my;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;
import java.util.Map;
import java.lang.Object;
import com.google.protobuf.Descriptors.GenericDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import java.io.FileInputStream;

import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class main {

    private static final int MAX_RESULTS = 100;

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {

        //Load image 
        String fileName = "./resources/receipt_1.jpg";
        Path path = Paths.get(fileName);
        byte[] data = new byte[0];
        try {
            data = Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        PrintStream text = null;
        try {
            detectText(fileName, text);
        } catch (Exception ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println(text);

    }

    public static String validate(String word_to_check) {
        //if the word_to_check finishes in s, remove it, also lower case it   
        word_to_check = word_to_check.toLowerCase();
        if (word_to_check != null && word_to_check.length() > 0 && word_to_check.charAt(word_to_check.length() - 1) == 's') {
            word_to_check = word_to_check.substring(0, word_to_check.length() - 1);
        }
        return word_to_check;
    }

    public static boolean check_valid(String word_to_check) {

        //if the word_to_check finishes in s, remove it, also lower case it   
        word_to_check = word_to_check.toLowerCase();
        if (word_to_check != null && word_to_check.length() > 0 && word_to_check.charAt(word_to_check.length() - 1) == 's') {
            word_to_check = word_to_check.substring(0, word_to_check.length() - 1);
        }

        //System.out.println("word to check" +word_to_check );
        //read whitelist file
        String fileNameDefined = "./resources/whitelist.txt";
        // -File class needed to turn stringName to actual file
        File file = new File(fileNameDefined);
        try {
            // -read from filePooped with Scanner class
            Scanner inputStream = new Scanner(file);
            inputStream.useDelimiter(",");
            // hashNext() loops line-by-line
            while (inputStream.hasNext()) {
                //read single line, put in string
                String data = inputStream.next();
                //System.out.println(data + "***");

                if (word_to_check.equals(data)) {
                    inputStream.close();
                    return true;
                }

            }
            // after loop, close scanner
            inputStream.close();

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }

        return false;
    }

    public static void detectText(String filePath, PrintStream out) throws Exception, IOException {
        System.out.println("starting to detect");
        List<AnnotateImageRequest> requests = new ArrayList<>();
        ArrayList<String> detected_words = new ArrayList<String>();

        ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));

        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Type.TEXT_DETECTION).build();
        AnnotateImageRequest request
                = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    out.printf("Error: %s\n", res.getError().getMessage());
                    return;
                }

                // For full list of available annotations, see http://g.co/cloud/vision/docs
                for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
                    if (check_valid(annotation.getDescription())) {
                        detected_words.add(validate(annotation.getDescription()));
                    }
                }

                //TODO check for blacklisted words and whitelisted words 
            }
        }

        System.out.println("detected words is" + detected_words);

    }
}
