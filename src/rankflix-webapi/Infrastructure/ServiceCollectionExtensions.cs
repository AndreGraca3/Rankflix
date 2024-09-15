using Microsoft.EntityFrameworkCore;
using Rankflix.Application.Service.External;
using Rankflix.Application.Service.Transaction;
using Rankflix.Infrastructure.Data;
using TMDbLib.Client;

namespace Rankflix.Infrastructure;

public static class ServiceCollectionExtensions
{
    public static IServiceCollection AddContentProvider(this IServiceCollection services, IConfiguration configuration)
    {
        services.AddScoped<TMDbClient>(_ => new TMDbClient(configuration["Tmdb:ApiKey"]));
        return services.AddScoped<IContentProvider, TmdbContentProvider>(provider =>
            {
                var contentProvider = new TmdbContentProvider(provider.GetRequiredService<TMDbClient>());
                contentProvider.InitializeGenresAsync().Wait();
                return contentProvider;
            }
        );
    }

    public static IServiceCollection AddSqlServer(
        this IServiceCollection services,
        IConfiguration configuration
    )
    {
        return services.AddDbContext<RankflixDataContext>(options =>
            options.UseSqlServer(configuration.GetConnectionString("WebApiDatabase"))
        );
    }

    public static IServiceCollection AddRankflixDataServices(this IServiceCollection services)
    {
        return services.AddScoped<ITransactionManager, TransactionManager>();
    }
}