rootProject.name = "data-structure"

// 문제 목록의 진실 공급원은 파일시스템 하나다.
// 여기에 목록을 손으로 적으면 디렉터리와 어긋나고, 그 드리프트는 조용히 통과한다.
//
// 이름 규칙: NN-소문자슬러그  (예: 01-dynamic-array)
// run.sh 가 같은 정규식을 쓴다. 둘이 어긋나면 한쪽만 발견하는 문제가 생긴다.
val PROBLEM_NAME = Regex("""^\d{2}-[a-z0-9]+(-[a-z0-9]+)*$""")

rootDir.listFiles { f -> f.isDirectory && PROBLEM_NAME.matches(f.name) }
    ?.sortedBy { it.name }
    ?.forEach { include(it.name) }
