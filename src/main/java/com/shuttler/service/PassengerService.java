package com.shuttler.service;

import com.shuttler.dao.PassengerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PassengerService {

    @Autowired
    PassengerRepository passengerRepository;

}
