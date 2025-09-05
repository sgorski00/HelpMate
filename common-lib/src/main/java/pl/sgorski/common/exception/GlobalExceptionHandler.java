package pl.sgorski.common.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotCompatibleRoleException.class)
    public ProblemDetail handleNotCompatibleRoleException(NotCompatibleRoleException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatusCode.valueOf(409),
                "User's role is not sufficient: " + ex.getMessage()
        );
        problemDetail.setTitle("Role Compatibility Error");
        return problemDetail;
    }

    @ExceptionHandler(IllegalStatusChangeException.class)
    public ProblemDetail handleIllegalStatusChangeException(IllegalStatusChangeException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatusCode.valueOf(409),
                "This ticket can't be changed: " + ex.getMessage()
        );
        problemDetail.setTitle("Ticket Update Error");
        return problemDetail;
    }

    @ExceptionHandler(TicketNotFoundException.class)
    public ProblemDetail handleTicketNotFoundException(TicketNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatusCode.valueOf(404),
                "Ticket not found: " + ex.getMessage()
        );
        problemDetail.setTitle("Ticket Not Found");
        return problemDetail;
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFoundException(UserNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatusCode.valueOf(404),
                "User not found: " + ex.getMessage()
        );
        problemDetail.setTitle("User Not Found");
        return problemDetail;
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ProblemDetail handleCommentNotFoundException(CommentNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatusCode.valueOf(404),
                "Comment not found: " + ex.getMessage()
        );
        problemDetail.setTitle("Comment Not Found");
        return problemDetail;
    }

    @ExceptionHandler(JwtException.class)
    public ProblemDetail handleJwtException(JwtException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatusCode.valueOf(409),
                "Invalid Jwt: " + ex.getMessage()
        );
        problemDetail.setTitle("JWT Error");
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatusCode.valueOf(500),
                "An unexpected error occurred: " + ex.getMessage()
        );
        problemDetail.setTitle("Internal Server Error");
        ex.printStackTrace();
        return problemDetail;
    }
}
