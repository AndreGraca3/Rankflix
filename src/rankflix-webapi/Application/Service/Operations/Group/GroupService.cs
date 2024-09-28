using Rankflix.Application.Domain;
using Rankflix.Application.Domain.Account;
using Rankflix.Application.Domain.Group;
using Rankflix.Application.Repository.Group;
using Rankflix.Application.Service.Transaction;

namespace Rankflix.Application.Service.Operations.Group;

public class GroupService(IGroupRepository groupRepository, ITransactionManager transactionManager) : IGroupService
{
    public async Task<PaginatedResult<GroupItem>> GetGroupsFromUserAsync(int skip, int take, UserId userId,
        string? name)
    {
        return await transactionManager.ExecuteAsync(async () =>
            await groupRepository.GetGroupsFromUserAsync(skip, take, userId, name));
    }

    public async Task<Domain.Group.Group> GetGroupByIdAsync(GroupId id)
    {
        return await transactionManager.ExecuteAsync(async () =>
        {
            var group = await groupRepository.GetGroupByIdAsync(id);
            return group ?? throw new InvalidOperationException("Group not found");
        });
    }

    public async Task<GroupId> CreateGroupAsync(string name, UserId ownerId)
    {
        return await transactionManager.ExecuteAsync(async () =>
            await groupRepository.CreateGroupAsync(name, ownerId));
    }

    public async Task DeleteGroupAsync(GroupId id)
    {
        await transactionManager.ExecuteAsync(async () =>
        {
            var group = await groupRepository.GetGroupByIdAsync(id);

            if (group == null)
            {
                throw new InvalidOperationException("Group not found");
            }

            await groupRepository.DeleteGroupAsync(group.Id);

            return Task.CompletedTask;
        });
    }
}