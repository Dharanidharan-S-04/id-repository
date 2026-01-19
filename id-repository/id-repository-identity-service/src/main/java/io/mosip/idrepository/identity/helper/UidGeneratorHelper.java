package io.mosip.idrepository.identity.helper;

import io.mosip.idrepository.core.logger.IdRepoLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * UID Generator Helper implementing Luhn Algorithm
 * Generates unique 10-digit UID with Luhn checksum validation
 * Note: Uniqueness is handled by the existing handle system
 */
@Component
public class UidGeneratorHelper {

    private static final Logger mosipLogger = IdRepoLogger.getLogger(UidGeneratorHelper.class);

    /**
     * Generate a unique 10-digit UID using Luhn Algorithm
     * - 9 digits are randomly generated (no leading zeros)
     * - 1 digit is checksum calculated using Luhn Algorithm
     * - Uniqueness is handled by the existing handle system
     *
     * @return unique 10-digit UID
     */
    public String generateUniqueUid() {
        String uid = generateUidWithLuhnChecksum();

        mosipLogger.debug("UidGeneratorHelper", "generateUniqueUid",
                "Generated UID: " + uid);

        return uid;
    }

    /**
     * Generate a 10-digit UID with Luhn checksum
     *
     * @return 10-digit UID string
     */
    private String generateUidWithLuhnChecksum() {
        // Step 1: Generate a random 9-digit number (no leading zeros)
        int randomNumber = generateRandom9DigitNumber();

        // Step 2: Split number into individual digits
        String randomNumberStr = String.valueOf(randomNumber);
        String[] digits = randomNumberStr.split("");

        // Step 3: Apply Luhn Algorithm logic
        int[] processedDigits = new int[9];
        for (int i = 0; i < 9; i++) {
            int digit = Integer.parseInt(digits[i]);

            // If position is EVEN (0-based index), multiply digit by 2
            if (i % 2 == 0) {
                int multiplied = digit * 2;

                // If multiplication result is greater than 9, split digits and add them together
                if (multiplied > 9) {
                    String multipliedStr = String.valueOf(multiplied);
                    String[] splitDigits = multipliedStr.split("");
                    processedDigits[i] = Integer.parseInt(splitDigits[0]) + Integer.parseInt(splitDigits[1]);
                } else {
                    processedDigits[i] = multiplied;
                }
            } else {
                // For ODD positions, keep digit unchanged
                processedDigits[i] = digit;
            }
        }

        // Step 4: Calculate checksum digit
        // Formula: ((sum % 10) - 10) * -1
        int sum = Arrays.stream(processedDigits).sum();
        int checkSumDigit = (10 - (sum % 10)) % 10;

        // Step 5: Append checksum digit to create final 10-digit UID
        return randomNumberStr + checkSumDigit;
    }

    /**
     * Generate a random 9-digit number with no leading zeros
     *
     * @return random 9-digit number
     */
    private final java.util.Random random = new java.util.Random();

    private int generateRandom9DigitNumber() {
        // Range ensures no leading zeros: 100000000 to 999999999
        return random.nextInt(999999999 - 100000000 + 1) + 100000000;
    }

    /**
     * Validate if a given UID follows Luhn Algorithm rules
     *
     * @param uid the UID to validate
     * @return true if valid, false otherwise
     */
    public boolean validateUidWithLuhn(String uid) {
        if (uid == null || uid.length() != 10) {
            return false;
        }

        try {
            // Extract the 9-digit base number (first 9 digits)
            String baseNumber = uid.substring(0, 9);
            int providedChecksum = Integer.parseInt(uid.substring(9));

            // Calculate checksum for the base number
            String[] digits = baseNumber.split("");
            int[] processedDigits = new int[9];

            for (int i = 0; i < 9; i++) {
                int digit = Integer.parseInt(digits[i]);

                if (i % 2 == 0) {
                    int multiplied = digit * 2;
                    if (multiplied > 9) {
                        String multipliedStr = String.valueOf(multiplied);
                        String[] splitDigits = multipliedStr.split("");
                        processedDigits[i] = Integer.parseInt(splitDigits[0]) + Integer.parseInt(splitDigits[1]);
                    } else {
                        processedDigits[i] = multiplied;
                    }
                } else {
                    processedDigits[i] = digit;
                }
            }

            int sum = Arrays.stream(processedDigits).sum();
            int calculatedChecksum = ((sum % 10) - 10) * -1;

            return calculatedChecksum == providedChecksum;

        } catch (NumberFormatException e) {
            return false;
        }
    }
}
