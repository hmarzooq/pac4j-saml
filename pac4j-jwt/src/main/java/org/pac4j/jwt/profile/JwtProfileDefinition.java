package org.pac4j.jwt.profile;

import org.pac4j.core.profile.AttributeLocation;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.profile.definition.CommonProfileDefinition;

import java.util.Map;

/**
 * This is {@link JwtProfileDefinition}.
 *
 * @author Misagh Moayyed
 * @since 5.0.1
 */
public class JwtProfileDefinition extends CommonProfileDefinition {
    private boolean keepNestedAttributes = true;

    public JwtProfileDefinition() {
        super(x -> new JwtProfile());
        setRestoreProfileFromTypedId(true);
    }

    @Override
    public void convertAndAdd(UserProfile profile, AttributeLocation attributeLocation, String name, Object value) {
        if (value instanceof Map) {
            var jsonObject = (Map<String, ?>) value;
            jsonObject.forEach((key, objectValue) -> super.convertAndAdd(profile, attributeLocation, key, objectValue));
            if (keepNestedAttributes) {
                super.convertAndAdd(profile, attributeLocation, name, value);
            }
        } else {
            super.convertAndAdd(profile, attributeLocation, name, value);
        }
    }

    public void setKeepNestedAttributes(boolean keepNestedAttributes) {
        this.keepNestedAttributes = keepNestedAttributes;
    }
}


