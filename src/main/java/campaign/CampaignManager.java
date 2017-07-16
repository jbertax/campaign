package campaign;

import java.io.Console;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Simple campaign manager. Finding segment match in lots of campaigns.
 * Also ability to distribute among campaigns to prevent campaign to 'starve'
 */
public class CampaignManager {
    private Set<Campaign> campaigns = new HashSet<>();
    /**
     * If this is bigger than 1, then shows more matching campaign up to this amount
     */
    private int preventStarve = 1;

    public CampaignManager(String fileName) {
        this(fileName, 1);
    }

    public CampaignManager(String fileName, int preventStarve) {
        if (preventStarve > 1) {
            this.preventStarve = preventStarve;
        }
        this.loadCampaignData(fileName);
    }

    private void addCampaign(String rawCampaignData) {
        campaigns.add(new Campaign(rawCampaignData));
    }

    private void loadCampaignData(String fileName) {

        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            stream.forEach(this::addCampaign);
        } catch (Exception e) {
            System.out.println("Could not load campaign file: " + fileName + ". Aborting: " + e.getMessage());
        }
    }

    protected String evaluateCampaign(String input) {
        // input parsed to a set
        Set inputSet = Stream.of(input.split(" "))
                .map(Integer::parseInt)
                .collect(Collectors.toSet());

        Map<String, Integer> campaignResultMap = new ConcurrentHashMap<>();
        for (Campaign cam : this.campaigns) {
            Integer segResult = cam.checkSegments(inputSet);
            if (segResult > 0) {
                campaignResultMap.put(cam.getName(), segResult);
            }
        }

        String result = "no campaign";
        if (campaignResultMap.size() > 0) {
            result = campaignResultMap.entrySet().parallelStream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(preventStarve)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.joining(" "));
        }
        return result;
    }

    public static void main(String args[]) {
        String fileName = "src/main/resources/campaign.txt";

        String input = "4763 1732 5354";  // in case of running in ide: no input
        Console console = System.console();
        if (console != null) {
            input = console.readLine("");
        }

        long timeA = System.currentTimeMillis();
        //distribute segments between campaigns with 2nd constructor parameter
        //CampaignManager campMgr = new CampaignManager(fileName, 2);
        CampaignManager campMgr = new CampaignManager(fileName);
        System.out.println(campMgr.evaluateCampaign(input));
        System.out.println();
        System.out.println();
        long timeB = System.currentTimeMillis();
        System.out.println("Load and process time(ms):" + Long.toString(timeB - timeA));
    }
}