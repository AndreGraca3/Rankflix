using Microsoft.AspNetCore.Mvc;
using Rankflix.Application.Domain;
using Rankflix.Application.Http.Models.Account.User;
using Rankflix.Application.Service.Operations.User;

namespace Rankflix.Application.Http.Controllers;

[ApiController]
public class UserController(IUserService userService) : ControllerBase
{
    [HttpGet(Uris.User.Base)]
    public async Task<ActionResult<PaginatedResult<UserOutputModel>>> GetUsers([FromQuery] string? username)
    {
        var paginatedUsers = await userService.GetUsers(username);
        return paginatedUsers.Select(u => u.ToOutputModel());
    }

    [HttpGet(Uris.User.Me)]
    public async Task<ActionResult<UserOutputModel>> GetMe()
    {
        throw new NotImplementedException();
    }
}