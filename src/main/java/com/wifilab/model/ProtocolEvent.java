package com.wifilab.model;

public record ProtocolEvent(
        int step,
        String actor,
        String message,
        String status,
        String explanation,
        String securityNote
) {
    // Giữ tương thích với các lời gọi constructor 4 tham số hiện tại.
    public ProtocolEvent(
            int step,
            String actor,
            String message,
            String status
    ) {
        this(
            step,
            actor,
            message,
            status,
            explain(message, status),
            security(status)
        );
    }

    private static String explain(String message, String status) {
        if (message == null) {
            return "Sự kiện mô phỏng giao thức.";
        }

        if (message.contains("Commit")) {
            return "Commit là một giai đoạn của SAE, trong đó hai bên trao đổi thông tin cần thiết để thực hiện xác thực dựa trên mật khẩu. Đây là mô tả khái quát, không phải dữ liệu mật mã thực tế.";
        }

        if (message.contains("Confirm")) {
            return "Confirm được dùng để xác nhận tính hợp lệ của quá trình trao đổi SAE. Trong mô hình này, kết quả được giả lập chứ không được tính bằng thuật toán mật mã.";
        }

        if (message.contains("Access-Accept")) {
            return "RADIUS gửi phản hồi chấp thuận. Trong triển khai thực tế, authenticator xử lý phản hồi theo chính sách kiểm soát truy cập.";
        }

        if (message.contains("Access-Reject")) {
            return "RADIUS từ chối yêu cầu xác thực. Thiết bị không được cấp quyền truy cập theo kịch bản này.";
        }

        if (message.contains("thời gian chờ") ||
            message.contains("không phản hồi")) {
            return "Khi không nhận được phản hồi đúng thời hạn, hệ thống cần xử lý timeout và không được mặc định coi yêu cầu là thành công.";
        }

        return "Đây là sự kiện minh họa một giai đoạn trong luồng giao thức. Xem trạng thái để biết bước này có cho phép tiếp tục hay không.";
    }

    private static String security(String status) {
        if ("SUCCESS".equalsIgnoreCase(status)) {
            return "Bước mô phỏng được đánh dấu thành công. Điều này không chứng minh đã thực hiện xác thực với thiết bị Wi-Fi thật.";
        }

        if ("FAILED".equalsIgnoreCase(status)) {
            return "Kịch bản mô phỏng đánh dấu xác thực thất bại. Trong hệ thống thật, kết quả phụ thuộc vào việc kiểm tra giao thức và chính sách thực tế.";
        }

        if ("BLOCKED".equalsIgnoreCase(status)) {
            return "Luồng kết nối thành công không được tiếp tục trong kịch bản mô phỏng này.";
        }

        if ("WARNING".equalsIgnoreCase(status)) {
            return "Có điều kiện bất thường cần được kiểm tra trước khi cho phép quá trình xác thực tiếp tục.";
        }

        return "Sự kiện mang tính thông tin, chưa khẳng định xác thực thành công hay thất bại.";
    }
}