using Rankflix.Application.Domain.Account;

namespace Rankflix.Application.Repository.Account.Token;

public interface ITokenRepository
{
    public Task<RefreshTokenItem?> GetRefreshTokenByValueAsync(Guid value);
    
    public Task<RefreshTokenItem> AddRefreshTokenAsync(UserId userId);
    
    public Task RemoveRefreshTokenByUserIdAsync(UserId userId);
}