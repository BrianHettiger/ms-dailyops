package com.moblize.ms.dailyops.utils.uom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moblize.core.model.uom.UOMAttributeMetadata;
import com.moblize.core.model.uom.UOMDataField;
import com.moblize.core.model.uom.UOMMetadata;
import com.moblize.core.model.uom.UOMPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class UOMConverter {

    ObjectMapper objectMapper = new ObjectMapper();

    private static final String CONVERT_FROM_END_POINT_NAME = "convertFromMoblizeUnits";
    private static final String CONVERT_TO_END_POINT_NAME = "convertToMoblizeUnits";

    private static UserUOMDataFieldsProvider _dataFieldsProvider;

    @Value("api.uom.rest.base.url")
    private String uomServerBaseUrl;
    @Value("api.uom.rest.username")
    private String uomUsername;
    @Value("api.uom.rest.password")
    private String uomPassword;

    public class ListOfJson<T> implements ParameterizedType {
        private final Class<?> wrapped;
        public ListOfJson(Class<T> wrapper) {
            this.wrapped = wrapper;
        }
        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{wrapped};
        }

        @Override
        public Type getRawType() {
            return List.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }

    public UOMConverter() {
        _dataFieldsProvider = new UserUOMDataFieldsProvider();
    }

    public <T> List<T> convertFromMoblizeUnits(List<T> rawData, UOMAttributeMetadata... attributes) {
      return convert(rawData, true, attributes);
    }

    public <T> List<T> convertToMoblizeUnits(List<T> rawData, UOMAttributeMetadata... attributes) {
        return convert(rawData, false, attributes);
    }

    public <T> List<T> convert(List<T> rawData,  boolean fromMoblizeUnits, UOMAttributeMetadata... attributes) {
        if (rawData == null || rawData.isEmpty() || rawData.get(0) == null) {
            return rawData;
        }
        Class<T> classOfT = (Class) rawData.get(0).getClass();
        UOMMetadata uomMetadata = new UOMMetadata();
        if (attributes != null && attributes.length > 0) {
            uomMetadata.setAttributeList(Arrays.asList(attributes));
            List<UOMDataField> dataFields = _dataFieldsProvider.getDataFields(uomMetadata.getAttributeList());
            uomMetadata.setDataFieldList(dataFields);
            UOMPayload<T> payload = new UOMPayload(uomMetadata, rawData);
            List<T> convertedData;
            if( fromMoblizeUnits) {
                convertedData = convertPayloadFromMoblizeUnits(payload, classOfT);
            }
            else{
                convertedData = convertPayloadToMoblizeUnits(payload, classOfT);
            }
            return convertedData;
        }
        return rawData;
    }

    protected <T> List<T> convertPayloadFromMoblizeUnits(UOMPayload<T> payload, Class<T> clazz) {
        List<T> convertedObjs = convertPayload(payload, clazz, CONVERT_FROM_END_POINT_NAME);
        return convertedObjs;
    }

    protected <T> List<T> convertPayloadToMoblizeUnits(UOMPayload<T> payload, Class<T> clazz) {
        List<T> convertedObjs = convertPayload(payload, clazz, CONVERT_TO_END_POINT_NAME);
        return convertedObjs;
    }

    protected <T> List<T> convertPayload(UOMPayload<T> payload, Class<T> clazz, String endPointName) {
        if (payload == null || payload.getMetaData() == null) {
            throw new IllegalArgumentException("Payload or metadata is null");
        }
        if (payload.getMetaData().getDataFieldList() == null || payload.getMetaData().getDataFieldList().isEmpty()) {
            log.debug("convertPayloadFromMoblizeUnits: data fields are null or empty. Returning original data");
            return payload.getRawObjectData();
        }
        String url = this.uomServerBaseUrl + endPointName;
        log.debug("convertPayloadFromMoblizeUnits: sending %s objects with metadata=%s", payload.getRawObjectData().size(), payload.getMetaData());

        ResponseEntity<String> responseEntity = post(url, payload);
        String jsonResponse = responseEntity.getBody();
        log.info("convertPayloadFromMoblizeUnits: response received : %s", jsonResponse);
        if(responseEntity.getStatusCode().is2xxSuccessful()) {
            List<T> convertedObjs = null;
            try {
                convertedObjs = objectMapper.readValue(
                        responseEntity.getBody(),
                        new ArrayList<T>().getClass()
                );
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            log.info("convertPayloadFromMoblizeUnits: %s objects converted", convertedObjs.size());
            return convertedObjs;
        }
        else {
            log.error("Failed parsing response :", jsonResponse);
            throw new RuntimeException("Failed parsing response : "+ jsonResponse);
        }

    }

    protected ResponseEntity<String> post(String url, UOMPayload payload) {

        try {
            RestTemplate restTemplate = new RestTemplate();
            final MultiValueMap<String, String> headers = new HttpHeaders();
            headers.put("Content-Type", List.of("application/json"));
            HttpEntity entity = new HttpEntity(payload, headers);
            final ResponseEntity<String> exchange =
                restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            return exchange;
        }
        catch(Exception ex){
            log.error("Error in post", ex);
            log.error("Error in post: payload %s", payload);
            throw ex;
        }
    }

}
