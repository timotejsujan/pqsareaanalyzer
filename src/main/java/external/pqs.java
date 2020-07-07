package external;

/**
 * @author Timotej Sujan
 */
class pqs {
    public pqs(int s, int e, boolean b, int seg, String seq) {
        segment = seg;
        start = s;
        end = e;
        strand = b;
        sequence = seq;
    }

    int segment;
    int start;
    int end;
    Boolean strand;
    String sequence;
}
