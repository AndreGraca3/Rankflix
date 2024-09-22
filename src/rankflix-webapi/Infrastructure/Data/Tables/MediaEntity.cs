using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace Rankflix.Infrastructure.Data.Tables;

[Table("media")]
public class MediaEntity
{
    [Key] [Column("tmdb_id")] public required int TmdbId { get; init; }

    [Column("title")] public required string Title { get; init; }

    [Column("type")] public required string Type { get; init; }
}