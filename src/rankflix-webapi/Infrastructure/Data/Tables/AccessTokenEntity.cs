using System.ComponentModel.DataAnnotations.Schema;

namespace Rankflix.Infrastructure.Data.Tables;

[Table("access_token")]
public class AccessTokenEntity
{
    [Column("value")] public Guid Value { get; init; }

    [Column("created_at")] public DateTime CreatedAt { get; }

    [Column("user_id")] public int UserId { get; init; }
}