using Rankflix.Application.Domain;
using Rankflix.Application.Domain.Account;
using Rankflix.Infrastructure.Data;
using Rankflix.Infrastructure.Data.Tables;

namespace Rankflix.Application.Repository.Review;

public class ReviewRepository(RankflixDataContext dbContext) : IReviewRepository
{
    public Task<PaginatedResult<Domain.Review>> GetReviewsAsync(int skip, int take)
    {
        throw new NotImplementedException();
    }

    public async Task<Domain.Review> GetReviewByIdAsync(ReviewId reviewId)
    {
        var review = await dbContext.Review.FindAsync(reviewId.Value);
        if (review == null)
        {
            throw new InvalidOperationException("Review not found");
        }

        return review.ToReview();
    }

    public async Task<ReviewId> AddReviewAsync(int rating, string comment, UserId userId)
    {
        var review = new ReviewEntity
        {
            Rating = rating,
            Comment = comment,
            UserId = userId.Value
        };

        dbContext.Review.Add(review);
        await dbContext.SaveChangesAsync();
        return new ReviewId(review.Id);
    }

    public async Task<Domain.Review> UpdateReviewAsync(ReviewId reviewId, int rating, string comment)
    {
        var review = await dbContext.Review.FindAsync(reviewId.Value);
        if (review == null)
        {
            throw new InvalidOperationException("Review not found");
        }

        review.Rating = rating;
        review.Comment = comment;
        await dbContext.SaveChangesAsync();
        return review.ToReview();
    }

    public async Task DeleteReviewAsync(ReviewId reviewId)
    {
        var review = await dbContext.Review.FindAsync(reviewId.Value);
        if (review == null)
        {
            throw new InvalidOperationException("Review not found");
        }

        dbContext.Review.Remove(review);
        await dbContext.SaveChangesAsync();
    }
}