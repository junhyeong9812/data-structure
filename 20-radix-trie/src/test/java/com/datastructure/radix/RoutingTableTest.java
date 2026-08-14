package com.datastructure.radix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * IP 최장 접두사 매칭.
 *
 * 이것이 longestPrefixOf 가 존재하는 이유다.
 * 라우터는 "이 주소에 정확히 맞는 규칙"이 아니라 "이 주소를 덮는 가장 구체적인 규칙"을 찾는다.
 * 10.1.2.3 은 0.0.0.0/0 에도, 10.0.0.0/8 에도, 10.1.2.0/24 에도 걸린다. 이길 것은 가장 긴 것이다.
 */
@DisplayName("RoutingTable")
class RoutingTableTest {

    static final List<String[]> ROUTES = List.of(
            new String[]{"0.0.0.0/0", "default"},
            new String[]{"10.0.0.0/8", "corp"},
            new String[]{"10.1.0.0/16", "branch"},
            new String[]{"10.1.2.0/24", "lab"},
            new String[]{"192.168.0.0/16", "home"});

    static RoutingTable sample() {
        RoutingTable rt = new RoutingTable();
        for (String[] r : ROUTES) {
            rt.add(r[0], r[1]);
        }
        return rt;
    }

    @Nested
    @DisplayName("주소를 32비트 문자열로")
    class ToBits {

        @Test
        @DisplayName("옥텟마다 8비트")
        void octets() {
            assertEquals("00000000000000000000000000000000", RoutingTable.toBits("0.0.0.0"));
            assertEquals("11111111111111111111111111111111",
                    RoutingTable.toBits("255.255.255.255"));
            assertEquals("00001010000000000000000000000000", RoutingTable.toBits("10.0.0.0"));
            assertEquals("11000000101010000000000000000000", RoutingTable.toBits("192.168.0.0"));
            assertEquals("10101100000100000000000000000000", RoutingTable.toBits("172.16.0.0"));
            assertEquals("00001010000000010000001000000000", RoutingTable.toBits("10.1.2.0"));
        }

        @Test
        @DisplayName("길이는 언제나 32")
        void alwaysThirtyTwo() {
            Random rnd = new Random(3L);
            for (int i = 0; i < 200; i++) {
                String ip = rnd.nextInt(256) + "." + rnd.nextInt(256) + "."
                        + rnd.nextInt(256) + "." + rnd.nextInt(256);
                assertEquals(32, RoutingTable.toBits(ip).length(), ip);
            }
        }
    }

    @Nested
    @DisplayName("최장 접두사가 이긴다")
    class LongestWins {

        @Test
        @DisplayName("가장 구체적인 규칙으로 간다")
        void mostSpecific() {
            RoutingTable rt = sample();
            assertEquals("lab", rt.lookup("10.1.2.3"));
            assertEquals("lab", rt.lookup("10.1.2.255"));
            assertEquals("branch", rt.lookup("10.1.9.9"));
            assertEquals("corp", rt.lookup("10.9.9.9"));
            assertEquals("home", rt.lookup("192.168.5.5"));
            assertEquals("default", rt.lookup("8.8.8.8"));
            assertEquals("default", rt.lookup("192.169.0.1"));
        }

        @Test
        @DisplayName("등록 순서와 무관하다")
        void orderIndependent() {
            List<String[]> shuffled = new ArrayList<>(ROUTES);
            Collections.shuffle(shuffled, new Random(31L));
            RoutingTable rt = new RoutingTable();
            for (String[] r : shuffled) {
                rt.add(r[0], r[1]);
            }
            assertEquals("lab", rt.lookup("10.1.2.3"));
            assertEquals("branch", rt.lookup("10.1.9.9"));
            assertEquals("corp", rt.lookup("10.9.9.9"));
            assertEquals("default", rt.lookup("8.8.8.8"));
        }

        @Test
        @DisplayName("기본 경로가 없으면 null 이다")
        void noDefaultRoute() {
            RoutingTable rt = new RoutingTable();
            for (String[] r : ROUTES) {
                if (!r[0].equals("0.0.0.0/0")) {
                    rt.add(r[0], r[1]);
                }
            }
            assertNull(rt.lookup("8.8.8.8"));
            assertNull(rt.lookup("11.0.0.1"));
            assertEquals("lab", rt.lookup("10.1.2.3"));
        }

        @Test
        @DisplayName("/0 은 모든 주소를 덮는다")
        void defaultCoversEverything() {
            RoutingTable rt = new RoutingTable();
            rt.add("0.0.0.0/0", "default");
            assertEquals(1, rt.size());
            assertEquals("default", rt.lookup("0.0.0.0"));
            assertEquals("default", rt.lookup("255.255.255.255"));
            assertEquals("default", rt.lookup("10.1.2.3"));
        }

        @Test
        @DisplayName("/32 는 주소 하나만 덮는다")
        void hostRoute() {
            RoutingTable rt = new RoutingTable();
            rt.add("10.0.0.0/8", "corp");
            rt.add("10.1.2.3/32", "host");
            assertEquals("host", rt.lookup("10.1.2.3"));
            assertEquals("corp", rt.lookup("10.1.2.4"));
        }

