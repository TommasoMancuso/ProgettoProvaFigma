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

    // CANCELLAZIONE FILE CON CANCELLAZIONE UTENTE
    public void deleteFile(String publicUrl) {

    if (publicUrl == null || publicUrl.isBlank()) {
        return;
    }

    try {
        // =========================
        // 1. Estrai path del file
        // =========================
        // esempio URL:
        // https://xyz.supabase.co/storage/v1/object/public/bucketName/folder/image.jpg

        String basePath = "/storage/v1/object/public/" + bucket + "/";

        int index = publicUrl.indexOf(basePath);

        if (index == -1) {
            throw new RuntimeException("URL Supabase non valido: " + publicUrl);
        }

        String filePath = publicUrl.substring(index + basePath.length());

        // =========================
        // 2. Costruisci URL DELETE
        // =========================
        String deleteUrl =
                supabaseUrl +
                "/storage/v1/object/" +
                bucket +
                "/" +
                filePath;

        // =========================
        // 3. Headers (SERVICE ROLE KEY)
        // =========================
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + supabaseKey);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        // =========================
        // 4. DELETE request
        // =========================
        restTemplate.exchange(
                deleteUrl,
                HttpMethod.DELETE,
                request,
                Void.class
        );

    } catch (Exception e) {
        throw new RuntimeException("Errore eliminazione file Supabase: " + e.getMessage(), e);
    }
}

}