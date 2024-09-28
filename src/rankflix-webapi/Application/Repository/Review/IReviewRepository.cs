using Rankflix.Application.Domain;
using Rankflix.Application.Domain.Account;

namespace Rankflix.Application.Repository.Review;

public interface IReviewRepository
{
    public Task<PaginatedResult<Domain.Review>> GetReviewsAsync(int skip, int take);
    
    public Task<Domain.Review> GetReviewByIdAsync(ReviewId reviewId);
    
    public Task<ReviewId> AddReviewAsync(int rating, string comment, UserId userId);
    
    public Task<Domain.Review> UpdateReviewAsync(ReviewId reviewId, int rating, string comment);
    
    public Task DeleteReviewAsync(ReviewId reviewId);
}