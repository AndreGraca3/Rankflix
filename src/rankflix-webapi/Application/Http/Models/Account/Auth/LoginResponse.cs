namespace Rankflix.Application.Http.Models.Account.Auth;

public class LoginResponse
{
    public required string AccessToken { get; set; }

    public required int ExpireMinutes { get; set; }
}