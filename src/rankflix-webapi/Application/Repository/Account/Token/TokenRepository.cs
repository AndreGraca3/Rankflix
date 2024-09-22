using Microsoft.EntityFrameworkCore;
using Rankflix.Application.Domain.Account;
using Rankflix.Infrastructure.Data;
using Rankflix.Infrastructure.Data.Tables;

namespace Rankflix.Application.Repository.Account.Token;

public class TokenRepository(RankflixDataContext dataContext) : ITokenRepository
{
    public async Task<RefreshTokenItem> AddRefreshTokenAsync(UserId userId)
    {
        var refreshToken = new RefreshTokenEntity
        {
            UserId = userId.Value,
            CreatedAt = DateTime.UtcNow
        };

        dataContext.RefreshToken.Add(refreshToken);
        await dataContext.SaveChangesAsync();

        return refreshToken.ToRefreshTokenItem();
    }

    public async Task RemoveRefreshTokenByUserIdAsync(UserId userId)
    {
        var refreshTokenEntity = await dataContext.RefreshToken
            .FirstOrDefaultAsync(rt => rt.UserId == userId.Value);

        if (refreshTokenEntity is not null)
        {
            dataContext.RefreshToken.Remove(refreshTokenEntity);
            await dataContext.SaveChangesAsync();
        }
    }
}