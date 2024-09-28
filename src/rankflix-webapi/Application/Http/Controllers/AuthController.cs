using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Rankflix.Application.Domain.Account;
using Rankflix.Application.Http.Models.Account.Auth;
using Rankflix.Application.Service.Operations.Account.Auth;

namespace Rankflix.Application.Http.Controllers;

[ApiController]
public class AuthController(IAuthService authService) : ControllerBase
{
    private const string RefreshTokenCookieName = "refresh_token";

    [HttpPost(Uris.Auth.DiscordAuth)]
    public async Task<ActionResult<LoginResponse>> DiscordLogin([FromBody] DiscordIdToken idToken)
    {
        var loginResult = await authService.DiscordLoginAsync(idToken.IdToken);

        SetRefreshTokenCookie(loginResult.RefreshToken);

        var accessToken = loginResult.AccessToken;
        return new LoginResponse
        {
            AccessToken = accessToken.Value,
            ExpireMinutes = accessToken.ExpiresAt.Subtract(DateTime.UtcNow).Minutes
        };
    }

    [HttpPost(Uris.Auth.RefreshToken)]
    public async Task<ActionResult<LoginResponse>> RefreshTokens()
    {
        var oldRefreshToken = Request.Cookies[RefreshTokenCookieName];
        if (oldRefreshToken is null) return BadRequest();

        var loginResult = await authService.RefreshTokensAsync(Guid.Parse(oldRefreshToken));

        SetRefreshTokenCookie(loginResult.RefreshToken);

        var accessToken = loginResult.AccessToken;
        return new LoginResponse
        {
            AccessToken = accessToken.Value,
            ExpireMinutes = (int)accessToken.ExpiresAt.Subtract(DateTime.UtcNow).TotalMinutes
        };
    }

    private void SetRefreshTokenCookie(RefreshToken refreshToken)
    {
        Response.Cookies.Append(RefreshTokenCookieName, refreshToken.Value.ToString(), new CookieOptions
        {
            HttpOnly = true,
            Secure = true,
            SameSite = SameSiteMode.Strict,
            // Path = Uris.Auth.RefreshToken,
            Expires = refreshToken.ExpiresAt
        });
    }
}