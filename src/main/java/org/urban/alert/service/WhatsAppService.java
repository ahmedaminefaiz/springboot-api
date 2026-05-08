package org.urban.alert.service;

/**
 * Contract for sending messages via WhatsApp.
 *
 * <p>Abstracts the underlying WhatsApp delivery provider so that callers
 * remain fully decoupled from any specific third-party implementation.
 */
public interface WhatsAppService {

    /**
     * Sends an OTP verification code to the specified phone number via WhatsApp.
     *
     * @param recipientPhone the recipient's phone number in international format (e.g. {@code +212XXXXXXXXX})
     * @param code           the OTP code to include in the message body
     */
    void sendOtp(String recipientPhone, String code);
}