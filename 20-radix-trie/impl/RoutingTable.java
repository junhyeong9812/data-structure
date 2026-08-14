package com.datastructure.radix;

public class RoutingTable {

    private final RadixTrie<String> routes = new RadixTrie<>();

    public void add(String cidr, String nextHop) {
        if (cidr == null) {
            throw new IllegalArgumentException("null 은 CIDR 이 아니다");
        }
        if (nextHop == null) {
            throw new IllegalArgumentException("null 은 next hop 이 아니다");
        }
        int slash = cidr.indexOf('/');
        if (slash < 0 || cidr.indexOf('/', slash + 1) >= 0) {
            throw new IllegalArgumentException("CIDR 은 주소/길이 형식이다: " + cidr);
        }
        int len = prefixLength(cidr.substring(slash + 1));
        routes.put(toBits(cidr.substring(0, slash)).substring(0, len), nextHop);
    }

    public String lookup(String ip) {
        String key = routes.longestPrefixOf(toBits(ip));
        return key == null ? null : routes.get(key);
    }

    public int size() {
        return routes.size();
    }

    static String toBits(String ip) {
        if (ip == null) {
            throw new IllegalArgumentException("null 은 IP 가 아니다");
        }
        String[] parts = ip.split("\\.", -1);
        if (parts.length != 4) {
            throw new IllegalArgumentException("옥텟이 4개가 아니다: " + ip);
        }
        StringBuilder bits = new StringBuilder(32);
        for (String part : parts) {
            int octet = octet(part, ip);
            for (int b = 7; b >= 0; b--) {
                bits.append((octet >>> b) & 1);
            }
        }
        return bits.toString();
    }

    private static int octet(String part, String ip) {
        if (part.isEmpty() || part.length() > 3) {
            throw new IllegalArgumentException("옥텟 형식이 아니다: '" + part + "' (" + ip + ")");
        }
        for (int i = 0; i < part.length(); i++) {
            if (part.charAt(i) < '0' || part.charAt(i) > '9') {
                throw new IllegalArgumentException("숫자가 아니다: '" + part + "' (" + ip + ")");
            }
        }
        if (part.length() > 1 && part.charAt(0) == '0') {
            throw new IllegalArgumentException("앞자리 0 은 받지 않는다: '" + part + "' (" + ip + ")");
        }
        int value = Integer.parseInt(part);
        if (value > 255) {
            throw new IllegalArgumentException("옥텟은 0~255 다: '" + part + "' (" + ip + ")");
        }
        return value;
    }

    private static int prefixLength(String text) {
        if (text.isEmpty() || text.length() > 2) {
            throw new IllegalArgumentException("prefix 길이 형식이 아니다: '" + text + "'");
        }
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) < '0' || text.charAt(i) > '9') {
                throw new IllegalArgumentException("prefix 길이가 숫자가 아니다: '" + text + "'");
            }
        }
        int len = Integer.parseInt(text);
        if (len > 32) {
            throw new IllegalArgumentException("prefix 길이는 0~32 다: " + len);
        }
        return len;
    }
}
