package de.sakuramc.coreapi.utils.clan;

import java.sql.Timestamp;
import java.util.List;

public class Clan {

    private int clanId;
    private String name;
    private String tag;
    private Timestamp creationDate;
    private List<ClanRank> ranks;
    private List<ClanMember> members;

    public int getClanId() {
        return clanId;
    }

    public void setClanId(int clanId) {
        this.clanId = clanId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public List<ClanRank> getRanks() {
        return ranks;
    }

    public void setRanks(List<ClanRank> ranks) {
        this.ranks = ranks;
    }

    public List<ClanMember> getMembers() {
        return members;
    }

    public void setMembers(List<ClanMember> members) {
        this.members = members;
    }

}
