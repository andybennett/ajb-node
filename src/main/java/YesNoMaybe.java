public class YesNoMaybe {
    public static String ask() {
        String result = "No";
        int rnd = RandomUtils.randomIntRange(1, 20);
        if (rnd >= 7 && rnd <= 12) {
            result = "Maybe";
        } else if (rnd > 12) {
            result = "Yes";
        }

        return result;
    }

    public static void main(String [] args) {
        System.out.println(YesNoMaybe.ask());

    }
}