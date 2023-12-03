package ge.itstep.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class ServiceResponse<T> {
    @Getter
    @Setter
    private T data;
    @Getter
    @Setter
    private String message;
    @Getter
    @Setter
    private boolean success = true;
}
