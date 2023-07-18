package inspien.localsync.Service;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;

@Service
public class FileUploaderService {

    public static void uploadFile(String url, String filePath, String syncdirectory) {
        File fileToUpload = new File(filePath); // 업로드할 파일 경로

        RestTemplate restTemplate = new RestTemplate();

        // 파일을 MultiPart로 전송하기 위한 요청 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(fileToUpload));
        body.add("filepath", syncdirectory);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 파일 업로드 요청 보내기
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            System.out.println("File uploaded successfully!");
        } else {
            System.out.println("File upload failed!");
        }
    }
}
