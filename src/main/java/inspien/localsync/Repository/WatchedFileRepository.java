package inspien.localsync.Repository;

import inspien.localsync.Entity.WatchedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.nio.file.Path;
import java.util.List;

public interface WatchedFileRepository extends JpaRepository<WatchedFile, Long> {


    WatchedFile findByLocalPath(String localPath);
    @Query("SELECT w.subPath FROM WatchedFile w WHERE w.name = :name")
    String findSubPathByName(@Param("name") String name);
    boolean existsByNameAndTypeAndLocalPathAndServerPathAndSubPath(String name, String type, String localPath, String subPath, String serverPath);
    List<WatchedFile> findByIsChangedfileTrueAndType(String file);

    // 필요한 추가 메서드 정의
}
