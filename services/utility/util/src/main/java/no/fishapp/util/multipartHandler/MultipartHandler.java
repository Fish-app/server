package no.fishapp.util.multipartHandler;

import com.ibm.websphere.jaxrs20.multipart.IAttachment;
import com.ibm.websphere.jaxrs20.multipart.IMultipartBody;

import javax.activation.DataHandler;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

public class MultipartHandler {

    private IMultipartBody multipartBody;

    private HashMap<String, DataHandler> fieldsMap = new HashMap<>();

    public MultipartHandler(IMultipartBody multipartBody) {
        this.multipartBody = multipartBody;
        List<IAttachment> attachments = multipartBody.getAllAttachments();


        for (IAttachment attachment : attachments) {
            if (attachment == null) {
                continue;
            }
            DataHandler dataHandler = attachment.getDataHandler();

            String[] contentDisposition = attachment.getHeaders().getFirst("Content-Disposition").split("; ");
            for (String tempName : contentDisposition) {
                String[] names = tempName.split("=");
                if (names.length == 2 && names[0].equals("name")) {
                    String fieldName = names[1].replace("\"", "");
                    fieldsMap.put(fieldName, dataHandler);
                }
            }
        }
    }


    private File readImage(String fieldName, File savePath) {
        return null;
    }

    private MultipartHandler handleFile(BiConsumer<InputStream, String> biConsumer) {
        return null;
    }


    public String getFieldAsString(String fieldName) throws MultipartReadException, MultipartNameNotFoundException {
        if (! fieldsMap.containsKey(fieldName)) {
            throw new MultipartNameNotFoundException("No feald with key");
        }
        try {
            return new String(fieldsMap.get(fieldName).getInputStream().readAllBytes(),
                              StandardCharsets.UTF_8
            );
        } catch (IOException e) {
            throw new MultipartReadException("Error parsing string from form field: " + fieldName, e, fieldName);

        }
    }

    public int getInt(String fieldName) throws MultipartReadException, MultipartNameNotFoundException {
        if (! fieldsMap.containsKey(fieldName)) {
            throw new MultipartNameNotFoundException("No feald with key");
        }
        try {
            return Integer.parseInt(getFieldAsString(fieldName));
        } catch (NumberFormatException e) {
            throw new MultipartReadException("Error parsing int from form field: " + fieldName, e, fieldName);

        }
    }

    public float getFloat(String fieldName) throws MultipartReadException, MultipartNameNotFoundException {
        if (! fieldsMap.containsKey(fieldName)) {
            throw new MultipartNameNotFoundException("No feald with key");
        }
        try {
            return Float.parseFloat(getFieldAsString(fieldName));
        } catch (NumberFormatException e) {
            throw new MultipartReadException("Error parsing int from form field: " + fieldName, e, fieldName);

        }
    }

    public DataHandler getFieldDataHandler(String fieldName) throws MultipartNameNotFoundException {
        if (! fieldsMap.containsKey(fieldName)) {
            throw new MultipartNameNotFoundException("No feald with key");
        } else {
            return fieldsMap.get(fieldName);
        }
    }
}
