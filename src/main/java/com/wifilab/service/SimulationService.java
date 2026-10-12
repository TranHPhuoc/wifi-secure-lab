package com.wifilab.service;

import com.wifilab.model.ProtocolEvent;
import com.wifilab.model.SimulationResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class SimulationService {

    /*
     * Educational protocol simulator.
     * This service does NOT implement real SAE cryptography,
     * EAP authentication, or RADIUS communication.
     */

    public SimulationResult simulateSae(String scenario) {
        String selectedScenario = normalizeScenario(scenario);

        return switch (selectedScenario) {
            case "SUCCESS" -> buildSaeSuccess();
            case "PASSWORD_MISMATCH" -> buildSaePasswordMismatch();
            case "CONFIRM_FAILURE" -> buildSaeConfirmFailure();
            case "FAILED", "FAILURE" -> buildSaePasswordMismatch();
            default -> throw new IllegalArgumentException(
                "Kịch bản SAE không hợp lệ: " + scenario
            );
        };
    }

    private SimulationResult buildSaeSuccess() {
        List<ProtocolEvent> events = List.of(
            new ProtocolEvent(
                1,
                "Client",
                "Khởi tạo xác thực WPA3-SAE",
                "INFO"
            ),
            new ProtocolEvent(
                2,
                "Client → Access Point",
                "SAE Commit: client gửi thông điệp khởi tạo trao đổi SAE",
                "INFO"
            ),
            new ProtocolEvent(
                3,
                "Access Point → Client",
                "SAE Commit: AP gửi thông điệp Commit phản hồi",
                "INFO"
            ),
            new ProtocolEvent(
                4,
                "Client ↔ Access Point",
                "Hai bên hoàn tất giai đoạn trao đổi Commit trong mô hình",
                "INFO"
            ),
            new ProtocolEvent(
                5,
                "Client → Access Point",
                "SAE Confirm: client gửi thông điệp xác nhận",
                "INFO"
            ),
            new ProtocolEvent(
                6,
                "Access Point → Client",
                "SAE Confirm: AP gửi thông điệp xác nhận",
                "INFO"
            ),
            new ProtocolEvent(
                7,
                "Client + Access Point",
                "Kịch bản giả định các bước xác thực SAE đều hợp lệ",
                "SUCCESS"
            ),
            new ProtocolEvent(
                8,
                "Key Establishment",
                "Có thể chuyển sang các bước bắt tay 4 bước để thiết lập khóa phiên",
                "INFO"
            )
        );

        return new SimulationResult(
            "WPA3-SAE",
            "SIMULATION",
            true,
            "Mô phỏng thành công: Commit và Confirm được giả định hợp lệ.",
            events
        );
    }

    private SimulationResult buildSaePasswordMismatch() {
        List<ProtocolEvent> events = List.of(
            new ProtocolEvent(
                1,
                "Client",
                "Client bắt đầu yêu cầu xác thực WPA3-SAE",
                "INFO"
            ),
            new ProtocolEvent(
                2,
                "Client ↔ Access Point",
                "Hai bên bắt đầu trao đổi SAE Commit",
                "INFO"
            ),
            new ProtocolEvent(
                3,
                "Client + Access Point",
                "Kịch bản mô phỏng giả định thông tin mật khẩu không khớp",
                "WARNING"
            ),
            new ProtocolEvent(
                4,
                "Authentication",
                "Xác thực không hoàn tất trong kịch bản này",
                "FAILED"
            ),
            new ProtocolEvent(
                5,
                "Key Establishment",
                "Không tiếp tục luồng kết nối thành công vì xác thực thất bại",
                "BLOCKED"
            )
        );

        return new SimulationResult(
            "WPA3-SAE",
            "SIMULATION",
            false,
            "Mô phỏng thất bại: kịch bản giả định thông tin xác thực không khớp.",
            events
        );
    }

    private SimulationResult buildSaeConfirmFailure() {
        List<ProtocolEvent> events = List.of(
            new ProtocolEvent(
                1,
                "Client",
                "Bắt đầu quá trình xác thực WPA3-SAE",
                "INFO"
            ),
            new ProtocolEvent(
                2,
                "Client ↔ Access Point",
                "Trao đổi SAE Commit trong mô hình",
                "INFO"
            ),
            new ProtocolEvent(
                3,
                "Client → Access Point",
                "Client gửi SAE Confirm",
                "INFO"
            ),
            new ProtocolEvent(
                4,
                "Access Point",
                "Kịch bản giả định thông điệp Confirm không hợp lệ",
                "WARNING"
            ),
            new ProtocolEvent(
                5,
                "Authentication",
                "Xác thực bị từ chối do kiểm tra Confirm thất bại trong mô hình",
                "FAILED"
            ),
            new ProtocolEvent(
                6,
                "Key Establishment",
                "Không chuyển tiếp sang luồng kết nối thành công",
                "BLOCKED"
            )
        );

        return new SimulationResult(
            "WPA3-SAE",
            "SIMULATION",
            false,
            "Mô phỏng thất bại: thông điệp Confirm được giả định không hợp lệ.",
            events
        );
    }

    public SimulationResult simulateEap(String scenario) {
        String selectedScenario = normalizeScenario(scenario);

        return switch (selectedScenario) {
            case "SUCCESS", "ACCEPT" -> buildEapAccept();
            case "FAILED", "FAILURE", "REJECT" -> buildEapReject();
            case "TIMEOUT" -> buildEapTimeout();
            default -> throw new IllegalArgumentException(
                "Kịch bản EAP/RADIUS không hợp lệ: " + scenario
            );
        };
    }

    private SimulationResult buildEapAccept() {
        List<ProtocolEvent> events = List.of(
            new ProtocolEvent(
                1,
                "Supplicant",
                "Thiết bị yêu cầu truy cập mạng",
                "INFO"
            ),
            new ProtocolEvent(
                2,
                "Authenticator",
                "Khởi tạo quá trình kiểm soát truy cập và chuyển tiếp trao đổi xác thực",
                "INFO"
            ),
            new ProtocolEvent(
                3,
                "Authenticator ↔ Supplicant",
                "Mô phỏng trao đổi các thông điệp EAP",
                "INFO"
            ),
            new ProtocolEvent(
                4,
                "Authenticator → RADIUS",
                "Chuyển yêu cầu xác thực tới máy chủ RADIUS",
                "INFO"
            ),
            new ProtocolEvent(
                5,
                "RADIUS Server",
                "Kịch bản giả định thông tin xác thực đáp ứng chính sách",
                "INFO"
            ),
            new ProtocolEvent(
                6,
                "RADIUS → Authenticator",
                "Access-Accept: máy chủ chấp thuận yêu cầu truy cập",
                "SUCCESS"
            ),
            new ProtocolEvent(
                7,
                "Authenticator",
                "Cho phép tiếp tục truy cập mạng theo chính sách được cấu hình",
                "SUCCESS"
            )
        );

        return new SimulationResult(
            "EAP-RADIUS",
            "SIMULATION",
            true,
            "Mô phỏng thành công: RADIUS trả về Access-Accept.",
            events
        );
    }

    private SimulationResult buildEapReject() {
        List<ProtocolEvent> events = List.of(
            new ProtocolEvent(
                1,
                "Supplicant",
                "Thiết bị yêu cầu truy cập mạng",
                "INFO"
            ),
            new ProtocolEvent(
                2,
                "Authenticator",
                "Bắt đầu luồng xác thực",
                "INFO"
            ),
            new ProtocolEvent(
                3,
                "Authenticator ↔ Supplicant",
                "Mô phỏng trao đổi các thông điệp EAP",
                "INFO"
            ),
            new ProtocolEvent(
                4,
                "Authenticator → RADIUS",
                "Gửi yêu cầu xác thực tới máy chủ RADIUS",
                "INFO"
            ),
            new ProtocolEvent(
                5,
                "RADIUS Server",
                "Kịch bản giả định yêu cầu không đáp ứng chính sách xác thực",
                "WARNING"
            ),
            new ProtocolEvent(
                6,
                "RADIUS → Authenticator",
                "Access-Reject: máy chủ từ chối yêu cầu truy cập",
                "FAILED"
            ),
            new ProtocolEvent(
                7,
                "Authenticator",
                "Không cấp quyền truy cập trong kịch bản này",
                "BLOCKED"
            )
        );

        return new SimulationResult(
            "EAP-RADIUS",
            "SIMULATION",
            false,
            "Mô phỏng thất bại: RADIUS trả về Access-Reject.",
            events
        );
    }

    private SimulationResult buildEapTimeout() {
        List<ProtocolEvent> events = List.of(
            new ProtocolEvent(
                1,
                "Supplicant",
                "Thiết bị yêu cầu truy cập mạng",
                "INFO"
            ),
            new ProtocolEvent(
                2,
                "Authenticator → RADIUS",
                "Authenticator gửi yêu cầu xác thực",
                "INFO"
            ),
            new ProtocolEvent(
                3,
                "RADIUS Server",
                "Kịch bản giả định máy chủ không phản hồi trong thời gian chờ",
                "WARNING"
            ),
            new ProtocolEvent(
                4,
                "Authenticator",
                "Hết thời gian chờ: chưa nhận được kết quả xác thực",
                "FAILED"
            ),
            new ProtocolEvent(
                5,
                "Access Control",
                "Không cấp quyền truy cập khi chưa có kết quả xác thực hợp lệ",
                "BLOCKED"
            )
        );

        return new SimulationResult(
            "EAP-RADIUS",
            "SIMULATION",
            false,
            "Mô phỏng thất bại: hết thời gian chờ phản hồi RADIUS.",
            events
        );
    }

    private String normalizeScenario(String scenario) {
        if (scenario == null || scenario.isBlank()) {
            throw new IllegalArgumentException(
                "Vui lòng chọn một kịch bản mô phỏng."
            );
        }

        return scenario.trim().toUpperCase(Locale.ROOT);
    }
}