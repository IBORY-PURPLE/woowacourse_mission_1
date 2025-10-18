package calculator;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.regex.Pattern;

public class Application {
    public static void main(String[] args) throws IOException {
        System.out.println("덧셈할 문자열을 입력해 주세요.");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String input = br.readLine();
            int result = add(input);
            System.out.println("결과 : " + result);
        } catch (IllegalArgumentException e) {
            // 요구사항: 잘못된 입력 시 IllegalArgumentException 발생 후 종료
            System.err.println("입력 오류: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * 규칙:
     * - "" => 0
     * - 기본 구분자: ',' 또는 ':'
     * - 커스텀 구분자: "//<구분자>\\n" (한 글자)
     * - 모든 토큰은 양의 정수여야 함(>0)
     * - 잘못된 형식/값이면 IllegalArgumentException
     */
    public static int add(String input) {
        if (input == null || input.isEmpty()) return 0;

        String delimitersRegex = "[,:]"; // 기본 구분자
        String numbers = input;

        // 커스텀 구분자 처리: //X\n...
        if (input.startsWith("//")) {
            int newlineIdx = input.indexOf('\n');
            if (newlineIdx < 0) {
                throw new IllegalArgumentException("커스텀 구분자 선언 뒤에 줄바꿈(\\n)이 필요합니다.");
            }
            String custom = input.substring(2, newlineIdx);
            if (custom.isEmpty() || custom.length() != 1) {
                throw new IllegalArgumentException("커스텀 구분자는 정확히 한 글자여야 합니다.");
            }
            String customEscaped = Pattern.quote(custom);
            delimitersRegex = customEscaped + "|" + delimitersRegex; // (?:custom)|[,:] 와 동일 효과
            numbers = input.substring(newlineIdx + 1);
        }

        // 분리
        String[] tokens = numbers.split(delimitersRegex, -1); // -1: 빈 토큰도 유지(검증용)
        if (tokens.length == 0) return 0;

        int sum = 0;
        for (String raw : tokens) {
            String t = raw.trim();
            if (t.isEmpty()) {
                throw new IllegalArgumentException("구분자 연속 사용 또는 비어있는 숫자 토큰이 있습니다.");
            }
            int value;
            try {
                value = Integer.parseInt(t);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("숫자가 아닌 값이 포함되어 있습니다: " + t);
            }
            if (value <= 0) {
                throw new IllegalArgumentException("모든 숫자는 양의 정수여야 합니다: " + value);
            }
            sum += value;
        }
        return sum;
    }
}
