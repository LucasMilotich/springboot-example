package rakuten.clients.fixer;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.HashMap;

public class FixerConvertResponse {

    @JsonProperty("success")
    Boolean success;
    @JsonProperty("result")
    BigDecimal result;
    @JsonProperty("error")
    HashMap error;

    public FixerConvertResponse(Boolean success, BigDecimal result, HashMap error) {
        this.success = success;
        this.result = result;
        this.error = error;
    }

    public FixerConvertResponse() {
    }
}
