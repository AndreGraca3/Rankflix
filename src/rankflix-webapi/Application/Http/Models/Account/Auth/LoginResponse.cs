namespace Rankflix.Application.Http.Models.Account.Auth;

public class LoginResponse
{
    public required string AccessToken { get; init; }

    public required int ExpireMinutes { get; init; }
}