using System.ComponentModel.DataAnnotations.Schema;

namespace Rankflix.Infrastructure.Data.Tables;

[Table("user")]
public class UserEntity
{
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    [Column("id")]
    public int Id { get; set; }

    [Column("email")] public required string Email { get; set; }

    [Column("username")] public required string Username { get; set; }

    [Column("avatar_url")] public string? AvatarUrl { get; set; }

    [Column("created_at")] public required DateTime CreatedAt { get; set; }
}