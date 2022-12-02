package com.payhez.shuttler.user.kafka;

import org.springframework.stereotype.Component;
import org.springframework.kafka.annotation.KafkaListener;

@Component
public class KafkaListeners {

    @KafkaListener(topics = "createUser", groupId = "management")
    public void userCreation(String data){
        System.out.println("HAHAHAHHHAHHA");
    }
}
