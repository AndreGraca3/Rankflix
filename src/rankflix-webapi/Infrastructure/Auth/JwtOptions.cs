namespace Rankflix.Infrastructure.Auth;

public class JwtOptions
{
    public string Key { get; init; }

    public string Issuer { get; init; }

    public string Audience { get; init; }
    
    public int ExpireMinutes { get; init; }
}
