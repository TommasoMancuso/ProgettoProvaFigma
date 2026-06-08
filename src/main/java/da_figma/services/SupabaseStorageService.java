package da_figma.services;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SupabaseStorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket}")
    private String bucket;

    private final RestTemplate restTemplate = new RestTemplate();

    // =========================
    // UPLOAD FILE
    // =========================
    public String uploadFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File vuoto o non valido");
        }

        try {
            String fileName = System.currentTimeMillis()
                    + "_" + file.getOriginalFilename();

            // URL upload (privato)
            String uploadUrl = supabaseUrl +
                    "/storage/v1/object/" +
                    bucket + "/" + fileName;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + supabaseKey);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            HttpEntity<byte[]> request =
                    new HttpEntity<>(file.getBytes(), headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    uploadUrl,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Upload su Supabase fallito");
            }

            // URL pubblico salvato nel DB
            return supabaseUrl +
                    "/storage/v1/object/public/" +
                    bucket + "/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Errore upload file", e);
        }
    }
}