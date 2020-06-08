package com.alpiq.autocomplete.controller;

import com.alpiq.autocomplete.trie.CityLocation;
import com.alpiq.autocomplete.trie.Result;
import com.alpiq.autocomplete.trie.Trie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(SuggestionController.class)
@AutoConfigureRestDocs(outputDir = "target/snippets")
public class SuggestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Trie<CityLocation> trieMock;

    @Test
    public void contextLoads() {
        assertThat(trieMock).isNotNull();
    }

    @Test
    public void testWarWordWithDefaultPagination() throws Exception {
        Mockito.when(trieMock.searchByPrefix("war")).thenReturn(List.of(new Result<>(new CityLocation("warsaw", "pl", 100000), 1.0)));
        mockMvc.perform(MockMvcRequestBuilders
                .get("/suggestions?q=war"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.suggestionList[0].name").value("warsaw PL"))
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.suggestionList[0].score").value(1.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.size").value(20))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.totalElements").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.totalPages").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.number").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.self.href").value("http://localhost:8080/suggestions"));


    }

    @Test
    public void testMultipleWordsPagination() throws Exception {
        Mockito.when(trieMock.searchByPrefix("war")).thenReturn(
                List.of(
                        new Result<>(new CityLocation("warsaw", "pl", 100000), 1.0),
                        new Result<>(new CityLocation("warsaw2", "pl", 10000), 0.8),
                        new Result<>(new CityLocation("warsaw3", "pl", 1000), 0.6)
                )
        );
        mockMvc.perform(RestDocumentationRequestBuilders
                .get("/suggestions?q=war&page=0&size=2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.suggestionList[0].name").value("warsaw PL"))
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.suggestionList[0].score").value(1.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.suggestionList[1].name").value("warsaw2 PL"))
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.suggestionList[1].score").value(0.8))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.size").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.totalElements").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.totalPages").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.number").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.self.href").value("http://localhost:8080/suggestions"))
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.first.href").value("http://localhost:8080/suggestions?page=0&size=2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.next.href").value("http://localhost:8080/suggestions?page=1&size=2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.last.href").value("http://localhost:8080/suggestions?page=1&size=2"))
                .andDo(document("suggestion2",
                        requestParameters(parameterWithName("q").description("Word to search for"), parameterWithName("page").description("Requested page"), parameterWithName("size").description("Requested size per page"))
                ));


    }
}