using System.Text.Json;
using System.Text.Json.Serialization;
using Microsoft.AspNetCore.Mvc;

namespace Rankflix.Application.Http.Problems;

public abstract class Problem(
    int status,
    string subtype,
    string title,
    string detail,
    object? data = null
)
{
    public const string MediaType = "application/problem+json";

    public Guid Id { get; } = Guid.NewGuid();
    public string Type { get; } = "https://rankflix.pt/probs/" + subtype;
    public string Title { get; } = title;
    public int Status { get; } = status;
    public string Detail { get; } = detail;
    public DateTime Timestamp { get; } = DateTime.UtcNow;

    [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingNull)]
    public object? Data { get; } = data;

    public override string ToString() => JsonSerializer.Serialize(this);
    
    public ActionResult ToActionResult()
    {
        return new ObjectResult(this) { StatusCode = Status, ContentTypes = { MediaType } };
    }
}