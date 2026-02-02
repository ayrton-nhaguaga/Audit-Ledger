package com.ayrtonnhaguaga.auditledger.crypto;


import com.ayrtonnhaguaga.auditledger.exception.ApiException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class CryptoService {

    static {
        // garante provider do BC (noo atrapalha mesmo se ja existir)
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    public String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return toHex(digest);
        } catch (Exception e) {
            throw ApiException.badRequest("SHA-256 error");
        }
    }

    /**
     * Verifica assinatura Ed25519.
     * publicKeyB64: chave pública raw(32) em Base64 OU X.509? (aceitamos os 2 modos)
     */
    public void verifyEd25519(String publicKeyB64, String signatureB64, String message) {
        try {
            byte[] sig = Base64.getDecoder().decode(signatureB64);
            PublicKey publicKey = parseEd25519PublicKey(publicKeyB64);

            Signature verifier = Signature.getInstance("Ed25519");
            verifier.initVerify(publicKey);
            verifier.update(message.getBytes(StandardCharsets.UTF_8));

            boolean ok = verifier.verify(sig);
            if (!ok) throw ApiException.badRequest("Assinatura inválida");
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw ApiException.badRequest("Falha ao verificar assinatura");
        }
    }

    /**
     * Aceita 2 formatos:
     * 1) raw 32 bytes (Base64) -> convertemos para SubjectPublicKeyInfo (X.509)
     * 2) já X.509 encoded (Base64) -> tentamos direto
     */
    private PublicKey parseEd25519PublicKey(String publicKeyB64) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(publicKeyB64);

        // Se for raw 32 bytes, convertendo para X.509 SubjectPublicKeyInfo
        if (decoded.length == 32) {
            // SubjectPublicKeyInfo para Ed25519:
            // SEQUENCE(
            //   SEQUENCE(OID 1.3.101.112),
            //   BIT STRING (0x00 + rawKey)
            // )
            // Header fixo (12 bytes) para Ed25519 + BIT STRING
            byte[] x509Prefix = new byte[] {
                    0x30, 0x2a,             // SEQUENCE len 42
                    0x30, 0x05,             // SEQUENCE len 5
                    0x06, 0x03, 0x2b, 0x65, 0x70, // OID 1.3.101.112 (Ed25519)
                    0x03, 0x21, 0x00        // BIT STRING len 33, 0 unused bits
            };

            byte[] x509 = new byte[x509Prefix.length + decoded.length];
            System.arraycopy(x509Prefix, 0, x509, 0, x509Prefix.length);
            System.arraycopy(decoded, 0, x509, x509Prefix.length, decoded.length);

            KeyFactory kf = KeyFactory.getInstance("Ed25519");
            return kf.generatePublic(new X509EncodedKeySpec(x509));
        }

        // Se já vier em X.509 (mais longo), tenta direto
        KeyFactory kf = KeyFactory.getInstance("Ed25519");
        return kf.generatePublic(new X509EncodedKeySpec(decoded));
    }

    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            String s = Integer.toHexString(b & 0xff);
            if (s.length() == 1) sb.append('0');
            sb.append(s);
        }
        return sb.toString();
    }
}