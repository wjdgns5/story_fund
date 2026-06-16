import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { loadTossPayments, ANONYMOUS } from "@tosspayments/tosspayments-sdk";
import api from "../api/axios";

// 코인 패키지 목록
const COIN_PACKAGES = [
  { id: 1, amount: 1000, coinAmount: 10, label: "10 코인", bonus: "" },
  {
    id: 2,
    amount: 5000,
    coinAmount: 55,
    label: "55 코인",
    bonus: "5코인 보너스",
  },
  {
    id: 3,
    amount: 10000,
    coinAmount: 120,
    label: "120 코인",
    bonus: "20코인 보너스",
  },
];

function PaymentPage() {
  const navigate = useNavigate();
  const [selected, setSelected] = useState(null); // 선택한 패키지
  const [loading, setLoading] = useState(false); // 결제 진행 중
  const [error, setError] = useState("");

  const handlePayment = async () => {
    if (!selected) {
      setError("코인 패키지를 선택해주세요.");
      return;
    }

    setLoading(true);
    setError("");

    try {
      // 1. 백엔드에 주문 생성 → orderId 발급
      const orderRes = await api.post("/api/payments/order", {
        amount: selected.amount,
        coinAmount: selected.coinAmount,
      });
      const { orderId } = orderRes.data;

      // 2. Toss SDK 초기화
      const tossPayments = await loadTossPayments(
        import.meta.env.VITE_TOSS_CLIENT_KEY,
      );

      // 3. 결제 요청 → Toss 결제창 실행
      const payment = tossPayments.payment({ customerKey: ANONYMOUS });
      await payment.requestPayment({
        method: "CARD",
        amount: {
          currency: "KRW",
          value: selected.amount,
        },
        orderId,
        orderName: `StoryFund 코인 ${selected.coinAmount}개`,
        successUrl: `${window.location.origin}/payment/success`,
        failUrl: `${window.location.origin}/payment/fail`,
      });

      // requestPayment 이후 코드는 실행 안 됨 (Toss 가 리다이렉트)
    } catch (err) {
      setError(err.response?.data?.message || "결제 요청에 실패했습니다.");
      setLoading(false);
    }
  };

  return (
    <div style={styles.container}>
      <button style={styles.backButton} onClick={() => navigate("/boards")}>
        ← 게시판으로
      </button>

      <h2 style={styles.pageTitle}>🪙 코인 충전</h2>
      <p style={styles.pageDesc}>
        충전한 코인으로 유료 게시글을 열람할 수 있어요.
      </p>

      {/* 코인 패키지 선택 */}
      <div style={styles.packageList}>
        {COIN_PACKAGES.map((pkg) => (
          <div
            key={pkg.id}
            style={{
              ...styles.packageCard,
              ...(selected?.id === pkg.id ? styles.packageCardSelected : {}),
            }}
            onClick={() => setSelected(pkg)}
          >
            <div style={styles.packageCoin}>{pkg.label}</div>
            <div style={styles.packagePrice}>
              {pkg.amount.toLocaleString()}원
            </div>
            {pkg.bonus && <div style={styles.packageBonus}>{pkg.bonus}</div>}
            {selected?.id === pkg.id && (
              <div style={styles.packageCheck}>✓</div>
            )}
          </div>
        ))}
      </div>

      {/* 선택 요약 */}
      {selected && (
        <div style={styles.summary}>
          <span>선택:</span>
          <strong>
            {selected.coinAmount}코인 / {selected.amount.toLocaleString()}원
          </strong>
        </div>
      )}

      {/* 에러 */}
      {error && <p style={styles.error}>{error}</p>}

      {/* 결제 버튼 */}
      <button
        style={{
          ...styles.payButton,
          opacity: loading ? 0.6 : 1,
          cursor: loading ? "not-allowed" : "pointer",
        }}
        onClick={handlePayment}
        disabled={loading}
      >
        {loading ? "결제창 여는 중..." : "결제하기"}
      </button>

      {/* 안내 */}
      <div style={styles.notice}>
        <p>• 결제는 Toss Payments 를 통해 안전하게 처리돼요.</p>
        <p>• 테스트 환경에서는 실제 결제가 발생하지 않아요.</p>
        <p>• 충전된 코인은 유료 게시글 열람에 사용돼요.</p>
      </div>
    </div>
  );
}

const styles = {
  container: {
    maxWidth: "500px",
    margin: "40px auto",
    padding: "0 20px 60px",
  },
  backButton: {
    background: "none",
    border: "none",
    color: "#4F8EF7",
    cursor: "pointer",
    fontSize: "14px",
    padding: "0",
    marginBottom: "24px",
    display: "block",
  },
  pageTitle: {
    fontSize: "24px",
    fontWeight: "700",
    margin: "0 0 8px",
  },
  pageDesc: {
    fontSize: "14px",
    color: "#888",
    margin: "0 0 28px",
  },
  packageList: {
    display: "flex",
    flexDirection: "column",
    gap: "12px",
    marginBottom: "20px",
  },
  packageCard: {
    padding: "18px 20px",
    border: "2px solid #eee",
    borderRadius: "12px",
    cursor: "pointer",
    position: "relative",
    transition: "all 0.15s",
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
  },
  packageCardSelected: {
    border: "2px solid #4F8EF7",
    background: "#F0F6FF",
  },
  packageCoin: {
    fontSize: "18px",
    fontWeight: "700",
  },
  packagePrice: {
    fontSize: "16px",
    color: "#555",
    fontWeight: "600",
  },
  packageBonus: {
    fontSize: "12px",
    padding: "3px 8px",
    background: "#FFD700",
    color: "#333",
    borderRadius: "99px",
    fontWeight: "600",
  },
  packageCheck: {
    position: "absolute",
    top: "10px",
    right: "14px",
    color: "#4F8EF7",
    fontWeight: "700",
    fontSize: "16px",
  },
  summary: {
    display: "flex",
    gap: "8px",
    alignItems: "center",
    padding: "12px 16px",
    background: "#f8f9fa",
    borderRadius: "8px",
    fontSize: "14px",
    color: "#555",
    marginBottom: "12px",
  },
  error: {
    color: "red",
    fontSize: "13px",
    margin: "0 0 12px",
  },
  payButton: {
    width: "100%",
    padding: "16px",
    background: "#4F8EF7",
    color: "white",
    border: "none",
    borderRadius: "10px",
    fontSize: "17px",
    fontWeight: "700",
    cursor: "pointer",
    marginBottom: "20px",
  },
  notice: {
    fontSize: "13px",
    color: "#aaa",
    lineHeight: "1.8",
  },
};

export default PaymentPage;
