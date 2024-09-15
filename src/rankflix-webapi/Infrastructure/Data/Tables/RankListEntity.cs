using System.ComponentModel.DataAnnotations.Schema;

namespace Rankflix.Infrastructure.Data.Tables;

[Table("rank_list")]
public class RankListEntity
{
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    [Column("id")]
    public int Id { get; init; }

    [Column("name")]
    public required string Name { get; init; }
}