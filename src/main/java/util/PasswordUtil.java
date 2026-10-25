package util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/** Hash y verificación de contraseñas mediante PBKDF2-HMAC-SHA256. */
public final class PasswordUtil {

    private static final String PREFIJO = "pbkdf2_sha256";
    private static final int ITERACIONES = 120_000;
    private static final int LONGITUD_SAL = 16;
    private static final int LONGITUD_HASH_BITS = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {
    }

    public static String hash(String contrasena) {
        if (contrasena == null || contrasena.isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }
        byte[] sal = new byte[LONGITUD_SAL];
        RANDOM.nextBytes(sal);
        byte[] hash = derivar(contrasena.toCharArray(), sal, ITERACIONES);
        return PREFIJO + "$" + ITERACIONES + "$"
                + Base64.getEncoder().encodeToString(sal) + "$"
                + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verificar(String contrasena, String almacenada) {
        if (contrasena == null || almacenada == null) {
            return false;
        }

        if (!esHash(almacenada)) {
            // Compatibilidad temporal con bases antiguas; se migra después del login.
            return MessageDigest.isEqual(
                    contrasena.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                    almacenada.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        }

        String[] partes = almacenada.split("\\$", -1);
        if (partes.length != 4) {
            return false;
        }

        try {
            int iteraciones = Integer.parseInt(partes[1]);
            byte[] sal = Base64.getDecoder().decode(partes[2]);
            byte[] esperado = Base64.getDecoder().decode(partes[3]);
            byte[] obtenido = derivar(contrasena.toCharArray(), sal, iteraciones);
            return MessageDigest.isEqual(esperado, obtenido);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean esHash(String valor) {
        return valor != null && valor.startsWith(PREFIJO + "$");
    }

    private static byte[] derivar(char[] contrasena, byte[] sal, int iteraciones) {
        PBEKeySpec spec = new PBEKeySpec(contrasena, sal, iteraciones, LONGITUD_HASH_BITS);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec)
                    .getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No se pudo proteger la contraseña.", e);
        } finally {
            spec.clearPassword();
            java.util.Arrays.fill(contrasena, '\0');
        }
    }
}
