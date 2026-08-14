package com.datastructure.interval;

import java.util.Arrays;
import java.util.List;

/**
 * [구현] 좌표 압축. 등장하는 좌표만 0 부터 번호를 다시 매긴다.
 *
 * 왜 필요한가. 13번 세그먼트 트리로 겹침을 풀려면 배열 한 칸이 좌표 한 개다.
 * 좌표가 0 부터 10 억까지 흩어져 있으면 배열이 10 억 칸이고, 관행대로 4n 을 잡으면 40 억 칸이다.
 * 그런데 구간이 1000 개면 실제로 등장하는 좌표는 아무리 많아도 2000 개다.
 * 나머지 칸은 전부 아무 구간의 경계도 아니라서, 있어도 답이 안 바뀐다.
 *
 * 순서만 보존하면 겹침 판정은 그대로다. 겹침은 좌표의 크기 비교로만 정해지기 때문이다.
 * 그래서 등장하는 좌표를 정렬해 0, 1, 2 ... 로 갈아끼워도 답이 같다.
 *
 * 압축된 구간도 [start, end) 반개구간이고 start 가 end 보다 작다.
 * 원래 start 가 end 보다 작았고 둘 다 좌표 목록에 있으므로 번호도 순서를 지킨다.
 */
public final class CoordinateCompressor {

    private final long[] coordinates;      // 정렬된 유일 좌표. 인덱스가 곧 압축된 값이다

    public CoordinateCompressor(List<Interval> intervals) {
        if (intervals == null) {
            throw new IllegalArgumentException("구간 목록이 null 이다");
        }
        long[] raw = new long[intervals.size() * 2];
        int at = 0;
        for (Interval iv : intervals) {
            if (iv == null) {
                throw new IllegalArgumentException("구간이 null 이다");
            }
            raw[at++] = iv.start;
            raw[at++] = iv.end;
        }
        Arrays.sort(raw);

        int unique = 0;
        for (int i = 0; i < raw.length; i++) {
            if (i == 0 || raw[i] != raw[i - 1]) {
                raw[unique++] = raw[i];
            }
        }
        this.coordinates = Arrays.copyOf(raw, unique);
    }

    /** 등장한 좌표의 개수. 구간이 n 개면 아무리 많아도 2n 이다. */
    public int size() {
        return coordinates.length;
    }

    /**
     * 좌표를 번호로. 등장하지 않은 좌표면 IllegalArgumentException.
     *
     * 없는 좌표에 "가장 가까운 번호"를 돌려주면 왕복이 조용히 깨진다.
     * 압축은 등장한 좌표에 대해서만 정의된다.
     */
    public int compressPoint(long value) {
        int at = Arrays.binarySearch(coordinates, value);
        if (at < 0) {
            throw new IllegalArgumentException("등장하지 않은 좌표다: " + value);
        }
        return at;
    }

    /** 번호를 좌표로. */
    public long decompressPoint(int index) {
        if (index < 0 || index >= coordinates.length) {
            throw new IllegalArgumentException("번호가 범위 밖이다: " + index);
        }
        return coordinates[index];
    }

    /** 구간을 번호 구간으로. */
    public Interval compress(Interval iv) {
        if (iv == null) {
            throw new IllegalArgumentException("구간이 null 이다");
        }
        return new Interval(compressPoint(iv.start), compressPoint(iv.end));
    }

    /** 번호 구간을 원래 구간으로. compress 의 역이다. */
    public Interval decompress(Interval compressed) {
        if (compressed == null) {
            throw new IllegalArgumentException("구간이 null 이다");
        }
        return new Interval(decompressPoint((int) compressed.start),
                decompressPoint((int) compressed.end));
    }
}
