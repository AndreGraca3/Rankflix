using Microsoft.AspNetCore.Mvc;
using Rankflix.Application.Domain;
using Rankflix.Application.Domain.Account;
using Rankflix.Application.Domain.Group;
using Rankflix.Application.Http.Models;
using Rankflix.Application.Service.Operations.Group;

namespace Rankflix.Application.Http.Controllers;

[ApiController]
public class GroupController(IGroupService groupService) : ControllerBase
{
    [HttpGet(Uris.Group.Base)]
    public async Task<ActionResult<PaginatedResult<GroupItem>>> GetGroupsFromUserAsync(
        [FromQuery] PaginationInputs paginationInputs, [FromQuery] UserId userId, [FromQuery] string? name)
    {
        return await groupService.GetGroupsFromUserAsync(paginationInputs.Page,
            paginationInputs.ItemsPerPage, userId, name);
    }

    [HttpGet(Uris.Group.GroupById)]
    public async Task<ActionResult<Group>> GetGroupByIdAsync(int groupId)
    {
        return await groupService.GetGroupByIdAsync(new GroupId(groupId));
    }

    [HttpPost(Uris.Group.Base)]
    public async Task<ActionResult<GroupId>> CreateGroupAsync([FromBody] GroupCreationRequest request)
    {
        return await groupService.CreateGroupAsync(request.Name, new UserId(69));
    }

    [HttpDelete(Uris.Group.GroupById)]
    public async Task<ActionResult> DeleteGroupAsync(int groupId)
    {
        await groupService.DeleteGroupAsync(new GroupId(groupId));
        return NoContent();
    }
}