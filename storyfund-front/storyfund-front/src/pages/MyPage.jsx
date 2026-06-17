import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";

function MyPage() {
  const navigate = useNavigate();

  const [userInfo, setUserInfo] = useState(null);
  const [boards, setBoards] = useState([]);
  const [payments, setPayments] = useState([]);
  const [unlocked, setUnlocked] = useState([]);
  const [activeTab, setActiveTab] = useState("boards"); // boards / payments / unlocked
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchAll();
  }, []);

  const fetchAll = async () => {
    try {
      // 세 개 동시 호출
      const [userRes, boardRes, paymentRes, unlockedRes] = await Promise.all([
        api.get("/api/users/me"),
        api.get("/api/users/me/boards"),
        api.get("/api/payments/history"),
        api.get("/api/users/me/unlocked"),
      ]);

      setUserInfo(userRes.data);
      setBoards(boardRes.data.content);
      setPayments(paymentRes.data.content);
      setUnlocked(unlockedRes.data.content);
    } catch (err) {
      console.error("마이페이지 조회 실패", err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div style={styles.center}>불러오는 중...</div>;
  if (!userInfo) return <div style={styles.center}>로그인이 필요합니다.</div>;

  return (
    <div style={styles.container}>
      {/* 뒤로가기 */}
      <button style={styles.backButton} onClick={() => navigate("/boards")}>
        ← 게시판으로
      </button>

      {/* 내 정보 카드 */}
      <div style={styles.profileCard}>
        <div style={styles.avatar}>
          {userInfo.nickname?.charAt(0).toUpperCase()}
        </div>
        <div style={styles.profileInfo}>
          <div style={styles.nickname}>{userInfo.nickname}</div>
          <div style={styles.email}>{userInfo.email}</div>
          <div style={styles.joinDate}>
            가입일: {new Date(userInfo.createdAt).toLocaleDateString("ko-KR")}
          </div>
        </div>
        <div style={styles.coinBox}>
          <div style={styles.coinAmount}>🪙 {userInfo.coin}</div>
          <div style={styles.coinLabel}>보유 코인</div>
          <button
            style={styles.chargeButton}
            onClick={() => navigate("/payment")}
          >
            충전하기
          </button>
        </div>
      </div>

      {/* 탭 */}
      <div style={styles.tabs}>
        {[
          { key: "boards", label: "내 게시글" },
          { key: "payments", label: "결제 내역" },
          { key: "unlocked", label: "열람 내역" },
        ].map((tab) => (
          <button
            key={tab.key}
            style={{
              ...styles.tab,
              ...(activeTab === tab.key ? styles.tabActive : {}),
            }}
            onClick={() => setActiveTab(tab.key)}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {/* 탭 내용 */}
      <div style={styles.tabContent}>
        {/* 내 게시글 */}
        {activeTab === "boards" && (
          <>
            {boards.length === 0 ? (
              <p style={styles.empty}>작성한 게시글이 없어요.</p>
            ) : (
              boards.map((board) => (
                <div
                  key={board.id}
                  style={styles.listItem}
                  onClick={() => navigate(`/boards/${board.id}`)}
                >
                  <div style={styles.listTitle}>
                    {board.isPaid && <span style={styles.paidBadge}>유료</span>}
                    {board.title}
                  </div>
                  <div style={styles.listMeta}>
                    <span>조회 {board.viewCount}</span>
                    <span>
                      {new Date(board.createdAt).toLocaleDateString("ko-KR")}
                    </span>
                  </div>
                </div>
              ))
            )}
          </>
        )}

        {/* 결제 내역 */}
        {activeTab === "payments" && (
          <>
            {payments.length === 0 ? (
              <p style={styles.empty}>결제 내역이 없어요.</p>
            ) : (
              payments.map((payment) => (
                <div key={payment.id} style={styles.listItem}>
                  <div style={styles.paymentRow}>
                    <div>
                      <div style={styles.listTitle}>
                        🪙 {payment.coinAmount}코인 충전
                      </div>
                      <div style={styles.listMeta}>
                        {new Date(payment.paidAt).toLocaleDateString("ko-KR")}
                      </div>
                    </div>
                    <div style={styles.paymentAmount}>
                      {payment.amount.toLocaleString()}원
                    </div>
                  </div>
                </div>
              ))
            )}
          </>
        )}

        {/* 열람 내역 */}
        {activeTab === "unlocked" && (
          <>
            {unlocked.length === 0 ? (
              <p style={styles.empty}>열람한 유료 게시글이 없어요.</p>
            ) : (
              unlocked.map((item) => (
                <div
                  key={item.boardId}
                  style={styles.listItem}
                  onClick={() => navigate(`/boards/${item.boardId}`)}
                >
                  <div style={styles.listTitle}>{item.boardTitle}</div>
                  <div style={styles.listMeta}>
                    열람일:{" "}
                    {new Date(item.unlockedAt).toLocaleDateString("ko-KR")}
                  </div>
                </div>
              ))
            )}
          </>
        )}
      </div>
    </div>
  );
}

const styles = {
  container: {
    maxWidth: "700px",
    margin: "40px auto",
    padding: "0 20px 60px",
  },
  center: {
    textAlign: "center",
    padding: "80px 20px",
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

  // ── 프로필 카드 ──
  profileCard: {
    display: "flex",
    alignItems: "center",
    gap: "20px",
    padding: "24px",
    border: "1px solid #eee",
    borderRadius: "14px",
    marginBottom: "28px",
    flexWrap: "wrap",
  },
  avatar: {
    width: "56px",
    height: "56px",
    borderRadius: "50%",
    background: "#4F8EF7",
    color: "white",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    fontSize: "24px",
    fontWeight: "700",
    flexShrink: 0,
  },
  profileInfo: {
    flex: 1,
  },
  nickname: {
    fontSize: "18px",
    fontWeight: "700",
    marginBottom: "4px",
  },
  email: {
    fontSize: "14px",
    color: "#888",
    marginBottom: "4px",
  },
  joinDate: {
    fontSize: "13px",
    color: "#aaa",
  },
  coinBox: {
    textAlign: "center",
    padding: "16px 20px",
    background: "#fffbea",
    border: "1px solid #FFD700",
    borderRadius: "10px",
    minWidth: "110px",
  },
  coinAmount: {
    fontSize: "22px",
    fontWeight: "700",
    marginBottom: "4px",
  },
  coinLabel: {
    fontSize: "12px",
    color: "#888",
    marginBottom: "10px",
  },
  chargeButton: {
    padding: "6px 14px",
    background: "#FFD700",
    color: "#333",
    border: "none",
    borderRadius: "6px",
    fontSize: "13px",
    fontWeight: "600",
    cursor: "pointer",
  },

  // ── 탭 ──
  tabs: {
    display: "flex",
    borderBottom: "2px solid #eee",
    marginBottom: "20px",
  },
  tab: {
    padding: "12px 20px",
    background: "none",
    border: "none",
    borderBottom: "2px solid transparent",
    marginBottom: "-2px",
    fontSize: "14px",
    fontWeight: "500",
    color: "#888",
    cursor: "pointer",
  },
  tabActive: {
    borderBottom: "2px solid #4F8EF7",
    color: "#4F8EF7",
    fontWeight: "700",
  },
  tabContent: {
    minHeight: "200px",
  },

  // ── 목록 아이템 ──
  listItem: {
    padding: "14px 16px",
    border: "1px solid #eee",
    borderRadius: "8px",
    marginBottom: "10px",
    cursor: "pointer",
    transition: "background 0.1s",
  },
  listTitle: {
    fontSize: "15px",
    fontWeight: "500",
    marginBottom: "6px",
    display: "flex",
    alignItems: "center",
    gap: "8px",
  },
  listMeta: {
    display: "flex",
    gap: "12px",
    fontSize: "13px",
    color: "#aaa",
  },
  paidBadge: {
    fontSize: "11px",
    padding: "2px 7px",
    background: "#FFD700",
    color: "#333",
    borderRadius: "99px",
    fontWeight: "600",
  },
  empty: {
    textAlign: "center",
    color: "#aaa",
    padding: "40px 0",
    fontSize: "14px",
  },
  paymentRow: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
  },
  paymentAmount: {
    fontSize: "16px",
    fontWeight: "700",
    color: "#4F8EF7",
  },
};

export default MyPage;
