using Microsoft.AspNetCore.Mvc;
using Rankflix.Application.Http.Models.Account.Auth;
using Rankflix.Application.Service.Operations.Account.Auth;
using Rankflix.Application.Service.Operations.Account.User;

namespace Rankflix.Application.Http.Controllers;

[ApiController]
public class AuthController(IAuthService authService, IUserService userService) : ControllerBase
{
    [HttpPost(Uris.Auth.DiscordAuth)]
    public async Task<ActionResult<LoginResponse>> DiscordLogin([FromBody] DiscordIdToken idToken)
    {
        var loginResult = await authService.DiscordLoginAsync(idToken.IdToken);

        var refreshToken = loginResult.RefreshToken;
        Response.Cookies.Append("refresh_token", refreshToken.Value.ToString(), new CookieOptions
        {
            HttpOnly = true,
            Secure = true,
            SameSite = SameSiteMode.Strict,
            // Path = Uris.Auth.RefreshToken,
            Expires = refreshToken.ExpiresAt
        });

        var accessToken = loginResult.AccessToken;

        return new LoginResponse
        {
            AccessToken = accessToken.Value,
            ExpireMinutes = (int)accessToken.ExpiresAt.Subtract(DateTime.Now).TotalMinutes
        };
    }
}