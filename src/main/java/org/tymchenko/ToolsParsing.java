package org.tymchenko;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ToolsParsing {
    private static final String URL_ALL_SPORTS_WITH_LEAGUES = "https://leonbets.com/api-2/betline/sports?ctag=en-US&flags=urlv2";
    private static final String URL_DATA_MATCHES_IN_LEAGUE = "https://leonbets.com/api-2/betline/events/all?ctag=en-US&league_id=(0)&hideClosed=true&flags=reg,urlv2,mm2,rrc,nodup";
    private static final String URL_DATA_GAME = "https://leonbets.com/api-2/betline/event/all?ctag=en-US&eventId=(0)&flags=reg,urlv2,mm2,rrc,nodup,smg,outv2";
    private static final String REPLACE_PARAMETER = "\\(0\\)";
    private static final String LEAGUES_TOP_CONDITION = "\"top\":true";
    private static final String ID_KEY = "id";
    private static final String MARKETS_KEY = "\"markets\"";
    private static final String DATE_TIME_KEY = "kickoff";
    private static final String NAME_KEY = "\"name\"";
    private static final String SEPARATOR = ",";
    private static final String OPEN_SQUARE_BRACKET = "[";
    private static final String CLOSE_SQUARE_BRACKET = "]";

    private static String getJsonData(String urlFull) throws IOException {
        StringBuilder response = new StringBuilder();

        URL url = new URL(urlFull);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    private static List<String> getIdsTopLeagues(String sportName) throws IOException {
        String jsonString = getJsonData(URL_ALL_SPORTS_WITH_LEAGUES);
        int indexStartSearch = jsonString.indexOf(sportName);
        if (indexStartSearch == -1) {
            return Collections.emptyList();
        }
        int indexEndSearch = getIndexBlockEnd(indexStartSearch, OPEN_SQUARE_BRACKET, CLOSE_SQUARE_BRACKET, jsonString);

        List<String> idsTopLeagues = new ArrayList<>();
        while (indexStartSearch < indexEndSearch) {
            indexStartSearch = jsonString.indexOf(LEAGUES_TOP_CONDITION, indexStartSearch);
            if (indexStartSearch == -1) {
                return Collections.emptyList();
            }
            if (indexStartSearch > indexEndSearch) {
                break;
            }
            idsTopLeagues.add(getValueFromJsonString(ID_KEY, jsonString, indexStartSearch, true));
            indexStartSearch += LEAGUES_TOP_CONDITION.length();
        }

        return idsTopLeagues;
    }

    public static List<Report> reports(String sportName) throws IOException {
        List<String> idsTopLeagues = getIdsTopLeagues(sportName);
        if (idsTopLeagues.isEmpty()) {
            return Collections.emptyList();
        }
        List<Report> reports = new ArrayList<>();
        for (String idLeagueTop : idsTopLeagues) {
            String urlDataMatches = URL_DATA_MATCHES_IN_LEAGUE.replaceAll(REPLACE_PARAMETER, idLeagueTop);
            String dataMatchesJson = getJsonData(urlDataMatches);

            String idFirsGame = getValueFromJsonString(ID_KEY, dataMatchesJson, 0, false);
            String urlDataFistGame = URL_DATA_GAME.replaceAll(REPLACE_PARAMETER, idFirsGame);
            String dataGameJson = getJsonData(urlDataFistGame);

            Report report = new Report();
            report.setSport(sportName);
            report.setIdMatch(idFirsGame);
            report.setMatchName(getValueFromJsonString(NAME_KEY, dataGameJson, 0, false));
            report.setTime(formatDateTimeUTC(Long.parseLong(getValueFromJsonString(DATE_TIME_KEY, dataGameJson, 0, false))));
            report.setLeague(getValueFromJsonString(NAME_KEY, dataGameJson, dataGameJson.indexOf(LEAGUES_TOP_CONDITION), true));
            report.setRegion(getValueFromJsonString(NAME_KEY, dataGameJson, dataGameJson.indexOf(LEAGUES_TOP_CONDITION), false));
            report.setMarketList(getMarkets(dataGameJson));
            reports.add(report);
        }
        return reports;
    }

    private static String getValueFromJsonString(String key, String jsonString, int startIndex, boolean isRevers) {
        int indexStart;
        if (isRevers) {
            indexStart = jsonString.lastIndexOf(key, startIndex);
        } else {
            indexStart = jsonString.indexOf(key, startIndex);
        }
        indexStart = indexStart + key.length() + 2;
        int indexEnd = jsonString.indexOf(SEPARATOR, indexStart);
        return jsonString.substring(indexStart, indexEnd).replaceAll("\"", "");
    }

    //startIndex is mast equals index openBlockSymbol or be less.
    private static int getIndexBlockEnd(int startIndex, String openBlockSymbol, String closeBlockSymbol, String stringSearch) {
        int indexOpen = stringSearch.indexOf(openBlockSymbol, startIndex) + openBlockSymbol.length();
        int indexEnd = indexOpen;
        while (indexOpen <= indexEnd && indexOpen != -1) {
            indexOpen = stringSearch.indexOf(openBlockSymbol, indexOpen);
            indexEnd = stringSearch.indexOf(closeBlockSymbol, indexEnd);
            if (indexOpen != -1) {
                indexOpen += openBlockSymbol.length();
                indexEnd += closeBlockSymbol.length();
            }
        }
        return indexEnd;
    }

    private static String formatDateTimeUTC(long timeMillis) {
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(timeMillis / 1000L, 0, ZoneOffset.UTC);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z").withZone(ZoneId.of("UTC"));
        return localDateTime.format(timeFormatter);
    }

    private static List<Market> getMarkets(String jsonString) {
        int indexStartMarketsBlock = jsonString.indexOf(MARKETS_KEY) + MARKETS_KEY.length() + 1;
        int indexEndMarketsBlock = getIndexBlockEnd(indexStartMarketsBlock, OPEN_SQUARE_BRACKET, CLOSE_SQUARE_BRACKET, jsonString);
        String marketsJson = jsonString.substring(indexStartMarketsBlock, indexEndMarketsBlock + 1);

        Gson gson = new Gson();
        Market[] marketsArr = gson.fromJson(marketsJson, Market[].class);
        return Arrays.stream(marketsArr).collect(Collectors.toCollection(ArrayList::new));
    }
}
