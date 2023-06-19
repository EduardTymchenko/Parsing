package org.tymchenko;

import java.util.List;

public class Report {
    private String sport;
    private String region;
    private String league;
    private String matchName;
    private String time;
    private String idMatch;
    private List<Market> marketList;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(sport).append(", ").append(region).append("-").append(league).append("\n");
        sb.append("\t").append(matchName).append(", ").append(time).append(", ").append(idMatch).append("\n");
        if (marketList != null) {
            int size = Math.min(marketList.size(), 4);
            for (int i = 0; i < size; i++) {
                sb.append(marketList.get(i).toString());
            }
        }
        return sb.toString();
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public void setLeague(String league) {
        this.league = league;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setMatchName(String matchName) {
        this.matchName = matchName;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setIdMatch(String idMatch) {
        this.idMatch = idMatch;
    }

    public void setMarketList(List<Market> marketList) {
        this.marketList = marketList;
    }
}
