using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using Microsoft.Extensions.Options;
using Microsoft.IdentityModel.Tokens;
using Rankflix.Application.Domain.Account;
using Rankflix.Infrastructure.Auth;

namespace Rankflix.Application.Service.External.Auth;

internal sealed class JwtProvider(IOptions<JwtOptions> options) : IJwtProvider
{
    private readonly JwtOptions _options = options.Value;

    public AccessToken Generate(User user)
    {
        var claims = new Claim[]
        {
            new(JwtRegisteredClaimNames.Sub, user.Id.Value.ToString()),
            new(JwtRegisteredClaimNames.Email, user.Email)
        };

        var signingCredentials = new SigningCredentials(
            new SymmetricSecurityKey(
                Encoding.UTF8.GetBytes(_options.Key)),
            SecurityAlgorithms.HmacSha256);

        var token = new JwtSecurityToken(
            _options.Issuer,
            _options.Audience,
            claims,
            null,
            DateTime.UtcNow.AddMinutes(_options.ExpireMinutes),
            signingCredentials);
        
        var tokenValue = new JwtSecurityTokenHandler().WriteToken(token);
        return new AccessToken { Value = tokenValue, ExpiresAt = token.ValidTo };
    }
}