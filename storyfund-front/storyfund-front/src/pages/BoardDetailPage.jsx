import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/axios";

function BoardDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [board, setBoard] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [myEmail, setMyEmail] = useState("");
  const [myRole, setMyRole] = useState("");

  useEffect(() => {
    fetchBoard();
    checkLoginInfo();
  }, [id]);

  // 게시글 상세 조회
  async function fetchBoard() {
    setLoading(true);
    try {
      const response = await api.get(`/api/boards/${id}`);
      setBoard(response.data);
    } catch (err) {
      setError("게시글을 불러올 수 없습니다.");
    } finally {
      setLoading(false);
    }
  }

  // 로그인 정보 꺼내기 (JWT 디코딩)
  function checkLoginInfo() {
    const token = localStorage.getItem("accessToken");
    if (!token) return;

    try {
      // JWT payload 는 Base64 인코딩 → atob 으로 디코딩
      const payload = JSON.parse(atob(token.split(".")[1]));
      setMyEmail(payload.sub); // subject = email
      setMyRole(payload.role); // 역할
    } catch (e) {
      console.error("토큰 파싱 실패", e);
    }
  }

  // 유료 글 코인 차감 열람
  const handleUnlock = async () => {
    try {
      await api.post(`/api/payments/unlock/${id}`);
      alert("코인 1개가 차감됐습니다.");
      // 열람 후 게시글 다시 조회 → 백엔드가 locked: false 로 반환
      const response = await api.get(`/api/boards/${id}`);
      setBoard(response.data);
    } catch (err) {
      alert(err.response?.data?.message || "열람에 실패했습니다.");
    }
  };

  // 게시글 삭제
  const handleDelete = async () => {
    if (!window.confirm("정말 삭제하시겠습니까?")) return;
    try {
      await api.delete(`/api/boards/${id}`);
      alert("삭제됐습니다.");
      navigate("/boards");
    } catch (err) {
      alert(err.response?.data?.message || "삭제에 실패했습니다.");
    }
  };

  // 작성자 본인인지 확인
  const isAuthor =
    myEmail && board?.authorEmail ? board.authorEmail === myEmail : false;

  // 관리자인지 확인
  const isAdmin = myRole === "ROLE_ADMIN";

  // 전체 내용 표시 여부 (백엔드가 locked 판단)
  const showFullContent = !board?.locked;

  // ── 로딩 ──
  if (loading) {
    return <div style={styles.center}>불러오는 중...</div>;
  }

  // ── 에러 ──
  if (error) {
    return (
      <div style={styles.center}>
        <p style={{ color: "red" }}>{error}</p>
        <button style={styles.blueButton} onClick={() => navigate("/boards")}>
          목록으로
        </button>
      </div>
    );
  }

  if (!board) return null;

  return (
    <div style={styles.container}>
      {/* 뒤로가기 */}
      <button style={styles.backButton} onClick={() => navigate("/boards")}>
        ← 목록으로
      </button>

      {/* 게시글 헤더 */}
      <div style={styles.header}>
        <div style={styles.titleRow}>
          {board.isPaid && <span style={styles.paidBadge}>🔒 유료</span>}
          <h1 style={styles.title}>{board.title}</h1>
        </div>
        <div style={styles.meta}>
          <span>✍️ {board.authorNickname}</span>
          <span>👁 조회 {board.viewCount}</span>
          <span>
            📅 {new Date(board.createdAt).toLocaleDateString("ko-KR")}
          </span>
        </div>
      </div>

      <hr style={styles.divider} />

      {/* 게시글 본문 */}
      {showFullContent ? (
        // ── 전체 내용 ──
        <div style={styles.content}>
          <p style={styles.contentText}>{board.content}</p>
        </div>
      ) : (
        // ── 잠긴 상태 ──
        <>
          {/* 미리보기 — 페이드 아웃 */}
          <div style={styles.previewWrap}>
            <p style={styles.contentText}>{board.content}</p>
            <div style={styles.fadeOut} />
          </div>

          {/* 잠금 박스 */}
          <div style={styles.lockBox}>
            <div style={styles.lockIcon}>🔒</div>
            <p style={styles.lockTitle}>유료 게시글입니다</p>
            <p style={styles.lockDesc}>
              코인 1개를 사용하면 전체 내용을 볼 수 있어요.
            </p>

            {myEmail ? (
              // 로그인한 경우 → 코인 차감 버튼
              <button style={styles.unlockButton} onClick={handleUnlock}>
                🪙 코인 1개로 전체 보기
              </button>
            ) : (
              // 비로그인 → 로그인 유도 버튼
              <button
                style={styles.loginButton}
                onClick={() => navigate("/login")}
              >
                🔑 로그인하고 열람하기
              </button>
            )}
          </div>
        </>
      )}

      {/* 수정 / 삭제 (작성자 or 관리자) */}
      {(isAuthor || isAdmin) && (
        <div style={styles.actionRow}>
          {isAuthor && (
            <button
              style={styles.editButton}
              onClick={() => navigate(`/boards/${id}/edit`)}
            >
              ✏️ 수정
            </button>
          )}
          <button style={styles.deleteButton} onClick={handleDelete}>
            🗑 삭제
          </button>
        </div>
      )}
    </div>
  );
}

