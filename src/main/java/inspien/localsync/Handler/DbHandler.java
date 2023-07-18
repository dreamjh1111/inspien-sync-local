package inspien.localsync.Handler;

import inspien.localsync.Entity.WatchedFile;
import inspien.localsync.Repository.WatchedFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DbHandler {
    private static final Logger logger = LoggerFactory.getLogger(DbHandler.class);
    private WatchedFileRepository watchedFileRepository;
    @Value("${watch.directory}")
    private String watchDirectory;
    @Value("${watch.syncdirectory}")
    private String syncdirectory;

    public DbHandler(WatchedFileRepository watchedFileRepository){
        this.watchedFileRepository = watchedFileRepository;
    }

    public void saveWatchedFile(WatchedFile watchedFile) {
        watchedFileRepository.save(watchedFile);
    }

    public void deleteWatchedFile(WatchedFile watchedFile) {
        watchedFileRepository.delete(watchedFile);
    }

    public WatchedFile createWatchedFile(File file) {
        WatchedFile watchedFile = new WatchedFile();
        watchedFile.setName(file.getName());
        watchedFile.setType(file.isDirectory() ? "directory" : "file");
        watchedFile.setLocalPath(file.getAbsolutePath());  // Set the local path
        String subPath = file.getAbsolutePath().replace(watchDirectory, "");
        watchedFile.setServerPath(syncdirectory + subPath);  // Set the server path
        watchedFile.setSubPath(subPath);
        watchedFile.setDeleted(false);
        watchedFile.setChangedfile(true);
        watchedFile.setCreatedAt(LocalDateTime.now());
        try {
            if (file.isDirectory()) {
                watchedFile.setHashValue(null);
            } else {
                String hashValue = FileHashHandler.calculateHash(file.getAbsolutePath());
                watchedFile.setHashValue(hashValue);
            }
        } catch (NoSuchAlgorithmException | IOException e) {
            // 예외 처리 필요
            e.printStackTrace();
        }
        return watchedFile;
    }

    public void deleteNonexistentFiles() {
        List<WatchedFile> filesInDatabase = watchedFileRepository.findAll();
        for (WatchedFile file : filesInDatabase) {
            File localFile = new File(file.getLocalPath());
            if (!localFile.exists()) {
                file.setDeleted(true);
                saveWatchedFile(file);
                logger.info("Deleted file not found in directory: {}", file.getLocalPath());
            }
        }
    }
}
