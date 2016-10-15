package pro.zackpollard.telegrambot.api.internal.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Zack Pollard
 */
public class FileManager {

    private static MessageDigest md5Digest;
    private static File tmpDirectory;
    private final Map<String, String> checksumIDs;
    private final Map<String, String> urlCache;

    public FileManager() {

        this.checksumIDs = new HashMap<>();
        this.urlCache = new HashMap<>();

        if (md5Digest == null) {

            try {
                md5Digest = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getFileChecksum(File file) throws IOException {

        FileInputStream fis = new FileInputStream(file);

        byte[] byteArray = new byte[1024];
        int bytesCount;

        while ((bytesCount = fis.read(byteArray)) != -1) {

            md5Digest.update(byteArray, 0, bytesCount);
        }

        fis.close();

        byte[] bytes = md5Digest.digest();

        StringBuilder sb = new StringBuilder();

        for (byte aByte : bytes) {

            sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

    public String getFileID(File file) {

        String checksum = null;

        try {
            checksum = getFileChecksum(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String fileID = checksumIDs.get(checksum);

        if (fileID != null) {

            return fileID;
        }

        return null;
    }

    public String cacheFileID(File file, String fileID) {

        String checksum = null;

        try {
            checksum = getFileChecksum(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (checksum != null) {

            checksumIDs.put(checksum, fileID);
        }

        return checksum;
    }

    public File getFile(URL url) {

        String path = this.urlCache.get(url.getPath());

        return path != null ? new File(path) : null;
    }

    public void cacheUrl(URL url, File file) {
        this.urlCache.put(url.getPath(), file.getAbsolutePath());
    }

    public static File getTemporaryFolder() {
        return tmpDirectory;
    }

    static {
        tmpDirectory = new File(System.getProperty("java.io.tmpdir"), "jtelegrambot-" + System.currentTimeMillis());
        tmpDirectory.mkdirs();
        tmpDirectory.deleteOnExit();
    }
}
