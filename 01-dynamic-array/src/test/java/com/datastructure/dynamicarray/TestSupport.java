package com.datastructure.dynamicarray;

import java.lang.reflect.Field;

/**
 * 테스트 공용 도우미.
 *
 * 보통 테스트는 공개 API 만 봐야 하지만, "size 밖 칸의 참조를 끊었는가"는 공개 API 로 관측할 수 없다.
 * toArray() 는 size 만큼만 복사하므로 끊었든 안 끊었든 결과가 같기 때문이다.
 * 그런데 그게 이 자료구조의 핵심 레슨이라 예외적으로 내부를 본다.
 *
 * 그래서 필드 이름 elements 는 계약의 일부다. 바꾸면 이 테스트가 깨진다.
 */
final class TestSupport {

    private TestSupport() {
    }

    static Object[] raw(DynamicArray<?> array) {
        try {
            Field f = DynamicArray.class.getDeclaredField("elements");
            f.setAccessible(true);
            return (Object[]) f.get(array);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("내부 배열 필드 이름은 elements 여야 한다", e);
        }
    }
}