        @Test
        @DisplayName("prefix 길이 밖의 비트는 무시한다")
        void hostBitsAreMasked() {
            // 10.1.2.3/8 은 10.0.0.0/8 과 같은 규칙이다. 라우터가 마스크를 씌우는 것과 같다.
            RoutingTable a = new RoutingTable();
            a.add("10.1.2.3/8", "corp");
            RoutingTable b = new RoutingTable();
            b.add("10.0.0.0/8", "corp");
            assertEquals("corp", a.lookup("10.99.99.99"));
            assertEquals(b.lookup("10.99.99.99"), a.lookup("10.99.99.99"));
            assertEquals(1, a.size());
        }

        @Test
        @DisplayName("같은 prefix 를 다시 넣으면 덮어쓴다")
        void samePrefixOverwrites() {
            RoutingTable rt = new RoutingTable();
            rt.add("10.0.0.0/8", "old");
            rt.add("10.0.0.0/8", "new");
            assertEquals(1, rt.size());
            assertEquals("new", rt.lookup("10.1.1.1"));
        }
    }

    @Nested
    @DisplayName("파싱 오류")
    class Parsing {

        private void bad(String cidr) {
            assertThrows(IllegalArgumentException.class,
                    () -> new RoutingTable().add(cidr, "hop"), "받아들이면 안 된다: " + cidr);
        }

        @Test
        @DisplayName("옥텟이 이상하면 거부")
        void badOctets() {
            bad("256.0.0.0/8");
            bad("10.0.0/8");
            bad("10.0.0.0.0/8");
            bad("10.0.0.a/8");
            bad("10.0.0./8");
            bad("10..0.0/8");
            bad("/8");
            bad("10.0.0.-1/8");
            bad("10.0.0.0000/8");
        }

        @Test
        @DisplayName("앞자리 0 은 거부한다")
        void leadingZero() {
            // 023 을 8진수 19 로 읽는 구현이 실제로 있다. 같은 문자열이 라이브러리마다 다른 주소가 된다.
            // 답이 갈리는 입력은 아예 안 받는 편이 낫다.
            bad("010.0.0.1/8");
            bad("10.0.0.01/8");
        }

        @Test
        @DisplayName("prefix 길이가 이상하면 거부")
        void badPrefixLength() {
            bad("10.0.0.0/33");
            bad("10.0.0.0/-1");
            bad("10.0.0.0/x");
            bad("10.0.0.0/");
            bad("10.0.0.0");
            bad("10.0.0.0/8/8");
            bad("10.0.0.0/100");
        }

        @Test
        @DisplayName("null 은 거부")
        void nulls() {
            RoutingTable rt = new RoutingTable();
            assertThrows(IllegalArgumentException.class, () -> rt.add(null, "hop"));
            assertThrows(IllegalArgumentException.class, () -> rt.add("10.0.0.0/8", null));
            assertThrows(IllegalArgumentException.class, () -> rt.lookup(null));
        }

        @Test
        @DisplayName("lookup 도 주소를 검사한다")
        void lookupValidates() {
            RoutingTable rt = sample();
            assertThrows(IllegalArgumentException.class, () -> rt.lookup("10.0.0"));
            assertThrows(IllegalArgumentException.class, () -> rt.lookup("300.0.0.1"));
            assertThrows(IllegalArgumentException.class, () -> rt.lookup("10.0.0.0/8"));
        }
    }

    @Nested
    @DisplayName("전수 조사와 대조")
    class BruteForce {

        @Test
        @DisplayName("무작위 주소 2000개를 마스크 계산과 대조한다")
        void matchesMaskArithmetic() {
            Random rnd = new Random(20260814L);
            List<int[]> rules = new ArrayList<>();     // {네트워크 주소, prefix 길이}
            RoutingTable rt = new RoutingTable();
            for (int i = 0; i < 60; i++) {
                int len = rnd.nextInt(33);
                int addr = rnd.nextInt();
                int network = len == 0 ? 0 : (addr & (int) (0xFFFFFFFFL << (32 - len)));
                rules.add(new int[]{network, len, i});
                rt.add(dotted(network) + "/" + len, "hop" + i);
            }

            for (int q = 0; q < 2000; q++) {
                int ip = rnd.nextInt();
                int bestLen = -1;
                int bestId = -1;
                for (int[] r : rules) {
                    int mask = r[1] == 0 ? 0 : (int) (0xFFFFFFFFL << (32 - r[1]));
                    if ((ip & mask) == r[0] && r[1] >= bestLen) {
                        // 같은 길이면 나중에 등록한 것이 덮어썼다
                        bestLen = r[1];
                        bestId = r[2];
                    }
                }
                String expected = bestLen < 0 ? null : "hop" + bestId;
                assertEquals(expected, rt.lookup(dotted(ip)), "ip=" + dotted(ip));
            }
        }

        private static String dotted(int addr) {
            return ((addr >>> 24) & 255) + "." + ((addr >>> 16) & 255) + "."
                    + ((addr >>> 8) & 255) + "." + (addr & 255);
        }
    }
}
