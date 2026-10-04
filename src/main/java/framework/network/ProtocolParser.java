package framework.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProtocolParser {
    private static final Pattern KEY_VALUE_PATTERN = Pattern.compile("(\\w+):\\s*\"([^\"]*)\"");
    private static final Pattern LIST_ITEM_PATTERN = Pattern.compile("\"([^\"]*)\"");

    public static Map<String, String> parseMap(String message) {
        Map<String, String> result = new HashMap<>();
        int start = message.indexOf('{');
        int end = message.lastIndexOf('}');
        if (start != -1 && end > start) {
            String body = message.substring(start + 1, end);
            Matcher matcher = KEY_VALUE_PATTERN.matcher(body);
            while (matcher.find()) {
                result.put(matcher.group(1).toUpperCase(), matcher.group(2));
            }
        }
        return result;
    }

    public static List<String> parseList(String message) {
        List<String> items = new ArrayList<>();
        int start = message.indexOf('[');
        int end = message.lastIndexOf(']');
        if (start != -1 && end > start) {
            String body = message.substring(start + 1, end);
            Matcher matcher = LIST_ITEM_PATTERN.matcher(body);
            while (matcher.find()) {
                items.add(matcher.group(1));
            }
        }
        return items;
    }
}