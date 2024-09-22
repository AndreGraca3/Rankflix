namespace Rankflix.Application.Http.Problems;

public class AuthenticationProblem(
    int status,
    string subtype,
    string title,
    string detail,
    object? data = null
) : Problem(status, subtype, title, detail, data)
{
    public class InvalidToken() : AuthenticationProblem(
        401,
        "invalid-token",
        "Invalid token",
        "Request's token is invalid or expired"
    );

    public class UnauthorizedResource() : AuthenticationProblem(
        403,
        "access-denied",
        "Access denied",
        "You do not have the necessary permissions to perform this action"
    );
}