package com.shuttler.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Document("driver")
@NoArgsConstructor
public class Driver extends User {
    private String driverLicenceId;
}
