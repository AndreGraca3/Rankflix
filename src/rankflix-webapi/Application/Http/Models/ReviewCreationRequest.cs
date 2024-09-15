namespace Rankflix.Application.Http.Models;

public class ReviewCreationRequest
{
    public int Rating { get; set; }

    public string Comment { get; set; }
}