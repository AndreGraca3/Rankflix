using Microsoft.EntityFrameworkCore;
using Rankflix.Application.Repository.Account.Token;
using Rankflix.Application.Repository.Account.User;
using Rankflix.Application.Repository.Review;
using Rankflix.Application.Service.External.Auth;
using Rankflix.Application.Service.External.Media;
using Rankflix.Application.Service.Operations.Account.Auth;
using Rankflix.Application.Service.Operations.Account.User;
using Rankflix.Application.Service.Operations.Review;
using Rankflix.Application.Service.Transaction;
using Rankflix.Infrastructure.Auth;
using Rankflix.Infrastructure.Data;
using TMDbLib.Client;

namespace Rankflix.Infrastructure;

public static class ServiceCollectionExtensions
{
    public static IServiceCollection AddContentProvider(
        this IServiceCollection services, IConfiguration configuration)
    {
        return services.AddSingleton(_ => new TMDbClient(configuration["Tmdb:ApiKey"]))
            .AddSingleton<TmdbContentProvider>()
            .AddHostedService(provider => provider.GetRequiredService<TmdbContentProvider>())
            .AddSingleton<IContentProvider>(provider => provider.GetRequiredService<TmdbContentProvider>());
    }

    public static IServiceCollection AddSqlServer(this IServiceCollection services, IConfiguration configuration)
    {
        return services.AddDbContext<RankflixDataContext>(options =>
            options.UseSqlServer(configuration.GetConnectionString("WebApiDatabase"))
        );
    }

    public static IServiceCollection AddRankflixDependencies(this IServiceCollection services)
    {
        return services

            // external services
            .AddScoped<IJwtProvider, JwtProvider>()

            // internal services
            .AddScoped<IAuthService, AuthService>()
            .AddScoped<IUserService, UserService>()
            .AddScoped<IReviewService, ReviewService>()

            // transaction manager
            .AddScoped<ITransactionManager, TransactionManager>()

            // repositories
            .AddScoped<IUserRepository, UserRepository>()
            .AddScoped<ITokenRepository, TokenRepository>()
            .AddScoped<IReviewRepository, ReviewRepository>();
    }

    public static IServiceCollection AddRankflixConfigurations(this IServiceCollection services,
        IConfiguration configuration)
    {
        return services
            .Configure<RefreshTokenOptions>(configuration.GetSection("RefreshToken"))
            .Configure<JwtOptions>(configuration.GetSection("Jwt"))
            .ConfigureOptions<JwtBearerOptionsSetup>();
    }
}