using System.Text.Json.Serialization;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.OpenApi.Models;
using Rankflix.Infrastructure;

namespace Rankflix;

public static class Program
{
    public static void Main(string[] args)
    {
        WebApplication app = CreateWebHostBuilder(args).Build();
        Configure(app);
        Console.WriteLine("Started server...");
        app.Run();
    }

    private static WebApplicationBuilder CreateWebHostBuilder(string[] args)
    {
        WebApplicationBuilder builder = WebApplication.CreateBuilder(args);
        ConfigureServices(builder);
        return builder;
    }

    private static void Configure(WebApplication app)
    {
        app.UseSwagger();
        app.UseSwaggerUI();

        app.UseAuthentication();
        app.UseAuthorization();

        app.MapControllers();
    }

    private static void ConfigureServices(WebApplicationBuilder builder)
    {
        builder.Services
            .AddRankflixAuthentication(builder.Configuration)
            .AddControllers()
            .AddJsonOptions(o => { o.JsonSerializerOptions.Converters.Add(new JsonStringEnumConverter()); });

        builder.Services
            .AddEndpointsApiExplorer()
            .AddSwaggerGen();

        builder.Services
            .AddContentProvider(builder.Configuration)
            .AddSqlServer(builder.Configuration)
            .AddRankflixDependencies();
    }
}