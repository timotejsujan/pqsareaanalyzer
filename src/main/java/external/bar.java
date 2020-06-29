package external;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Timotej Sujan
 */
class bar
{
    Map<Character, Integer> bases = new HashMap<>();
    double r = 0;

    double rel_freq(int n) {
        return (double)n/count();
    }

    int count() {
        return bases.values().stream().reduce(0, Integer::sum);
    }

    double h(Character key) {
        double rf = rel_freq(bases.get(key));
        return rf * log2(rf);
    }

    private double log2(double x)
    {
        return (x == 0 ?  0 : Math.log(x) / Math.log(2));
    }
}
