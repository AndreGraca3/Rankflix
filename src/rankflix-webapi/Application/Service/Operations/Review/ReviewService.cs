using Rankflix.Application.Domain;
using Rankflix.Application.Domain.Account;
using Rankflix.Application.Repository;
using Rankflix.Application.Repository.Review;
using Rankflix.Application.Service.Transaction;

namespace Rankflix.Application.Service.Operations.Review;

public class ReviewService(IReviewRepository reviewRepository, ITransactionManager transactionManager) : IReviewService
{
    public async Task<PaginatedResult<Domain.Review>> GetReviewsAsync(int skip, int take)
    {
        return await transactionManager.ExecuteAsync(async () => await reviewRepository.GetReviewsAsync(skip, take));
    }

    public async Task<Domain.Review> GetReviewByIdAsync(ReviewId reviewId)
    {
        return await transactionManager.ExecuteAsync(async () => await reviewRepository.GetReviewByIdAsync(reviewId));
    }

    public async Task<ReviewId> AddReviewAsync(int rating, string comment, UserId userId)
    {
        return await transactionManager.ExecuteAsync(async () =>
            await reviewRepository.AddReviewAsync(rating, comment, userId));
    }

    public async Task<Domain.Review> UpdateReviewAsync(ReviewId reviewId, int rating, string comment, UserId userId)
    {
        return await transactionManager.ExecuteAsync(async () =>
        {
            var review = await reviewRepository.GetReviewByIdAsync(reviewId);
            if (!review.IsOwner(userId))
            {
                throw new UnauthorizedAccessException("User is not the owner of the review");
            }

            if (review.IsTooOld())
            {
                throw new InvalidOperationException("Review is too old to be updated");
            }

            return await reviewRepository.UpdateReviewAsync(reviewId, rating, comment);
        });
    }

    public async Task DeleteReviewAsync(ReviewId reviewId, UserId userId)
    {
        await transactionManager.ExecuteAsync(async () =>
        {
            var review = await reviewRepository.GetReviewByIdAsync(reviewId);
            if (!review.IsOwner(userId))
            {
                throw new UnauthorizedAccessException("User is not the owner of the review");
            }

            if (review.IsTooOld())
            {
                throw new InvalidOperationException("Review is too old to be deleted");
            }

            await reviewRepository.DeleteReviewAsync(reviewId);
            return Task.CompletedTask;
        });
    }
}