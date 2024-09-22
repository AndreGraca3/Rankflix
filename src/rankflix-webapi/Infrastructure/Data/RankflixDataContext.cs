using Microsoft.EntityFrameworkCore;
using Rankflix.Infrastructure.Data.Tables;

namespace Rankflix.Infrastructure.Data;

public class RankflixDataContext(DbContextOptions options) : DbContext(options)
{
    public DbSet<UserEntity> User { get; set; }

    public DbSet<RefreshTokenEntity> RefreshToken { get; set; }

    public DbSet<MediaEntity> Media { get; set; }

    public DbSet<ReviewEntity> Review { get; set; }
}