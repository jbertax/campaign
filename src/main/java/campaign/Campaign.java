package campaign;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Campaign {
    private String name;
    private List<Integer> segments = new ArrayList<>();

    /**
     * Digests String data of campaign
     * For instance: selma 22207 28404 28113 24604 15686 17632 7975
     *
     * @param data First word is name, others are segment numbers
     */
    public Campaign(String data) {
        this.name = data.substring(0, data.indexOf(' '));
        this.segments = Arrays.stream(data.substring(data.indexOf(' ') + 1).split(" "))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    /**
     * Checks how many of the given segments this campaign has.
     *
     * @param segments The Integer set of segments to check
     * @return How many of the given segments this campaign has
     */
    public int checkSegments(Set<Integer> segments) {
        Predicate<Integer> predicate = s -> segments.contains(s);
        long found = this.segments.stream().filter(predicate).count();
        return (int) found;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List getSegments() {
        return segments;
    }

    public void setSegments(List segments) {
        this.segments = segments;
    }
}
