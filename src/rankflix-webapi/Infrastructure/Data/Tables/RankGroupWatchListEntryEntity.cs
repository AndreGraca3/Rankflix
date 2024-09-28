using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;

namespace Rankflix.Infrastructure.Data.Tables;

[Table("rank_group_watchlist")]
[PrimaryKey("MediaId", "GroupId")]
public class RankGroupWatchListEntryEntity
{
    [Column("media_id")] public int MediaId { get; set; }

    [Column("rank_group_id")] public int GroupId { get; set; }

    [Column("added_by")] public int AddedByUserId { get; set; }

    [Column("added_at")] public DateTime AddedAt { get; set; }
}