using Microsoft.AspNetCore.Mvc;
using Rankflix.Application.Domain;
using Rankflix.Application.Http.Models;
using Rankflix.Application.Service.Operations.Review;

namespace Rankflix.Application.Http.Controllers;

[ApiController]
public class ReviewController(IReviewService reviewService) : ControllerBase
{
    [HttpGet(Uris.Review.Base)]
    public async Task<ActionResult<PaginatedResult<Review>>> GetReviewsAsync([FromQuery] int page = 1)
    {
        return await reviewService.GetReviewsAsync(page);
    }

    [HttpGet(Uris.Review.ReviewById)]
    public async Task<ActionResult<Review>> GetReviewByIdAsync([FromRoute] int reviewId)
    {
        return await reviewService.GetReviewByIdAsync(reviewId);
    }

    [HttpPost(Uris.Review.Base)]
    public async Task<ActionResult<Review>> CreateReviewAsync([FromBody] ReviewCreationRequest reviewRequest)
    {
        throw  new NotImplementedException();
        
        //var reviewId = await reviewService.AddReviewAsync(reviewRequest.Rating, reviewRequest.Comment);
        //return Created(Uris.Review.BuildReviewByIdUri(reviewId.Value), reviewId);
    }

    [HttpPut(Uris.Review.ReviewById)]
    public async Task<ActionResult<Review>> UpdateReviewAsync(Guid reviewId,
        [FromBody] ReviewCreationRequest reviewRequest)
    {
        throw new NotImplementedException();
        //return await reviewService.UpdateReviewAsync(new ReviewId { Value = reviewId }, reviewRequest.Rating,
          //  reviewRequest.Comment);
    }

    [HttpDelete(Uris.Review.ReviewById)]
    public async Task<ActionResult> DeleteReviewAsync([FromRoute] int reviewId)
    {
        //await reviewService.DeleteReviewAsync(reviewId);
        return NoContent();
    }
}