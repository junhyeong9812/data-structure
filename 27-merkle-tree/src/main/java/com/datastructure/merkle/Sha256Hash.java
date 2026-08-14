package com.datastructure.merkle;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 진짜 해시. SHA-256 은 32바이트를 낸다.
 *
 * MessageDigest 는 상태를 가지므로 스레드마다 새로 만든다.
 * 하나를 필드에 두고 나눠 쓰면 두 스레드가 같은 버퍼에 update 를 섞어 넣어
 * 조용히 틀린 해시가 나온다. 그 값은 예외도 안 내고 그냥 다르다.
 *
 * 이 클래스에는 TODO 가 없다.
 */
public final class Sha256Hash implements HashFunction {

    @Override
    public byte[] hash(byte[] data) {
        if (data == null) {
            throw new IllegalArgumentException("입력은 null 일 수 없다");
        }
        try {
            return MessageDigest.getInstance("SHA-256").digest(data);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 은 모든 자바 구현이 반드시 제공한다. 여기 오면 JDK 가 깨진 것이다.
            throw new IllegalStateException("SHA-256 을 찾을 수 없다", e);
        }
    }

    @Override
    public String toString() {
        return "SHA-256";
    }
}
