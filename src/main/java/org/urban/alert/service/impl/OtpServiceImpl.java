package org.urban.alert.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.urban.alert.entity.PhoneVerification;
import org.urban.alert.repository.PhoneVerificationRepository;
import org.urban.alert.service.OtpService;
import org.urban.alert.service.WhatsAppService;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OtpServiceImpl implements OtpService {

    @Autowired
    private PhoneVerificationRepository phoneVerificationRepository;

    @Autowired
    private WhatsAppService whatsAppService;

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional
    public void sendOtp(String phone) {
        phoneVerificationRepository.invalidatePreviousCodes(phone);

        String code = String.format("%06d", secureRandom.nextInt(1_000_000));
        PhoneVerification verification = new PhoneVerification(phone, code,
                LocalDateTime.now().plusMinutes(15));
        phoneVerificationRepository.save(verification);

        whatsAppService.sendOtp(phone, code);
    }

    @Override
    @Transactional
    public boolean verifyCode(String phone, String code) {
        Optional<PhoneVerification> result = phoneVerificationRepository

                .findTopByPhoneAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(phone,
                        LocalDateTime.now());

        if (result.isEmpty() || !result.get().getCode().equals(code)) {
            return false;
        }

        result.get().setUsed(true);
        phoneVerificationRepository.save(result.get());
        return true;
    }
}