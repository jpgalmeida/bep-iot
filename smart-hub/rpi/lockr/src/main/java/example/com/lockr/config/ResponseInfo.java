package example.com.lockr.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

@JsonInclude(Include.NON_NULL)
public class ResponseInfo extends JsonObject {

	public final int status;
	public final String location;
	@JsonUnwrapped
	@JsonInclude(Include.NON_NULL)
	public final Object responseMessage;

	public ResponseInfo(int status, String pathinfo, Object responseMessage) {
		this.status = status;
		this.location = pathinfo;
		this.responseMessage = responseMessage;
	}
}
