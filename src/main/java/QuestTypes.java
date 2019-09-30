public class QuestTypes {
    private static String[] questTypes = {"acquire", "ambush", "assassinate", "free", "transport", "capture", "locate",
            "investigate", "destroy", "slay", "scout"};

    public static String getQuestType() {
        return questTypes[RandomUtils.randomIntRange(0, questTypes.length - 1)];
    }

    public static void main(String [] args) {
        System.out.println(QuestTypes.getQuestType());
    }
}