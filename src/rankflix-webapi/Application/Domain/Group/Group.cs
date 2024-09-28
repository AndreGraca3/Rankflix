using Rankflix.Application.Domain.Account;

namespace Rankflix.Application.Domain.Group;

public class Group
{
    public required GroupId Id { get; set; }

    public required string Name { get; set; }

    public required UserItem Owner { get; set; }

    public required List<UserItem> Members { get; set; }
}

public record GroupItem(GroupId Id, string Name, UserItem Owner);

public record GroupId(int Value);