import { useNavigate, useSearchParams } from "react-router-dom";

function PaymentFailPage() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  const code = searchParams.get("code"); // 에러 코드
  const message = searchParams.get("message"); // 에러 메시지

  return (
    <div style={styles.container}>
      <div style={styles.icon}>❌</div>
      <h2 style={styles.title}>결제가 취소됐어요</h2>
      <p style={styles.text}>{message || "결제 진행 중 오류가 발생했어요."}</p>
      {code && <p style={styles.code}>오류 코드: {code}</p>}
      <div style={styles.buttonRow}>
        <button style={styles.retryButton} onClick={() => navigate("/payment")}>
          다시 시도하기
        </button>
        <button style={styles.boardButton} onClick={() => navigate("/boards")}>
          게시판으로
        </button>
      </div>
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
  code: {
    fontSize: "13px",
    color: "#aaa",
    margin: "0 0 32px",
  },
  buttonRow: {
    display: "flex",
    gap: "10px",
    justifyContent: "center",
    marginTop: "24px",
  },
  retryButton: {
    padding: "12px 24px",
    background: "#4F8EF7",
    color: "white",
    border: "none",
    borderRadius: "8px",
    fontSize: "15px",
    cursor: "pointer",
    fontWeight: "600",
  },
  boardButton: {
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

export default PaymentFailPage;
