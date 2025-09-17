package pl.sgorski.comment_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.sgorski.comment_service.config.SecurityConfig;
import pl.sgorski.comment_service.dto.CommentResponse;
import pl.sgorski.comment_service.mapper.CommentMapper;
import pl.sgorski.comment_service.model.Comment;
import pl.sgorski.comment_service.service.CommentSecurityService;
import pl.sgorski.comment_service.service.CommentService;
import pl.sgorski.common.exception.CommentNotFoundException;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc
@Import({SecurityConfig.class, CommentSecurityService.class})
@ActiveProfiles("test")
public class CommentControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder decoder;

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private CommentMapper commentMapper;

    @MockitoBean
    private CommentSecurityService securityService;

    private CommentResponse response;
    private String message;

    @BeforeEach
    void setUp() {
        message = "new comment";
        response = new CommentResponse();
        response.setContent(message);
    }

    @Test
    @WithMockUser(value = "557958b8-d1fd-4b01-b5da-781647b219cb", roles = "TECHNICIAN")
    void shouldAddComment_Status201_Technician() throws Exception {
        long ticketId = 1L;
        when(commentMapper.toComment(any(), anyLong(), any(UUID.class))).thenReturn(new Comment());
        when(commentService.saveComment(any())).thenReturn(new Comment());
        when(commentMapper.toResponse(any())).thenReturn(response);

        mockMvc.perform(post("/api/comments/{ticketId}", ticketId)
                        .contentType("application/json")
                        .content("{\"content\":\"" + message + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String resBody = result.getResponse().getContentAsString();
                    assertTrue(resBody.contains(message));
                });

        verify(commentService, times(1)).saveComment(any(Comment.class));
    }

    @Test
    @WithMockUser(value = "557958b8-d1fd-4b01-b5da-781647b219cb", roles = "ADMIN")
    void shouldAddComment_Status201_Admin() throws Exception {
        long ticketId = 1L;
        when(commentMapper.toComment(any(), anyLong(), any(UUID.class))).thenReturn(new Comment());
        when(commentService.saveComment(any())).thenReturn(new Comment());
        when(commentMapper.toResponse(any())).thenReturn(response);

        mockMvc.perform(post("/api/comments/{ticketId}", ticketId)
                        .contentType("application/json")
                        .content("{\"content\":\"" + message + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String resBody = result.getResponse().getContentAsString();
                    assertTrue(resBody.contains(message));
                });

        verify(commentService, times(1)).saveComment(any(Comment.class));
    }

    @Test
    @WithMockUser(value = "557958b8-d1fd-4b01-b5da-781647b219cb", roles = "USER")
    void shouldAddComment_Status201_Creator() throws Exception {
        long ticketId = 1L;
        when(securityService.isTicketCreator(anyLong(), anyString())).thenReturn(true);
        when(commentMapper.toComment(any(), anyLong(), any(UUID.class))).thenReturn(new Comment());
        when(commentService.saveComment(any())).thenReturn(new Comment());
        when(commentMapper.toResponse(any())).thenReturn(response);

        mockMvc.perform(post("/api/comments/{ticketId}", ticketId)
                        .contentType("application/json")
                        .content("{\"content\":\"" + message + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String resBody = result.getResponse().getContentAsString();
                    assertTrue(resBody.contains(message));
                });

        verify(commentService, times(1)).saveComment(any(Comment.class));
    }

    @Test
    @WithMockUser(value = "557958b8-d1fd-4b01-b5da-781647b219cb", roles = "USER")
    void shouldNotAddComment_Status403_NotTicketCreator() throws Exception {
        long ticketId = 1L;
        when(securityService.isTicketCreator(anyLong(), anyString())).thenReturn(false);
        when(commentMapper.toComment(any(), anyLong(), any(UUID.class))).thenReturn(new Comment());
        when(commentService.saveComment(any())).thenReturn(new Comment());
        when(commentMapper.toResponse(any())).thenReturn(response);

        mockMvc.perform(post("/api/comments/{ticketId}", ticketId)
                        .contentType("application/json")
                        .content("{\"content\":\"" + message + "\"}"))
                .andExpect(status().isForbidden());

        verify(commentService, never()).saveComment(any(Comment.class));
    }

    @Test
    @WithMockUser(value = "557958b8-d1fd-4b01-b5da-781647b219cb", roles = "ADMIN")
    void shouldNotAddComment_NullContent_Status500() throws Exception {
        long ticketId = 1L;
        when(commentMapper.toComment(any(), anyLong(), any(UUID.class))).thenReturn(new Comment());
        when(commentService.saveComment(any())).thenReturn(new Comment());
        when(commentMapper.toResponse(any())).thenReturn(response);

        mockMvc.perform(post("/api/comments/{ticketId}", ticketId)
                        .contentType("application/json")
                        .content("{\"content\":" + null + "}"))
                .andExpect(status().is5xxServerError());

        verify(commentService, never()).saveComment(any(Comment.class));
    }

    @Test
    @WithMockUser(value = "557958b8-d1fd-4b01-b5da-781647b219cb", roles = "ADMIN")
    void shouldNotAddComment_TooLongContent_Status500() throws Exception {
        long ticketId = 1L;
        when(commentMapper.toComment(any(), anyLong(), any(UUID.class))).thenReturn(new Comment());
        when(commentService.saveComment(any())).thenReturn(new Comment());
        when(commentMapper.toResponse(any())).thenReturn(response);

        mockMvc.perform(post("/api/comments/{ticketId}", ticketId)
                        .contentType("application/json")
                        .content("{\"content\":\"" + "a".repeat(1001) + "\"}"))
                .andExpect(status().is5xxServerError());

        verify(commentService, never()).saveComment(any(Comment.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteComment_Status204_Admin() throws Exception {
        long commentId = 1L;
        mockMvc.perform(delete("/api/comments/{id}", commentId))
                .andExpect(status().isNoContent());

        verify(commentService, times(1)).deleteCommentById(commentId);
    }

    @Test
    @WithMockUser(value = "557958b8-d1fd-4b01-b5da-781647b219cb", roles = "TECHNICIAN")
    void shouldNotDeleteComment_Status403_Technician() throws Exception {
        long commentId = 1L;
        when(securityService.isCommentAuthor(anyLong(), any())).thenReturn(false);

        mockMvc.perform(delete("/api/comments/{id}", commentId))
                .andExpect(status().isForbidden());

        verify(commentService, never()).deleteCommentById(commentId);
    }

    @Test
    @WithMockUser(value = "557958b8-d1fd-4b01-b5da-781647b219cb", roles = "USER")
    void shouldDeleteComment_Status204_Creator() throws Exception {
        long commentId = 1L;
        when(securityService.isCommentAuthor(anyLong(), any())).thenReturn(true);

        mockMvc.perform(delete("/api/comments/{id}", commentId))
                .andExpect(status().isNoContent());

        verify(commentService, times(1)).deleteCommentById(commentId);
    }

    @Test
    @WithMockUser(value = "557958b8-d1fd-4b01-b5da-781647b219cb", roles = "USER")
    void shouldNotDeleteComment_Status403_NotCreator() throws Exception {
        long commentId = 1L;
        when(securityService.isCommentAuthor(anyLong(), any())).thenReturn(false);

        mockMvc.perform(delete("/api/comments/{id}", commentId))
                .andExpect(status().isForbidden());

        verify(commentService, never()).deleteCommentById(commentId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldNotDeleteComment_Status404_CommentNotFound() throws Exception {
        long commentId = 1L;
        doThrow(CommentNotFoundException.class).when(commentService).deleteCommentById(anyLong());

        mockMvc.perform(delete("/api/comments/{id}", commentId))
                .andExpect(status().is4xxClientError());

        verify(commentService, times(1)).deleteCommentById(commentId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldNotDeleteComment_Status409_AlreadyDeleted() throws Exception {
        long commentId = 1L;
        doThrow(IllegalStateException.class).when(commentService).deleteCommentById(anyLong());

        mockMvc.perform(delete("/api/comments/{id}", commentId))
                .andExpect(status().isConflict());

        verify(commentService, times(1)).deleteCommentById(commentId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnComments_Status200_Admin() throws Exception {
        when(commentService.getCommentsByTicketId(anyLong())).thenReturn(Set.of(new Comment()));
        when(commentMapper.toResponse(any())).thenReturn(new CommentResponse());
        long ticketId = 1L;

        mockMvc.perform(get("/api/comments")
                        .param("ticketId", String.valueOf(ticketId)))
                .andExpect(status().isOk());

        verify(commentService, times(1)).getCommentsByTicketId(anyLong());
    }

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void shouldReturnComments_Status200_Technician() throws Exception {
        when(commentService.getCommentsByTicketId(anyLong())).thenReturn(Set.of(new Comment()));
        when(commentMapper.toResponse(any())).thenReturn(new CommentResponse());
        long ticketId = 1L;

        mockMvc.perform(get("/api/comments")
                        .param("ticketId", String.valueOf(ticketId)))
                .andExpect(status().isOk());

        verify(commentService, times(1)).getCommentsByTicketId(anyLong());
    }

    @Test
    @WithMockUser(value = "557958b8-d1fd-4b01-b5da-781647b219cb", roles = "USER")
    void shouldReturnComments_Status200_Reporter() throws Exception {
        when(securityService.isTicketCreator(anyLong(), anyString())).thenReturn(true);
        when(commentService.getCommentsByTicketId(anyLong())).thenReturn(Set.of(new Comment()));
        when(commentMapper.toResponse(any())).thenReturn(new CommentResponse());
        long ticketId = 1L;

        mockMvc.perform(get("/api/comments")
                        .param("ticketId", String.valueOf(ticketId)))
                .andExpect(status().isOk());

        verify(commentService, times(1)).getCommentsByTicketId(anyLong());
    }

    @Test
    @WithMockUser(value = "557958b8-d1fd-4b01-b5da-781647b219cb", roles = "USER")
    void shouldNotReturnComments_Status403_NotReporter() throws Exception {
        when(securityService.isTicketCreator(anyLong(), anyString())).thenReturn(false);
        long ticketId = 1L;

        mockMvc.perform(get("/api/comments")
                        .param("ticketId", String.valueOf(ticketId)))
                .andExpect(status().isForbidden());

        verify(commentService, never()).getCommentsByTicketId(anyLong());
    }
}
