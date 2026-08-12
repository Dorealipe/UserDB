import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public class EncryptionManager {
	public static SecretKey generateKey()
	{
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(256);
			return keyGenerator.generateKey();
		} catch (Exception e) {
			System.err.println(e);
			return null;
		}
	}

	public static Cipher generateCipher() {
		try {
			return Cipher.getInstance("AES");
		} catch (Exception e) {
			return null;
		}
	}
	public static byte[] encrypt(String plainText, SecretKey key, Cipher cipher) {
			try {
				byte[] text = plainText.getBytes(StandardCharsets.UTF_8);

				cipher.init(Cipher.ENCRYPT_MODE,key);
				return cipher.doFinal(text);
			} catch (Exception e) {
				System.err.println(e);
				return null;
			}
	}
	public static String decrypt(byte[] encrypted, SecretKey key, Cipher cipher) {
		try {

			cipher.init(Cipher.DECRYPT_MODE,key);
			byte[] bytesDecrypted = cipher.doFinal(encrypted);
			return new String(bytesDecrypted);
		} catch (Exception e) {
			System.err.println(e);
			return null;
		}
	}

}
