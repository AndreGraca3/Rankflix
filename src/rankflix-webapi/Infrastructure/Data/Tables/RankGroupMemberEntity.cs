using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;

namespace Rankflix.Infrastructure.Data.Tables;

[Table("rank_group_member")]
[PrimaryKey("GroupId", "UserId")]
public class RankGroupMemberEntity
{
    [Column("group_id")]
    public int GroupId { get; set; }
    
    [Column("user_id")]
    public int UserId { get; set; }
}