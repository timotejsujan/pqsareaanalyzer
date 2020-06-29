package external;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Timotej Sujan
 */
class genom {
    genom(String name) {
        this.name = name;
    }
    String name;
    List<scaffold> scaffolds = new ArrayList<>();
}