const styles = {
  container: {
    maxWidth: "800px",
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
  header: {
    marginBottom: "16px",
  },
  titleRow: {
    display: "flex",
    alignItems: "center",
    gap: "10px",
    marginBottom: "10px",
    flexWrap: "wrap",
  },
  title: {
    fontSize: "24px",
    fontWeight: "600",
    margin: "0",
  },
  paidBadge: {
    fontSize: "12px",
    padding: "4px 10px",
    background: "#FFD700",
    color: "#333",
    borderRadius: "99px",
    fontWeight: "700",
    whiteSpace: "nowrap",
  },
  meta: {
    display: "flex",
    gap: "16px",
    fontSize: "13px",
    color: "#888",
    flexWrap: "wrap",
  },
  divider: {
    border: "none",
    borderTop: "1px solid #eee",
    margin: "16px 0 24px",
  },
  content: {
    minHeight: "200px",
  },
  contentText: {
    fontSize: "16px",
    lineHeight: "1.9",
    color: "#333",
    whiteSpace: "pre-wrap",
    margin: "0",
  },

  // ── 잠금 미리보기 ──
  previewWrap: {
    position: "relative",
    maxHeight: "160px",
    overflow: "hidden",
    marginBottom: "0",
  },
  fadeOut: {
    position: "absolute",
    bottom: 0,
    left: 0,
    right: 0,
    height: "100px",
    background: "linear-gradient(rgba(255,255,255,0), rgba(255,255,255,1))",
    pointerEvents: "none",
  },

  // ── 잠금 박스 ──
  lockBox: {
    textAlign: "center",
    padding: "36px 24px",
    background: "#fffbea",
    border: "2px solid #FFD700",
    borderRadius: "14px",
    marginTop: "0",
  },
  lockIcon: {
    fontSize: "36px",
    marginBottom: "12px",
  },
  lockTitle: {
    fontSize: "18px",
    fontWeight: "700",
    color: "#333",
    margin: "0 0 8px",
  },
  lockDesc: {
    fontSize: "14px",
    color: "#888",
    margin: "0 0 24px",
  },
  unlockButton: {
    padding: "14px 36px",
    background: "#FFD700",
    color: "#111",
    border: "none",
    borderRadius: "8px",
    fontSize: "16px",
    fontWeight: "700",
    cursor: "pointer",
    display: "inline-block",
    transition: "opacity 0.15s",
  },
  loginButton: {
    padding: "14px 36px",
    background: "#4F8EF7",
    color: "white",
    border: "none",
    borderRadius: "8px",
    fontSize: "16px",
    fontWeight: "700",
    cursor: "pointer",
    display: "inline-block",
  },

  // ── 액션 버튼 ──
  actionRow: {
    display: "flex",
    justifyContent: "flex-end",
    gap: "8px",
    marginTop: "48px",
    paddingTop: "24px",
    borderTop: "1px solid #eee",
  },
  blueButton: {
    padding: "10px 20px",
    background: "#4F8EF7",
    color: "white",
    border: "none",
    borderRadius: "6px",
    fontSize: "14px",
    cursor: "pointer",
    display: "inline-block",
  },
  editButton: {
    padding: "10px 20px",
    background: "#6c757d",
    color: "white",
    border: "none",
    borderRadius: "6px",
    fontSize: "14px",
    cursor: "pointer",
  },
  deleteButton: {
    padding: "10px 20px",
    background: "#dc3545",
    color: "white",
    border: "none",
    borderRadius: "6px",
    fontSize: "14px",
    cursor: "pointer",
  },
};

export default BoardDetailPage;
