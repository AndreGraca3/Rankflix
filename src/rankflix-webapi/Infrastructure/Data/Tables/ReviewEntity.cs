using System.ComponentModel.DataAnnotations.Schema;
using Rankflix.Application.Domain;
using Rankflix.Application.Domain.Account;

namespace Rankflix.Infrastructure.Data.Tables;

[Table("review")]
public class ReviewEntity
{
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    [Column("id")]
    public int Id { get; set; }

    [Column("rating")] public float Rating { get; set; }

    [Column("comment")] public string Comment { get; set; }

    [Column("created_at")] public DateTime CreatedAt { get; set; }

    [Column("media_id")] public int MediaId { get; set; }

    [Column("user_id")] public int UserId { get; set; }

    public Review ToReview()
    {
        return new Review
        {
            Id = new ReviewId(Id),
            Rating = Rating,
            Comment = Comment,
            CreatedAt = CreatedAt,
            UserId = new UserId(UserId),
            MediaId = MediaId
        };
    }
}