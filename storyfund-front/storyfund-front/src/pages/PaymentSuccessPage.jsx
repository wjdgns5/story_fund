import { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import api from "../api/axios";

function PaymentSuccessPage() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams(); // URL 파라미터 읽기
  const [status, setStatus] = useState("loading"); // loading / success / fail
  const [message, setMessage] = useState("");

  useEffect(() => {
    confirmPayment();
  }, []);

  const confirmPayment = async () => {
    // Toss 가 보내준 파라미터 꺼내기
    const paymentKey = searchParams.get("paymentKey");
    const orderId = searchParams.get("orderId");
    const amount = searchParams.get("amount");

    // 파라미터 없으면 에러
    if (!paymentKey || !orderId || !amount) {
      setStatus("fail");
      setMessage("결제 정보가 올바르지 않습니다.");
      return;
    }

    try {
      // 백엔드에 결제 검증 요청 (Toss 서버 최종 승인 + 코인 충전)
      await api.post("/api/payments/confirm", {
        paymentKey,
        orderId,
        amount: Number(amount),
      });

      setStatus("success");
      setMessage(`${Number(amount).toLocaleString()}원 결제가 완료됐어요.`);
    } catch (err) {
      setStatus("fail");
      setMessage(err.response?.data?.message || "결제 검증에 실패했습니다.");
    }
  };

  // ── 처리 중 ──
  if (status === "loading") {
    return (
      <div style={styles.container}>
        <div style={styles.icon}>⏳</div>
        <p style={styles.text}>결제를 처리하는 중이에요...</p>
      </div>
    );
  }

  // ── 성공 ──
  if (status === "success") {
    return (
      <div style={styles.container}>
        <div style={styles.icon}>✅</div>
        <h2 style={styles.title}>결제 완료!</h2>
        <p style={styles.text}>{message}</p>
        <p style={styles.subText}>
          코인이 충전됐어요. 유료 게시글을 열람해보세요.
        </p>
        <div style={styles.buttonRow}>
          <button
            style={styles.boardButton}
            onClick={() => navigate("/boards")}
          >
            게시판으로
          </button>
          <button
            style={styles.chargeButton}
            onClick={() => navigate("/payment")}
          >
            추가 충전
          </button>
        </div>
      </div>
    );
  }

  // ── 실패 ──
  return (
    <div style={styles.container}>
      <div style={styles.icon}>❌</div>
      <h2 style={styles.title}>결제 실패</h2>
      <p style={styles.text}>{message}</p>
      <button style={styles.boardButton} onClick={() => navigate("/payment")}>
        다시 시도하기
      </button>
    </div>
  );
}

const styles = {
  container: {
    maxWidth: "400px",
    margin: "100px auto",
    padding: "40px 20px",
    textAlign: "center",
  },
  icon: {
    fontSize: "56px",
    marginBottom: "16px",
  },
  title: {
    fontSize: "24px",
    fontWeight: "700",
    margin: "0 0 12px",
  },
  text: {
    fontSize: "16px",
    color: "#555",
    margin: "0 0 8px",
  },
  subText: {
    fontSize: "14px",
    color: "#aaa",
    margin: "0 0 32px",
  },
  buttonRow: {
    display: "flex",
    gap: "10px",
    justifyContent: "center",
    marginTop: "24px",
  },
  boardButton: {
    padding: "12px 24px",
    background: "#4F8EF7",
    color: "white",
    border: "none",
    borderRadius: "8px",
    fontSize: "15px",
    cursor: "pointer",
    fontWeight: "600",
  },
  chargeButton: {
    padding: "12px 24px",
    background: "#6c757d",
    color: "white",
    border: "none",
    borderRadius: "8px",
    fontSize: "15px",
    cursor: "pointer",
    fontWeight: "600",
  },
};

export default PaymentSuccessPage;
