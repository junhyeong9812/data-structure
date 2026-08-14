package com.datastructure.searchindex;

/**
 * [구현] tf 곱하기 log(N 나누기 df).
 *
 * TF-IDF 는 하나가 아니라 계열이다. tf 에 로그를 씌우기도 하고,
 * df 에 1 을 더해 0 나눗셈을 막기도 하고(log(N/(1+df))), 문서 길이로 나누기도 한다.
 * 여기서는 가장 기본형 하나를 골라 고정한다. 고르지 않으면 기댓값을 쓸 수 없다.
 *
 *   점수 = tf 곱하기 자연로그(N 나누기 df)
 *
 * 나눗셈을 double 로 해야 한다. int 로 나누면 N 이 100 이고 df 가 60 일 때
 * 100/60 이 1 이 되어 log(1) = 0 이다. 흔한 항이 전부 0 점이 되고, 그 사이의 순위 차이가 통째로 사라진다.
 * 값이 이상해질 뿐 예외는 안 나므로 눈으로는 못 찾는다.
 *
 * df 가 N 과 같으면(모든 문서에 있는 항) log(1) = 0 이라 점수가 0 이다.
 * 버그가 아니라 이 식의 뜻이다. 모든 문서에 있는 말은 문서를 가르지 못한다.
 * 그래서 문서가 하나뿐인 색인에서는 모든 항의 점수가 0 이고 순위가 문서 번호 순으로 무너진다.
 */
public class TfIdfScorer implements Scorer {

    @Override
    public double score(int termFrequency, int documentFrequency, int documentCount) {
        if (termFrequency <= 0 || documentFrequency <= 0 || documentCount <= 0) {
            return 0.0;
        }
        return termFrequency * Math.log((double) documentCount / documentFrequency);
    }

    @Override
    public String toString() {
        return "TF-IDF";
    }
}
