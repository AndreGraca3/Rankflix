using Rankflix.Application.Domain;

namespace Rankflix.Application.Repository;

public interface IReviewRepository
{
    public Task<PaginatedResult<Review>> GetReviewsAsync(int page);
    
    public Task<Review> GetReviewByIdAsync(int reviewId);
    
    public Task<ReviewId> AddReviewAsync(int rating, string comment, UserId userId);
    
    public Task<Review> UpdateReviewAsync(ReviewId reviewId, int rating, string comment, UserId userId);
    
    public Task DeleteReviewAsync(int reviewId, UserId userId);
}