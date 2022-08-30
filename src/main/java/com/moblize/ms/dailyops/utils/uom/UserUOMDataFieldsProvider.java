package com.moblize.ms.dailyops.utils.uom;

import com.moblize.core.model.User;
import com.moblize.core.model.uom.SystemOfUnit;
import com.moblize.core.model.uom.UOMAttributeMetadata;
import com.moblize.core.model.uom.UOMDataField;
import com.moblize.core.model.uom.UOMSettings;
import com.moblize.ms.dailyops.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.URLDecoder;
import java.util.*;

import static com.moblize.core.model.User.find;

@Slf4j
public class UserUOMDataFieldsProvider {

    public List<UOMDataField> getDataFields(List<UOMAttributeMetadata> attributes){
        List<UOMDataField> dataFields = getAllDataFields();
        removeUnneededDataFields(dataFields, attributes);
        return dataFields;
    }


    protected SystemOfUnit getSystemOfUnit(String name) {
        SystemOfUnit systemOfUnit =
                SystemOfUnit.find.field("name").equal(name).get();
        return systemOfUnit;
    }

    protected User getUserInSession() {
//        String userName = SecurityUtils.getCurrentUserLogin()
//            .orElse(null);
        String userName = SecurityUtils.getCurrentUserLogin()
            .map(u -> {
                if (u.equalsIgnoreCase("anonymoususer")) {
                    return null;
                }
                return u;
            }).orElse(null);

        log.debug("getUserInSession: userName={}", userName);

        User user = null;
        if (userName != null && userName.trim().length() > 0) {
            userName = URLDecoder.decode(userName);
            user = find.field("email").equal(userName).get();
            log.debug("getUserInSession: user found = {}", user);
        }
        return user;
    }

    protected void removeUnneededDataFields(List<UOMDataField> dataFields, List<UOMAttributeMetadata> attributes) {
        //Creating map for attributes that needs conversion
        Map<String, UOMAttributeMetadata> mapNeededDataFields = new HashMap<>();
        if (attributes != null) {
            for (UOMAttributeMetadata attribute : attributes) {
                mapNeededDataFields.put(attribute.getDataField(), attribute);
            }
        }
        if (dataFields != null && !dataFields.isEmpty() ) {
	        //Logic to remove fields that are not needed
	        Iterator<UOMDataField> itr = dataFields.iterator();
	        while (itr.hasNext()) {
	            UOMDataField dataField = itr.next();
	            if (!mapNeededDataFields.containsKey(dataField.getDataField())) {
	                itr.remove();
	            }
	        }
        }
    }

    protected List<UOMDataField> getAllDataFields() {
        User user = getUserInSession();
        if (user == null) return null;
        UOMSettings uomSettings = user.getUomSettings();
        List<UOMDataField> userDataFields;
        String systemSource = "US";
        if (uomSettings != null) {
            userDataFields = uomSettings.getDataFields();
            if(userDataFields == null){
                userDataFields = new ArrayList<>();
            }
            systemSource = uomSettings.getSystemSource();
        } else {
            userDataFields = new ArrayList<>();
            systemSource = "US";
        }

        if ((!"US".equals(systemSource))||(userDataFields.size() != 0)) {
            SystemOfUnit systemOfUnit =
                    getSystemOfUnit(systemSource);

            if (systemOfUnit != null) {
                List<UOMDataField> defaultDataFields = systemOfUnit.getDataFields();
                // Creating map of dataFields that user overrides
                Map<String, UOMDataField> mapUserDataFields = new HashMap<>();
                if (userDataFields != null && uomSettings.getDataFields() != null) {
                    for (UOMDataField dataField : uomSettings.getDataFields()) {
                        mapUserDataFields.put(dataField.getDataField(), dataField);
                    }
                }
                // Removing fields from default/source system of units that user overrides
                Iterator<UOMDataField> itr = defaultDataFields.iterator();
                while (itr.hasNext()) {
                    UOMDataField dataField = itr.next();
                    if (mapUserDataFields.containsKey(dataField.getDataField())) {
                        itr.remove();
                    }
                }
                // Adding fields that were overriden by user.
                if (uomSettings.getDataFields() != null) {
                    defaultDataFields.addAll(uomSettings.getDataFields());
                }

                userDataFields = defaultDataFields;
            }
        }
        return userDataFields;
    }
}
