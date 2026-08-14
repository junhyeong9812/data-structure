package com.datastructure.merkle;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 테스트가 공유하는 도구. 자료구조가 아니라 검증 장비다.
 *
 * 넷이 있다.
 *   1. 결정적 난수         - Random 을 쓰면 JDK 가 바뀔 때 기댓값이 흔들린다
 *   2. 해시 호출 계수기    - 시간을 재지 않고 계산 횟수를 센다
 *   3. 느린 참조 구현      - 층 배열을 안 쓰고 List 재귀로만 뿌리를 구한다. 대조용이다
 *   4. 전수 비교           - diffBlocks 의 답이 맞는지 볼 기준선
 *
 * 기댓값 hex 는 파이썬 hashlib 으로 따로 계산해 옮긴 것이다. 손으로 쓰지 않았다.
 */
final class TestSupport {

    private TestSupport() {
    }

    static byte[] bytes(String s) {
        return s.getBytes(StandardCharsets.UTF_8);
    }

    /** "block-0" 부터 n 개. */
    static List<byte[]> blocks(int n) {
        return blocks(n, "block-");
    }

    static List<byte[]> blocks(int n, String prefix) {
        List<byte[]> out = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            out.add(bytes(prefix + i));
        }
        return out;
    }

    static String hex(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (byte b : data) {
            sb.append(Character.forDigit((b >> 4) & 0xf, 16));
            sb.append(Character.forDigit(b & 0xf, 16));
        }
        return sb.toString();
    }

    static byte[] concat(byte[] left, byte[] right) {
        byte[] out = new byte[left.length + right.length];
        System.arraycopy(left, 0, out, 0, left.length);
        System.arraycopy(right, 0, out, left.length, right.length);
        return out;
    }

    /**
     * 느린 참조 구현. 층 배열도 인덱스 산술도 안 쓴다.
     * MerkleTree 와 같은 규칙(승격) 을 List 재귀로만 적어 서로를 대조한다.
     */
    static byte[] naiveRoot(List<byte[]> blocks, MerkleHashing hashing) {
        List<byte[]> level = new ArrayList<>();
        for (byte[] b : blocks) {
            level.add(hashing.leafHash(b));
        }
        while (level.size() > 1) {
            List<byte[]> next = new ArrayList<>();
            for (int i = 0; i < level.size(); i += 2) {
                if (i + 1 < level.size()) {
                    next.add(hashing.nodeHash(level.get(i), level.get(i + 1)));
                } else {
                    next.add(level.get(i));
                }
            }
            level = next;
        }
        return level.get(0);
    }

    /** 마지막 노드를 자기 자신과 짝지어 올리는 비트코인 방식. CVE-2012-2459 를 보이려고만 둔다. */
    static byte[] duplicatingRoot(List<byte[]> blocks, MerkleHashing hashing) {
        List<byte[]> level = new ArrayList<>();
        for (byte[] b : blocks) {
            level.add(hashing.leafHash(b));
        }
        while (level.size() > 1) {
            List<byte[]> next = new ArrayList<>();
            for (int i = 0; i < level.size(); i += 2) {
                byte[] left = level.get(i);
                byte[] right = i + 1 < level.size() ? level.get(i + 1) : left;
                next.add(hashing.nodeHash(left, right));
            }
            level = next;
        }
        return level.get(0);
    }

    /** 전수 비교. 이 답과 diffBlocks 의 답이 같아야 한다. */
    static List<Integer> naiveDiff(List<byte[]> local, List<byte[]> remote) {
        List<Integer> out = new ArrayList<>();
        for (int i = 0; i < local.size(); i++) {
            if (!Arrays.equals(local.get(i), remote.get(i))) {
                out.add(i);
            }
        }
        return out;
    }

    /** 해시가 몇 번 불렸는지 센다. 시간이 아니라 횟수를 재야 결정적이다. */
    static final class CountingHash implements HashFunction {

        private final HashFunction inner;
        private long calls;

        CountingHash(HashFunction inner) {
            this.inner = inner;
        }

        @Override
        public byte[] hash(byte[] data) {
            calls++;
            return inner.hash(data);
        }

        long calls() {
            return calls;
        }

        void reset() {
            calls = 0;
        }
    }

    /** 결정적 난수. 같은 seed 면 언제 어디서 돌려도 같은 값이 나온다. */
    static final class Dice {

        private long state;

        Dice(long seed) {
            this.state = seed;
        }

        int next(int bound) {
            state = state * 6364136223846793005L + 1442695040888963407L;
            return (int) Math.floorMod(state >>> 33, bound);
        }

        /** 길이 1 이상 16 이하의 아무 바이트 열. */
        byte[] block() {
            byte[] out = new byte[1 + next(16)];
            for (int i = 0; i < out.length; i++) {
                out[i] = (byte) next(256);
            }
            return out;
        }

        List<byte[]> blocks(int count) {
            List<byte[]> out = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                out.add(block());
            }
            return out;
        }
    }
}
