using System.ComponentModel.DataAnnotations.Schema;

namespace Rankflix.Infrastructure.Data.Tables;

[Table("rank_group")]
public class RankGroupEntity
{
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    [Column("id")]
    public int Id { get; init; }

    [Column("name")]
    public required string Name { get; init; }
    
    [Column("owner_id")]
    public required int OwnerId { get; init; }
}