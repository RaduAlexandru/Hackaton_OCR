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


public class main {
    
    private static final int MAX_RESULTS = 100;
    
    
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        
        System.out.println("fuck");
        
        /*
    ImageAnnotatorClient vision = null; vision = ImageAnnotatorClient.create();

        // The path to the image file to annotate
            String fileName = "./resources/receipt_1.jpg";

            // Reads the image file into memory
            Path path = Paths.get(fileName);
        byte[] data = new byte[0];
        try {
            data = Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteString imgBytes = ByteString.copyFrom(data);

            // Builds the image annotation request
            List<AnnotateImageRequest> requests = new ArrayList<>();
            Image img = Image.newBuilder().setContent(imgBytes).build();
            Feature feat = Feature.newBuilder().setType(Type.LABEL_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feat)
                    .setImage(img)
                    .build();
            requests.add(request);

            // Performs label detection on the image file
            BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.out.printf("Error: %s\n", res.getError().getMessage());
                    return;
                }

                //for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
                //    annotation.getAllFields().forEach((k, v)->
                //            System.out.printf("%s : %s\n", k, v.toString()));
                //}

                for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {

                    for (Map.Entry<FieldDescriptor, Object> entry : annotation.getAllFields().entrySet()) {
                        System.out.println("Item : " + entry.getKey() + " Count : " + entry.toString());
                    }


                }
            }

*/      

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
            detectText(fileName,text );
        } catch (Exception ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println(text);
        
        
    }
    
    
    
    public static void detectText(String filePath, PrintStream out) throws Exception, IOException {
        System.out.println("starting to detect");
  List<AnnotateImageRequest> requests = new ArrayList<>();

  ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));

  Image img = Image.newBuilder().setContent(imgBytes).build();
  Feature feat = Feature.newBuilder().setType(Type.TEXT_DETECTION).build();
  AnnotateImageRequest request =
      AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
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
          //System.out.println("printing....");
          System.out.println(annotation.getDescription());
          //System.out.println(annotation.getBoundingPoly());
        //out.printf("Text: %s\n", annotation.getDescription());
        //out.printf("Position : %s\n", annotation.getBoundingPoly());
      }
      
      //TODO check for blacklisted words and whitelisted words 
      
      
    }
  }
}
}
