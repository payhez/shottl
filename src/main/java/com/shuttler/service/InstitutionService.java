package com.shuttler.service;

import com.shuttler.dao.InstitutionRepository;
import com.shuttler.model.Institution;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class InstitutionService {

    @Autowired
    private InstitutionRepository institutionRepository;

    public Institution addInstitution(final Institution institution) {
        String invitationCode = UUID.randomUUID().toString();
        institution.setInvitationCode(invitationCode);
        institutionRepository.save(institution);
        return institution;
    }

}
