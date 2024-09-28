using Rankflix.Application.Domain;
using Rankflix.Application.Domain.Account;
using Rankflix.Application.Domain.Group;

namespace Rankflix.Application.Repository.Group;

public interface IGroupRepository
{
    Task<PaginatedResult<GroupItem>> GetGroupsFromUserAsync(int skip, int take, UserId userId, string? name);
    Task<Domain.Group.Group?> GetGroupByIdAsync(GroupId id);
    Task<GroupId> CreateGroupAsync(string name, UserId ownerId);
    Task DeleteGroupAsync(GroupId id);
}