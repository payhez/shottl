package com.shottl.utils;

import com.shottl.model.User;
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

    public static boolean isUUID(final String s) {
        try {
            if (s == null) return false;
            UUID.fromString(s);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
