package campaign;

import java.io.Console;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public void loadCampaignData(String fileName) {

        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            stream.forEach(this::addCampaign);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String evaluateCampaign(String input) {
        Set inputSet = Stream.of(input.split(" "))
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
        // input parsed to a set
        Map<String, Integer> campaignResultMap = new HashMap();
        Iterator<Campaign> ite = this.campaigns.iterator();
        while (ite.hasNext()) {
            Campaign cam = ite.next();
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
        CampaignManager campMgr = new CampaignManager(fileName);
//        use this below to setup how much campaigns to use to prevent 'staver'
//        CampaignManager campMgr = new CampaignManager(fileName, 2);
        String input = "4763 1732";  // in ide: no input
        Console console = System.console();
        if (console != null) {
            input = console.readLine("");
        }
        System.out.println(campMgr.evaluateCampaign(input));
    }
}