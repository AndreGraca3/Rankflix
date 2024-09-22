using Microsoft.AspNetCore.Mvc;
using Rankflix.Application.Domain;
using Rankflix.Application.Http.Models.Account.User;
using Rankflix.Application.Service.Operations.Account.User;

namespace Rankflix.Application.Http.Controllers;

[ApiController]
public class UserController(IUserService userService) : ControllerBase
{
    [HttpGet(Uris.User.Base)]
    public async Task<ActionResult<PaginatedResult<UserOutputModel>>> GetUsersAsync(
        [FromQuery] string? username, int page = 1)
    {
        var paginatedUsers = await userService.GetUsersAsync(page, username);
        return paginatedUsers.Select(u => u.ToOutputModel());
    }

    [HttpGet(Uris.User.Me)]
    public async Task<ActionResult<UserOutputModel>> GetAuthenticatedUserAsync()
    {
        throw new NotImplementedException();
    }
}