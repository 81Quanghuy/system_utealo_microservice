package vn.iostar.groupservice.config.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.apache.http.HttpStatus;
import vn.iostar.groupservice.dto.response.GenericResponse;
import vn.iostar.groupservice.exception.wrapper.MyFeignException;

import java.io.IOException;
import java.io.InputStream;

public class CustomErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String s, Response response) {
        GenericResponse genericResponse;
        if (response.status() == HttpStatus.SC_SERVICE_UNAVAILABLE){
            return new MyFeignException(response.request().requestTemplate().feignTarget().name() + " is unavailable");
        }
        try {
            InputStream body = response.body().asInputStream();
            ObjectMapper mapper = new ObjectMapper();
            genericResponse = mapper.readValue(body, GenericResponse.class);
        } catch (IOException e) {
            throw new MyFeignException("Request failed from: " + response.request().requestTemplate().feignTarget().name());
        }
        return new MyFeignException(genericResponse.getMessage());
    }
}
