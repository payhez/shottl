package com.shuttler.utils;

import com.shuttler.model.User;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

public class UserUtils {

    public static boolean hasAtLeastOneCommunicationChannel(final User user) {
        return BooleanUtils.isFalse(
                StringUtils.isBlank(user.getEmail())
                        && StringUtils.isBlank(user.getPhoneNumber())
        );
    }

    public static boolean isUUID(final String stringToBeValidated) {
        if (stringToBeValidated == null) {
            return false;
        }

        try {
            UUID uuid = UUID.fromString(stringToBeValidated);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
