package com.example.webapp.test.Controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.webapp.Controller.A01Controller;

@ExtendWith(SpringExtension.class)
public class A01ControllerTest {
	
	@Autowired
    private static MockMvc mockMvc;
    @MockBean
    private static A01Controller a01c;

    @BeforeAll
    static void init () {
        mockMvc = MockMvcBuilders.standaloneSetup(A01Controller.class).build();
    }

    @Test
    void testController() throws Exception {
    	when(a01c.getStatus()).thenReturn(new ResponseEntity<>("Successfull",HttpStatus.OK));
        mockMvc.perform(get("/healthz")).andDo(print()).andExpect(status().isOk())
        .andExpect(content().string(containsString("Successfull")));
    }
    
    @Test
    void shouldGive405ForPost() throws Exception {
    	when(a01c.getStatus()).thenReturn(new ResponseEntity<>("Successfull",HttpStatus.OK));
        mockMvc.perform(post("/healthz")).andDo(print()).andExpect(status().isMethodNotAllowed());
    }

}
