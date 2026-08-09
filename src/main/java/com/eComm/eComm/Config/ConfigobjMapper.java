package com.eComm.eComm.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class ConfigobjMapper {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // 1. Register the module that knows how to handle LocalDateTime
        mapper.registerModule(new JavaTimeModule());

        // 2. Tell Jackson to write dates as nice Strings (e.g. "2023-10-25T10:00:00")
        // instead of ugly numeric arrays ([2023, 10, 25, 10, 0])
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return mapper;
    }
}