using Rankflix.Application.Domain;
using Rankflix.Infrastructure.Data;

namespace Rankflix.Application.Repository;

public class ReviewRepository(RankflixDataContext dbContext): IReviewRepository
{
    public Task<PaginatedResult<Review>> GetReviewsAsync(int page)
    {
        throw new NotImplementedException();
    }

    public Task<Review> GetReviewByIdAsync(int reviewId)
    {
        throw new NotImplementedException();
    }

    public Task<ReviewId> AddReviewAsync(int rating, string comment, UserId userId)
    {
        throw new NotImplementedException();
    }

    public Task<Review> UpdateReviewAsync(ReviewId reviewId, int rating, string comment, UserId userId)
    {
        throw new NotImplementedException();
    }

    public Task DeleteReviewAsync(int reviewId, UserId userId)
    {
        throw new NotImplementedException();
    }
}