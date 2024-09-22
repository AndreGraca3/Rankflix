using Rankflix.Application.Domain.Account;

namespace Rankflix.Application.Service.External.Auth;

public interface IJwtProvider
{
    AccessToken Generate(User user);
}
