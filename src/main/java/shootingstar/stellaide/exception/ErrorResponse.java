package shootingstar.stellaide.exception;

import lombok.Data;

@Data
public class ErrorResponse {
    private String code;
    private String description;

    public ErrorResponse(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    public ErrorResponse(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
