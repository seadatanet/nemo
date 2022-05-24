package fr.ifremer.ws.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

import java.io.IOException;
import java.util.*;

public class RestClientUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final JsonFactory jsonFactory = new JsonFactory(objectMapper);

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
        objectMapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    public static JsonNode jsonGet(String apiUrl, String path) throws IOException {
        apiUrl = apiUrl.replaceFirst("/+$", "");
        path = path.replaceFirst("^/+", "");
        String requestUrl = apiUrl + "/" + path;

        HttpClient httpClient = new HttpClient();
        GetMethod httpGet = new GetMethod(requestUrl);
        int statusCode = httpClient.executeMethod(httpGet);

        if (statusCode < HttpStatus.SC_OK || statusCode >= HttpStatus.SC_MULTIPLE_CHOICES) {
            String msg = "http response code " + statusCode;
            msg += " (requested url was " + requestUrl + ")";
            throw new RuntimeException(msg);
        }
        if (statusCode != HttpStatus.SC_NO_CONTENT) {
            return jsonFactory.createParser(httpGet.getResponseBodyAsStream()).readValueAsTree();
        } else {
            return null;
        }
    }

    public static <T> List<T> queryForList(String apiUri, String path, Class<T> itemsType) {
        try {
            JsonNode root = jsonGet(apiUri, path);
            List<T> list = new ArrayList<>(root.size());
            for (int i = 0; i < root.size(); i++) {
                T item = objectMapper.convertValue(root.get(i), itemsType);
                list.add(item);
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T queryForObject(String apiUrl, String path, Class<T> itemType) {
        try {
            JsonNode root = jsonGet(apiUrl, path);
            return objectMapper.convertValue(root, itemType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, String> queryForMap(String apiUrl, String path) {
        try {
            JsonNode root = jsonGet(apiUrl, path);
            Iterator<String> it = root.fieldNames();
            Map<String, String> result = new HashMap<>();
            while (it.hasNext()) {
                String fieldName = it.next();
                result.put(fieldName, root.get(fieldName).asText());
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
