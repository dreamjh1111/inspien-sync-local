package inspien.localsync.Handler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 파일을 해쉬 암호화 시키는 Service
 */
public class FileHashHandler {

    public static String calculateHash(String filePath) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(Files.readAllBytes(Paths.get(filePath)));

        StringBuilder hashBuilder = new StringBuilder();
        for (byte hashByte : hashBytes) {
            String hex = Integer.toHexString(0xff & hashByte);
            if (hex.length() == 1) {
                hashBuilder.append('0');
            }
            hashBuilder.append(hex);
        }

        return hashBuilder.toString();
    }
}
