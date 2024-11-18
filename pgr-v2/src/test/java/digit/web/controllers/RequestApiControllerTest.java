package digit.web.controllers;

import digit.TestConfiguration;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * API tests for RequestApiController
 */
@Ignore
@RunWith(SpringRunner.class)
@WebMvcTest(RequestApiController.class)
@Import(TestConfiguration.class)
public class RequestApiControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void requestCountPostSuccess() throws Exception {
		mockMvc.perform(post("/request/_count").contentType(MediaType
						.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk());
	}

	@Test
	public void requestCountPostFailure() throws Exception {
		mockMvc.perform(post("/request/_count").contentType(MediaType
						.APPLICATION_JSON_UTF8))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void requestCreatePostSuccess() throws Exception {
		mockMvc.perform(post("/request/_create").contentType(MediaType
						.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk());
	}

	@Test
	public void requestCreatePostFailure() throws Exception {
		mockMvc.perform(post("/request/_create").contentType(MediaType
						.APPLICATION_JSON_UTF8))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void requestSearchPostSuccess() throws Exception {
		mockMvc.perform(post("/request/_search").contentType(MediaType
						.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk());
	}

	@Test
	public void requestSearchPostFailure() throws Exception {
		mockMvc.perform(post("/request/_search").contentType(MediaType
						.APPLICATION_JSON_UTF8))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void requestUpdatePostSuccess() throws Exception {
		mockMvc.perform(post("/request/_update").contentType(MediaType
						.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk());
	}

	@Test
	public void requestUpdatePostFailure() throws Exception {
		mockMvc.perform(post("/request/_update").contentType(MediaType
						.APPLICATION_JSON_UTF8))
				.andExpect(status().isBadRequest());
	}

}
