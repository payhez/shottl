package com.shuttler.service;

import com.shuttler.exception.KeycloakException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class ServiceHelper {

    protected void handleUserSignupError(final Throwable ex) {
        log.error("User could not be added due to: ", ex);
        if (ex instanceof DuplicateKeyException) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The communication channel already exists!");
        } else if (ex instanceof KeycloakException kce) {
            throw new ResponseStatusException(HttpStatus.valueOf(kce.getHttpStatus()), kce.getMessage());
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error!", ex);
    }

}
