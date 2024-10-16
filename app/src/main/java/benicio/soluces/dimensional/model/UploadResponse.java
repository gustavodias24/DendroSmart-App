package benicio.soluces.dimensional.model;

public class UploadResponse {
    private String message;
    private String file_id;

    // Getters e Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFileId() {
        return file_id;
    }

    public void setFileId(String file_id) {
        this.file_id = file_id;
    }
}
