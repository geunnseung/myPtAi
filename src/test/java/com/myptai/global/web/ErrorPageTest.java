package com.myptai.global.web;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.RequestDispatcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ErrorPageTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 없는_페이지는_전용_404_화면을_보여준다() throws Exception {
        mockMvc.perform(get("/error")
                        .accept(MediaType.TEXT_HTML)
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 404)
                        .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, "/unknown"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("<title>페이지를 찾을 수 없습니다 | My PT AI</title>")))
                .andExpect(content().string(containsString("페이지를 찾을 수 없습니다.")));
    }

    @Test
    void 서버_오류는_전용_500_화면을_보여준다() throws Exception {
        mockMvc.perform(get("/error")
                        .accept(MediaType.TEXT_HTML)
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 500)
                        .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, "/profile"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("<title>서버 오류 | My PT AI</title>")))
                .andExpect(content().string(containsString("요청을 처리하지 못했습니다.")));
    }
}
