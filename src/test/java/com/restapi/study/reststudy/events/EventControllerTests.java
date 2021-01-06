package com.restapi.study.reststudy.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void createEvent() throws Exception {
        EventDto event = EventDto.builder()
                //입력값 제한하는 방법
                .name("Spring")
                .description("Rest api development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2021,1,2,11,16))
                .closeEnrollmentDateTime(LocalDateTime.of(2020,1,2,13,14))
                .beginEventDateTime(LocalDateTime.of(2020,1,2,12,12))
                .endEventDateTime(LocalDateTime.of(2020,1,1,12,12))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .build();

        mockMvc.perform(post("/api/events") // 요청을 제대로 만들어준다.
                    .contentType(MediaType.APPLICATION_JSON_UTF8) // 본문에 JSON을 담아서 보내준다고 알려줌
                    .accept(MediaTypes.HAL_JSON) // ACCEPT 헤더를 통해 알려줌 (하이퍼링크 어플리케이션 랭귀지) 스펙 정보를 알려주겠다.
                                                    //accept 헤더를 보내기 어려운 경우 확장자 형태로 알려줄 수 있다. api.json, api.xml
                    .content(objectMapper.writeValueAsString(event))
                )
                .andDo(print())
                .andExpect(status().isCreated()) // status().is(201) 같은 문법
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("free").value(Matchers.not(true)))

        ;
    }

    @Test
    public void createEvent_Bad_Request() throws Exception {
        Event event = Event.builder()
                //입력값 제한하는 방법
                .id(100)
                .name("Spring")
                .description("Rest api development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2021,1,2,11,16))
                .closeEnrollmentDateTime(LocalDateTime.of(2020,1,2,13,14))
                .beginEventDateTime(LocalDateTime.of(2020,1,2,12,12))
                .endEventDateTime(LocalDateTime.of(2020,1,1,12,12))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남")
                .free(true)
                .offline(false)
                .build();

        mockMvc.perform(post("/api/events") // 요청을 제대로 만들어준다.
                .contentType(MediaType.APPLICATION_JSON_UTF8) // 본문에 JSON을 담아서 보내준다고 알려줌
                .accept(MediaTypes.HAL_JSON) // ACCEPT 헤더를 통해 알려줌 (하이퍼링크 어플리케이션 랭귀지) 스펙 정보를 알려주겠다.
                //accept 헤더를 보내기 어려운 경우 확장자 형태로 알려줄 수 있다. api.json, api.xml
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createEvent_Bad_Requset_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events"))
                .andExpect(status().isbadRequest());
    }
}
