using Rankflix.Application.Domain.Account;

namespace Rankflix.Application.Service.Results;

public class LoginResult
{
    public required AccessToken AccessToken { get; init; }

    public required RefreshToken RefreshToken { get; init; }
}