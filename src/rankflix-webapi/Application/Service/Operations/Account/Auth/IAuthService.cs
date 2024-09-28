using Rankflix.Application.Service.Results;

namespace Rankflix.Application.Service.Operations.Account.Auth;

public interface IAuthService
{
    Task<LoginResult> DiscordLoginAsync(string discordIdToken);

    Task<LoginResult> RefreshTokensAsync(Guid refreshToken);
}