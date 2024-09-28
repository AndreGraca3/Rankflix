using Rankflix.Application.Domain;
using Rankflix.Application.Domain.Account;
using Rankflix.Application.Domain.Group;

namespace Rankflix.Application.Service.Operations.Group;

public interface IGroupService
{
    public Task<PaginatedResult<GroupItem>> GetGroupsFromUserAsync(int skip, int take, UserId userId, string? name);

    public Task<Domain.Group.Group> GetGroupByIdAsync(GroupId id);

    public Task<GroupId> CreateGroupAsync(string name, UserId ownerId);

    public Task DeleteGroupAsync(GroupId id);
}