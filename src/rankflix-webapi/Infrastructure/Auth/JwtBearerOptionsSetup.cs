using System.Text;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.Extensions.Options;
using Microsoft.IdentityModel.Tokens;
using Rankflix.Application.Http.Problems;
using JsonSerializer = System.Text.Json.JsonSerializer;

namespace Rankflix.Infrastructure.Auth;

public class JwtBearerOptionsSetup(IOptions<JwtOptions> jwtOptions) : IConfigureNamedOptions<JwtBearerOptions>
{
    private readonly JwtOptions _jwtOptions = jwtOptions.Value;

    public void Configure(JwtBearerOptions options)
    {
        options.TokenValidationParameters = new TokenValidationParameters
        {
            ValidateIssuer = true,
            ValidateAudience = true,
            ValidateLifetime = true,
            ValidateIssuerSigningKey = true,
            ValidIssuer = _jwtOptions.Issuer,
            ValidAudience = _jwtOptions.Audience,
            IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_jwtOptions.Key))
        };

        options.Events = new JwtBearerEvents
        {
            OnChallenge = context =>
            {
                context.HandleResponse();

                context.Response.StatusCode = StatusCodes.Status401Unauthorized;
                context.Response.ContentType = Problem.MediaType;

                var problemJson = JsonSerializer.Serialize(new AuthenticationProblem.InvalidToken());
                return context.Response.WriteAsync(problemJson);
            }
        };
    }

    public void Configure(string? name, JwtBearerOptions options)
    {
        Configure(options);
    }
}