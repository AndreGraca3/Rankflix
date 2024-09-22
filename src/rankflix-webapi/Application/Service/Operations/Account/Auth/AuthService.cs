using Microsoft.Extensions.Options;
using Rankflix.Application.Domain.Account;
using Rankflix.Application.Repository.Account.Token;
using Rankflix.Application.Repository.Account.User;
using Rankflix.Application.Service.External.Auth;
using Rankflix.Application.Service.Results;
using Rankflix.Application.Service.Transaction;
using Rankflix.Infrastructure.Auth;

namespace Rankflix.Application.Service.Operations.Account.Auth;

public class AuthService(
    IUserRepository userRepository,
    IJwtProvider jwtProvider,
    IOptions<RefreshTokenOptions> refreshTokenOptions,
    ITokenRepository tokenRepository,
    ITransactionManager transactionManager) : IAuthService
{
    private readonly RefreshTokenOptions _refreshTokenOptions = refreshTokenOptions.Value;

    public async Task<LoginResult> DiscordLoginAsync(string discordIdToken)
    {
        return await transactionManager.ExecuteAsync(async () =>
        {
            var email = "user";
            var username = "user";
            string? avatar = null;

            var user = await GetOrAddUserAsync(email, username, avatar);

            return new LoginResult
            {
                AccessToken = GenerateAccessToken(user),
                RefreshToken = await GenerateRefreshTokenAsync(user.Id)
            };
        });
    }

    private async Task<RefreshToken> GenerateRefreshTokenAsync(UserId userId)
    {
        await tokenRepository.RemoveRefreshTokenByUserIdAsync(userId);

        var refreshToken = await tokenRepository.AddRefreshTokenAsync(userId);
        var expiresAt = refreshToken.CreatedAt.AddMinutes(_refreshTokenOptions.ExpireMinutes);

        return refreshToken.ToRefreshToken(expiresAt);
    }

    public async Task RevokeRefreshTokenAsync(UserId userId)
    {
        await transactionManager.ExecuteAsync(async () => tokenRepository.RemoveRefreshTokenByUserIdAsync(userId));
    }

    // helper methods

    // To be used by the many OAuth login methods
    private async Task<Domain.Account.User> GetOrAddUserAsync(string email, string username, string? avatar)
    {
        var user = await userRepository.GetUserByEmailOrUsernameAsync(email, username);
        if (user != null) return user;

        if (user?.Email == email)
        {
            throw new Exception(email);
        }

        if (user?.Username == username)
        {
            throw new Exception(username);
        }

        var userId = await userRepository.AddUserAsync(email, username, avatar);
        return new Domain.Account.User
            { Id = userId, Email = email, Username = username, AvatarUrl = avatar, CreatedAt = DateTime.UtcNow };
    }

    private AccessToken GenerateAccessToken(Domain.Account.User user)
    {
        return jwtProvider.Generate(user);
    }
}