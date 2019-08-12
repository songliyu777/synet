package com.synet.net.webclient;

import com.synet.net.exception.ServiceException;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springside.modules.utils.mapper.JsonMapper;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.nio.ByteBuffer;
import java.util.Objects;

@Slf4j
public class LightningWebClient {

    protected WebClient webClient;

    public LightningWebClient() {
        this(WebClient.builder());
    }

    public LightningWebClient(WebClient.Builder builder) {
        TcpClient tcpClient = TcpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(10))
                            .addHandlerLast(new WriteTimeoutHandler(10));
                });
        webClient = builder.clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient))).build();
    }

    public LightningWebClient(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * get请求
     *
     * @param uri          请求路径
     * @param bodyType     响应body类型
     * @param uriVariables query参数
     * @param <T>          类型
     * @return body
     */
    public <T> Mono<T> get(String uri, Class<T> bodyType, Object... uriVariables) {
        return this.webClient.get().uri(uri, uriVariables)
                .retrieve()
                .onStatus(this::isError, this::errorHandle)
                .bodyToMono(bodyType);
    }

    public <T> Mono<T> getJson(String uri, Class<T> bodyType, Object... uriVariables) {
        return this.webClient.get()
                .uri(uri, uriVariables)
                .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .onStatus(this::isError, this::errorHandle)
                .bodyToMono(bodyType);
    }

    /**
     * post请求
     *
     * @param uri      请求路径
     * @param body     请求数据
     * @param bodyType 响应body类型
     * @param <T>      类型
     * @return body
     */
    public <T> Mono<T> postForm(String uri, MultiValueMap<String, String> body, Class<T> bodyType) {
        return webClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(body))
                .retrieve()
                .onStatus(this::isError, this::errorHandle)
                .bodyToMono(bodyType);
    }

    public Mono<ByteBuffer> postProtobuf(String uri, ByteBuffer buffer){
        return webClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(BodyInserters.fromObject(buffer))
                .retrieve()
                .onStatus(this::isError, this::errorHandle)
                .bodyToMono(ByteBuffer.class);
    }

    /**
     * post请求
     *
     * @param uri      请求路径
     * @param body     请求数据
     * @param bodyType 响应body类型
     * @param <T>      类型
     * @return body
     */
    public <T> Mono<T> postJson(String uri, Object body, Class<T> bodyType) {
        return webClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(body))
                .retrieve()
                .onStatus(this::isError, this::errorHandle)
                .bodyToMono(bodyType);
    }

    /**
     * 是否错误
     *
     * @param httpStatus http状态码
     * @return 是或否
     */
    public boolean isError(HttpStatus httpStatus) {
        return !httpStatus.is2xxSuccessful();
    }

    Mono<String> CreateDefaultResult(HttpStatus status) {
        Result result = new Result();
        result.setCode(Long.valueOf(status.value()));
        result.setMessage(status.toString());
        return Mono.just(JsonMapper.INSTANCE.toJson(result));
    }

    /**
     * 错误异常处理
     *
     * @param clientResponse 响应
     * @return 异常
     */
    public Mono<? extends Throwable> errorHandle(ClientResponse clientResponse) {
        return clientResponse.bodyToMono(String.class)
                .switchIfEmpty(CreateDefaultResult(clientResponse.statusCode()))
                .map(errorStr -> {
                    Result result = JsonMapper.INSTANCE.fromJson(errorStr, Result.class);
                    if (Objects.isNull(result.getCode())) {
                        log.error(errorStr);
                        result.setCode(0L);
                        result.setMessage("unknown error parse from webclient");
                    }
                    return result;
                })
                .flatMap(result -> Mono.error(ServiceException.build(result.getCode(), result.getMessage())));
    }
}
