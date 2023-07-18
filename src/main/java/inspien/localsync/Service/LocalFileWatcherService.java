package inspien.localsync.Service;

import inspien.localsync.Entity.WatchedFile;
import inspien.localsync.Handler.DbHandler;
import inspien.localsync.Repository.WatchedFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class LocalFileWatcherService {

    private static final Logger logger = LoggerFactory.getLogger(LocalFileWatcherService.class);


    @Value("${server.url}")
    private String serverUrl;
    @Value("${watch.directory}")
    private String watchDirectory;

    private WatchedFileRepository watchedFileRepository;
    private DbHandler dbHandler;

    public LocalFileWatcherService(WatchedFileRepository watchedFileRepository,
                                   DbHandler dbHandler){
        this.watchedFileRepository = watchedFileRepository;
        this.dbHandler = dbHandler;
    }

    private void scanAndSaveFiles(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isHidden() || file.getName().startsWith(".")) {
                        continue;
                    }
                    WatchedFile watchedFile = dbHandler.createWatchedFile(file);

                    if (!watchedFileRepository.existsByNameAndTypeAndLocalPathAndServerPathAndSubPath
                            (
                                    watchedFile.getName(),
                                    watchedFile.getType(),
                                    watchedFile.getLocalPath(),
                                    watchedFile.getServerPath(),
                                    watchedFile.getSubPath()
                                    )) {
                        dbHandler.saveWatchedFile(watchedFile);
                    }else {
                        // 파일이 이미 존재하는 경우 이전 해시값과 현재 해시값 비교하여 변동 여부 확인
                        WatchedFile existingFile = watchedFileRepository.findByLocalPath(watchedFile.getLocalPath());
                        if (existingFile != null) {
                            String previousHashValue = existingFile.getHashValue();
                            if (previousHashValue != null && !previousHashValue.equals(watchedFile.getHashValue())) {
                                watchedFile.setChangedfile(true);
                                logger.info("Hash value changed for file: {}", watchedFile.getLocalPath());
                                dbHandler.deleteWatchedFile(existingFile);
                                dbHandler.saveWatchedFile(watchedFile);
                            }
                        }
                    }
                    if (file.isDirectory()) {
                        scanAndSaveFiles(file); // 하위 디렉토리도 탐색
                    }
                }
            }
        }
    }

    public void isFileUploadByUpdateFile(){
        List<WatchedFile> watchedFiles = watchedFileRepository.findByIsChangedfileTrueAndType("file");
        for (WatchedFile watchedFile : watchedFiles) {
            String localPath = watchedFile.getLocalPath();
            String syncdirectory = watchedFile.getServerPath();

            int lastIndex = syncdirectory.lastIndexOf('/');
            String directoryPath = syncdirectory.substring(0, lastIndex);

            FileUploaderService.uploadFile(serverUrl, localPath, directoryPath);
        }
    }

    // 스케줄러 설정
    @Scheduled(fixedRate = 5000) // 5초마다 실행
    public void runDbSaveService() {
        scanAndSaveFiles(new File(watchDirectory));
        logger.info("Running DbSaveService");
        dbHandler.deleteNonexistentFiles();
        isFileUploadByUpdateFile();
        logger.info("Running UploadCheker");
    }
}
