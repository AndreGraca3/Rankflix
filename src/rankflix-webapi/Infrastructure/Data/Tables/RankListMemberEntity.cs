using System.ComponentModel.DataAnnotations.Schema;

namespace Rankflix.Infrastructure.Data.Tables;

[Table("rank_list_member")]
public class RankListMemberEntity
{
    [Column("list_id")]
    public int ListId { get; set; }
    
    [Column("user_id")]
    public int UserId { get; set; }
    
    [Column("is_owner")]
    public bool IsOwner { get; set; }
}