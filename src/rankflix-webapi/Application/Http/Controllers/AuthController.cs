using Microsoft.AspNetCore.Mvc;
using Rankflix.Application.Http.Models.Account.Auth;

namespace Rankflix.Application.Http.Controllers;

[ApiController]
public class AuthController : ControllerBase
{
    [HttpPost(Uris.Auth.DiscordAuth)]
    public async Task<ActionResult> DiscordLogin([FromBody] DiscordIdToken idToken)
    {
        await Task.CompletedTask;
        return NoContent();
    }
}