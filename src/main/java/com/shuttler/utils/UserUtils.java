package com.shuttler.utils;

import com.shuttler.model.User;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

public class UserUtils {

    public static boolean hasAtLeastOneCommunicationChannel(final User user) {
        return BooleanUtils.isFalse(
                StringUtils.isBlank(user.getEmail())
                        && StringUtils.isBlank(user.getPhoneNumber())
        );
    }
}
