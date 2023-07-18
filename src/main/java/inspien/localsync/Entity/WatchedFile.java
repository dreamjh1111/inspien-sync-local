package inspien.localsync.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class WatchedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;
    private String localPath;
    private String serverPath;
    private String subPath;
    private LocalDateTime createdAt;
    private boolean isDeleted;
    private String hashValue;
    private boolean isChangedfile;


    // 생성자, getter, setter, toString 등의 필요한 메서드 추가
    public String getServerPath() {
        return serverPath;
    }

    public boolean isDeleted() {
        return isDeleted;
    }
    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
    public String getHashValue() {
        return hashValue;
    }
    public void setHashValue(String hashValue) {
        this.hashValue = hashValue;
    }
    public boolean isChangedfile() {
        return isChangedfile;
    }
    public void setChangedfile(boolean changedfile){
        isChangedfile = changedfile;
    }
}