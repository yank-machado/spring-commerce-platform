package com.marketplace.salesapi.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;

import java.io.IOException;

/**
 * Configuração para serialização e deserialização adequada de classes Page/Pageable do Spring Data
 */
@Configuration
public class JacksonConfig {

    @Bean
    @SuppressWarnings("rawtypes")
    public Module pageModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Page.class, new PageSerializer());
        return module;
    }
    
    public static class PageSerializer extends JsonSerializer<Page> {
        @Override
        @SuppressWarnings("rawtypes")
        public void serialize(Page page, JsonGenerator jsonGenerator, 
                              SerializerProvider serializers) throws IOException {
            jsonGenerator.writeStartObject();
            
            jsonGenerator.writeObjectField("content", page.getContent());
            jsonGenerator.writeNumberField("totalElements", page.getTotalElements());
            jsonGenerator.writeNumberField("totalPages", page.getTotalPages());
            jsonGenerator.writeNumberField("number", page.getNumber());
            jsonGenerator.writeNumberField("size", page.getSize());
            jsonGenerator.writeBooleanField("first", page.isFirst());
            jsonGenerator.writeBooleanField("last", page.isLast());
            jsonGenerator.writeBooleanField("empty", page.isEmpty());
            
            // Escrever diretamente os campos do Pageable em vez de serializar o objeto inteiro
            if (page.getPageable() != null && page.getPageable().isPaged()) {
                jsonGenerator.writeObjectFieldStart("pageable");
                jsonGenerator.writeNumberField("pageNumber", page.getPageable().getPageNumber());
                jsonGenerator.writeNumberField("pageSize", page.getPageable().getPageSize());
                
                // Lidar com getOffset seguramente para evitar UnsupportedOperationException
                try {
                    jsonGenerator.writeNumberField("offset", page.getPageable().getOffset());
                } catch (UnsupportedOperationException e) {
                    // Calcular manualmente se getOffset lançar exceção
                    jsonGenerator.writeNumberField("offset", 
                        (long) page.getPageable().getPageNumber() * page.getPageable().getPageSize());
                }
                
                if (page.getPageable().getSort() != null) {
                    jsonGenerator.writeBooleanField("sorted", page.getPageable().getSort().isSorted());
                } else {
                    jsonGenerator.writeBooleanField("sorted", false);
                }
                
                jsonGenerator.writeEndObject();
            } else {
                jsonGenerator.writeObjectFieldStart("pageable");
                jsonGenerator.writeBooleanField("paged", false);
                jsonGenerator.writeNumberField("pageNumber", 0);
                jsonGenerator.writeNumberField("pageSize", page.getSize());
                jsonGenerator.writeNumberField("offset", 0);
                jsonGenerator.writeBooleanField("sorted", false);
                jsonGenerator.writeEndObject();
            }
            
            jsonGenerator.writeEndObject();
        }
    }
} 