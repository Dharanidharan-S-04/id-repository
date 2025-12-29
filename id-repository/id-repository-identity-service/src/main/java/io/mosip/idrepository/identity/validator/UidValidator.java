package io.mosip.idrepository.identity.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.idrepository.identity.helper.UidGeneratorHelper;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;

/**
 * UID Validator for Myanmar UID validation using Luhn Algorithm
 */
@Component
public class UidValidator {

    @Autowired
    private UidGeneratorHelper uidGeneratorHelper;

    private static final String UID_REGEX_PATTERN = "^[0-9]{10}$"; // Exactly 10 digits

    /**
     * Validate UID format and Luhn Algorithm checksum
     *
     * @param uid the UID to validate
     * @return true if valid
     * @throws InvalidIDException if invalid
     */
    public boolean validateUid(Object uid) {
        if (uid == null) {
            throw new InvalidIDException("UID", "UID cannot be null");
        }

        String uidStr = uid.toString();

        // First check format (exactly 10 digits)
        Pattern pattern = Pattern.compile(UID_REGEX_PATTERN);
        Matcher matcher = pattern.matcher(uidStr);
        if (!matcher.matches()) {
            throw new InvalidIDException("UID", "UID must be exactly 10 digits");
        }

        // Then validate using Luhn Algorithm
        if (!uidGeneratorHelper.validateUidWithLuhn(uidStr)) {
            throw new InvalidIDException("UID", "Invalid UID checksum");
        }

        return true;
    }
}
