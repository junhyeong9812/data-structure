#!/usr/bin/env bash
#
# 자료구조 문제집 실행기.
#
#   ./run.sh          모든 문제의 테스트를 돌린다 (스켈레톤 상태에서는 실패가 정상)
#   ./run.sh 01       01번 문제만 돌린다
#   ./run.sh --check  impl/ 의 정답이 실제로 통과하는지 확인한다
#
# 종료 코드 계약
#   - 테스트 assertion 실패는 정상 상태다. 전체 실행에서는 exit 0.
#   - 컴파일 실패, Gradle 오류, 결과 XML 없음, impl/ 누락은 "인프라 실패"다. exit 1.
#     "돌려봤더니 아무것도 안 나왔다"를 성공으로 보고하면 안 된다.
#   - --check 는 정답이 하나라도 실패하거나 검증 대상이 0개면 exit 1.
#
# JDK 는 gradle.properties 에서 21 로 고정되어 있으므로 JAVA_HOME 을 export 할 필요가 없다.
# --check 는 src/main 을 건드리지 않는다. impl/ 이 검증 전용 소스셋이라 그대로 컴파일해 돌린다.

set -uo pipefail
cd "$(dirname "$0")"

GRADLE="./gradlew"
[ -x "$GRADLE" ] || GRADLE="gradle"

# 문제 목록의 진실 공급원은 파일시스템 하나다.
# 정규식은 settings.gradle.kts 의 PROBLEM_NAME 과 **글자 그대로 같아야 한다.**
# 다르면 한쪽만 발견하는 문제가 생기고, 그 드리프트는 조용히 통과한다.
PROBLEM_NAME='^[0-9]{2}-[a-z0-9]+(-[a-z0-9]+)*$'
probs() {
    for d in */; do
        d=${d%/}
        [[ $d =~ $PROBLEM_NAME ]] && printf '%s\n' "$d"
    done | sort
}

infra_rc=0        # 인프라 실패 누적 (컴파일/Gradle/결과없음)
test_rc=0         # 테스트 실패 누적

# 결과 XML 을 읽어 한 줄 요약. 결과가 없으면 인프라 실패로 본다.
summary() {
    local dir=$1 label=$2 total=0 fail=0 found=0
    for f in "$dir"/*.xml; do
        [ -e "$f" ] || continue
        found=1
        local t fl er
        t=$(grep -oE 'tests="[0-9]+"' "$f" | head -1 | grep -oE '[0-9]+')
        fl=$(grep -oE 'failures="[0-9]+"' "$f" | head -1 | grep -oE '[0-9]+')
        er=$(grep -oE 'errors="[0-9]+"' "$f" | head -1 | grep -oE '[0-9]+')
        total=$((total + t)); fail=$((fail + fl + er))
    done
    if [ "$found" -eq 0 ] || [ "$total" -eq 0 ]; then
        echo "  $label: 결과 없음 -- 컴파일 실패이거나 테스트가 발견되지 않았다"
        return 2                                   # 2 = 인프라 실패
    fi
    echo "  $label: 통과 $((total - fail)) / $total"
    [ "$fail" -eq 0 ] || return 1                  # 1 = 테스트 실패
}

# ---------------------------------------------------------------- --check
if [ "${1:-}" = "--check" ]; then
    checked=0
    while IFS= read -r p; do
        echo "=== $p impl 검증 ==="
        if [ ! -d "$p/impl" ] || [ -z "$(find "$p/impl" -name '*.java' -print -quit 2>/dev/null)" ]; then
            # 29개로 늘릴 때 impl/ 생성이 빠지거나 비어 있으면 조용히 넘어가면 안 된다.
            echo "  impl/ 이 없거나 비어 있다. 정답 없는 문제는 넘길 수 없다."
            infra_rc=1
            continue
        fi
        checked=$((checked + 1))
        rm -rf "$p/build/test-results/checkImplTest"
        $GRADLE ":$p:checkImplTest" --console=plain -q >/dev/null 2>&1
        gradle_rc=$?

        # 종료 코드만 보면 "테스트가 틀렸다"와 "빌드가 깨졌다"를 구분할 수 없다.
        # XML 을 먼저 읽어 분류하고, 결과가 없을 때만 인프라 실패로 본다.
        summary "$p/build/test-results/checkImplTest" "impl"
        case $? in
            1) test_rc=1
               echo "  정답이 테스트를 통과하지 못한다:  ./gradlew :$p:checkImplTest" ;;
            2) infra_rc=1
               echo "  빌드가 깨졌다:  ./gradlew :$p:checkImplTest" ;;
            0) [ "$gradle_rc" -eq 0 ] || { infra_rc=1; echo "  결과는 정상인데 gradle 이 실패했다. 확인 필요"; } ;;
        esac
    done < <(probs)

    if [ "$checked" -eq 0 ]; then
        echo "검증한 문제가 하나도 없다. 성공으로 볼 수 없다."
        exit 1
    fi
    [ "$infra_rc" -eq 0 ] && [ "$test_rc" -eq 0 ] || exit 1
    exit 0
fi

# ---------------------------------------------------------------- 전체 실행
if [ $# -eq 0 ]; then
    found=0
    while IFS= read -r p; do
        found=1
        echo; echo "########## $p ##########"
        rm -rf "$p/build/test-results/test"
        if ! $GRADLE ":$p:test" --console=plain -q >/dev/null 2>&1; then
            # ignoreFailures=true 라 assertion 실패로는 여기 안 온다. 오면 진짜 빌드 문제다.
            echo "  gradle 실패. 자세히 보려면:  ./gradlew :$p:test"
            infra_rc=1
            continue
        fi
        summary "$p/build/test-results/test" "스켈레톤"
        [ $? -eq 2 ] && infra_rc=1      # 테스트 실패(1)는 정상 상태이므로 무시
    done < <(probs)

    if [ "$found" -eq 0 ]; then
        echo "문제 디렉터리를 찾지 못했다."
        exit 1
    fi
    exit "$infra_rc"
fi

# ---------------------------------------------------------------- 개별 실행
p=$(probs | grep "^$1-" | head -1)
if [ -z "$p" ]; then
    echo "그런 문제가 없습니다: $1"; echo "사용 가능:"; probs | sed 's/^/  /'; exit 1
fi
exec $GRADLE ":$p:test" --console=plain
