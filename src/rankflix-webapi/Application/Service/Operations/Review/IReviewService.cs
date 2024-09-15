using Rankflix.Application.Domain;

namespace Rankflix.Application.Service.Operations.Review;

public interface IReviewService
{
    public Task<PaginatedResult<Domain.Review>> GetReviewsAsync(int page);

    public Task<Domain.Review> GetReviewByIdAsync(int reviewId);

    public Task<ReviewId> AddReviewAsync(int rating, string comment, UserId userId);

    public Task<Domain.Review> UpdateReviewAsync(ReviewId reviewId, int rating, string comment, UserId userId);

    public Task DeleteReviewAsync(int reviewId, UserId userId);
}