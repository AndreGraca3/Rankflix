using Rankflix.Application.Domain;
using Rankflix.Application.Domain.Account;
using Rankflix.Application.Repository;
using Rankflix.Application.Repository.Review;
using Rankflix.Application.Service.Transaction;

namespace Rankflix.Application.Service.Operations.Review;

public class ReviewService(IReviewRepository reviewRepository, ITransactionManager transactionManager) : IReviewService
{
    public async Task<PaginatedResult<Domain.Review>> GetReviewsAsync(int page)
    {
        return await transactionManager.ExecuteAsync(async () => await reviewRepository.GetReviewsAsync(page));
    }

    public async Task<Domain.Review> GetReviewByIdAsync(int reviewId)
    {
        return await transactionManager.ExecuteAsync(async () => await reviewRepository.GetReviewByIdAsync(reviewId));
    }

    public async Task<ReviewId> AddReviewAsync(int rating, string comment, UserId userId)
    {
        return await transactionManager.ExecuteAsync(async () =>
            await reviewRepository.AddReviewAsync(rating, comment, userId));
    }

    public Task<Domain.Review> UpdateReviewAsync(ReviewId reviewId, int rating, string comment, UserId userId)
    {
        // update only if 5 minutes have passed since the review was created and the user is the owner
        throw new NotImplementedException();
    }

    public Task DeleteReviewAsync(int reviewId, UserId userId)
    {
        // delete only if 5 minutes have passed since the review was created and the user is the owner
        throw new NotImplementedException();
    }
}