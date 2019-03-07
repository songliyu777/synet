package com.synet.server.logic.configuration;

import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;

@Configuration
public class ProtoFeignConfiguration {
    //Autowire the message converters.
    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    //add the protobuf http message converter
    @Bean
    ProtobufHttpMessageConverter protobufHttpMessageConverter() {

        return new ProtobufHttpMessageConverter();
    }

    //override the encoder
//    @Bean
//    public Encoder springEncoder() {
//        return new SpringEncoder(this.messageConverters);
//    }
//
//    //override the encoder
//    @Bean
//    public Decoder springDecoder() {
//        return new ResponseEntityDecoder(new SpringDecoder(this.messageConverters));
//    }
}
