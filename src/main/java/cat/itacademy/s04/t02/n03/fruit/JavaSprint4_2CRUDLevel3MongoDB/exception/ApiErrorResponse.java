package cat.itacademy.s04.t02.n03.fruit.JavaSprint4_2CRUDLevel3MongoDB.exception;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ApiErrorResponse(int status, String message,
                               @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                            LocalDateTime localDateTime) {
}
