using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Rankflix.Application.Domain.Account;

namespace Rankflix.Infrastructure.Data.Tables;

[Table("refresh_token")]
public class RefreshTokenEntity
{
    [Key] [Column("value")] public Guid Value { get; init; }

    [Column("created_at")] public DateTime CreatedAt { get; init; }

    [Column("user_id")] public int UserId { get; init; }

    public RefreshTokenItem ToRefreshTokenItem()
    {
        return new RefreshTokenItem
        {
            Value = Value,
            CreatedAt = CreatedAt,
            UserId = new UserId(UserId)
        };
    }
}