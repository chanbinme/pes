package co.pes.common.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@NoArgsConstructor(access = AccessLevel.NONE)
public class SHA512Utils {

    public static String encrypt(String password) throws NoSuchAlgorithmException {

        String output = "";
        StringBuilder stringBuilder = new StringBuilder();
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
        messageDigest.update(password.getBytes());
        byte[] messageDigestBytes = messageDigest.digest();

        for (byte temp : messageDigestBytes) {
            StringBuilder str = new StringBuilder(Integer.toHexString(temp & 0xFF));
            while (str.length() < 2) {
                str.insert(0, "0");
            }
            str = new StringBuilder(str.substring(str.length() - 2));
            stringBuilder.append(str);
        }
        output = stringBuilder.toString();

        return output;
    }
}

