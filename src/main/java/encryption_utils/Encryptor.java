package encryption_utils;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimplePBEConfig;
import org.jasypt.salt.StringFixedSaltGenerator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class Encryptor {

    public static void main(String[] args) {
        try {
            InputStream configIn = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
            Properties congigProperties = new Properties();
            congigProperties.load(configIn);
            configIn.close();

            StringEncryptor encryptor = getStringEncryptor(
                    Integer.parseInt(congigProperties.getProperty("encryptor.pool_size")),
                    System.getenv(congigProperties.getProperty("env.encryption_pw_name")),
                    System.getenv(congigProperties.getProperty("env.encryption_salt_name")));

            InputStream contextIn = Thread.currentThread().getContextClassLoader().getResourceAsStream("context.properties");
            Properties contextProperties = new Properties();
            contextProperties.load(contextIn);
            contextIn.close();

            Properties outProperties = new Properties();
            for (Map.Entry<Object, Object> entry : contextProperties.entrySet()) {
                String value = (String) entry.getValue();
                if (value.startsWith("ENC-")) {
                    String encryptedValue = "DEC-" + encryptor.encrypt(value.split("-", 2)[1]);
                    String key = (String) entry.getKey();
                    outProperties.setProperty(key, encryptedValue);
                }
            }

            FileOutputStream out = new FileOutputStream("src/main/resources/output.properties");
            outProperties.store(out, "Copy all the encrypted values into your context.properties and delete the output.properties");
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static PooledPBEStringEncryptor getStringEncryptor(int poolSize, String password, String salt) {
        SimplePBEConfig config = new SimplePBEConfig();
        config.setPoolSize(poolSize);
        config.setPassword(password);
        config.setSaltGenerator(new StringFixedSaltGenerator(salt));

        PooledPBEStringEncryptor stringEncryptor = new PooledPBEStringEncryptor();
        stringEncryptor.setConfig(config);
        stringEncryptor.initialize();

        return stringEncryptor;
    }
}
