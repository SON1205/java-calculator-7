package calculator.mvc.model;

import java.util.ArrayList;
import java.util.List;

public class CalculatorImpl implements Calculator {
    private List<Character> separators = new ArrayList<>();
    private List<String> customSeparators = List.of("//", "\\n");
    private List<Long> numbers = new ArrayList<>();
    private static Calculator instance = new CalculatorImpl();

    private CalculatorImpl() {
        separators.add(',');
        separators.add(':');
    }

    public static Calculator getInstance() {
        return instance;
    }

    public static Calculator reset() {
        return new CalculatorImpl();
    }

    public void findCustomSeparator(String input) throws IllegalStateException {
        int idx = 2;

        if (existCustomSeparator(input)) {
            if ('0' <= input.charAt(idx) && input.charAt(idx) <= '9') {
                throw new IllegalArgumentException("커스텀 구분자로 숫자를 입력할 수 없습니다.");
            }

            separators.add(input.charAt(idx));
        }
    }

    private boolean existCustomSeparator(String input) throws IllegalStateException {
        if (input.startsWith(customSeparators.get(0))) {
            if (input.indexOf(customSeparators.get(1)) == -1) {
                throw new IllegalArgumentException("\"\\n\"가 문자열에 존재하지 않습니다.");
            }

            if (input.indexOf(customSeparators.get(1)) > 3) {
                throw new IllegalArgumentException("길이 1인 커스텀 구분자를 입력하지 않았습니다.");
            }

            if (input.indexOf(customSeparators.get(1)) == 3) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void parseNumbersFromString(String input) throws IllegalStateException {
        boolean hasCustomSeparator = existCustomSeparator(input);

        StringBuilder regExTmp = new StringBuilder();

        regExTmp.append("[");
        for (int i = 0; i < separators.size(); i++) {
            if (separators.get(i) == '\\') {
                regExTmp.append('\\').append("\\");
                continue;
            }

            regExTmp.append(separators.get(i));
        }
        regExTmp.append("]");

        if (hasCustomSeparator) {
            input = input.substring(5);
        }

        //여기서 오류가 계속 남
        // //[\n1,2[3
        String[] tokens = input.split(regExTmp.toString());

        for (String s : tokens) {
            try {
                Long num = Long.valueOf(s);
                numbers.add(num);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("피연산자가 숫자가 아닙니다.");
            }
        }
    }

    @Override
    public long addNumbers() throws IllegalArgumentException {
        long tmp = 0;

        for (Long num : numbers) {
            if (num <= 0) {
                throw new IllegalArgumentException("양수가 아니므로 더하기 계산을 할 수 없습니다.");
            }

            if (tmp > Long.MAX_VALUE - num) {
                throw new IllegalArgumentException("결과값이 유효한 범위를 벗어났습니다.");
            }

            tmp += num;
        }

        return tmp;
    }
}
