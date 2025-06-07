// package quochung.server.util;

// import java.util.regex.Matcher;
// import java.util.regex.Pattern;

// public class HtmlSanitizer {

// // Danh sách các thẻ HTML an toàn
// private static final String[] ALLOWED_TAGS = { "b", "i", "em", "strong", "a",
// "p", "br", "ul", "ol", "li" };

// public static String escapeHtml(String input) {
// if (input == null) {
// return null;
// }

// // Chuyển đổi các thẻ không an toàn
// StringBuilder escapedHtml = new StringBuilder();

// // Biểu thức chính quy để tìm các thẻ HTML
// String regex = "<(\\/?[a-zA-Z][a-zA-Z0-9]*)[^>]*>";
// Pattern pattern = Pattern.compile(regex);
// Matcher matcher = pattern.matcher(input);

// int lastMatchEnd = 0;
// while (matcher.find()) {
// // Lấy thẻ HTML
// String tag = matcher.group(0);
// String tagName = matcher.group(1).toLowerCase();

// // Thêm văn bản trước thẻ HTML
// escapedHtml.append(escapeSpecialChars(input.substring(lastMatchEnd,
// matcher.start())));

// // Kiểm tra thẻ có nằm trong danh sách cho phép không
// if (isAllowedTag(tagName)) {
// escapedHtml.append(tag); // Giữ nguyên thẻ an toàn
// } else {
// escapedHtml.append("&lt;").append(tagName).append("&gt;"); // Thay thế thẻ
// không an toàn
// }

// lastMatchEnd = matcher.end();
// }

// // Thêm phần còn lại của chuỗi sau thẻ cuối cùng
// escapedHtml.append(escapeSpecialChars(input.substring(lastMatchEnd)));

// return escapedHtml.toString();
// }

// private static boolean isAllowedTag(String tagName) {
// for (String allowedTag : ALLOWED_TAGS) {
// if (allowedTag.equals(tagName)) {
// return true; // Thẻ an toàn
// }
// }
// return false; // Thẻ không an toàn
// }

// private static String escapeSpecialChars(String input) {
// StringBuilder escaped = new StringBuilder();
// for (char c : input.toCharArray()) {
// switch (c) {
// case '<':
// escaped.append("&lt;");
// break;
// case '>':
// escaped.append("&gt;");
// break;
// case '&':
// escaped.append("&amp;");
// break;
// case '"':
// escaped.append("&quot;");
// break;
// case '\'':
// escaped.append("&apos;");
// break;
// default:
// escaped.append(c);
// }
// }
// return escaped.toString();
// }
// }
