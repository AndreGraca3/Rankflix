using Rankflix.Application.Domain;
using Rankflix.Application.Domain.Account;
using Rankflix.Infrastructure.Data;

namespace Rankflix.Application.Repository.Review;

public class ReviewRepository(RankflixDataContext dbContext): IReviewRepository
{
    public Task<PaginatedResult<Domain.Review>> GetReviewsAsync(int page)
    {
        throw new NotImplementedException();
    }

    public Task<Domain.Review> GetReviewByIdAsync(int reviewId)
    {
        throw new NotImplementedException();
    }

    public Task<ReviewId> AddReviewAsync(int rating, string comment, UserId userId)
    {
        throw new NotImplementedException();
    }

    public Task<Domain.Review> UpdateReviewAsync(ReviewId reviewId, int rating, string comment, UserId userId)
    {
        throw new NotImplementedException();
    }

    public Task DeleteReviewAsync(int reviewId, UserId userId)
    {
        throw new NotImplementedException();
    }
}