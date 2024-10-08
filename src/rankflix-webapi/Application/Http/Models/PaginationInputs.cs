using System.ComponentModel.DataAnnotations;
using Microsoft.AspNetCore.Mvc.ModelBinding;

namespace Rankflix.Application.Http.Models;

public class PaginationInputs
{
    [Range(1, int.MaxValue)] public int Page { get; set; } = 1;

    [Range(1, 100)] public int ItemsPerPage { get; set; } = 20;

    [BindNever] public int Skip => (Page - 1) * ItemsPerPage;
}