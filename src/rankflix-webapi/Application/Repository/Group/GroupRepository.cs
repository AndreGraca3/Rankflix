using Microsoft.EntityFrameworkCore;
using Rankflix.Application.Domain;
using Rankflix.Application.Domain.Account;
using Rankflix.Application.Domain.Group;
using Rankflix.Infrastructure.Data;
using Rankflix.Infrastructure.Data.Tables;

namespace Rankflix.Application.Repository.Group;

public class GroupRepository(RankflixDataContext dataContext) : IGroupRepository
{
    public async Task<PaginatedResult<GroupItem>> GetGroupsFromUserAsync(int skip, int take, UserId userId,
        string? name)
    {
        var query = from rankGroup in dataContext.Group
            where name == null || rankGroup.Name.Contains(name)
            join member in dataContext.GroupMember on rankGroup.Id equals member.GroupId
            where member.UserId == userId.Value
            join owner in dataContext.User on rankGroup.OwnerId equals owner.Id
            select new GroupItem(new GroupId(rankGroup.Id), rankGroup.Name,
                new UserItem(owner.Id, owner.Username, owner.AvatarUrl));

        var total = await query.CountAsync();

        var groups = await query
            .Skip(skip)
            .Take(take)
            .ToListAsync();

        return new PaginatedResult<GroupItem>(groups, total, skip, take);
    }

    public async Task<Domain.Group.Group?> GetGroupByIdAsync(GroupId id)
    {
        var query = from rankGroup in dataContext.Group
            where rankGroup.Id == id.Value
            join owner in dataContext.User on rankGroup.OwnerId equals owner.Id
            select new Domain.Group.Group
            {
                Id = new GroupId(rankGroup.Id),
                Name = rankGroup.Name,
                Owner = new UserItem(owner.Id, owner.Username, owner.AvatarUrl),
                Members = dataContext.GroupMember
                    .Where(gm => gm.GroupId == rankGroup.Id)
                    .Join(dataContext.User, gm => gm.UserId, u => u.Id,
                        (gm, u) => new UserItem(u.Id, u.Username, u.AvatarUrl))
                    .ToList()
            };

        return await query.FirstOrDefaultAsync();
    }

    public async Task<GroupId> CreateGroupAsync(string name, UserId ownerId)
    {
        var group = new RankGroupEntity()
        {
            Name = name,
            OwnerId = ownerId.Value
        };

        await dataContext.Group.AddAsync(group);
        await dataContext.SaveChangesAsync();

        return new GroupId(group.Id);
    }

    public async Task DeleteGroupAsync(GroupId id)
    {
        var group = await dataContext.Group.FindAsync(id.Value);
        if (group == null) return;

        dataContext.Group.Remove(group);
        await dataContext.SaveChangesAsync();
    }
}