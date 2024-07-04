package io.fireflyest.relatelock.util;

import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.util.NumberConversions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 文本工具类
 * @author Fireflyest
 * @since 1.0
 */
public final class StrUtils {
    
    private static final Gson gson = new Gson();

    /**
     * 变量格式 %key%
     */
    public static final Pattern PERCENT_PATTERN = Pattern.compile("%([^%]*)%");

    /**
     * 变量格式 ${key}
     */
    public static final Pattern BRACE_PATTERN = Pattern.compile("\\$\\{([^{}]*)}");

    /**
     * 变量格式 {index}
     */
    public static final Pattern FORMAT_PATTERN = Pattern.compile("\\{([^{}]*)}");

    /**
     * 驼峰后面的每个首大写字母
     */
    public static final Pattern CASE_BORDER_LETTER = Pattern.compile("(?<=[a-z])[A-Z]");
    
    /**
     * 用于识别被分割的单词
     */
    public static final Pattern SPLIT_WORDS = Pattern.compile("[a-zA-Z0-9]+");

    /**
     * 判断是否纯文本
     */
    public static final Pattern PURE_TEXT = Pattern.compile("^[a-zA-Z0-9]+$");

    /**
     * 小驼峰
     */
    public static final String CAMEL = "camelCamel";

    /**
     * 常量
     */
    public static final String CONSTANT = "CONSTANT_CONSTANT";

    /**
     * 点分割
     */
    public static final String DOT = "dot.dot";

    /**
     * 大驼峰
     */
    public static final String PASCAL = "PascalPascal";

    /**
     * 路径
     */
    public static final String PATH = "path\\path";

    /**
     * 句子
     */
    public static final String SENTENCE = "sentence sentence";

    /**
     * 下划线分割
     */
    public static final String SNAKE = "snake_snake";

    /**
     * 标题
     */
    public static final String TITLE = "Title Title";

    /**
     * 用于获取对应变量
     */
    public interface VariableContainer {
        String getVar(String key);
    }

    private StrUtils() {
        // 
    }

    /**
     * 变量替换
     * @param pattern 正则表达式的编译表示
     * @param str 需要变量替换的文本
     * @param begin 变量前方包裹符号的数量
     * @param end 变量后方包裹符号的数量
     * @param container 存储变量的容器
     * @return 替换后的文本
     */
    public static String varReplace(@Nonnull Pattern pattern, 
                                    @Nullable String str, 
                                    int begin, 
                                    int end, 
                                    @Nonnull VariableContainer container) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        final Matcher varMatcher = pattern.matcher(str);
        final StringBuilder stringBuilder = new StringBuilder();
        while (varMatcher.find()) {
            final String parameter = varMatcher.group();
            final String parameterName = parameter.substring(begin, parameter.length() - end);
            varMatcher.appendReplacement(stringBuilder, container.getVar(parameterName));
        }
        varMatcher.appendTail(stringBuilder);
        return stringBuilder.toString();
    }

    /**
     * 格式化文本
     * @param text 文本
     * @param vars 变量
     * @return 文本
     */
    public static String format(@Nullable String text, Object... vars) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return varReplace(FORMAT_PATTERN, text, 1, 1, new VariableContainer() {

            private int index = 0;

            @Override
            public String getVar(String key) {
                // 有序号用序号，没有序号按顺序给
                if (!"".equals(key)) {
                    index = NumberConversions.toInt(key);
                }
                // 判断是否越界
                if (index >= vars.length) {
                    index = vars.length - 1;
                }
                return String.valueOf(vars[index++]);
            }

        });
    }

    /**
     * 列表转换为文本
     * @param obj 列表
     * @return 文本
     */
    public static String toJson(@Nonnull Object obj) {
        return gson.toJson(obj);
    }

    /**
     * 文本转列表
     * @param <T> 对象类型
     * @param str 文本
     * @return 列表
     */
    public static <T> List<T> jsonToList(@Nullable String str) {
        if (str == null || str.isEmpty()) {
            return Collections.emptyList();
        }
        return gson.fromJson(str, new TypeToken<List<T>>() {}.getType());
    }

    /**
     * 文本转对象
     * @param <T> 对象类型
     * @param str 文本
     * @param type 对象的类
     * @return 对象
     */
    public static <T> T jsonToObj(@Nullable String str, Class<T> type) {
        return str == null ? null : gson.fromJson(str, type);
    }

    /**
     * 拼接字符串
     * @param objs 要拼接的对象
     * @return 拼接结果
     */
    public static String append(@Nonnull Object... objs) {
        final StringBuilder sb = new StringBuilder();
        for (Object obj : objs) {
            sb.append(obj);
        }
        return sb.toString();
    }

    /**
     * 转为Base64
     * 
     * @param str 文本
     * @return Base64格式
     */
    public static String base64Encode(String str) {
        return Base64.getEncoder().encodeToString(str.getBytes());
    }

    /**
     * 转Base64为普通文本
     * 
     * @param str Base64格式文本
     * @return 普通文本
     */
    public static String base64Decode(String str) {
        return new String(Base64.getDecoder().decode(str));
    }

    /**
     * test转换为Test
     * @param str 文本
     * @return 首字母大写
     */
    public static String upperFirst(@Nullable String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1).toLowerCase();
    }

    /**
     * 其他格式转为句子格式
     * @param str 文本
     * @param formType 原格式
     * @return 句子格式文本
     */
    public static String toSentence(@Nullable String str, @Nonnull String formType) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        switch (formType) {
            case CAMEL:
            case PASCAL:
                // 大小驼峰按大小写变化分割
                return varReplace(CASE_BORDER_LETTER, str, 0, 0, key -> " " + key.toLowerCase());
            case CONSTANT:
            case DOT:
            case PATH:
            case SNAKE:
            case TITLE:
            default:
                // 其他用符号分割的
                final Matcher wordMatcher = SPLIT_WORDS.matcher(str);
                final StringBuilder stringBuilder = new StringBuilder();
                boolean first = true;
                while (wordMatcher.find()) {
                    if (!first) {
                        stringBuilder.append(" ");
                    }
                    stringBuilder.append(wordMatcher.group().toLowerCase());
                    first = false;
                }
                return stringBuilder.toString();
        }
    }

    /**
     * 其他格式转为句子格式
     * @param str 文本
     * @return 句子格式文本
     */
    public static String toSentence(@Nullable String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        // 驼峰是纯文本
        if (PURE_TEXT.matcher(str).matches()) {
            return toSentence(str, CAMEL);
        }
        // 其他用符号分割
        return toSentence(str, "");
    }

}
